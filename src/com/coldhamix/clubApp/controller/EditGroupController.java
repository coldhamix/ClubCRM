package com.coldhamix.clubApp.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import org.controlsfx.control.ListSelectionView;

import com.coldhamix.clubApp.entities.Course;
import com.coldhamix.clubApp.entities.Group;
import com.coldhamix.clubApp.entities.Student;
import com.coldhamix.clubApp.operations.Courses;
import com.coldhamix.clubApp.operations.Members;
import com.coldhamix.clubApp.operations.Students;


public class EditGroupController {

	private static final String ADD_LABEL = "Добавление группы";
	private static final String ADD_BUTTON = "Добавить";

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addStudent;

    @FXML
    private Button removeStudent;

    @FXML
    private ChoiceBox<Course> groupTitle;

    @FXML
    private Label label;

    @FXML
    private Button saveButton;

    @FXML
    private ListSelectionView<Student> stListView;
    
    @FXML
	private ProgressIndicator saving;
    
    @FXML
    private VBox container;
    
    private Group editing;
    private Stage stage;
    
    private boolean saved;
    
    private ObservableList<Course> lessons = FXCollections.observableArrayList();
    private FilteredList<Course> fLessons;

    private ObservableList<Student> students = FXCollections.observableArrayList();
    
    @FXML
    void initialize() {        
    	stListView.setCellFactory(new Callback<ListView<Student>, ListCell<Student>>() {
			@Override
			public ListCell<Student> call(ListView<Student> param) {
				return new ListCell<Student>() {
                     @Override
                     public void updateItem(Student item, boolean empty) {
                         super.updateItem(item, empty);
                         if (empty) {
                             setText(null);
                             setGraphic(null);
                         } else {
                             setText(item == null ? "null" : item.getName());
                             setGraphic(null);
                         }
                     }

                 };
			}
		});
    	
    	
    	groupTitle.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
    		if(oldValue == null && newValue != null) { 
    			groupTitle.getStyleClass().remove("error"); 
    		}
    	});
    	groupTitle.setConverter(new StringConverter<Course> (){
            @Override public String toString(Course object) {
                if (object == null) {
                    return "[Выберите занятие]";
                }
                return object.getTitle();
            }

            @Override
            public Course fromString(String string) {
                throw new RuntimeException("not required for non editable ComboBox");
            }
        });
    	
    	// load lessons
        Course[] arr = Courses.getAll();
        lessons.add(null);
        lessons.addAll(arr);
        
        // make a filtered list for search purposes
        fLessons = new FilteredList<>(lessons, p -> p != null ? !p.isArchived() : true);
        
        groupTitle.setItems(fLessons);
        groupTitle.getSelectionModel().selectFirst();
    }
    
    void userInit(Group g, Stage stage) {
    	this.stage = stage;
    	if(g == null) {
    		editing = new Group();
			label.setText(ADD_LABEL);
			saveButton.setText(ADD_BUTTON);
			
			Student[] sArr = Students.getAll();
	    	for(Student s: sArr) {
	    		if(s.isArchived()) continue; // skip archived students
	    		students.add(s);
	    	}
	    	//Collections.sort(students);
	    	stListView.getSourceItems().addAll(students);
		} else {  
       		editing = g;
       		
       		for(Course l: lessons) {
       			if(l != null && l.getId() == g.getLessonId()) {
       				groupTitle.getSelectionModel().select(l);
       				break;
       			}
       		}
       		
        	Student[] members = Members.getAllStudents(g.getId());
        	List<Student> target = (members != null && members.length > 0) ? Arrays.asList(members) : Collections.emptyList();
        	
        	Student[] students = Students.getAll();
        	List<Student> source = new ArrayList<Student>();
        	for (Student a: students) {
        		boolean found = false;
        		for (Student b: target) {
        			if(a.getId() == b.getId()) {
        				found = true;
        				break;
        			}
        		}
        		
        		if(!found && !a.isArchived()) {
        			source.add(a);
        		}
        	}
        	
        	stListView.getSourceItems().addAll(source);
        	stListView.getTargetItems().addAll(target);
		}
    }

    @FXML
    void save() {
    	if(groupTitle.getSelectionModel().getSelectedItem() == null) {
        	groupTitle.getStyleClass().add("error");
        	return;
    	}
    	
    	editing.setLessonId( groupTitle.getSelectionModel().getSelectedItem().getId() );
    	editing.update();
    	
    	Thread t = new Thread(() -> {
			int groupId = editing.getId();
			Members.deleteAll(groupId);
			Members.addAll(groupId, stListView.getTargetItems().toArray(new Student[stListView.getTargetItems().size()]));				
		});
    	t.start();
    	try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	saved = true;
    	stage.close();
    }
    
    public void setStage(Stage stage) {
    	this.stage = stage;
    }
    
    Group getResult() {
    	return saved ? editing : null;
    }
}
