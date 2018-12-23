package com.marcos.relatorio.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Vencimento {
	
	private LocalDate dataVencimento;
	private List<Double> valores;
	
	public Vencimento(LocalDate dataVencimento) {
		valores = new ArrayList<Double>();
		this.dataVencimento = dataVencimento;
	}
	
	public LocalDate getDataVencimento() {
		return dataVencimento;
	}
	public void setDataVencimento(LocalDate dataVencimento) {
		this.dataVencimento = dataVencimento;
	}
	public List<Double> getValores() {
		return valores;
	}
	public void setValores(List<Double> valores) {
		this.valores = valores;
	}
}
