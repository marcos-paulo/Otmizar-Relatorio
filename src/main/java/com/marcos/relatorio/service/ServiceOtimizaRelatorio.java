package com.marcos.relatorio.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marcos.relatorio.model.Arquivo;
import com.marcos.relatorio.model.Filial;
import com.marcos.relatorio.model.Vencimento;
import com.marcos.relatorio.repository.PreferencesReposirory;

import javafx.collections.ObservableList;

@Service
public class ServiceOtimizaRelatorio {
	
	@Autowired
	private PreferencesReposirory preferences;

	private List<String> nomesFiliais;
	
	private List<Filial> filiais;
	private Filial filial;
	private List<Vencimento> vencimentos;
	private Vencimento vencimento;

	private int maiorQuantidadeDeBoletosPorVencimento;
	private int colunaDoValor;
	
	private List<Integer> linhasDeTotais;
	private List<HSSFCellStyle> listaEstilosFiliais;

	private LocalDate dataInicial;
	private LocalDate dataFinal;
	
	private static final int[] cores = 
		{29,35,38,41,43,44,45,46,47,48,49,50,54,55};
	
	public ServiceOtimizaRelatorio() {
		this.nomesFiliais = new ArrayList<String>();
		this.nomesFiliais.add(">> GP - PINDORETAMA");
		this.nomesFiliais.add(">> GP - BEBERIBE");
		this.nomesFiliais.add(">> GAMA - CASCAVEL");
		this.nomesFiliais.add(">> GAMA - BEBERIBE");
		this.nomesFiliais.add(">> GAMA - PACATUBA");
		this.nomesFiliais.add(">> GAMA - PINDORETAMA");
		this.nomesFiliais.add(">> GAMA - FORTIM");
		this.nomesFiliais.add(">> GAMA - GUAIUBA");
		this.nomesFiliais.add(">> GAMA - MARANGUAPE");

		this.filiais = new ArrayList<Filial>();
		this.linhasDeTotais = new ArrayList<Integer>();
		this.listaEstilosFiliais = new ArrayList<>();
	}

