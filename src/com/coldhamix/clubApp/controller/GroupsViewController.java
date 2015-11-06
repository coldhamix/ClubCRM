package com.coldhamix.clubApp.controller;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.coldhamix.clubApp.ClubApp;
import com.coldhamix.clubApp.entities.Group;
import com.coldhamix.clubApp.operations.Groups;

public class GroupsViewController extends ControllerBase {

    @FXML
    private Button resetSearch;

    @FXML
    private Button viewGroup;

    @FXML
    private TableColumn<Group, String> titleColumn;
    
    @FXML
    private TableColumn<Group, String> countColumn;

    @FXML
    private TextField searchField;

    @FXML
    private Button archiveButton;

    @FXML
    private Font x1;

    @FXML
    private TableView<Group> groupsList;

    @FXML
    private Insets x2;

    @FXML
    private Button addGroup;

    @FXML
    private ToggleButton archiveMode;

    private ObservableList<Group> groups = FXCollections.observableArrayList();
    private FilteredList<Group> fGroups;
    private SortedList<Group> sGroups;
    
    @FXML
    void initialize() {
        assert resetSearch != null : "fx:id=\"resetSearch\" was not injected: check your FXML file 'GroupsView.fxml'.";
        assert viewGroup != null : "fx:id=\"viewGroup\" was not injected: check your FXML file 'GroupsView.fxml'.";
        assert titleColumn != null : "fx:id=\"titleColumn\" was not injected: check your FXML file 'GroupsView.fxml'.";
        assert searchField != null : "fx:id=\"searchField\" was not injected: check your FXML file 'GroupsView.fxml'.";
        assert archiveButton != null : "fx:id=\"archiveButton\" was not injected: check your FXML file 'GroupsView.fxml'.";
        assert x1 != null : "fx:id=\"x1\" was not injected: check your FXML file 'GroupsView.fxml'.";
        assert groupsList != null : "fx:id=\"groupsList\" was not injected: check your FXML file 'GroupsView.fxml'.";
        assert x2 != null : "fx:id=\"x2\" was not injected: check your FXML file 'GroupsView.fxml'.";
        assert addGroup != null : "fx:id=\"addGroup\" was not injected: check your FXML file 'GroupsView.fxml'.";
        assert archiveMode != null : "fx:id=\"archiveMode\" was not injected: check your FXML file 'GroupsView.fxml'.";

        // set placeholder
        groupsList.setPlaceholder( new Label("Нет результатов") );
        
        // bind table with model
        titleColumn.setCellValueFactory( cellData -> cellData.getValue().title());
        countColumn.setCellValueFactory( cellData -> cellData.getValue().count().asString());
        
        // load students
        Group[] arr = Groups.getAll();
        groups.addAll(arr);
        
        // make a filtered list for search purposes
        fGroups = new FilteredList<>(groups, p -> archiveMode.isSelected() == p.isArchived());
		
        // search listener binding
		ChangeListener<Object> listener = (observable, oldValue, newValue) -> updateList();
		searchField.textProperty().addListener(listener);
        archiveMode.selectedProperty().addListener(listener);
        
        // add items to the table
        sGroups = new SortedList<>(fGroups);
        sGroups.comparatorProperty().bind(groupsList.comparatorProperty());
        groupsList.setItems(sGroups);
        
        // selection listener
        groupsList.getSelectionModel().selectedItemProperty().addListener(
        		(observable, oldValue, newValue) -> onGroupSelected(newValue));
        groupsList.getSelectionModel().selectFirst();
        
        updateButtons();
    }

    private void onGroupSelected(Group newValue) {
    	updateButtons();
    }

    void updateList() {
    	fGroups.setPredicate(group -> {
    		if(searchField.getText() == null || searchField.getText().trim().isEmpty()) {
    			return archiveMode.isSelected() == group.isArchived();
    		}
    		
    		String lowerCaseFilter = searchField.getText().trim().toLowerCase();
    		if(group.getTitle().toLowerCase().contains(lowerCaseFilter)) {
    				return archiveMode.isSelected() == group.isArchived();
    		}
    		return false;        		
    	});   	
    	
        groupsList.getSelectionModel().selectFirst();
    }

	@FXML
    void toggleArchiveMode(ActionEvent event) {
    	groupsList.getSelectionModel().selectFirst();
    	updateButtons();
    }

    @FXML
    void reset(ActionEvent event) {
    	searchField.clear();
    }

    @FXML
    void showGroup(ActionEvent event) {    	
    	Group group = groupsList.getSelectionModel().getSelectedItem();
		if(group != null) {
			group = showEdit(group);
	    	if(group == null) return; // canceled
			group.update();
			
			updateList();
			groupsList.getSelectionModel().select(group);			
		}

    }

    Group showEdit(Group group) {
		try {
			Stage dialogStage = new Stage();
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(stage);
			dialogStage.getIcons().add( new Image("file:res/dialog.png") );
			
			// load fxml from file
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ClubApp.class.getResource("view/GroupData.fxml"));
			AnchorPane parent = (AnchorPane) loader.load();

			EditGroupController c = loader.getController();
			c.userInit(group, dialogStage);
			
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
    void archiveToggle(ActionEvent event) {
    	int sel = groupsList.getSelectionModel().getSelectedIndex();
    	Group group = groupsList.getSelectionModel().getSelectedItem();
    	boolean archive = !archiveMode.isSelected();
    	if(group != null) {
    		group.setArchived(archive);
    		updateList();
    		groupsList.getSelectionModel().select(sel >= groupsList.getItems().size() ? groupsList.getItems().size() - 1 : sel);
    		updateButtons();
    	}
    }

    void updateButtons() {
    	// if nothing is selected or there is nothing to select
    	if(groupsList.getSelectionModel().getSelectedItem() == null) {
    		archiveButton.setDisable(true);
    		archiveButton.setText("Скрыть");
    		viewGroup.setDisable(true);
    	} else {
    		viewGroup.setDisable(false);
    		archiveButton.setDisable(false);
	    	archiveButton.setText(archiveMode.isSelected() ? "Вернуть" : "Скрыть");
    	}
	}

	@FXML
    void add(ActionEvent event) {
    	// launch create dialog
    	Group group = showEdit(null);
    	if(group == null) return; // canceled
    	
    	// go to the non-archive mode
    	archiveMode.setSelected(false);
    	
    	// save and add to the list
    	group.update();
    	groups.add(group);
    	
    	updateList();
    	groupsList.getSelectionModel().select(group);
    }

}
