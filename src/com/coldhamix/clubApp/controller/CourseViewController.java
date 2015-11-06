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
import com.coldhamix.clubApp.entities.Course;
import com.coldhamix.clubApp.operations.Courses;


public class CourseViewController extends ControllerBase {
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addLesson;

    @FXML
    private Button archiveButton;

    @FXML
    private ToggleButton archiveMode;

    @FXML
    private Button editButton;

    @FXML
    private TableView<Course> lessonsList;

    @FXML
    private Button resetSearch;

    @FXML
    private TextField searchField;

    @FXML
    private TableColumn<Course, String> titleColumn;
    
    @FXML
    private TableColumn<Course, String> priceColumn;

    @FXML
    private Label totalGroups;

    @FXML
    private Label totalMembers;
    
    boolean archived = true;
    
    @FXML
    void initialize() {
		assert addLesson != null : "fx:id=\"addLesson\" was not injected: check your FXML file 'LessonsView.fxml'.";
		assert archiveButton != null : "fx:id=\"archiveButton\" was not injected: check your FXML file 'LessonsView.fxml'.";
		assert archiveMode != null : "fx:id=\"archiveMode\" was not injected: check your FXML file 'LessonsView.fxml'.";
		assert editButton != null : "fx:id=\"editButton\" was not injected: check your FXML file 'LessonsView.fxml'.";
		assert lessonsList != null : "fx:id=\"lessonsList\" was not injected: check your FXML file 'LessonsView.fxml'.";
		assert resetSearch != null : "fx:id=\"resetSearch\" was not injected: check your FXML file 'LessonsView.fxml'.";
		assert searchField != null : "fx:id=\"searchField\" was not injected: check your FXML file 'LessonsView.fxml'.";
		assert titleColumn != null : "fx:id=\"titleColumn\" was not injected: check your FXML file 'LessonsView.fxml'.";
		assert totalGroups != null : "fx:id=\"totalGroups\" was not injected: check your FXML file 'LessonsView.fxml'.";
		assert totalMembers != null : "fx:id=\"totalMembers\" was not injected: check your FXML file 'LessonsView.fxml'.";
        
        // change table's placeholder
		lessonsList.setPlaceholder( new Label("Нет результатов") );
        
        // proper display of students
		titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty() );
		priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asString());
        
        // load students
        Course[] arr = Courses.getAll();
        lessons.addAll(arr);
        
        // make a filtered list for search purposes
        fLessons = new FilteredList<>(lessons, p -> archiveMode.isSelected() == p.isArchived());
		
        // search listener binding
		ChangeListener<Object> listener = (observable, oldValue, newValue) -> updateList();
		searchField.textProperty().addListener(listener);
        archiveMode.selectedProperty().addListener(listener);
        
        // add items to the table
        sLessons = new SortedList<>(fLessons);
        sLessons.comparatorProperty().bind(lessonsList.comparatorProperty());
        lessonsList.setItems(sLessons);
        
        // selection listener
        lessonsList.getSelectionModel().selectedItemProperty().addListener(
        		(observable, oldValue, newValue) -> onLessonSelected(newValue));
        lessonsList.getSelectionModel().selectFirst();
        updateButtons();
    }
    
    /*
     * CONTROLLER SECTION
     */
    private ObservableList<Course> lessons = FXCollections.observableArrayList();
    private FilteredList<Course> fLessons;
    private SortedList<Course> sLessons;
    
    void updateList() {
    	fLessons.setPredicate(lesson -> {
    		if(searchField.getText() == null || searchField.getText().trim().isEmpty()) {
    			return archiveMode.isSelected() == lesson.isArchived();
    		}
    		
    		String lowerCaseFilter = searchField.getText().trim().toLowerCase();
    		if(lesson.getTitle().toLowerCase().contains(lowerCaseFilter)) {
    				return archiveMode.isSelected() == lesson.isArchived();
    		}
    		return false;        		
    	});   	
    	
        lessonsList.getSelectionModel().selectFirst();
    }
    
    void updateButtons() {
    	// if nothing is selected or there is nothing to select
    	if(lessonsList.getSelectionModel().getSelectedItem() == null) {
    		archiveButton.setDisable(true);
    		archiveButton.setText("Скрыть");
    		editButton.setDisable(true);
    	} else {
    		editButton.setDisable(false);
    		archiveButton.setDisable(false);
	    	archiveButton.setText(archiveMode.isSelected() ? "Вернуть" : "Скрыть");
    	}
    }
    
    @FXML
    void reset() {
    	searchField.clear();
    }
    
    
    @FXML 
    void edit() {
    	Course lesson = lessonsList.getSelectionModel().getSelectedItem();
    	if(lesson != null) {
    		lesson = showEdit(lesson);
        	if(lesson == null) return; // canceled
        	lesson.update();
    		
    		updateList();
    		lessonsList.getSelectionModel().select(lesson);
    		
    	}
    }
    
    @FXML
    void add() {
    	// launch create dialog
    	Course lesson = showEdit(null);
    	if(lesson == null) return; // canceled
    	
    	// go to the non-archive mode
    	archiveMode.setSelected(false);
    	
    	// save and add to the list
    	lesson.update();
    	lessons.add(lesson);
    	
    	updateList();
    	lessonsList.getSelectionModel().select(lesson);
    }
    
    Course showEdit(Course lesson) {
		try {
			Stage dialogStage = new Stage();
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(stage);
			dialogStage.getIcons().add( new Image("file:res/dialog.png") );
			
			// load fxml from file
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ClubApp.class.getResource("view/EditCourse.fxml"));
			AnchorPane parent = (AnchorPane) loader.load();
			parent.getStyleClass().add("root");

			EditCourseController c = loader.getController();
			c.userInit(lesson, dialogStage);
			
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
    	int sel = lessonsList.getSelectionModel().getSelectedIndex();
    	Course lesson = lessonsList.getSelectionModel().getSelectedItem();
    	boolean archive = !archiveMode.isSelected();
    	if(lesson != null) {
    		lesson.setArchived(archive);
    		updateList();
    		lessonsList.getSelectionModel().select(sel >= lessonsList.getItems().size() ? lessonsList.getItems().size() - 1 : sel);
    		updateButtons();
    	}
    }
    
    @FXML
    void toggleArchiveMode() {
    	lessonsList.getSelectionModel().selectFirst();
    	updateButtons();
    }
    
    void onLessonSelected(Course l) {
    	
    	if(l == null) {
    		totalGroups.setText("");
    		totalMembers.setText("");
    		updateButtons();
    		return;
    	}
    	
    	totalGroups.setText("" + Courses.totalGroups(l.getId()));
    	totalMembers.setText("" + Courses.totalStudents(l.getId()));
    	updateButtons();
    }
}
