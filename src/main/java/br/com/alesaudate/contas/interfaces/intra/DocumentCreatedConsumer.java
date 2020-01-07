package br.com.alesaudate.contas.interfaces.intra;

import br.com.alesaudate.contas.domain.Document;
import br.com.alesaudate.contas.interfaces.InteractionScheme;

import br.com.alesaudate.contas.interfaces.intra.events.WantsToListEntriesEvent;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.bus.Event;
import reactor.fn.Consumer;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DocumentCreatedConsumer implements Consumer<Event<Document>> {


    InteractionScheme interactionScheme;

    EventsProducerService eventsProducerService;

    @Override
    public void accept(Event<Document> documentCreatedEventEvent) {
        interactionScheme.tell("Acabei de salvar o documento %s", documentCreatedEventEvent.getData().getName());
        String responseToFile = interactionScheme.ask("Quer que eu liste as entradas dele?");
        eventsProducerService.publishWantsToListEntries(new WantsToListEntriesEvent(responseToFile, documentCreatedEventEvent.getData()));

    }

}
