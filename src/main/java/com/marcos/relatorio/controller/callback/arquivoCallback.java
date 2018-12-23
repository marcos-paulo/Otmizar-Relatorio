package com.marcos.relatorio.controller.callback;

import com.marcos.relatorio.model.Arquivo;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

//não entendo pra que serve a classe mas ela é necessária para retornar um objeto somente leitura, ou algo do tipo, não sei...
public class arquivoCallback implements Callback<CellDataFeatures<Arquivo, Arquivo>, ObservableValue<Arquivo>> {
	@Override
	public ObservableValue<Arquivo> call(CellDataFeatures<Arquivo, Arquivo> param) {
		return new ReadOnlyObjectWrapper<Arquivo>(param.getValue());
	}
}
