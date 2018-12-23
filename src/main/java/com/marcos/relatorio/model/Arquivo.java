package com.marcos.relatorio.model;

import java.io.File;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Arquivo {
	
	private File file;
	private StringProperty nome;

	public Arquivo() {
		this(null);
	}
		
	public Arquivo(File file) {
		this.nome = new SimpleStringProperty();
		setFile(file);
	}

	public File getFile() {
		return this.file;
	}

	public void setFile(File file) {
		this.file = file;
		nome.set(file.getName());
	}

	public StringProperty getNome() {
		return nome;
	}

	
}
