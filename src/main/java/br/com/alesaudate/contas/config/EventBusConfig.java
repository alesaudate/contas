package br.com.alesaudate.contas.config;


import br.com.alesaudate.contas.events.listeners.*;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import reactor.Environment;
import reactor.bus.EventBus;

import static reactor.bus.selector.Selectors.$;

@Configuration
@Setter(onMethod_ = @Autowired)
public class EventBusConfig implements ApplicationListener<ContextRefreshedEvent> {

    public static final String FILE_FOUND_EVENT = "fileFoundEvent";
    public static final String DOCUMENT_FOUND_EVENT = "documentFoundEvent";
    public static final String DOCUMENT_READY = "documentReady";
    public static final String READY_FOR_EVENTS = "readyForEvents";

    EventBus bus;

    ListPreferencesListener listPreferencesListener;

    NewFileDetectedMessageListener newFileDetectedMessageListener;

    NewDocumentDetectedListener newDocumentDetectedListener;

    SaveDocumentListener saveDocumentListener;

    AcceptCommandsListener acceptCommandsListener;

    ListEntriesListener listEntriesListener;



    @Bean
    public Environment env() {
        return Environment.initializeIfEmpty().assignErrorJournal();
    }

    @Bean
    EventBus createEventBus(Environment env) {
        return EventBus.create(env, Environment.THREAD_POOL);
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        bus.on($("."), listPreferencesListener);
        bus.on($("."), listEntriesListener);
        bus.on($(FILE_FOUND_EVENT), newFileDetectedMessageListener);
        bus.on($(DOCUMENT_FOUND_EVENT), newDocumentDetectedListener);
        bus.on($(DOCUMENT_READY),saveDocumentListener);
        bus.on($(READY_FOR_EVENTS), acceptCommandsListener);



    }
}
