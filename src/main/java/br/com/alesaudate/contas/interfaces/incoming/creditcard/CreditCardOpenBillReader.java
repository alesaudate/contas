package br.com.alesaudate.contas.interfaces.incoming.creditcard;

import br.com.alesaudate.contas.domain.Document;
import br.com.alesaudate.contas.domain.DocumentType;
import br.com.alesaudate.contas.domain.Entry;
import br.com.alesaudate.contas.interfaces.incoming.DataReader;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CreditCardOpenBillReader implements DataReader {

    private static final String CHECK_PHRASE = "COMPROVANTE CARTÕES DE CRÉDITO FATURA EM ABERTO";

    @Override
    public boolean fileIsCorrect(byte[] data) {
        try {
            PDDocument document = PDDocument.load(data);


            PDFTextStripper stripper = new PDFTextStripper();

            String pdfFileInText = stripper.getText(document);

            document.close();
            return pdfFileInText.contains(CHECK_PHRASE);
        }
        catch (Exception e) {
            return false;
        }

    }

    @Override
    public Document loadDocument(byte[] data) throws IOException, ParseException {

        Document document = new Document();
        document.setEntries(load(data));
        return document;
    }


    protected List<Entry> load(byte[] data) throws IOException, ParseException {
        PDDocument document = PDDocument.load(data);


        PDFTextStripper stripper = new PDFTextStripper();

        String pdfFileInText = stripper.getText(document);

        document.close();

        String lines[] = pdfFileInText.split("\\r?\\n");

        Pattern p = Pattern.compile("^\\d{2}/\\d{2}/\\d{4} \\p{ASCII}+ (-?[0-9\\.]+,[0-9]{2}$)");

        List<Entry> entries = new ArrayList<>();

        for (String line : lines) {
            boolean found = p.matcher(line).find();
            if (found) {
                entries.add(assembleEntry(line));
            }
        }
        return entries;
    }

    private static final String INSTALLMENTS_DELIMITER = "/";
    private static final Pattern NUMBER_PATTERN = Pattern.compile("(-?[0-9\\.]+,[0-9]{2})");

    private Entry assembleEntry(String line) throws ParseException {


        Pattern installmentsPattern = Pattern.compile("\\([0-9]{2}" + INSTALLMENTS_DELIMITER + "[0-9]{2}\\)");


        Matcher matcher =  NUMBER_PATTERN.matcher(line);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        String date = line.substring(0, line.indexOf(" "));

        matcher.find();
        matcher.find();
        String rsAmount = matcher.group();

        String description = line.substring(line.indexOf(" "), line.indexOf("US$"));

        rsAmount = rsAmount.replace(".", "").replace(",", ".");


        Integer installmentNumber = 1;
        Integer totalNumberOfInstallments = 1;

        matcher = installmentsPattern.matcher(description);

        if (matcher.find()) {
            String installmentsText = description.substring(description.indexOf("(") + 1);
            installmentsText = installmentsText.substring(0, installmentsText.indexOf(")"));
            installmentNumber = Integer.valueOf(installmentsText.substring(0, installmentsText.indexOf(INSTALLMENTS_DELIMITER)));
            totalNumberOfInstallments = Integer.valueOf(installmentsText.substring(installmentsText.indexOf(INSTALLMENTS_DELIMITER) + INSTALLMENTS_DELIMITER.length()).trim());

            description = description.substring(0, description.indexOf("("));

        }
        description = description.trim();

        BigDecimal rsAmountAsBigDecimal = new BigDecimal(rsAmount);

        Entry entry = new Entry();
        entry.setAmount(rsAmountAsBigDecimal);
        entry.setItemName(description);
        entry.setDate(sdf.parse(date));
        entry.setInstallmentNumber(installmentNumber);
        entry.setTotalNumberOfInstallments(totalNumberOfInstallments);

        return entry;



    }


}
