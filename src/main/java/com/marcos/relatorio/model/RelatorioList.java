package com.marcos.relatorio.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "relatorios")
public class RelatorioList {

	private List<Relatorio> relatorios;

	@XmlElement(name = "relatorio")
	public List<Relatorio> getRelatorios() {
		return relatorios;
	}

	public void setRelatorios(List<Relatorio> relatorios) {
		this.relatorios = relatorios;
	}
	
}
