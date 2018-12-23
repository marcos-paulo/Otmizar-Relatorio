package com.marcos.relatorio.repository;

import java.io.File;

public enum PreferencesRepository {
	KEY_RELATORIO_INPUT_PATH("RELATORIO_INPUT_PATH", System.getProperty("user.home")),
	KEY_RELATORIO_OUTPUT_FILE("RELATORIO_OUTPUT_FILE", System.getProperty("user.home") + File.separator + "Relatorio.xls"),
	KEY_CONFIGURACAO_IMPORT_PATH("CONFIGURACAO_IMPORT_PATH", "./"),
	KEY_CONFIGURACAO_OUTPUT_FILE("CONFIGURACAO_OUTPUT_FILE", "./arquivo.xml");
	
	private PreferencesRepository(String key, String valueDefault) {
		
	}
	
	
}
