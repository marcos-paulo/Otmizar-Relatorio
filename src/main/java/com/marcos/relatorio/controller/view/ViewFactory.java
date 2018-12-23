package com.marcos.relatorio.controller.view;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewFactory {
	
	private Stage stage;
	private Scene scene;

	public ViewFactory(Stage stage) {
		
		if (stage == null) {
			this.stage = new Stage();
		} else {
			stage = new Stage();
		}
		
	}

	public Stage getStage() {
		return stage;
	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}
	
	

}
