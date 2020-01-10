package br.com.alesaudate.contas.events.listeners;


import br.com.alesaudate.contas.domain.Document;
import br.com.alesaudate.contas.events.EventsProducerService;
import br.com.alesaudate.contas.events.SystemLock;
import br.com.alesaudate.contas.events.definitions.DocumentFileEvent;
import br.com.alesaudate.contas.events.definitions.FileReadingErrorEvent;
import br.com.alesaudate.contas.interfaces.incoming.GenericReader;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class NewFileDetectedMessageListener extends GenericMessageListener<String> {


    @Autowired
    private SystemLock systemLock;

    @Autowired
    private GenericReader reader;

    @Override
    protected void doAccept(String file) throws Exception{

        try {
            byte[] data = FileUtils.readFileToByteArray(new File(file));
            if (reader.fileIsCorrect(data)) {
                Document document = reader.loadDocument(data);
                getEventsProducerService().publishDocumentFound(new DocumentFileEvent(document, file));
            }
        }
        catch (Exception e) {
            getEventsProducerService().publishFileReadingError(new FileReadingErrorEvent(file, e));
            throw  e;
        }
    }

}
