package com.marcos.relatorio.model;

import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Relatorio {
	
	/** nome do relatório que será exibido no log */
	private final StringProperty nomeRelatorio;
	/** nome resumido do relatório */ 
	private final StringProperty nomeResumido;
	/** numero da linha onde encontrar o período */
	private final IntegerProperty linhaPeriodo;
	/** numero da coluna onde encontrar o período */
	private final IntegerProperty colunaPeriodo;
	/** numero da coluna onde encontrar o valor */
	private final IntegerProperty colunaDoValor;
	
	private final ObservableList<String> filiais;
		
	public Relatorio() {
		this(null,null,0,0,0,null);
	}
	public Relatorio(String nomeRelatorio, String nomeResumido, int linhaPeriodo, int colunaPeriodo, int colunaDoValor, List<String> listaFiliais) {
		this.nomeRelatorio = new SimpleStringProperty(nomeRelatorio);
		this.nomeResumido = new SimpleStringProperty(nomeResumido);
		this.linhaPeriodo = new SimpleIntegerProperty(linhaPeriodo);
		this.colunaPeriodo = new SimpleIntegerProperty(colunaPeriodo);
		this.colunaDoValor = new SimpleIntegerProperty(colunaDoValor);
		this.filiais = FXCollections.observableArrayList();
		if (listaFiliais != null) {
			this.filiais.addAll(listaFiliais);
		}
	}
	public String getNomeRelatorio() {
		return this.nomeRelatorio.get();
	}
	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio.set(nomeRelatorio);
	}
	public String getNomeResumido() {
		return this.nomeResumido.get();
	}
	public void setNomeResumido (String nomeResumido) {
		this.nomeResumido.set(nomeResumido);
	}
	public int getLinhaPeriodo() {
		return this.linhaPeriodo.get();
	}
	public void setLinhaPeriodo(int linhaPeriodo) {
		this.linhaPeriodo.set(linhaPeriodo);
	}
	public int getColunaPeriodo() {
		return this.colunaPeriodo.get();
	}
	public void setColunaPeriodo(int colunaPeriodo) {
		this.colunaPeriodo.set(colunaPeriodo);
	}
	public int getColunaDoValor() {
		return this.colunaDoValor.get();
	}
	public void setColunaDoValor(int colunaDoValor) {
		this.colunaDoValor.set(colunaDoValor);
	}
	public List<String> getFiliais() {
//		if (filiais.size() == 0) {
//			return this.filiais.subList(0, 0);
//		} else {
//			return this.filiais.subList(0, filiais.size()-1);
//		}
		return this.filiais.subList(0, filiais.size());
	}
	public void setFiliais(List<String> listaFiliais) {
		this.filiais.clear();
		this.filiais.addAll(listaFiliais);
	}
	
	public StringProperty getNomeRelatorioProperty() {
		return nomeRelatorio;
	}
	public StringProperty getNomeResumidoProperty() {
		return nomeResumido;
	}
	public IntegerProperty getLinhaPeriodoProperty() {
		return linhaPeriodo;
	}
	public IntegerProperty getColunaPeriodoProperty() {
		return colunaPeriodo;
	}
	public IntegerProperty getColunaDoValorProperty() {
		return colunaDoValor;
	}
	public ObservableList<String> getFiliaisObservableList() {
		return filiais;
	}
	public Boolean possuiFiliais() {
		return filiais.size() > 0 ? true : false;
	}
}
