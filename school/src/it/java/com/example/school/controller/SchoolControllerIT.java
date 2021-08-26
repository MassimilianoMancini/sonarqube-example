package com.example.school.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.school.model.Student;
import com.example.school.repository.StudentRepository;
import com.example.school.repository.mongo.StudentMongoRepository;
import com.example.school.view.StudentView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

@ExtendWith(MockitoExtension.class)
class SchoolControllerIT {

	@Mock
	private StudentView studentView;

	private StudentRepository studentRepository;
	private SchoolController schoolController;
	private static int mongoPort = Integer.parseInt(System.getProperty("mongo.port", "27017"));
	private static final String STUDENT_COLLECTION_NAME = "student";
	private static final String SCHOOL_DB_NAME = "school";

	@BeforeEach
	void setup() {
		studentRepository = new StudentMongoRepository(new MongoClient(new ServerAddress("localhost", mongoPort)),
				SCHOOL_DB_NAME, STUDENT_COLLECTION_NAME);
		for (Student student : studentRepository.findAll()) {
			studentRepository.delete(student.getId());
		}
		schoolController = new SchoolController(studentView, studentRepository);
	}

	@Test
	void testAllStudents() {
		Student student = new Student("1", "test");
		studentRepository.save(student);
		schoolController.allStudents();
		verify(studentView).showAllStudents(asList(student));
	}

	@Test
	void testNewStudent() {
		Student student = new Student("1", "test");
		schoolController.newStudent(student);
		verify(studentView).studentAdded(student);
	}

	@Test
	void testDeleteStudent() {
		Student studentToDelete = new Student("1", "test");
		studentRepository.save(studentToDelete);
		schoolController.deleteStudent(studentToDelete);
		verify(studentView).studentRemoved(studentToDelete);

	}

}
