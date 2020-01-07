package br.com.alesaudate.contas.interfaces.incoming.bankaccount;

import br.com.alesaudate.contas.domain.Document;
import br.com.alesaudate.contas.domain.DocumentType;
import br.com.alesaudate.contas.domain.Entry;
import br.com.alesaudate.contas.domain.EntryType;
import br.com.alesaudate.contas.interfaces.incoming.DataReader;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

@Component
public class BankAccountReader implements DataReader {


    private static final String CHECK_PHRASE = "EXTRATO DE CONTA CORRENTE";

    @Override
    public boolean fileIsCorrect(byte[] data) {
        try {
            PDDocument document = PDDocument.load(data);


            PDFTextStripper stripper = new PDFTextStripper();

            String pdfFileInText = stripper.getText(document);

            return pdfFileInText.contains(CHECK_PHRASE);
        }
        catch (Exception e) {
            return false;
        }
    }

    public Document loadDocument(byte[] data) throws IOException, ParseException {
        Document document = new Document();
        document.setEntries(load(data));
        document.setType(DocumentType.BANK_ACCOUNT);
        return document;
    }



    protected List<Entry> load (byte[] data) throws IOException, ParseException {

        PDDocument document = PDDocument.load(data);


        PDFTextStripper stripper = new PDFTextStripper();

        String pdfFileInText = stripper.getText(document);


        String lines[] = pdfFileInText.split("\\r?\\n");

        Pattern p = Pattern.compile("^\\d{2}/\\d{2}/\\d{4} \\p{ASCII}+ (-?[0-9\\.]+,[0-9]{2}) (-?[0-9\\.]+,[0-9]{2})");
        Pattern intermediatePattern = Pattern.compile("^\\d{2}/\\d{2}/\\d{4} \\p{ASCII}+");

        StringBuilder intermediate = new StringBuilder();
        boolean appending = false;

        List<Entry> entries = new ArrayList<>();
        for (String line : lines) {
            boolean found = p.matcher(line).find();
            if (found) {
                entries.add(assembleEntry(line));
                appending = false;
                intermediate = new StringBuilder();
            }
            else {
                if (intermediatePattern.matcher(line).find()) {
                    appending = true;
                    intermediate.append(line).append(" ");
                }
                else if (appending) {
                    intermediate.append(line).append(" ");

                    if (p.matcher(intermediate.toString()).find()) {
                        entries.add(assembleEntry(intermediate.toString()));
                        appending = false;
                        intermediate = new StringBuilder();
                    }
                }

            }
        }
        return entries;
    }


    private Entry assembleEntry(String line) throws ParseException {

        Pattern numberPattern = Pattern.compile("(-?[0-9\\.]+,[0-9]{2})");
        Matcher matcher = numberPattern.matcher(line);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");


        String date = line.substring(0, line.indexOf(" "));

        matcher.find();
        String firstNumber = matcher.group();
        matcher.find();
        String secondNumber = matcher.group();

        String description = line.substring(line.indexOf(" "), line.indexOf(firstNumber)).trim();

        firstNumber = firstNumber.replace(".", "").replace(",", ".");


        BigDecimal amount = new BigDecimal(firstNumber);
        EntryType type = EntryType.CREDIT;

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            amount = amount.abs();
            type = EntryType.DEBT;
        }

        Entry entry = new Entry();
        entry.setAmount(amount);
        entry.setItemName(description);
        entry.setDate(sdf.parse(date));
        entry.setTotalNumberOfInstallments(1);
        entry.setInstallmentNumber(1);
        entry.setEntryType(type);

        return entry;



    }





    public static void main(String[] args) throws Exception{


        File file = new File("/home/asaudate/Downloads/comprovanteEnvioCCAvanzadaExtracto6B211F3FFCD25C5C334EA2DA.pdf");
        byte[] data = FileUtils.readFileToByteArray(file);

        new BankAccountReader().load(data);



    }
}
