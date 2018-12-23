package com.marcos.relatorio.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.marcos.layoutfactory.Controller;
import com.marcos.layoutfactory.LayoutFactory;
import com.marcos.layoutfactory.anotation.ControllerLayout;
import com.marcos.relatorio.Contexto;
import com.marcos.relatorio.controller.util.ControleUtil;
import com.marcos.relatorio.model.Relatorio;
import com.marcos.relatorio.repository.RelatorioDataRepository;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.converter.NumberStringConverter;

@Component
@ControllerLayout
public class FormularioRelatorioController implements Controller, Initializable {

	@FXML
	private TextField nomeDoRelatorio;
	@FXML
	private TextField nomeResumido;
	@FXML
	private TextField numeroDaLinhaDoPeriodo;
	@FXML
	private TextField numeroDaColunaDoPeriodo;
	@FXML
	private TextField numeroDaColunaDoValor;
	
	@Autowired
	private RelatorioDataRepository relatorioDataRepository;
	
	@Autowired
	private Contexto contexto;
	
	private Relatorio relatorio;
	
	private boolean relatorionovo;
	
	private LayoutFactory layoutFactory;
	
	private Stage stage;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
		Tooltip tootipNomeDoRelatorio = new Tooltip(
				"Digite aqui o nomo do formulário!");
		Tooltip tootipNomeReduzido = new Tooltip(
				"Digite aqui o nomo reduzido do formulário!");
		Tooltip tootipLinhaPeriodo = new Tooltip(
				"Digite aqui o numero da linha que corresponde ao período."
				+ " onde a linha 1 no EXCEl aqui será representada pelo numero 0, e assim por diante.");
		Tooltip tootipColunaPeriodo = new Tooltip(
				"Digite aqui o número da coluna que corresponde ao periodo."
				+ " onde a coluna A no EXCEL aqui será representada pelo numero 0, e assim por diante.");
		Tooltip tootipColunaValor = new Tooltip(
				"Digite aqui o número da coluna que corresponde ao valor."
				+ " onde a coluna A no EXCEL aqui será representada pelo numero 0, e assim por diante.");

		this.nomeDoRelatorio.setTooltip(tootipNomeDoRelatorio);
		this.nomeResumido.setTooltip(tootipNomeReduzido);
		this.numeroDaLinhaDoPeriodo.setTooltip(tootipLinhaPeriodo);
		this.numeroDaColunaDoPeriodo.setTooltip(tootipColunaPeriodo);
		this.numeroDaColunaDoValor.setTooltip(tootipColunaValor);
		
		ControleUtil.converterEmNumberField(this.numeroDaLinhaDoPeriodo);
		ControleUtil.converterEmNumberField(this.numeroDaColunaDoPeriodo);
		ControleUtil.converterEmNumberField(this.numeroDaColunaDoValor);
		
		setRelatorio(null);
		
	}

	public void setRelatorio(Relatorio relatorio) {
		
		if (relatorio == null) {
			this.relatorionovo = true;
			this.relatorio = new Relatorio();
		} else {
			this.relatorionovo = false;
			this.relatorio = relatorio;
		}

		Bindings.bindBidirectional(
				nomeDoRelatorio.textProperty(),
				this.relatorio.getNomeRelatorioProperty());
		
		Bindings.bindBidirectional(
				nomeResumido.textProperty(),
				this.relatorio.getNomeResumidoProperty());
		
		Bindings.bindBidirectional(
				numeroDaLinhaDoPeriodo.textProperty(),
				this.relatorio.getLinhaPeriodoProperty(),
				new NumberStringConverter());
		
		Bindings.bindBidirectional(
				numeroDaColunaDoPeriodo.textProperty(),
				this.relatorio.getColunaPeriodoProperty(),
				new NumberStringConverter());
		
		Bindings.bindBidirectional(
				numeroDaColunaDoValor.textProperty(),
				this.relatorio.getColunaDoValorProperty(),
				new NumberStringConverter());
		
	}
	
	@FXML
	private void handleSalvar() {
		if (relatorionovo) {
			relatorioDataRepository.salvar(relatorio);
		} else {
			relatorioDataRepository.salvarAlteracoes();
		}
		stage.hide();
		
	}

	@Override
	public void configureStage(Stage stage, Window owner) {
	
		this.stage = stage == null ? new Stage() : stage;
		
		if (owner != null) {
			this.stage.initOwner(owner);
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
		this.stage.show();		
	}	
	
}
