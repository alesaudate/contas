package br.com.alesaudate.contas.events.listeners;


import br.com.alesaudate.contas.config.EventBusConfig;
import br.com.alesaudate.contas.domain.Document;
import br.com.alesaudate.contas.events.EventsProducerService;
import br.com.alesaudate.contas.events.SystemLock;
import br.com.alesaudate.contas.events.definitions.DocumentFileEvent;
import br.com.alesaudate.contas.events.definitions.FileReadingErrorEvent;
import br.com.alesaudate.contas.interfaces.InteractionScheme;
import br.com.alesaudate.contas.interfaces.incoming.GenericReader;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewFileDetectedMessageListener extends GenericMessageListener<String> {


    SystemLock systemLock;

    GenericReader reader;

    InteractionScheme io;

    @Override
    protected void doAccept(String file) throws Exception{
        io.tell("Encontrado documento %s . Iniciando processamento.", file);

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

    @Override
    public String listenToEvent() {
        return EventBusConfig.FILE_FOUND_EVENT;
    }
}
