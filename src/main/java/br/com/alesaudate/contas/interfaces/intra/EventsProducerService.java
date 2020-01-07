package br.com.alesaudate.contas.interfaces.intra;


import static br.com.alesaudate.contas.config.EventBusConfig.DOCUMENT_CREATED;
import static br.com.alesaudate.contas.config.EventBusConfig.WANTS_TO_LIST_ENTRIES;

import br.com.alesaudate.contas.domain.Document;
import br.com.alesaudate.contas.interfaces.intra.events.WantsToListEntriesEvent;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.bus.EventBus;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class EventsProducerService {



    EventBus eventBus;


    public void publishDocumentCreated(Document document) {
        eventBus.notify(DOCUMENT_CREATED, Event.wrap(document));
    }


    public void publishWantsToListEntries(WantsToListEntriesEvent event) {
        eventBus.notify(WANTS_TO_LIST_ENTRIES, Event.wrap(event));
    }
}
