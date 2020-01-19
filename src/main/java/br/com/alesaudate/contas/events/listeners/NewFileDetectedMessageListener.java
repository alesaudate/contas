package br.com.alesaudate.contas.events.listeners;


import br.com.alesaudate.contas.domain.Document;
import br.com.alesaudate.contas.events.definitions.DocumentFileEvent;
import br.com.alesaudate.contas.interfaces.InteractionScheme;
import br.com.alesaudate.contas.interfaces.incoming.GenericReader;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewFileDetectedMessageListener extends GenericMessageListener<String> {

    GenericReader reader;

    InteractionScheme io;

    NewDocumentDetectedListener newDocumentDetectedListener;

    @Override
    public void accept(String file) throws IOException, ParseException, InvalidDocumentException {
        io.tell("Encontrado documento %s . Iniciando processamento.", file);


        byte[] data = FileUtils.readFileToByteArray(new File(file));
        if (reader.fileIsCorrect(data)) {
            Document document = reader.loadDocument(data);
            io.tell("O documento (%s) é válido e teve o pré-processamento finalizado.", file);
            newDocumentDetectedListener.accept(new DocumentFileEvent(document, file));
        }
        else {
            io.tell("O documento %s não foi reconhecido como um formato válido.", file);
            throw new InvalidDocumentException();
        }

    }

}
