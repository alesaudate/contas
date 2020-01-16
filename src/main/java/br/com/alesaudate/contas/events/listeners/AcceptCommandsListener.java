package br.com.alesaudate.contas.events.listeners;

import br.com.alesaudate.contas.interfaces.InteractionScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import reactor.bus.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AcceptCommandsListener extends GenericMessageListener<Object> implements ApplicationListener<ContextRefreshedEvent> {

    private List<InputCommandListener> commandListeners;

    @Autowired
    public AcceptCommandsListener(InteractionScheme io) {
        super(false);
        this.io = io;
        this.commandListeners = new ArrayList<>();
    }

    InteractionScheme io;

    @Override
    protected void doAccept(Object data) throws Exception {


        String command = io.ask("Aguardando novas instruções...");

        commandListeners.stream()
                .filter(listener -> listener.matchExpected(command))
                .findAny()
                .ifPresent(listener -> listener.doAccept(command));

        getEventsProducerService().publishInputCommand(command);

    }


    @Override
    public String listenToEvent() {
        return "readyForEvents";
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<String, InputCommandListener> commandListenerMap = event.getApplicationContext().getBeansOfType(InputCommandListener.class);
        this.commandListeners = commandListenerMap.values().stream().collect(Collectors.toList());

    }
}
