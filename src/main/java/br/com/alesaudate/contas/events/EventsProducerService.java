package br.com.alesaudate.contas.events;

import static br.com.alesaudate.contas.config.EventBusConfig.*;
import br.com.alesaudate.contas.domain.Document;
import br.com.alesaudate.contas.events.definitions.DocumentFileEvent;
import br.com.alesaudate.contas.events.definitions.FileReadingErrorEvent;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.bus.EventBus;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class EventsProducerService {



    EventBus eventBus;


    public void publishFileFound(String filePath) {
        eventBus.notify(FILE_FOUND_EVENT, Event.wrap(filePath));
    }

    public void publishDocumentFound(DocumentFileEvent event) {
        eventBus.notify(DOCUMENT_FOUND_EVENT, Event.wrap(event));

    }

    public void publishDocumentReady(DocumentFileEvent event) {
        eventBus.notify(DOCUMENT_READY, Event.wrap(event));
    }

    public void publishDocumentCreated(DocumentFileEvent event) {
        eventBus.notify(DOCUMENT_CREATED, Event.wrap(event));
    }

    public void publishReadyForEvents() {
        eventBus.notify(READY_FOR_EVENTS, Event.wrap(new Object()));
    }

    public void publishInputCommand(String command) {
        eventBus.notify(".", Event.wrap(command));
    }

    public void publishFileReadingError(FileReadingErrorEvent event) {

    }

}
