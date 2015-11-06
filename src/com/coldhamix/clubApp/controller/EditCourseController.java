package com.coldhamix.clubApp.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import com.coldhamix.clubApp.entities.Course;

public class EditCourseController extends ControllerBase {

	private static final String ADD_LABEL = "Добавить курс";
	private static final String ADD_BUTTON = "Добавить";

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label editLabel;

    @FXML
    private GridPane grid;

    @FXML
    private TextField courseTitle;
    
    @FXML
    private TextField coursePrice;

    @FXML
    private Button saveButton;
    
    private Course editing;
	private boolean saved = false;
	
	private boolean titleError = false, priceError = false;

    @FXML
    void initialize() {
        assert editLabel != null : "fx:id=\"editLabel\" was not injected: check your FXML file 'EditLesson.fxml'.";
        assert grid != null : "fx:id=\"grid\" was not injected: check your FXML file 'EditLesson.fxml'.";
        assert courseTitle != null : "fx:id=\"groupTitle\" was not injected: check your FXML file 'EditLesson.fxml'.";
        assert saveButton != null : "fx:id=\"saveButton\" was not injected: check your FXML file 'EditLesson.fxml'.";

		coursePrice.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent t) {
				char ar[] = t.getCharacter().toCharArray();
				char ch = ar[t.getCharacter().toCharArray().length - 1];
				
				if (coursePrice.getText().length() > 6) {
					t.consume();
					return;
				}
				
				if (!(ch >= '0' && ch <= '9')) {
					t.consume();
					return;
				}
			}
		});
        
        courseTitle.textProperty().addListener((observable, oldValue, newValue) -> check());
        coursePrice.textProperty().addListener((observable, oldValue, newValue) -> check());
        courseTitle.requestFocus();
    }
    
    void check() {
    	if (courseTitle.getText().trim().isEmpty()) {
    		if (!titleError) {
    			courseTitle.getStyleClass().add("error");
    			titleError = true;
    		}
    	} else {
    		courseTitle.getStyleClass().remove("error");
    		titleError = false;
    	}
    	
    	if (coursePrice.getText().trim().isEmpty()) {
    		if (!priceError) {
    			coursePrice.getStyleClass().add("error");
    			priceError = true;
    		}
    	} else {
    		coursePrice.getStyleClass().remove("error");
    		priceError = false;
    	}
    }
    
    void userInit(Course l, Stage stage) {
    	this.stage = stage;
    	if(l == null) {
    		editing = new Course();
			editLabel.setText(ADD_LABEL);
			saveButton.setText(ADD_BUTTON);
       	} else {  
       		editing = l;
	    	courseTitle.setText( editing.getTitle() );
	    	coursePrice.setText( "" + (int) editing.getPrice() );
       	}
    }
    

    @FXML
    void save() {
    	if(courseTitle.getText().trim().isEmpty()) return;
    	
    	editing.setTitle( courseTitle.getText().trim() );
    	
    	float price = 0.0f;
    	try {
    		price = Float.parseFloat(coursePrice.getText().trim());
    	} catch (Exception e) {
    		price = 0.0f;
    	}
    	editing.setPrice(price);

    	saved = true;
    	stage.close();
    }
    
    Course getResult() {
    	return saved ? editing : null;
    }

}
