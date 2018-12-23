package com.marcos.relatorio.controller;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.marcos.layoutfactory.LayoutFactory;
import com.marcos.layoutfactory.anotation.ControllerLayout;
import com.marcos.relatorio.Contexto;
import com.marcos.relatorio.controller.callback.arquivoCallback;
import com.marcos.relatorio.controller.components.TableCellExcluir;
import com.marcos.relatorio.model.Arquivo;
import com.marcos.relatorio.repository.PreferencesReposirory;
import com.marcos.relatorio.service.ServiceRelatorio;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;

@Component()
@ControllerLayout(widthScene=650, heightScene=450)
public class InicialController implements Initializable{
	
	@FXML
	private TableView<Arquivo> tabelaArquivos;

	@FXML
	private TableColumn<Arquivo, String> colNomeArquivo;
	
	@FXML
	private TableColumn<Arquivo, Arquivo> colBtnExcluir;
	
	@FXML
	private Button btnSelecionar;
	
	@FXML
	private Button btnOtimizar;
	
	@FXML
	private Label labelFeedBack;
	
	@FXML
	private Label labelLocalArquivo;
	
	@Autowired
	private PreferencesReposirory preferences;	
	
	@Autowired
	private ServiceRelatorio serviceRelatorio;
	
	@Autowired
	private Contexto contexto ;
	
	@FXML
	private void handleSelecionarArquivo() {
		List<File> files = abrirFileChooser();
		if (files != null) {
			for (File file : files) {
				System.out.println(file.getParentFile().getAbsolutePath());
				this.preferences.setInputPath(file.getParentFile().getAbsolutePath());
				this.tabelaArquivos.getItems().add(new Arquivo(file));
			}
		}		
	};
	
	@FXML
	private void handleLocalDoArquivoDeSaida() {
		File file = abrirFileChooserLocalArquivoDeSaida();
		if (file != null) {
			this.preferences.setOutputPath(file.getAbsolutePath());
		}
	};
	
	
	@FXML
	private void handleOtimizarRelatorios() {
		try {
			this.labelFeedBack.textProperty().set("Processando...");
			serviceRelatorio.otimizarRelatorios(tabelaArquivos.getItems());
			this.labelFeedBack.textProperty().set("Relatório Otimizado com Sucesso!");
		} catch (Exception e) {
			e.printStackTrace();
			this.labelFeedBack.textProperty().set(e.getMessage());
		}
	}
	
	private List<File> abrirFileChooser() {
		FileChooser gerenciadorDeArquivos = new FileChooser();
		File file = new File(preferences.getInputPath());
		file = file.isDirectory() ? file : file.getParentFile();
		gerenciadorDeArquivos.setInitialDirectory(file);
		gerenciadorDeArquivos.setTitle("Selecione a planilha");
        gerenciadorDeArquivos.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLS", "*.xls"));        
        List<File> files = gerenciadorDeArquivos.showOpenMultipleDialog(null);
        return files;
	}
	
	private File abrirFileChooserLocalArquivoDeSaida() {
		FileChooser gerenciadorDeArquivos = new FileChooser();
		File file = new File(preferences.getOutputPath());
		file = file.isDirectory() ? file : file.getParentFile();
		gerenciadorDeArquivos.setInitialDirectory(file);
		file = gerenciadorDeArquivos.showSaveDialog(null);
		return file;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.labelFeedBack.textProperty().set("");
		this.colNomeArquivo.setCellValueFactory(cellData -> cellData.getValue().getNome());
		this.colBtnExcluir.setCellValueFactory(new arquivoCallback());
		this.colBtnExcluir.setCellFactory(param -> new TableCellExcluir(tabelaArquivos.getItems(), this.labelFeedBack));
		
		this.btnOtimizar.disableProperty().bind(Bindings.size(this.tabelaArquivos.getItems()).isEqualTo(0));
				
		this.labelLocalArquivo.textProperty().bindBidirectional(preferences.getOutputPathProperty());		
		
	}
	
	@FXML
	private void handlerAbrirConfiguracoes() {
		
		LayoutFactory factory;
		try {
			factory = new LayoutFactory(this.contexto.getControllerFactory(), ConfiguracoesController.class);
			ConfiguracoesController cc = factory.getController();
			cc.configureStage(null, contexto.getPrimaryStage());
			cc.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
}
