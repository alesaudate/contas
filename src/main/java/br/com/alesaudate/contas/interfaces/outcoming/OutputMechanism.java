package br.com.alesaudate.contas.interfaces.outcoming;

import java.io.IOException;

public interface OutputMechanism {

    void handleFile(String file) throws IOException;

}
