package br.com.alesaudate.contas.interfaces.incoming.multiple;

import br.com.alesaudate.contas.domain.Category;
import br.com.alesaudate.contas.domain.Document;
import br.com.alesaudate.contas.domain.Entry;
import br.com.alesaudate.contas.domain.EntryType;
import br.com.alesaudate.contas.interfaces.incoming.DataReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class XLSXReader implements DataReader {

    //TODO finalizar importação de CSV / Excel

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public boolean fileIsCorrect(byte[] data) {

        try {
            XSSFSheet sheet = getSheet(data, "Fatura CC");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Document loadDocument(byte[] data) throws IOException, ParseException {

        List<Entry> entriesFaturaCC = getEntriesFromSheet(getSheet(data, "Fatura CC"));
        List<Entry> entriesExtrato = getEntriesFromSheet(getSheet(data, "Extrato"));
        List<Entry> entriesCreditos = getEntriesFromSheet(getSheet(data, "Créditos"));
        entriesCreditos.stream().forEach(entry -> entry.setEntryType(EntryType.CREDIT));

        List<Entry> allEntries = new ArrayList<>();
        allEntries.addAll(entriesFaturaCC);
        allEntries.addAll(entriesExtrato);
        allEntries.addAll(entriesCreditos);

        Document document = new Document();
        document.setEntries(allEntries);
        return document;
    }

    private List<Entry> getEntriesFromSheet(XSSFSheet sheet) throws ParseException {
        XSSFRow headersRow = sheet.getRow(0);

        int currentRow = 1;
        XSSFRow dataRow = sheet.getRow(1);
        List<Entry> entries = new ArrayList<>();

        while (dataRow != null && dataRow.getCell(0) != null && StringUtils.isNotEmpty(dataRow.getCell(0).getRawValue())) {
            entries.add(getEntryFromRow(dataRow));
            dataRow = sheet.getRow(++currentRow);
        }
        return entries;
    }

    private Entry getEntryFromRow(XSSFRow dataRow) throws ParseException {
        String date = getCellValue(dataRow, 0);
        String categoryName = getCellValue(dataRow, 1);
        String itemName = getCellValue(dataRow, 2);
        String amount = getCellValue(dataRow, 3);
        String description = getCellValue(dataRow, 4);
        String installment = getCellValue(dataRow, 5);
        String totalNumberOfInstallments = getCellValue(dataRow, 6);

        Entry entry = new Entry();
        Category category = new Category();
        category.setName(categoryName);
        entry.setCategory(category);
        entry.setDate(DATE_FORMAT.parse(date));
        entry.setAmount(new BigDecimal(amount));
        entry.setItemName(itemName);
        entry.setDescription(description);
        if (StringUtils.isNotEmpty(installment)) {
            entry.setInstallmentNumber(Double.valueOf(installment).intValue());
        }
        else {
            entry.setInstallmentNumber(1);
        }

        if (StringUtils.isNotEmpty(totalNumberOfInstallments)) {
            entry.setTotalNumberOfInstallments(Double.valueOf(totalNumberOfInstallments).intValue());
        }
        else {
            entry.setTotalNumberOfInstallments(1);
        }

        entry.setEntryType(EntryType.DEBT);

        return entry;

    }


    private String getCellValue(XSSFRow row, int cellNumber) {

        XSSFCell cell = row.getCell(cellNumber);



        if (cell != null) {
            CellType type = cell.getCellType();
            switch (type) {
                case STRING: return cell.getStringCellValue();
                case NUMERIC: {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return DATE_FORMAT.format(cell.getDateCellValue());
                    }
                    return String.valueOf(cell.getNumericCellValue());
                }
            }

        }
        return "";
    }


    protected XSSFSheet getSheet(byte[] data, String sheetName) throws IOException {
        try {
            OPCPackage pkg = OPCPackage.open(new ByteArrayInputStream(data));
            XSSFWorkbook workbook = new XSSFWorkbook(pkg);
            return workbook.getSheet(sheetName);
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

}
