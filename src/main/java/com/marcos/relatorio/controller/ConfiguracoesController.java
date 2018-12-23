package com.marcos.relatorio.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.marcos.layoutfactory.Controller;
import com.marcos.layoutfactory.LayoutFactory;
import com.marcos.layoutfactory.anotation.ControllerLayout;
import com.marcos.relatorio.Contexto;
import com.marcos.relatorio.model.Relatorio;
import com.marcos.relatorio.repository.RelatorioDataRepository;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

@Component
@ControllerLayout(widthScene=650, heightScene=450)
public class ConfiguracoesController implements Initializable, Controller {

	@FXML
	private TableView<Relatorio> tabelaRelatorios;
	
	@FXML
	private TableColumn<Relatorio, String> colunaNomeDoRelatorio; 
	
	@FXML
	private TableView<String> tabelaFiliais;
	
	@FXML
	private TableColumn<String, String> colunaNomeDaFilial; 
	
	@FXML
	private Label nomeDoRelatorio;

	@FXML
	private Label nomeResumido;
	
	@FXML
	private Label numeroDaLinhaDoPeriodo;
	
	@FXML
	private Label numeroDaColunaDoPeriodo;
	
	@FXML
	private Label numeroDaColunaDoValor;
	
	@FXML
	private Button butaoEditar;
	
	@FXML
	private Label labelLocalOndeSalvar;
	
	@Autowired
	private RelatorioDataRepository relatorioDataRepository;

	@Autowired
	private Contexto contexto;
	
	private Stage stage;
	
	private LayoutFactory layoutFactory;
	
	private Relatorio relatorio;
	
	@SuppressWarnings("unused")
	private void adicionarTestes() {
		
		
		List<Relatorio> lista = new ArrayList<>();
		
		List<String>filiais1 = new ArrayList<String>();
		filiais1.add(">> GP - PINDORETAMA");
		filiais1.add(">> GP - BEBERIBE");
		
		List<String>filiais2 = new ArrayList<String>();
		filiais2.add(">> GAMA - CASCAVEL");
		filiais2.add(">> GAMA - BEBERIBE");
		filiais2.add(">> GAMA - PACATUBA");
		filiais2.add(">> GAMA - PINDORETAMA");
		filiais2.add(">> GAMA - FORTIM");
		filiais2.add(">> GAMA - GUAIUBA");
		filiais2.add(">> GAMA - MARANGUAPE");
		
		lista.add(new Relatorio("GAMA - >> GAMA - ESCRITORIO", null, 7, 7, 11, filiais2));
		lista.add(new Relatorio("GAMA POPULAR - >> CENTRAL - GP", null, 7, 5, 9, filiais1));
		lista.add(new Relatorio("FCIA GAMA - R. G. COM DE MED LTDA ME", ">> GAMA - MORRO BRANCO", 7, 4, 8, null));
		
		relatorioDataRepository.salvar(lista);		
						
	}	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.colunaNomeDoRelatorio.setCellValueFactory(
				cellData -> cellData.getValue().getNomeRelatorioProperty());
		
		this.colunaNomeDaFilial.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue()));
				
		visualizarDetalhesDoRelatorio(null);
		
		adicionarTestes();
		
		tabelaRelatorios.setItems(relatorioDataRepository.getRelatorios());
		tabelaRelatorios.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> visualizarDetalhesDoRelatorio(newValue));
	}
	
	private void visualizarDetalhesDoRelatorio(Relatorio relatorio) {
		this.relatorio = relatorio;
		if (relatorio != null) {
			nomeDoRelatorio.setText(relatorio.getNomeRelatorio());
			nomeResumido.setText(relatorio.getNomeResumido());
			numeroDaLinhaDoPeriodo.setText(relatorio.getLinhaPeriodo()+"");
			numeroDaColunaDoPeriodo.setText(relatorio.getColunaPeriodo()+"");
			numeroDaColunaDoValor.setText(relatorio.getColunaDoValor()+"");
			tabelaFiliais.setItems(relatorio.getFiliaisObservableList());
			tabelaFiliais.visibleProperty().set(relatorio.possuiFiliais());
			butaoEditar.setDisable(false);
		} else {
			nomeDoRelatorio.setText("");
			nomeResumido.setText("");
			numeroDaLinhaDoPeriodo.setText("");
			numeroDaColunaDoPeriodo.setText("");
			numeroDaColunaDoValor.setText("");
			tabelaFiliais.setItems(null);
			tabelaFiliais.visibleProperty().set(false);
			butaoEditar.setDisable(true);
		}
	}

	private void abrirFormularioRelatorio(Relatorio relatorio) {
		LayoutFactory layout;
		try {
			layout = new LayoutFactory(contexto.getControllerFactory(), FormularioRelatorioController.class);
			FormularioRelatorioController frc = layout.getController();
			frc.setRelatorio(relatorio);
			frc.configureStage(null, contexto.getPrimaryStage());
			frc.show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@FXML
	private void handlerNovo() {
		abrirFormularioRelatorio(null);		
	}
	
	@FXML
	private void handlerEditar() {
		abrirFormularioRelatorio(relatorio);
	}


	@Override
	public void configureStage(Stage stage, Window janelaPai) {
		
		this.stage = stage == null ? new Stage() : stage;
		
		if (janelaPai != null) {
			this.stage.initOwner(janelaPai);
		}
		
		this.stage.setResizable(false);
		this.stage.initModality(Modality.WINDOW_MODAL);
		this.stage.getIcons().add(contexto.getImage());
		try {
			this.stage.setScene(layoutFactory.getScene());
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}


	@Override
	public void setLayoutFactory(LayoutFactory layoutFactory) {
		this.layoutFactory = layoutFactory;
	}


	@Override
	public void show() {
		stage.show();		
	}
	
}