	public boolean otimizaRelatorios(ObservableList<Arquivo> args) throws Exception {
		boolean retorno = true;
		if (args.size() >= 1) {
			/* TODO sysout */ System.out.println("O programa possue " + args.size() + " argumento(s)!");

			for (Arquivo arg : args) {
				try {
					extrairDadosRelatorio(arg);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					retorno = false;
					throw new Exception(e.getMessage());
				} catch (IOException e) {
					e.printStackTrace();
					retorno = false;
					throw new Exception(e.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
					retorno = false;
					throw new Exception(e.getMessage());
				}
			}
			try {
				exportarDadosRelatorio();
				retorno = false;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				retorno = false;
				throw new Exception(e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				retorno = false;
				throw new Exception(e.getMessage());
			}
		} else {
			/* TODO sysout */ System.out.println("Especifique pelo menos 1 argumento com o nome do arquivo com extenção .xls!");
		}
		return retorno;
	}
	
	private void exportarDadosRelatorio() throws FileNotFoundException, IOException {

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheetBoletos = workbook.createSheet("boletos");
		HSSFCellStyle estiloDia = workbook.createCellStyle();
		estiloDia.setAlignment(HorizontalAlignment.CENTER);
		colocarBordas(estiloDia);
		HSSFCellStyle estiloPadrao = workbook.createCellStyle();
		colocarBordas(estiloPadrao);
		
		sheetBoletos.setColumnWidth(0, (10*700));
		int rownum = 0;
		for (Filial filial : filiais) {
			rownum++;
			Row row = sheetBoletos.createRow(rownum++);
			int cellnum = 0;
			Cell cellFilial = row.createCell(cellnum);
			cellFilial.setCellValue(filial.getNome());
			HSSFCellStyle estiloFilial = workbook.createCellStyle();
			cellFilial.setCellStyle(estiloFilial);
			listaEstilosFiliais.add(estiloFilial);
			int primeiraLinhaDoVencimento = rownum;
			int ultimaLinhaDosVencimentos = rownum;
			for (Vencimento v : filial.getVencimentos()) {
				ultimaLinhaDosVencimentos = rownum;
				cellnum = 0;
				row = sheetBoletos.createRow(rownum++);
				Cell cellVencimento = row.createCell(cellnum++);
				cellVencimento.setCellValue("DIA " + v.getDataVencimento().getDayOfMonth());
				cellVencimento.setCellStyle(estiloDia);
				Cell cellSomaValores = row.createCell(cellnum++);
				
				int quantidadeDeCelulasDaSoma = cellnum + maiorQuantidadeDeBoletosPorVencimento + 6;
				CellRangeAddress range = new CellRangeAddress(rownum - 1, rownum - 1, cellnum,
						quantidadeDeCelulasDaSoma);
				
				cellSomaValores.setCellFormula("sum(" + range.formatAsString() + ")");
				cellSomaValores.setCellStyle(estiloPadrao);
				for (Double valor : v.getValores()) {
					Cell cellValor = row.createCell(cellnum++); // cria celula para armazenar o valor de cada boleto
					cellValor.setCellStyle(estiloPadrao);
					cellValor.setCellValue(valor);
				}
				
				for (int i = cellnum -1 ; i < quantidadeDeCelulasDaSoma; i++) {
					Cell cellSemValor = row.createCell(cellnum++);	// cria celulas sem valor apenas para colocar a borda
					cellSemValor.setCellStyle(estiloPadrao);
				}
				
			}
			linhasDeTotais.add(rownum);
			row = sheetBoletos.createRow(rownum++);
			Cell cellLabelTotalPorFilial = row.createCell(0);
			cellLabelTotalPorFilial.setCellStyle(estiloPadrao);
			cellLabelTotalPorFilial.setCellValue("Total Por Filial =>");
			Cell cellValorTotalPorFilial = row.createCell(1);
			cellValorTotalPorFilial.setCellStyle(estiloPadrao);
			CellRangeAddress range = new CellRangeAddress(primeiraLinhaDoVencimento, ultimaLinhaDosVencimentos, 1, 1);
			cellValorTotalPorFilial.setCellFormula("sum(" + range.formatAsString() + ")");
				
		}
	
		rownum++;
		Row row = sheetBoletos.createRow(rownum++);
		Cell cellLabelTotalGeral = row.createCell(0);
		cellLabelTotalGeral.setCellValue("Total Geral no Período =>");
		Cell cellValorTotalGeral = row.createCell(1);

		StringBuilder range = new StringBuilder();
		
		for (int i = 0; i < this.linhasDeTotais.size(); i++) {
			range.append(i!=0?", ":"");
			range.append("B"+(linhasDeTotais.get(i)+1));
		}
		
		cellValorTotalGeral.setCellFormula("sum(" + range.toString() + ")");

		Font fonteFiliais = workbook.createFont();
		fonteFiliais.setBold(true);
		int i = -1;
		for (HSSFCellStyle styleCell : this.listaEstilosFiliais) { // for que estiliza as celulas com nome de filiais
			i = i < cores.length ? ++i : 0;
			styleCell.setFillForegroundColor((short) cores[i]);
			styleCell.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			styleCell.setFont(fonteFiliais);
			colocarBordas(styleCell);
		}
		
		
		/* TODO sysout */ System.out.println("Arquivo de destino " + preferences.getOutputPath());
		FileOutputStream out = new FileOutputStream(preferences.getOutputPath());
		workbook.write(out);
		workbook.close();
		out.close();

	}
	
	private void colocarBordas(CellStyle cellStyle) {
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
	}

	private void extrairDadosRelatorio(Arquivo arquivo) throws FileNotFoundException, IOException, ParseException {

		FileInputStream relatorio = new FileInputStream(arquivo.getFile());

		HSSFWorkbook workbook = new HSSFWorkbook(relatorio);

		HSSFSheet sheetRelatorio = workbook.getSheetAt(0);

		Iterator<Row> rowIterator = sheetRelatorio.iterator();

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();

			if (row.getRowNum() == 0) {
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					if (cell.getStringCellValue().equals("GAMA - >> GAMA - ESCRITORIO")) {
						this.colunaDoValor = 9;
						String periodo = sheetRelatorio.getRow(7).getCell(5).getStringCellValue();
						extrairPeriodo(periodo);
						/* TODO sysout */ System.out.println("Processando relatório GAMA - >> GAMA - ESCRITORIO \n no período de " + periodo);
						break;
					} else if (cell.getStringCellValue().equals("GAMA POPULAR - >> CENTRAL - GP")) {
						this.colunaDoValor = 9;
						String periodo = sheetRelatorio.getRow(7).getCell(5).getStringCellValue();
						extrairPeriodo(periodo);
						/* TODO sysout */ System.out.println("Processando relatório GAMA POPULAR - >> CENTRAL - GP \n no período de " + periodo);
						break;
					} else if (cell.getStringCellValue().equals("FCIA GAMA - R. G. COM DE MED LTDA ME")) {
						this.colunaDoValor = 8;
						this.filial = new Filial(">> GAMA - MORRO BRANCO", vencimentos);
						filiais.add(this.filial);
						String periodo = sheetRelatorio.getRow(7).getCell(4).getStringCellValue();
						extrairPeriodo(periodo);
						/* TODO sysout */ System.out.println("Processando relatório FCIA GAMA - R. G. COM DE MED LTDA ME \n no período de " + periodo);
						break;
					}
				}
			}
			
			
			if (row.getRowNum() >= 11) { // linha em que comeca o relatorio: 12 e no array representa o indice 11

				Iterator<Cell> cellIterator = row.cellIterator();

				while (cellIterator.hasNext()) {

					Cell cell = cellIterator.next();

					if (!rowIterator.hasNext()) {
						break;
					} else if (cell.getCellTypeEnum() == CellType.STRING && comparaFilial(cell.getStringCellValue())) {
						this.filial = new Filial(cell.getStringCellValue(), this.vencimentos);
						this.filiais.add(this.filial);
						break;
					} else if (cell.getCellTypeEnum() == CellType.STRING
							&& cell.getStringCellValue().equals("Data Vencimento:") && cellIterator.hasNext()) {
						if (vencimento != null
								&& vencimento.getValores().size() > maiorQuantidadeDeBoletosPorVencimento) {
							maiorQuantidadeDeBoletosPorVencimento = vencimento.getValores().size();
						}
						// alguns relatorios possuem a coluna de data mesclada em duas colunas, e outras não, entao
						// eu testo se a proxima coluna possue um valor de data, se for true pega o valor, se for false 
						// testa a proxima coluna

						while (cellIterator.hasNext()) {
							cell = cellIterator.next();
							if (cell.getCellTypeEnum() == CellType.NUMERIC) {
								LocalDate data = LocalDateTime.ofInstant(cell.getDateCellValue().toInstant(),
										ZoneId.systemDefault()).toLocalDate();
								localizarVencimento(data);
								break;
							}
						}
						break;
					} else if (cell.getCellTypeEnum() == CellType.STRING
							&& cell.getStringCellValue().equals("Total Data:")) {
						break;
					} else if (cell.getCellTypeEnum() == CellType.STRING
							&& cell.getStringCellValue().equals("Total Filial:")) {
						break;
					} else if (cell.getCellTypeEnum() == CellType.STRING
							&& cell.getStringCellValue().equals("Total Geral:")) {
						break;
					} else {
						if (cell.getColumnIndex() == this.colunaDoValor) {
							vencimento.getValores().add(cell.getNumericCellValue());
						}
					}
				}
			}
		}

		workbook.close();

		relatorio.close();

		imprimirLista();
	}
	
