package com.marcos.relatorio.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marcos.relatorio.model.Arquivo;
import com.marcos.relatorio.repository.PreferencesReposirory;

import javafx.collections.ObservableList;

@Service
public class ServiceRelatorio {

	@Autowired
	private PreferencesReposirory preferences;
	
	private ImportarRelatorio importarRelatorio;	 
		
	private ExportarRelatorio exportarRelatorio;
	
	public void otimizarRelatorios(ObservableList<Arquivo> args) throws Exception {
		
		importarRelatorio = new ImportarRelatorio();
		exportarRelatorio = new ExportarRelatorio(preferences);
		
		if (args.size() >= 1) {
			/* TODO sysout */ System.out.println("O programa possue " + args.size() + " argumento(s)!");

			for (Arquivo arg : args) {
				try {
					importarRelatorio.extrairDadosRelatorio(arg);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					throw new Exception(e.getMessage());
				} catch (IOException e) {
					e.printStackTrace();
					throw new Exception(e.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception(e.getMessage());
				}
			}
			
			importarRelatorio.imprimirLista();
			
			try {
				exportarRelatorio.exportarRelatorio(importarRelatorio.getFiliais());				
			} catch (IOException e) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
		} else {
			/* TODO sysout */ System.out.println("Especifique pelo menos 1 argumento com o nome do arquivo com extenção .xls!");
		}
		
	}
	
}
