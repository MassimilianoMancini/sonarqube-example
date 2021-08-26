package com.example.school.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;

@RunWith(GUITestRunner.class)
public class SchoolSwingAppE2E extends AssertJSwingJUnitTestCase {



	@ClassRule
	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:5.0.2");

	private static final String STUDENT_COLLECTION_NAME = "student";
	private static final String SCHOOL_DB_NAME = "school";
	private static final String STUDENT_FIXTURE_1_ID = "1";
	private static final String STUDENT_FIXTURE_1_NAME = "first student";
	private static final String STUDENT_FIXTURE_2_ID = "2";
	private static final String STUDENT_FIXTURE_2_NAME = "second student";

	private MongoClient mongoClient;
	private FrameFixture window;

	@Override
	protected void onSetUp() throws Exception {
		String containerIpAddress = mongo.getContainerIpAddress();
		Integer mappedPort = mongo.getFirstMappedPort();
		mongoClient = new MongoClient(containerIpAddress, mappedPort);
		// always start with an empty database
		mongoClient.getDatabase(SCHOOL_DB_NAME).drop();
		addTestStudentToDatabase(STUDENT_FIXTURE_1_ID, STUDENT_FIXTURE_1_NAME);
		addTestStudentToDatabase(STUDENT_FIXTURE_2_ID, STUDENT_FIXTURE_2_NAME);
		// start the swing application
		application("com.example.school.app.swing.SchoolSwingApp")
		.withArgs("--mongo-host=" + containerIpAddress, "--mongo-port=" + mappedPort,
				"--db-name=" + SCHOOL_DB_NAME, "--db-collection=" + STUDENT_COLLECTION_NAME)
		.start();
		
		// get reference of its JFrame
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Student View".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());
		
	}


	@Override
	protected void onTearDown() {
		mongoClient.close();
	}
	
	@Test
	public void testOnStartAllDatabaseElementsAreShown() {
		assertThat(window.list().contents())
			.anySatisfy(e -> assertThat(e).contains(STUDENT_FIXTURE_1_ID, STUDENT_FIXTURE_1_NAME))
			.anySatisfy(e -> assertThat(e).contains(STUDENT_FIXTURE_2_ID, STUDENT_FIXTURE_2_NAME));
	}
	
	@Test
	public void testAddButtonSuccess() {
		window.textBox("idTextBox").enterText("10");
		window.textBox("nameTextBox").enterText("new student");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.list().contents()).anySatisfy(e -> assertThat(e).contains("10", "new student"));
	}
	
	@Test
	public void testAddButtonError() {
		window.textBox("idTextBox").enterText(STUDENT_FIXTURE_1_ID);
		window.textBox("nameTextBox").enterText("new one");
		window.button(JButtonMatcher.withText("Add")).click();
		assertThat(window.label("errorMessageLabel").text()).contains(STUDENT_FIXTURE_1_ID, STUDENT_FIXTURE_1_NAME);
	}
	
	@Test
	public void testDeleteButtonSuccess() {
		window.list("studentList").selectItem(Pattern.compile(".*" + STUDENT_FIXTURE_1_NAME + ".*"));
		window.button(JButtonMatcher.withText("Delete Selected")).click();
		assertThat(window.list().contents()).noneMatch(e -> e.contains(STUDENT_FIXTURE_1_NAME));
	}
	
	@Test
	public void testDeleteButtonError() {
		// select student in the list...
		window.list("studentList").selectItem(Pattern.compile(".*" + STUDENT_FIXTURE_1_NAME + ".*"));
		// ...in the meantime, manually remove the student from the database
		removeTestStudentFromDatabase(STUDENT_FIXTURE_1_ID);
		// now press the delete button
		window.button(JButtonMatcher.withText("Delete Selected")).click();
		// and verify an error is shown
		assertThat(window.label("errorMessageLabel").text()).contains(STUDENT_FIXTURE_1_ID, STUDENT_FIXTURE_1_NAME);
		
	}

	private void addTestStudentToDatabase(String id, String name) {
		mongoClient.getDatabase(SCHOOL_DB_NAME).getCollection(STUDENT_COLLECTION_NAME)
				.insertOne(new Document().append("id", id).append("name", name));
	}
	
	private void removeTestStudentFromDatabase(String id) {
		mongoClient
			.getDatabase(SCHOOL_DB_NAME)
			.getCollection(STUDENT_COLLECTION_NAME)
			.deleteOne(Filters.eq("id", id));
	}


}
