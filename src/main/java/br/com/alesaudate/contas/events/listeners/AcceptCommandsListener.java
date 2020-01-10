package br.com.alesaudate.contas.events.listeners;

import br.com.alesaudate.contas.interfaces.InteractionScheme;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AcceptCommandsListener extends GenericMessageListener<Object> {


    @Autowired
    public AcceptCommandsListener(InteractionScheme io) {
        super(false);
        this.io = io;
    }

    InteractionScheme io;

    @Override
    protected void doAccept(Object data) throws Exception {


        String command = io.ask("Aguardando novas instruções...");

        getEventsProducerService().publishInputCommand(command);

    }

}
