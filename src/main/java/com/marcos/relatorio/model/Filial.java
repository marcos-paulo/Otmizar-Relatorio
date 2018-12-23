package com.marcos.relatorio.model;

import java.util.ArrayList;
import java.util.List;

public class Filial {
	
	private String nome;
	private List<Vencimento> vencimentos;
	private int maiorQuantidadeDeBoletos;

	public Filial() {
		vencimentos = new ArrayList<Vencimento>();
	}
	
	public Filial(String nome) {
		super();
		this.nome = nome;
	}

	public Filial(String nome, List<Vencimento> vencimentos) {
		this.nome = nome;
		this.vencimentos = vencimentos;
	}

	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public List<Vencimento> getVencimentos() {
		return vencimentos;
	}
	
	public void setVencimentos(List<Vencimento> vencimentos) {
		this.vencimentos = vencimentos;
	}
	
	public int getMaiorQuantidadeDeBoletos() {
		return maiorQuantidadeDeBoletos;
	}

	public void setMaiorQuantidadeDeBoletos(int maiorQuantidadeDeBoletos) {
		this.maiorQuantidadeDeBoletos = maiorQuantidadeDeBoletos;
	}
	
}
