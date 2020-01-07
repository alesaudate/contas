package br.com.alesaudate.contas.interfaces.incoming;

import br.com.alesaudate.contas.domain.Document;
import java.io.IOException;
import java.text.ParseException;

public interface DataReader {



    boolean fileIsCorrect(byte[] data);

    Document loadDocument(byte[] data) throws IOException, ParseException;
}
