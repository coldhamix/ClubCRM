package com.coldhamix.clubApp.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.coldhamix.clubApp.ClubApp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class RootLayoutController extends ControllerBase {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button goToGroups;

    @FXML
    private Button goToLessons;

    @FXML
    private Button goToPayments;

    @FXML
    private Button goToStudents;

    @FXML
    private Button goToVisits;

    @FXML
    private BorderPane rootLayout;

    @FXML
    private Label statusBarLabel;


    @FXML
    void showGroups(ActionEvent event) {
    	deactivateAll();
    	goToGroups.getStyleClass().add("topMenuButtonActive");
    	showWindow("GroupsView.fxml");
    	
    }

    @FXML
    void showCourses(ActionEvent event) {
    	deactivateAll();
    	goToLessons.getStyleClass().add("topMenuButtonActive");
    	showWindow("CoursesView.fxml");
    }

    @FXML
    void showPayments(ActionEvent event) {
    	deactivateAll();
    	goToPayments.getStyleClass().add("topMenuButtonActive");
    	showWindow("PaymentsView.fxml");
    }

    @FXML
    void showStudents(ActionEvent event) {
    	deactivateAll();
    	goToStudents.getStyleClass().add("topMenuButtonActive");
    	showWindow("StudentsView.fxml");
    }

    @FXML
    void showVisits(ActionEvent event) {
    	deactivateAll();
    	goToVisits.getStyleClass().add("topMenuButtonActive");
    	showWindow("VisitsView.fxml");
    }

    @FXML
    void initialize() {
        assert goToGroups != null : "fx:id=\"goToGroups\" was not injected: check your FXML file 'RootLayout.fxml'.";
        assert goToLessons != null : "fx:id=\"goToLessons\" was not injected: check your FXML file 'RootLayout.fxml'.";
        assert goToPayments != null : "fx:id=\"goToPayments\" was not injected: check your FXML file 'RootLayout.fxml'.";
        assert goToStudents != null : "fx:id=\"goToStudents\" was not injected: check your FXML file 'RootLayout.fxml'.";
        assert goToVisits != null : "fx:id=\"goToVisits\" was not injected: check your FXML file 'RootLayout.fxml'.";
        assert rootLayout != null : "fx:id=\"rootLayout\" was not injected: check your FXML file 'RootLayout.fxml'.";
        assert statusBarLabel != null : "fx:id=\"statusBarLabel\" was not injected: check your FXML file 'RootLayout.fxml'.";


    }
    
    void deactivateAll() {
    	goToGroups.getStyleClass().remove("topMenuButtonActive");
    	goToLessons.getStyleClass().remove("topMenuButtonActive");
    	goToPayments.getStyleClass().remove("topMenuButtonActive");
    	goToStudents.getStyleClass().remove("topMenuButtonActive");
    	goToVisits.getStyleClass().remove("topMenuButtonActive");
    }
    
	FXMLLoader showWindow(String window) {
		try {
			// load fxml from file
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ClubApp.class.getResource("view/" + window));
			AnchorPane parent = (AnchorPane) loader.load();

			rootLayout.setCenter(parent);
			parent.autosize();
			
			ControllerBase c = loader.getController();
			if(c != null) c.setStage(stage);
			
			return loader;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}


}