	private void localizarVencimento(LocalDate data) {
		for (Vencimento v : this.vencimentos) {
			if (v.getDataVencimento().isEqual(data)) {
				this.vencimento = v;
				break;
			}
		}		
	}

	private void extrairPeriodo(String periodo) throws ParseException {
		
		String[] periodos = periodo.replaceAll(" ", "").split("à");
		DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		System.out.printf("data inicial %s e data final %s \n", periodos[0], periodos[1] );
		
		this.vencimentos = new ArrayList<>();
		
		dataInicial = LocalDate.parse(periodos[0], formato);
		dataFinal = LocalDate.parse(periodos[1], formato);
					
		for (long i = 0; i <= ChronoUnit.DAYS.between(dataInicial, dataFinal) ; i++) {
			Vencimento v = new Vencimento(dataInicial.plusDays(i));
			this.vencimentos.add(v);
			System.out.printf("Data %s\n",dataInicial.plusDays(i).format(formato));
		}
		
	}

	public boolean comparaFilial(String valor) {
		boolean resposta = false;

		for (int i = 0; i < nomesFiliais.size(); i++) {
			if (nomesFiliais.get(i).equals(valor)) {
				resposta = true;
				break;
			}
		}

		return resposta;
	}

	private void imprimirLista() {
		for (Filial f : this.filiais) {
			/* TODO sysout */ System.out.println("Filial " + f.getNome());
			for (Vencimento v : f.getVencimentos()) {
				StringBuilder vencimento = new StringBuilder("Dia => " + v.getDataVencimento());
				for (Double valor : v.getValores()) {
					vencimento.append(String.format(" %10.2f", valor));
				}
				/* TODO sysout */ System.out.println(vencimento.toString());
			}
		}
	}

}
