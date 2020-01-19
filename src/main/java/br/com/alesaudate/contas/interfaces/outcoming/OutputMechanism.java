package br.com.alesaudate.contas.interfaces.outcoming;

import java.io.IOException;

public interface OutputMechanism {

    void handleFile(String file) throws IOException;

    void writeData(byte[] data, String file) throws IOException;
}
