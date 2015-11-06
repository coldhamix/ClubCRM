package com.coldhamix.clubApp.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.coldhamix.clubApp.entities.Student;


public class EditStudentController {

	private static final String ADD_LABEL = "Добавить ученика";
	private static final String ADD_BUTTON = "Добавить";
	
	@FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    
    
    @FXML
    private Label editLabel;

    @FXML
    private Button saveButton;

    // fields
    @FXML
    private TextField studentName;

    @FXML
    private DatePicker datePicker;
    
    @FXML
    private TextField institution;

    @FXML
    private TextField parentName;

    @FXML
    private TextField phone;
    
    private Student editing;
    private Stage stage;

    boolean nameError = false;
    boolean phoneError = false;
    boolean parentNameError = false;
    boolean institutionError = false;
    
    private boolean saved;

    @FXML
    void initialize() {
        assert editLabel != null : "fx:id=\"editLabel\" was not injected: check your FXML file 'EditStudent.fxml'.";
        assert saveButton != null : "fx:id=\"saveButton\" was not injected: check your FXML file 'EditStudent.fxml'.";
        assert studentName != null : "fx:id=\"studentName\" was not injected: check your FXML file 'EditStudent.fxml'.";
        assert datePicker != null : "fx:id=\"datePicker\" was not injected: check your FXML file 'EditStudent.fxml'.";
        
        studentName.textProperty().addListener((observable, oldValue, newValue) -> check());
        phone.textProperty().addListener((observable, oldValue, newValue) -> check());
        parentName.textProperty().addListener((observable, oldValue, newValue) -> check());
        institution.textProperty().addListener((observable, oldValue, newValue) -> check());
    }
    
    void check() {
    	if (studentName.getText().trim().isEmpty()) {
    		if (!nameError) {
	    		studentName.getStyleClass().add("error");
	    		nameError = true;
    		}
    	} else {
    		studentName.getStyleClass().remove("error");
    		nameError = false;
    	}
    	
    	if (phone.getText().trim().isEmpty()) {
    		if (!phoneError) {
	    		phone.getStyleClass().add("error");
	    		phoneError = true;
    		}
    	} else {
    		phone.getStyleClass().remove("error");
    		phoneError = false;
    	}
    	
    	if (parentName.getText().trim().isEmpty()) {
    		if (!parentNameError) {
    			parentName.getStyleClass().add("error");
    			parentNameError = true;
    		}
    	} else {
    		parentName.getStyleClass().remove("error");
    		parentNameError = false;
    	}
    	
    	if (institution.getText().trim().isEmpty()) {
    		if (!institutionError) {
    			institution.getStyleClass().add("error");
    			institutionError = true;
    		}
    	} else {
    		institution.getStyleClass().remove("error");
    		institutionError = false;
    	}
    }
    
    void userInit(Student s, Stage stage) {
    	this.stage = stage;
    	if (s == null) {
    		editing = new Student();
			editLabel.setText(ADD_LABEL);
			saveButton.setText(ADD_BUTTON);
			datePicker.setValue(LocalDate.now());
       	} else {  
       		editing = s;
	    	studentName.setText(editing.getName());
	    	datePicker.setValue(LocalDate.ofEpochDay(editing.getBirthday() / 86400));
	    	phone.setText(editing.getPhone());
	    	parentName.setText(editing.getParentName());
	    	institution.setText(editing.getInstitution());
       	}
    	
        studentName.requestFocus();
    }

    @FXML
    void save() {
    	if (nameError || phoneError || parentNameError || institutionError) {
    		return;
    	}
    	
    	editing.setName(studentName.getText().trim());
    	editing.setBirthday((int) datePicker.getValue().toEpochDay() * 86400);
    	editing.setPhone(phone.getText().trim());
    	editing.setParentName(parentName.getText().trim());
    	editing.setInstitution(institution.getText().trim());
    	
    	saved = true;
    	stage.close();
    }
    
    public void setStage(Stage stage) {
    	this.stage = stage;
    }
    
    Student getResult() {
    	return saved ? editing : null;
    }
}
