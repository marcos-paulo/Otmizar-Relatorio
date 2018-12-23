package com.marcos.relatorio.controller.components;



import com.marcos.relatorio.model.Arquivo;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;

public class TableCellExcluir extends TableCell<Arquivo, Arquivo> {
	
	private ObservableList<Arquivo> arquivos;
	private Button btnExcluir;
	private Label labelFeedBack;
	
	public TableCellExcluir(ObservableList<Arquivo> arquivos, Label labelFeedBack) {
		this.arquivos = arquivos;
		this.labelFeedBack = labelFeedBack;
	}
	
	@Override
	protected void updateItem(Arquivo item, boolean empty) {
		super.updateItem(item, empty);
		
		if (item != null) {
			btnExcluir = new Button("Excluir");
			setGraphic(btnExcluir);
			btnExcluir.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					arquivos.remove(item);
					labelFeedBack.textProperty().set("");
				}
			});
		} else {
			setGraphic(null);
		}
		
	}
	
}
