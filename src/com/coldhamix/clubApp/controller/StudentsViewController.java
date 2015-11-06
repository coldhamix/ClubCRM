package com.coldhamix.clubApp.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.coldhamix.clubApp.ClubApp;
import com.coldhamix.clubApp.entities.Student;
import com.coldhamix.clubApp.operations.Students;
import com.coldhamix.clubApp.util.DateUtil;


public class StudentsViewController extends ControllerBase {
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    
    @FXML
    private ToggleButton archiveMode;
    
    @FXML
    private TableView<Student> studentsList;

    @FXML
    private TableColumn<Student, String> nameColumn;

    // search field
    @FXML
    private TextField searchField;

    @FXML
    private Button resetSearch;
    
    // buttons
    @FXML
    private Button statsButton;
    
    @FXML
    private Button editButton;
    
    @FXML
    private Button archiveButton;

    @FXML
    private Button addStudent;
    
    // data labels
    @FXML
    private Label birthdayLabel;

    @FXML
    private Label institutionLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label parentNameLabel;

    
    boolean archived = true;
    
    @FXML
    void initialize() {      
    	 assert resetSearch != null : "fx:id=\"resetSearch\" was not injected: check your FXML file 'StudentsView.fxml'.";
         assert addStudent != null : "fx:id=\"addStudent\" was not injected: check your FXML file 'StudentsView.fxml'.";
         assert searchField != null : "fx:id=\"searchField\" was not injected: check your FXML file 'StudentsView.fxml'.";
         assert birthdayLabel != null : "fx:id=\"birthdayLabel\" was not injected: check your FXML file 'StudentsView.fxml'.";
         assert archiveMode != null : "fx:id=\"archiveMode\" was not injected: check your FXML file 'StudentsView.fxml'.";
         assert institutionLabel != null : "fx:id=\"institutionLabel\" was not injected: check your FXML file 'StudentsView.fxml'.";
         assert phoneLabel != null : "fx:id=\"phoneLabel\" was not injected: check your FXML file 'StudentsView.fxml'.";
         assert parentNameLabel != null : "fx:id=\"parentNameLabel\" was not injected: check your FXML file 'StudentsView.fxml'.";
         assert nameColumn != null : "fx:id=\"nameColumn\" was not injected: check your FXML file 'StudentsView.fxml'.";
         assert archiveButton != null : "fx:id=\"archiveButton\" was not injected: check your FXML file 'StudentsView.fxml'.";
         assert editButton != null : "fx:id=\"editButton\" was not injected: check your FXML file 'StudentsView.fxml'.";
         assert statsButton != null : "fx:id=\"statsButton\" was not injected: check your FXML file 'StudentsView.fxml'.";
         assert studentsList != null : "fx:id=\"studentsList\" was not injected: check your FXML file 'StudentsView.fxml'.";

         
        // change table's placeholder
        studentsList.setPlaceholder(new Label("Нет результатов"));
        
        // proper display of students
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        
        // load students
        Student[] arr = Students.getAll();
        students.addAll(arr);
        
        // make a filtered list for search purposes
        fStudents = new FilteredList<>(students, p -> archiveMode.isSelected() == p.isArchived());
		
        // search listener binding
		ChangeListener<Object> listener = (observable, oldValue, newValue) -> updateList();
		searchField.textProperty().addListener(listener);
        archiveMode.selectedProperty().addListener(listener);
        
        // add items to the table
        sStudents = new SortedList<>(fStudents);
        sStudents.comparatorProperty().bind(studentsList.comparatorProperty());
        studentsList.setItems(sStudents);
        
        // selection listener
        studentsList.getSelectionModel().selectedItemProperty().addListener(
        		(observable, oldValue, newValue) -> onStudentSelected(newValue));
        studentsList.getSelectionModel().selectFirst();
        updateButtons();
    }
    
    /*
     * CONTROLLER SECTION
     */
    private ObservableList<Student> students = FXCollections.observableArrayList();
    private FilteredList<Student> fStudents;
    private SortedList<Student> sStudents;
    
    void updateList() {
    	fStudents.setPredicate(student -> {
    		if(searchField.getText() == null || searchField.getText().trim().isEmpty()) {
    			return archiveMode.isSelected() == student.isArchived();
    		}
    		
    		String lowerCaseFilter = searchField.getText().trim().toLowerCase();
    		if(student.getName().toLowerCase().contains(lowerCaseFilter)) {
    				return archiveMode.isSelected() == student.isArchived();
    		}
    		return false;        		
    	});   	
    	
        studentsList.getSelectionModel().selectFirst();
    }
    
    void updateButtons() {
    	// if nothing is selected or there is nothing to select
    	if(studentsList.getSelectionModel().getSelectedItem() == null) {
    		archiveButton.setDisable(true);
    		archiveButton.setText("Скрыть");
    		editButton.setDisable(true);
    		statsButton.setDisable(true);
    	} else {
    		editButton.setDisable(false);
    		archiveButton.setDisable(false);
	    	archiveButton.setText(archiveMode.isSelected() ? "Вернуть" : "Скрыть");
	    	statsButton.setDisable(false);
	    }
    }
    
    @FXML
    void reset() {
    	searchField.clear();
    }
    
    
    @FXML 
    void edit() {
    	Student student = studentsList.getSelectionModel().getSelectedItem();
    	if(student != null) {
    		student = showEdit(student);
        	if(student == null) return; // canceled
    		student.update();
    		
    		updateList();
    		studentsList.getSelectionModel().select(student);
    		
    	}
    }
    
    @FXML
    void add() {
    	// launch create dialog
    	Student student = showEdit(null);
    	if(student == null) return; // canceled
    	
    	// go to the non-archive mode
    	archiveMode.setSelected(false);
    	
    	// save and add to the list
    	student.update();
    	students.add(student);
    	
    	updateList();
    	studentsList.getSelectionModel().select(student);
    }
    
    Student showEdit(Student student) {
		try {
			Stage dialogStage = new Stage();
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(stage);
			dialogStage.getIcons().add( new Image("file:res/dialog.png") );
			
			// load fxml from file
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ClubApp.class.getResource("view/EditStudent.fxml"));
			AnchorPane parent = (AnchorPane) loader.load();
			parent.getStyleClass().add("root");

			EditStudentController c = loader.getController();
			c.userInit(student, dialogStage);
			
			Scene s = new Scene(parent);
			dialogStage.setScene(s);
			dialogStage.showAndWait();
			
			return c.getResult();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
    }
    
    @FXML
    void archiveToggle() {
    	int sel = studentsList.getSelectionModel().getSelectedIndex();
    	Student student = studentsList.getSelectionModel().getSelectedItem();
    	boolean archive = !archiveMode.isSelected();
    	if(student!= null) {
    		student.setArchived(archive);
    		updateList();
    		studentsList.getSelectionModel().select(sel >= studentsList.getItems().size() ? studentsList.getItems().size() - 1 : sel);
    		updateButtons();
    	}
    }
    
    @FXML
    void toggleArchiveMode() {
    	studentsList.getSelectionModel().selectFirst();
    	updateButtons();
    }
    
    void onStudentSelected(Student s) {
    	// if empty selection
    	if (s == null) {
        	birthdayLabel.setText("");
        	phoneLabel.setText("");
        	parentNameLabel.setText("");
        	institutionLabel.setText("");
    	} else {
	    	birthdayLabel.setText(DateUtil.format(s.getBirthday()));
	    	phoneLabel.setText(s.getPhone());
	    	parentNameLabel.setText(s.getParentName());
	    	institutionLabel.setText(s.getInstitution());
    	}
    	updateButtons();
    }
}
