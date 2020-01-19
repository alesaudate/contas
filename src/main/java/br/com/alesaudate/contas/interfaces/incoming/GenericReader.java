package br.com.alesaudate.contas.interfaces.incoming;

import br.com.alesaudate.contas.domain.Document;
import br.com.alesaudate.contas.interfaces.incoming.bankaccount.BankAccountReader;
import br.com.alesaudate.contas.interfaces.incoming.creditcard.CreditCardClosedBillReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.alesaudate.contas.interfaces.incoming.creditcard.CreditCardOpenBillReader;
import br.com.alesaudate.contas.interfaces.incoming.multiple.XLSXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class GenericReader implements DataReader {


    private List<DataReader> dataReaders;

    @Autowired
    public GenericReader(BankAccountReader bankAccountReader,
                         CreditCardClosedBillReader creditCardClosedBillReader,
                         CreditCardOpenBillReader creditCardOpenBillReader,
                         XLSXReader xlsxReader) {
        this.dataReaders = Arrays.asList(
                bankAccountReader,
                new DateCorrectionReader(creditCardClosedBillReader),
                creditCardOpenBillReader,
                new DateCorrectionReader(xlsxReader)
        );
    }




    @Override
    public boolean fileIsCorrect(byte[] data) {
        return dataReaders.stream().filter(dr -> dr.fileIsCorrect(data)).findAny().isPresent();
    }

    @Override
    public Document loadDocument(byte[] data) throws IOException, ParseException {
        DataReader reader = dataReaders.stream().filter(dr -> dr.fileIsCorrect(data)).findAny().get();
        return reader.loadDocument(data);
    }
}
