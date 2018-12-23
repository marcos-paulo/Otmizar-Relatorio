package com.marcos.relatorio.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.springframework.stereotype.Component;

import com.marcos.relatorio.model.Relatorio;
import com.marcos.relatorio.model.RelatorioList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Component
public class RelatorioDataRepository {
 
	private ObservableList<Relatorio> relatorios = FXCollections.observableArrayList();	
	
	public ObservableList<Relatorio> getRelatorios() {
		
		if (relatorios.isEmpty()) {
			File file = new File("arquivo.xml");
			loadRelatorioDataFromFile(file);			
		}
				
		return relatorios;
	}	
	
	public void salvarAlteracoes() {
		File file = new File("arquivo.xml");
		saveRelatoriosDataToFile(file, null);
	}
	
	public void salvar(List<Relatorio> relatorios) {
		File file = new File("arquivo.xml");
		saveRelatoriosDataToFile(file, relatorios);
	}
	
	public void salvar(Relatorio relatorio) {
		File file = new File("arquivo.xml");
		List<Relatorio> relatorios = new ArrayList<>(this.relatorios.subList(0, this.relatorios.size()));
		relatorios.add(relatorio);
		saveRelatoriosDataToFile(file, relatorios);
	}
	
	/**
	 * Carrega os dados de relatorios do arquivo especificado. 
	 * 
	 * @param file
	 */
	private void loadRelatorioDataFromFile(File file) {
	    try {
	        JAXBContext context = JAXBContext
	                .newInstance(RelatorioList.class);
	        Unmarshaller um = context.createUnmarshaller();

	        // Reading XML from the file and unmarshalling.
	        RelatorioList wrapper = (RelatorioList) um.unmarshal(file);
	        
	        relatorios.clear();
	        relatorios.addAll(wrapper.getRelatorios());
	        
	    } catch (Exception e) { // catches ANY exception
	    	System.out.println("Não foi possível carregar dados do arquivo:\n" 
                    + file.getPath());
	        /*Dialogs.create()
	                .title("Erro")
	                .masthead("Não foi possível carregar dados do arquivo:\n" 
	                          + file.getPath()).showException(e);*/
	    }
	}

	/**
	 * Salva os dados de relatorio atual no arquivo especificado.
	 * 
	 * @param file
	 */
	private void saveRelatoriosDataToFile(File file, List<Relatorio> relatorios) {
	    
		if (relatorios == null) {
			relatorios = new ArrayList<>(this.relatorios.subList(0, this.relatorios.size()));
		}		
		
		try {
	        JAXBContext context = JAXBContext
	                .newInstance(RelatorioList.class);
	        Marshaller m = context.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	        // Envolvendo nossos dados da pessoa.
	        RelatorioList wrapper = new RelatorioList();
	        wrapper.setRelatorios(relatorios);

	        // Enpacotando e salvando XML  no arquivo.
	        m.marshal(wrapper, file);
	        
	        this.relatorios.clear();
	        this.relatorios.addAll(relatorios);

//	        Salva o caminho do arquivo no registro.
//	        setPersonFilePath(file);
	    } catch (Exception e) { // catches ANY exception
	    	System.out.println("Não foi possível salvar os dados do arquivo:\n" 
	                          + file.getPath() + "\n" +
	                          e.getMessage()
	                          );
	    	e.printStackTrace();
//	        Dialogs.create().title("Erro")
//	                .masthead("Não foi possível salvar os dados do arquivo:\n" 
//	                          + file.getPath()).showException(e);
	    }
	}


		
}
