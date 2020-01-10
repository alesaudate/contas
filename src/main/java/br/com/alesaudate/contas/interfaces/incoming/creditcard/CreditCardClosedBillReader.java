package br.com.alesaudate.contas.interfaces.incoming.creditcard;

import br.com.alesaudate.contas.domain.Document;
import br.com.alesaudate.contas.domain.DocumentType;
import br.com.alesaudate.contas.domain.Entry;
import br.com.alesaudate.contas.interfaces.incoming.DataReader;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

@Component
public class CreditCardClosedBillReader implements DataReader {


    private static final String CHECK_PHRASE = "Limite Total de Crédito";

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

    public Document loadDocument(byte[] data) throws IOException, ParseException {
        Document document = new Document();
        document.setEntries(load(data));
        return document;
    }


    protected List<Entry> load (byte[] data) throws IOException, ParseException {

        PDDocument document = PDDocument.load(data);


        PDFTextStripper stripper = new PDFTextStripper();

        String pdfFileInText = stripper.getText(document);

        document.close();


        String lines[] = pdfFileInText.split("\\r?\\n");

        Pattern p = Pattern.compile("^\\d{2}/\\d{2} \\p{ASCII}+ (-?[0-9\\.]+,[0-9]{2})$");

        String year = String.valueOf(LocalDate.now().getYear());

        List<Entry> entries = new ArrayList<>();
        for (String line : lines) {
            boolean found = p.matcher(line).find();
            if (found) {
                entries.add(assembleEntry(line, year));
            }
            else if (line.startsWith("Data de fechamento desta fatura")) {
                year = line.substring(line.lastIndexOf('/') + 1);
            }
        }
        return entries;
    }


    private static final String INSTALLMENTS_PLACEHOLDER = "PARC ";
    private static final String INSTALLMENTS_DELIMITER = "/";


    private Entry assembleEntry(String line, String year) throws ParseException {

        Pattern numberPattern = Pattern.compile("(-?[0-9\\.]+,[0-9]{2})");
        Pattern installmentsPattern = Pattern.compile(INSTALLMENTS_PLACEHOLDER + "[0-9]{2}" + INSTALLMENTS_DELIMITER + "[0-9]{2}");


        Matcher matcher = numberPattern.matcher(line);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");


        String date = line.substring(0, line.indexOf(" ")) + "/" + year;

        matcher.find();
        String firstNumber = matcher.group();

        String description = line.substring(line.indexOf(" "), line.indexOf(firstNumber));

        firstNumber = firstNumber.replace(".", "").replace(",", ".");


        Integer installmentNumber = 1;
        Integer totalNumberOfInstallments = 1;

        matcher = installmentsPattern.matcher(description);

        if (matcher.find()) {
            String installmentsText = description.substring(description.indexOf(INSTALLMENTS_PLACEHOLDER) + INSTALLMENTS_PLACEHOLDER.length());
            installmentNumber = Integer.valueOf(installmentsText.substring(0, installmentsText.indexOf(INSTALLMENTS_DELIMITER)));
            totalNumberOfInstallments = Integer.valueOf(installmentsText.substring(installmentsText.indexOf(INSTALLMENTS_DELIMITER) + INSTALLMENTS_DELIMITER.length()).trim());

            description = description.substring(0, description.indexOf(INSTALLMENTS_PLACEHOLDER));

        }
        description = description.trim();

        Entry entry = new Entry();
        entry.setAmount(new BigDecimal(firstNumber));
        entry.setItemName(description);
        entry.setDate(sdf.parse(date));
        entry.setInstallmentNumber(installmentNumber);
        entry.setTotalNumberOfInstallments(totalNumberOfInstallments);

        return entry;



    }



    public static void main(String[] args) throws IOException, ParseException {
        new File("/home/asaudate/Downloads/Extrato_21_11_2019.pdf");
        new CreditCardClosedBillReader().load(null);



    }
}
