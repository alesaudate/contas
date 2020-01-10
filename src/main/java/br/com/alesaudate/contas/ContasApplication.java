package br.com.alesaudate.contas;

import br.com.alesaudate.contas.events.EventsProducerService;
import br.com.alesaudate.contas.interfaces.InteractionScheme;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.bus.Event;
import reactor.bus.EventBus;

@SpringBootApplication
@Configuration
public class ContasApplication {



    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(ContasApplication.class, args);
        EventsProducerService eventsProducerService = ctx.getBean(EventsProducerService.class);
        eventsProducerService.publishReadyForEvents();
    }
}
