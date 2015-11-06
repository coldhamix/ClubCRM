package com.coldhamix.clubApp.controller;

import javafx.stage.Stage;

public abstract class ControllerBase {
	
	protected Stage stage;
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public Stage getStage() {
		return stage;
	}

}
