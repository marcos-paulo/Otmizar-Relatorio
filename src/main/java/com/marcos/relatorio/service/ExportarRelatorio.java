package com.marcos.relatorio.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Component;

import com.marcos.relatorio.model.Filial;
import com.marcos.relatorio.model.Vencimento;
import com.marcos.relatorio.repository.PreferencesReposirory;
import com.marcos.relatorio.util.Log;

@Component
public class ExportarRelatorio {
	
	private static final int QUANTIDADE_DE_CELULAS_DE_BOLETOS_A_MAIS = 6;

	private PreferencesReposirory preferences;
	
	private List<Filial> filiais;
		
	private List<Integer> linhasDeTotais;
	
	private static final int[] cores = 
		{29,35,38,41,43,44,45,46,47,48,49,50,54,55};

	private HSSFWorkbook workbook;

	private HSSFSheet sheetBoletos;

	private HSSFCellStyle estiloDia;

	private HSSFCellStyle estiloPadrao;

	private HSSFCellStyle estiloFilial;

	private List<HSSFCellStyle> listaEstilosFiliais;
	
	public ExportarRelatorio(PreferencesReposirory preferencesReposirory) {
		this.preferences = preferencesReposirory;
		this.linhasDeTotais = new ArrayList<Integer>();
		this.listaEstilosFiliais = new ArrayList<>();
	}
	
	public void exportarRelatorio(List<Filial> filiais) throws IOException {
		this.filiais = filiais;
		
		criarPlanilha();
		
		exportarDadosRelatorio();
		
		salvarPlanilha();
		
	}

	private void exportarDadosRelatorio() {
		
		sheetBoletos.setColumnWidth(0, (10*700));
		int rownum = 0;
		for (Filial filial : filiais) {
			rownum++;
			
			imprimirFilial(filial);
			
			Row row = sheetBoletos.createRow(rownum++);
			
			estiloFilial = workbook.createCellStyle();
			listaEstilosFiliais.add(estiloFilial);

			int cellnum = 0;
			Cell cellFilial = row.createCell(cellnum);
			cellFilial.setCellValue(filial.getNome());
			cellFilial.setCellStyle(estiloFilial);
			
			int primeiraLinhaDeTotalPorVencimento = rownum;
			int ultimaLinhaDeTotalPorVencimentos = rownum;
			
			for (Vencimento v : filial.getVencimentos()) {
				ultimaLinhaDeTotalPorVencimentos = rownum;
			
				row = sheetBoletos.createRow(rownum++);

				cellnum = 0;
				Cell cellVencimento = row.createCell(cellnum++);
				cellVencimento.setCellValue("DIA " + v.getDataVencimento().getDayOfMonth());
				cellVencimento.setCellStyle(estiloDia);
				
				Cell cellSomaValoresPorVencimento = row.createCell(cellnum++);
				
				int quantidadeDeCelulasDaSoma = cellnum + filial.getMaiorQuantidadeDeBoletos() + QUANTIDADE_DE_CELULAS_DE_BOLETOS_A_MAIS;
				
				CellRangeAddress range = new CellRangeAddress(rownum - 1, rownum - 1, cellnum,
						quantidadeDeCelulasDaSoma - 1);
				
				cellSomaValoresPorVencimento.setCellFormula("sum(" + range.formatAsString() + ")");
				cellSomaValoresPorVencimento.setCellStyle(estiloPadrao);
				
				for (Double valor : v.getValores()) {
					novaCelulaDeValor(row, cellnum++, valor);
				}
				
				for (int i = cellnum; i < quantidadeDeCelulasDaSoma; i++) {
					novaCelulaDeValor(row, i, null); // cria celulas sem valor apenas para colocar a borda
				}
				
			}
			
			novaLinhaDeTotal(rownum++, primeiraLinhaDeTotalPorVencimento, ultimaLinhaDeTotalPorVencimentos);
				
		}
	
		rownum++;
		novaLinhaTotalGeral(rownum++);
		colocarEstilos();
	}

	/**
	 * preenche alguns estilos na planilhaS
	 */
	private void colocarEstilos() {
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
	}

