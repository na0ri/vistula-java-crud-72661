package com.vistula.studentcrud;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("")
public class MainView extends VerticalLayout {

    private final StudentRepository repository;

    private final Grid<Student> grid = new Grid<>(Student.class, false);

    private final TextField nameField = new TextField("Name");
    private final TextField emailField = new TextField("Email");
    private final TextField courseField = new TextField("Course");

    private Student selectedStudent;

    public MainView(StudentRepository repository) {
        this.repository = repository;

        H1 title = new H1("Student Management System");

        grid.addColumn(Student::getId).setHeader("ID");
        grid.addColumn(Student::getName).setHeader("Name");
        grid.addColumn(Student::getEmail).setHeader("Email");
        grid.addColumn(Student::getCourse).setHeader("Course");

        grid.setWidthFull();

        grid.asSingleSelect().addValueChangeListener(event -> {
            selectedStudent = event.getValue();

            if (selectedStudent != null) {
                nameField.setValue(selectedStudent.getName());
                emailField.setValue(selectedStudent.getEmail());
                courseField.setValue(selectedStudent.getCourse());
            }
        });

        Button saveButton = new Button("Save Student", event -> saveStudent());

        Button deleteButton = new Button("Delete Student", event -> deleteStudent());

        Button clearButton = new Button("Clear", event -> clearForm());

        HorizontalLayout form = new HorizontalLayout(
                nameField,
                emailField,
                courseField
        );

        HorizontalLayout buttons = new HorizontalLayout(
                saveButton,
                deleteButton,
                clearButton
        );

        add(title, grid, form, buttons);

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        refreshGrid();
    }

    private void saveStudent() {

        String name = nameField.getValue().trim();
        String email = emailField.getValue().trim();
        String course = courseField.getValue().trim();

        if (name.isEmpty() || email.isEmpty() || course.isEmpty()) {
            Notification.show("Please fill in all fields");
            return;
        }

        Student student;

        if (selectedStudent == null) {
            student = new Student();
        } else {
            student = selectedStudent;
        }

        student.setName(name);
        student.setEmail(email);
        student.setCourse(course);

        repository.save(student);

        Notification.show("Student saved successfully");

        refreshGrid();
        clearForm();
    }

    private void deleteStudent() {

        if (selectedStudent == null) {
            Notification.show("Please select a student");
            return;
        }

        repository.delete(selectedStudent);

        Notification.show("Student deleted successfully");

        refreshGrid();
        clearForm();
    }

    private void refreshGrid() {
        grid.setItems(repository.findAll());
    }

    private void clearForm() {

        selectedStudent = null;

        grid.deselectAll();

        nameField.clear();
        emailField.clear();
        courseField.clear();
    }
}