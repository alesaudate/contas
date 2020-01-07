package br.com.alesaudate.contas.config;


import static reactor.bus.selector.Selectors.$;

import br.com.alesaudate.contas.interfaces.intra.DocumentCreatedConsumer;
import br.com.alesaudate.contas.interfaces.intra.ListPreferencesConsumer;
import br.com.alesaudate.contas.interfaces.intra.WantsToListEntriesConsumer;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import reactor.Environment;
import reactor.bus.EventBus;

@Configuration
@Setter(onMethod_ = @Autowired)
public class EventBusConfig implements ApplicationListener<ContextRefreshedEvent> {

    public static final String DOCUMENT_CREATED = "documentCreated";
    public static final String WANTS_TO_LIST_ENTRIES = "wantsToListEntries";


    EventBus bus;

    DocumentCreatedConsumer documentCreatedConsumer;

    WantsToListEntriesConsumer wantsToListEntriesConsumer;

    ListPreferencesConsumer listPreferencesConsumer;


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

        bus.on($(DOCUMENT_CREATED), documentCreatedConsumer);
        bus.on($(WANTS_TO_LIST_ENTRIES), wantsToListEntriesConsumer);
        bus.on($("."), listPreferencesConsumer);

    }
}
