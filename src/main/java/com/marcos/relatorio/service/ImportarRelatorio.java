package com.marcos.relatorio.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import com.marcos.relatorio.model.Arquivo;
import com.marcos.relatorio.model.Filial;
import com.marcos.relatorio.model.Vencimento;
import com.marcos.relatorio.util.Log;

@Component
public class ImportarRelatorio {

	private List<String> nomesFiliais;
	
	private List<Filial> filiais;
	
	private Filial filial;
	
	private List<Vencimento> vencimentos;
	
	private Vencimento vencimento;
	
	private int colunaDoValor;
	
	private static final DateTimeFormatter DDMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	private	List<LocalDate> datasDeVencimentoDoRelatorio;
	
	private LocalDate dataInicial;
	
	private LocalDate dataFinal;

	private HSSFWorkbook workbook;

	private HSSFSheet sheetRelatorio;

	private Iterator<Row> rowIterator;

	private Iterator<Cell> cellIterator;
	
	public ImportarRelatorio() {
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
	}
	
	public void extrairDadosRelatorio(Arquivo arquivo) throws FileNotFoundException, IOException {

		FileInputStream relatorio = new FileInputStream(arquivo.getFile());

		workbook = new HSSFWorkbook(relatorio);

		sheetRelatorio = workbook.getSheetAt(0);

		rowIterator = sheetRelatorio.iterator();

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			
			if (row.getRowNum() == 0) {
				cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					if (cell.getStringCellValue().equals("GAMA - >> GAMA - ESCRITORIO")) {
						paramentrizarRelatorio(11, 7, 7, cell.getStringCellValue());
						break;
					} else if (cell.getStringCellValue().equals("GAMA POPULAR - >> CENTRAL - GP")) {
						paramentrizarRelatorio(9, 7, 5, cell.getStringCellValue());
						break;
					} else if (cell.getStringCellValue().equals("FCIA GAMA - R. G. COM DE MED LTDA ME")) {
						paramentrizarRelatorio(8, 7, 4, cell.getStringCellValue());
						novaFilial(">> GAMA - MORRO BRANCO");
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
					} else if ( cell.getCellTypeEnum() == CellType.STRING && comparaFilial(cell.getStringCellValue())) {
						Log.infoln(getClass(), cell.getStringCellValue());
						novaFilial(cell.getStringCellValue());
						break;
					} else if ( cell.getCellTypeEnum() == CellType.STRING
							&& cell.getStringCellValue().equals("Data Vencimento:") && cellIterator.hasNext()) {
						// alguns relatorios possuem a coluna de data mesclada em duas colunas, e outras não, entao
						// eu testo se a proxima coluna possue um valor de data, se for true pega o valor, se for false 
						// testa a proxima coluna
						LocalDate dataVencimento = LocalDate.now();
						while (cellIterator.hasNext()) {
							cell = cellIterator.next();
							if (cell.getCellTypeEnum() == CellType.NUMERIC) {
								dataVencimento = LocalDateTime.ofInstant(cell.getDateCellValue().toInstant(),
										ZoneId.systemDefault()).toLocalDate();
								localizarVencimento(dataVencimento);
								break;
							}
						}
						Log.infof(getClass(),
								"Data Vencimento: %s \n",
								dataVencimento.format(DDMMYYYY)
								);
						break;
					} else if ( cell.getCellTypeEnum() == CellType.STRING
							&& cell.getStringCellValue().equals("Total Data:")) {
						if (vencimento != null && vencimento.getValores().size() > filial.getMaiorQuantidadeDeBoletos()) {
							filial.setMaiorQuantidadeDeBoletos(vencimento.getValores().size());
						}						
						Log.infof(getClass(), "Vencimento: %s | %s %s boletos | Maior Quantidade %s | Filial: %s \n ********************************************************************************************* \n",
								vencimento.getDataVencimento().format(DDMMYYYY),
								cell.getStringCellValue(),
								vencimento.getValores().size(),
								filial.getMaiorQuantidadeDeBoletos(),
								filial.getNome());
						break;
					} else if ( cell.getCellTypeEnum() == CellType.STRING
							&& cell.getStringCellValue().equals("Total Filial:")) {
						this.vencimento = null;
						Log.infoln(getClass(), cell.getStringCellValue());
						break;
					} else if (cell.getCellTypeEnum() == CellType.STRING
							&& cell.getStringCellValue().equals("Total Geral:")) {
						Log.infoln(getClass(), cell.getStringCellValue());
						break;
					} else if ( cell.getColumnIndex() == this.colunaDoValor) {
						Log.infoln(getClass(), cell.getNumericCellValue()+"");
						if (vencimento != null) {
							vencimento.getValores().add(cell.getNumericCellValue());
						} else {
							throw new NullPointerException("Não há nenhum vencimento disponivel em " + filial.getNome());
						}
					}
				}
			}
		}

		workbook.close();

		relatorio.close();

		Log.infoln(getClass(), "\n Análise do relatório completa \n");
		
	}
	
	/**
	 * Instancia uma nova filial
	 * @param nomeFilial nome da nova filial
	 */
	private void novaFilial(String nomeFilial) {
	
		vencimentos = new ArrayList<>();
		
		for (LocalDate localDate : datasDeVencimentoDoRelatorio) {
			vencimentos.add(new Vencimento(LocalDate.from(localDate)));
		}
		
		this.filial = new Filial(nomeFilial, vencimentos);
		filiais.add(this.filial);
	
	}

	/**
	 * 
	 * @param colunaDoValor numero da coluna onde encontrar o valor
	 * @param linhaPeriodo numero da linha onde encontrar o período
	 * @param colunaPeriodo numero da coluna onde encontrar o período
	 * @param nomeRelatorio nome do relatório que será exibido no log
	 */
	private void paramentrizarRelatorio(int colunaDoValor, int linhaPeriodo, int colunaPeriodo, String nomeRelatorio) {
		this.colunaDoValor = colunaDoValor;
		String periodo = sheetRelatorio.getRow(linhaPeriodo).getCell(colunaPeriodo).getStringCellValue();
		extrairPeriodo(periodo);
		/* TODO sysout parametrizarRelatorio */ 
		System.out.printf("\n ******************************************************************************** "
				+ "\nProcessando relatório %s \n no período de %s \n", nomeRelatorio, periodo);
	}
	
	/**
	 * Extrai da string <b>periodo</b> duas datas que correspondem a data inicial e a data final do relatório
	 * @param periodo
	 */
	private void extrairPeriodo(String periodo){
		
		String[] periodos = periodo.replaceAll(" ", "").split("à");
					
		dataInicial = LocalDate.parse(periodos[0], DDMMYYYY);
		dataFinal = LocalDate.parse(periodos[1], DDMMYYYY);
		
		datasDeVencimentoDoRelatorio = new ArrayList<>();
		
		for (long i = 0; i <= ChronoUnit.DAYS.between(dataInicial, dataFinal) ; i++) {
			datasDeVencimentoDoRelatorio.add(dataInicial.plusDays(i));
		}
	
	}
	
	/**
	 * compara o valor da celula com com uma lista de filiais conhecidas
	 * @param nomeDaFilial nome da filial que será comparado
	 * @return retorna true se encontrar o nome da filial na lista
	 */
	public boolean comparaFilial(String nomeDaFilial) {
		boolean resposta = false;

		for (int i = 0; i < nomesFiliais.size(); i++) {
			if (nomesFiliais.get(i).equals(nomeDaFilial)) {
				resposta = true;
				break;
			}
		}

		return resposta;
	}
	
	/**
	 * Localiza o vencimento que esta sendo analizado dentro de filias.vencimentos <br>
	 * e o coloca como variavel global vencimento para ser utilizada no resto da classe
	 * @param dataVencimento
	 */
	private void localizarVencimento(LocalDate dataVencimento) {
		for (Vencimento v : this.vencimentos) {
			if (v.getDataVencimento().isEqual(dataVencimento)) {
				this.vencimento = v;
				break;
			}
		}		
	}

	public List<Filial> getFiliais() {
		return filiais;
	}
	
	public void imprimirLista() {
		for (Filial f : this.filiais) {
			Log.infoln(getClass(), "Filial " + f.getNome());
			for (Vencimento v : f.getVencimentos()) {
				StringBuilder vencimento = new StringBuilder("Dia => " + v.getDataVencimento());
				for (Double valor : v.getValores()) {
					vencimento.append(String.format(" %10.2f", valor));
				}
				Log.infoln(getClass(), vencimento.toString());
			}
		}
	}
	
}