	/**
	 * Cria uma nova linha de total Geral
	 * @param rownum
	 */
	private void novaLinhaTotalGeral(int rownum) {
		
		Row row = sheetBoletos.createRow(rownum);
		
		Cell cellLabelTotalGeral = row.createCell(0);
		cellLabelTotalGeral.setCellValue("Total Geral no Período =>");
		
		StringBuilder range = new StringBuilder();
		
		for (int i = 0; i < this.linhasDeTotais.size(); i++) {
			range.append(i!=0?", ":"");
			range.append("B"+(linhasDeTotais.get(i)+1));
		}
		
		Cell cellValorTotalGeral = row.createCell(1);
		cellValorTotalGeral.setCellFormula("sum(" + range.toString() + ")");
	}

	/**
	 * Cria uma nova linha de total por filial
	 * @param rownum
	 * @param primeiraLinhaDeTotalPorVencimento
	 * @param ultimaLinhaDeTotalPorVencimentos 
	 */
	private void novaLinhaDeTotal(int rownum, int primeiraLinhaDeTotalPorVencimento,
			int ultimaLinhaDeTotalPorVencimentos) {

		linhasDeTotais.add(rownum);
		Row row = sheetBoletos.createRow(rownum);
		
		Cell cellLabelTotalPorFilial = row.createCell(0);
		cellLabelTotalPorFilial.setCellStyle(estiloPadrao);
		cellLabelTotalPorFilial.setCellValue("Total Por Filial =>");
		
		CellRangeAddress range = new CellRangeAddress(primeiraLinhaDeTotalPorVencimento, ultimaLinhaDeTotalPorVencimentos, 1, 1);

		Cell cellValorTotalPorFilial = row.createCell(1);
		cellValorTotalPorFilial.setCellStyle(estiloPadrao);
		cellValorTotalPorFilial.setCellFormula("sum(" + range.formatAsString() + ")");
		
	}

	private void salvarPlanilha() throws IOException {
		try {
			Log.infoln(getClass(), "Arquivo de destino " + preferences.getOutputPath());
			FileOutputStream out = new FileOutputStream(preferences.getOutputPath());
			workbook.write(out);
			workbook.close();
			out.close();
		} catch (FileNotFoundException e) {
			File file = new File(preferences.getOutputPath());
			throw new FileNotFoundException("O arquivo " + file.getName() + " está aberto em outro processo, feche-o e tente novamente!");
		}
	}

	private void criarPlanilha() {
		workbook = new HSSFWorkbook();
		sheetBoletos = workbook.createSheet("boletos");
		estiloDia = workbook.createCellStyle();
		estiloDia.setAlignment(HorizontalAlignment.CENTER);
		estiloPadrao = workbook.createCellStyle();
		estiloPadrao.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
		colocarBordas(estiloDia);
		colocarBordas(estiloPadrao);
	}
	
	private void colocarBordas(CellStyle cellStyle) {
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
	}
	
	/**
	 * cria uma celula que irá armazenar um valor de boleto
	 * @param row
	 * @param cellnum
	 * @param valor
	 * @return
	 */
	private Cell novaCelulaDeValor(Row row, int cellnum, Double valor){
		Cell cellValor = row.createCell(cellnum); // cria celula para armazenar o valor de cada boleto
		cellValor.setCellStyle(estiloPadrao);
		if (valor != null) {
			cellValor.setCellValue(valor);
		}
		return cellValor;
	}
	
	private void imprimirFilial(Filial filial) {
		Log.infof(getClass(), "****************************************************************************************\n"
				+ "Carregando Filial %s no novo Relatorio, e a maior quantidade de boletos é %s\n", filial.getNome(), filial.getMaiorQuantidadeDeBoletos());
		for (Vencimento v : filial.getVencimentos()) {
			StringBuilder vencimento = new StringBuilder("Dia => " + v.getDataVencimento());
			for (Double valor : v.getValores()) {
				vencimento.append(String.format(" %10.2f", valor));
			}
			Log.infoln(getClass(), vencimento.toString());
		}
	}

}