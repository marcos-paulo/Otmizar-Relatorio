
package com.marcos.relatorio.repository;

import java.io.File;
import java.util.prefs.Preferences;

import org.springframework.stereotype.Component;

import com.marcos.relatorio.Main;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@Component
public class PreferencesReposirory2 {

	private static final String KEY_FILE_INPUT = "FILE_INPUT";
	private static final String KEY_FILE_OUTPUT = "FILE_OUTPUT";
	
	private Preferences preferences;
	
	private StringProperty inputPathProperty;
	private StringProperty outputPathProperty;
	
	public PreferencesReposirory2() {
		
		preferences = Preferences.userNodeForPackage(Main.class);
		
		//TODO
//		preferences.remove("file");
//		preferences.remove(KEY_FILE_INPUT);
//		preferences.remove(KEY_FILE_OUTPUT);
		
		if (preferences.get(KEY_FILE_INPUT, null) == null) {
			preferences.put(KEY_FILE_INPUT, System.getProperty("user.home"));
		}
		
		// TODO
		System.out.println("preferencesRepository path input "+ preferences.get(KEY_FILE_INPUT, null));
		
		if (preferences.get(KEY_FILE_OUTPUT, null) == null) {
			File file = new File(System.getProperty("user.home") + File.separator + "Relatorio.xls");
			preferences.put(KEY_FILE_OUTPUT, file.getAbsolutePath());	
		}
		
		// TODO
		System.out.println("preferencesRepository path output " + preferences.get(KEY_FILE_OUTPUT, null));
		
		inputPathProperty = new SimpleStringProperty(preferences.get(KEY_FILE_INPUT, null));

		outputPathProperty = new SimpleStringProperty(preferences.get(KEY_FILE_OUTPUT, null));

		inputPathProperty.addListener((observabel, oldValue, newValue) -> {
			preferences.put(KEY_FILE_INPUT, newValue);
		});
		
		outputPathProperty.addListener((observable, oldValue, newValue) -> {
			preferences.put(KEY_FILE_OUTPUT, newValue);
		});
		
	}
	
	public String getInputPath() {
		return inputPathProperty.get();
	}
	
	public void setInputPath(String string) {
		inputPathProperty.set(string);
	}
	
	public String getOutputPath() {
		return outputPathProperty.get();
	}
	
	public void setOutputPath(String string) {
		outputPathProperty.set(string);
	}

	public StringProperty getIputPathProperty() {
		return inputPathProperty;
	}

	public StringProperty getOutputPathProperty() {
		return outputPathProperty;
	}

}
