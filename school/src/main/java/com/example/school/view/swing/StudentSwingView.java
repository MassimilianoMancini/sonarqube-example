package com.example.school.view.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.example.school.controller.SchoolController;
import com.example.school.model.Student;
import com.example.school.view.StudentView;

public class StudentSwingView extends JFrame implements StudentView {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1L;
	private JPanel contentPane;
	private JTextField txtId;
	private JLabel lblNewLabel;
	private JTextField txtName;
	private JButton btnAdd;
	private JScrollPane scrollPane;
	private JList<Student> listStudents;
	private DefaultListModel<Student> listStudentsModel;

	private JButton btnDelete;
	private JLabel errorMessageLabel;
	private transient SchoolController schoolController;

	transient KeyAdapter btnAddEnabler = new KeyAdapter() {
		@Override
		public void keyReleased(KeyEvent e) {
			btnAdd.setEnabled(!txtId.getText().trim().isEmpty() && !txtName.getText().trim().isEmpty());
		}
	};

	DefaultListModel<Student> getListStudentsModel() {
		return listStudentsModel;
	}



	public StudentSwingView() {
		setTitle("Student View");
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gblContentPane = new GridBagLayout();
		gblContentPane.columnWidths = new int[] { 27, 96, 0 };
		gblContentPane.rowHeights = new int[] { 20, 14, 0, 0, 0, 0, 0 };
		gblContentPane.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gblContentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gblContentPane);

		JLabel lblId = new JLabel("id");
		GridBagConstraints gbcLblId = new GridBagConstraints();
		gbcLblId.anchor = GridBagConstraints.EAST;
		gbcLblId.insets = new Insets(0, 0, 5, 5);
		gbcLblId.gridx = 0;
		gbcLblId.gridy = 0;
		contentPane.add(lblId, gbcLblId);

		txtId = new JTextField();
		txtId.addKeyListener(btnAddEnabler);
		txtId.setName("idTextBox");
		GridBagConstraints gbcTxtId = new GridBagConstraints();
		gbcTxtId.anchor = GridBagConstraints.NORTH;
		gbcTxtId.fill = GridBagConstraints.HORIZONTAL;
		gbcTxtId.insets = new Insets(0, 0, 5, 0);
		gbcTxtId.gridx = 1;
		gbcTxtId.gridy = 0;
		contentPane.add(txtId, gbcTxtId);
		txtId.setColumns(10);

		lblNewLabel = new JLabel("name");
		GridBagConstraints gbcLblNewLabel = new GridBagConstraints();
		gbcLblNewLabel.anchor = GridBagConstraints.EAST;
		gbcLblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbcLblNewLabel.gridx = 0;
		gbcLblNewLabel.gridy = 1;
		contentPane.add(lblNewLabel, gbcLblNewLabel);

		txtName = new JTextField();
		txtName.addKeyListener(btnAddEnabler);
		txtName.setName("nameTextBox");
		GridBagConstraints gbcTextField = new GridBagConstraints();
		gbcTextField.insets = new Insets(0, 0, 5, 0);
		gbcTextField.fill = GridBagConstraints.HORIZONTAL;
		gbcTextField.gridx = 1;
		gbcTextField.gridy = 1;
		contentPane.add(txtName, gbcTextField);
		txtName.setColumns(10);

		btnAdd = new JButton("Add");
		btnAdd.setEnabled(false);
		btnAdd.addActionListener(e -> schoolController.newStudent(new Student(txtId.getText(), txtName.getText())));
		GridBagConstraints gbcBtnAddButton = new GridBagConstraints();
		gbcBtnAddButton.insets = new Insets(0, 0, 5, 0);
		gbcBtnAddButton.gridwidth = 2;
		gbcBtnAddButton.gridx = 0;
		gbcBtnAddButton.gridy = 2;
		contentPane.add(btnAdd, gbcBtnAddButton);

		scrollPane = new JScrollPane();
		GridBagConstraints gbcScrollPane = new GridBagConstraints();
		gbcScrollPane.insets = new Insets(0, 0, 5, 0);
		gbcScrollPane.fill = GridBagConstraints.BOTH;
		gbcScrollPane.gridwidth = 2;
		gbcScrollPane.gridx = 0;
		gbcScrollPane.gridy = 3;
		contentPane.add(scrollPane, gbcScrollPane);

		listStudentsModel = new DefaultListModel<>();
		listStudents = new JList<>(listStudentsModel);
		listStudents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listStudents.setName("studentList");
		listStudents.addListSelectionListener(e -> btnDelete.setEnabled(listStudents.getSelectedIndex() != -1));
		listStudents.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				Student student = (Student) value;
				return super.getListCellRendererComponent(list, getDisplayString(student), index, isSelected, cellHasFocus);
			}
		});
		scrollPane.setViewportView(listStudents);

		btnDelete = new JButton("Delete Selected");
		btnDelete.setEnabled(false);
		btnDelete.addActionListener(e -> schoolController.deleteStudent(listStudents.getSelectedValue()));
		GridBagConstraints gbcBtnDeleteButton = new GridBagConstraints();
		gbcBtnDeleteButton.insets = new Insets(0, 0, 5, 0);
		gbcBtnDeleteButton.gridwidth = 2;
		gbcBtnDeleteButton.gridx = 0;
		gbcBtnDeleteButton.gridy = 4;
		contentPane.add(btnDelete, gbcBtnDeleteButton);

		errorMessageLabel = new JLabel(" ");
		errorMessageLabel.setForeground(Color.RED);
		errorMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		errorMessageLabel.setName("errorMessageLabel");
		GridBagConstraints gbcErrorMessageLabel = new GridBagConstraints();
		gbcErrorMessageLabel.gridwidth = 2;
		gbcErrorMessageLabel.gridx = 0;
		gbcErrorMessageLabel.gridy = 5;
		contentPane.add(errorMessageLabel, gbcErrorMessageLabel);
	}

	@Override
	public void showAllStudents(List<Student> students) {
		students.stream().forEach(listStudentsModel::addElement);
	}

	@Override
	public void studentAdded(Student student) {
		listStudentsModel.addElement(student);
		resetErrorLabel();

	}

	@Override
	public void studentRemoved(Student student) {
		listStudentsModel.removeElement(student);
		resetErrorLabel();
	}

	@Override
	public void showError(String message, Student student) {
		errorMessageLabel.setText(message + ": " + getDisplayString(student));

	}

	@Override
	public void showErrorStudentNotFound(String message, Student student) {
		errorMessageLabel.setText(message + ": " + getDisplayString(student));
		listStudentsModel.removeElement(student);
		
	}

	public void setSchoolController(SchoolController schoolController) {
		this.schoolController = schoolController;
	}

	private void resetErrorLabel() {
		errorMessageLabel.setText(" ");

	}

	private String getDisplayString(Student student) {
		return student.getId() + " - " + student.getName();
	}


}
