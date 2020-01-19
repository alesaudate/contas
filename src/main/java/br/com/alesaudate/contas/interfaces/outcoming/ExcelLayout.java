package br.com.alesaudate.contas.interfaces.outcoming;

import br.com.alesaudate.contas.domain.Entry;
import br.com.alesaudate.contas.domain.EntryType;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExcelLayout extends Layout {


    @Override
    public byte[] format(List<Entry> entryList) throws IOException {

        validate(entryList);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        XSSFWorkbook workbook = (XSSFWorkbook)WorkbookFactory.create(true);

        CellStyle cellDateStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        cellDateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));


        XSSFSheet debitsSheet = workbook.createSheet("Débitos");
        XSSFSheet creditsSheet = workbook.createSheet("Créditos");
        XSSFSheet balanceSheet = workbook.createSheet("Balanço");

        fillHeaderRow(debitsSheet.createRow(0), 8);
        fillHeaderRow(creditsSheet.createRow(0), 4);

        List<Entry> debitList = entryList.stream().filter((entry -> entry.getEntryType().equals(EntryType.DEBT))).collect(Collectors.toList());
        List<Entry> creditList = entryList.stream().filter((entry -> entry.getEntryType().equals(EntryType.CREDIT))).collect(Collectors.toList());

        for (int i = 0; i < debitList.size(); i++) {
            createDebtsRow(debitsSheet,debitList.get(i), i+1, cellDateStyle);
        }

        for (int i = 0; i < creditList.size(); i++) {
            createCreditsRow(creditsSheet, creditList.get(i), i + 1, cellDateStyle);
        }

        fillSumOfTotals(debitsSheet, debitList.size() + 4);
        fillSumOfTotals(creditsSheet, creditList.size() + 4);

        fillBalanceSheet(balanceSheet, debitList.size() + 5, creditList.size() + 5);

        XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
        workbook.write(baos);

        return baos.toByteArray();
    }

    private void fillBalanceSheet (XSSFSheet sheet, int debtsRowNum, int creditsRowNum) {

        XSSFRow debtsRow = sheet.createRow(1);
        debtsRow.createCell(0).setCellValue("Débitos");
        debtsRow.createCell(1).setCellFormula(String.format("Débitos!D%d", debtsRowNum));

        XSSFRow creditsRow = sheet.createRow(2);
        creditsRow.createCell(0).setCellValue("Créditos");
        creditsRow.createCell(1).setCellFormula(String.format("Créditos!D%d", creditsRowNum));


        XSSFRow balanceRow = sheet.createRow(5);
        balanceRow.createCell(0).setCellValue("Razão");
        balanceRow.createCell(1).setCellFormula("B3 - B2");

    }

    private void fillSumOfTotals(XSSFSheet sheet, int rowNum) {

        XSSFRow row = sheet.createRow(rowNum);

        row.createCell(2).setCellValue("TOTAL");
        row.createCell(3).setCellFormula(String.format("SUM(D2:D%d)", rowNum - 3));


    }

    private void createCreditsRow(XSSFSheet sheet, Entry entry, int rownum, CellStyle dateStyle) {

        XSSFRow row = sheet.createRow(rownum);
        XSSFCell dateCell = row.createCell(0);
        dateCell.setCellStyle(dateStyle);
        dateCell.setCellValue(entry.getDate());

        row.createCell(1, CellType.STRING).setCellValue(entry.getCategoryName());
        row.createCell(2, CellType.STRING).setCellValue(entry.getItemName());
        row.createCell(3, CellType.NUMERIC).setCellValue(entry.getAmount().doubleValue());
        row.createCell(4, CellType.STRING).setCellValue(entry.getDescription());
    }


    private void createDebtsRow(XSSFSheet sheet, Entry entry, int rownum, CellStyle dateStyle) {



        int actualRowNum = rownum + 1;

        XSSFRow row = sheet.createRow(rownum);
        XSSFCell dateCell = row.createCell(0);
        dateCell.setCellStyle(dateStyle);
        dateCell.setCellValue(entry.getDate());

        row.createCell(1, CellType.STRING).setCellValue(entry.getCategoryName());
        row.createCell(2, CellType.STRING).setCellValue(entry.getItemName());
        row.createCell(3, CellType.NUMERIC).setCellValue(entry.getAmount().doubleValue());
        row.createCell(4, CellType.STRING).setCellValue(entry.getDescription());
        row.createCell(5, CellType.NUMERIC).setCellValue(entry.getInstallmentNumber().doubleValue());
        row.createCell(6, CellType.NUMERIC).setCellValue(entry.getTotalNumberOfInstallments().doubleValue());
        row.createCell(7, CellType.FORMULA).setCellFormula(String.format("(G%1$d - F%1$d) * D%1$d", actualRowNum));
        row.createCell(8, CellType.FORMULA).setCellFormula(String.format("IF(G%1$d>F%1$d,D%1$d,0)", actualRowNum));



    }

    private void fillHeaderRow(XSSFRow row, int limitColumn) {
        XSSFCell dateCell = row.createCell(0, CellType.STRING);
        dateCell.setCellValue("Data");

        XSSFCell categoryCell = row.createCell(1, CellType.STRING);
        categoryCell.setCellValue("Categoria");

        XSSFCell itemCell = row.createCell(2, CellType.STRING);
        itemCell.setCellValue("Item");

        XSSFCell valueCell = row.createCell(3, CellType.STRING);
        valueCell.setCellValue("Valor R$");

        XSSFCell descriptioncell = row.createCell(4, CellType.STRING);
        descriptioncell.setCellValue("Descrição");

        if (limitColumn > 4) {
            XSSFCell installmentCell = row.createCell(5, CellType.STRING);
            installmentCell.setCellValue("Parcela");

            XSSFCell totalInstallmentsCell = row.createCell(6, CellType.STRING);
            totalInstallmentsCell.setCellValue("Total de parcelas");

            XSSFCell howMuchLeftToPayCell = row.createCell(7, CellType.STRING);
            howMuchLeftToPayCell.setCellValue("A pagar");

            XSSFCell howMuchToPayNextMonthCell = row.createCell(8, CellType.STRING);
            howMuchToPayNextMonthCell.setCellValue("A pagar na próxima fatura");
        }

    }


    private void validate(List<Entry> entries) {
        if (entries.isEmpty()) {
            throw new RuntimeException("Entries list is empty");
        }

        Optional<Entry> optionalEntry = entries.stream().filter(Objects::isNull).findAny();
        if (optionalEntry.isPresent()) {
            throw new RuntimeException("Entrada com tipo nulo");
        }
    }

    @Override
    public String getExtension() {
        return "xlsx";
    }
}
