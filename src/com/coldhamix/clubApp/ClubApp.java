package com.coldhamix.clubApp;

import java.io.IOException;
import java.util.Locale;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import com.coldhamix.clubApp.controller.ControllerBase;
import com.coldhamix.clubApp.util.DbHelper;

public class ClubApp extends Application {

	private static final String APP_TITLE = "Детский Клуб";
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	
	public static FXMLLoader loader = new FXMLLoader();
	
	@Override
	public void start(Stage primaryStage) {
		// correct date
		Locale.setDefault(Locale.forLanguageTag("ru-RU"));
		
		// connection
		DbHelper.connect("club.db");
		DbHelper.createTables();
		
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle(APP_TITLE);
		this.primaryStage.getIcons().add( new Image("file:res/appicon.png") );
		
		initRoot();
	}
	
	public void initRoot() {
		try {
			// Load fxml from file
			loader.setLocation(ClubApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			
			
			// init with the stage
			ControllerBase c = loader.getController();
			c.setStage(primaryStage);
			
			// show root window
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
