package br.com.alesaudate.contas.events.listeners;

import org.springframework.stereotype.Component;

@Component
public class ListEntriesListener extends InputCommandListener {



    @Override
    protected boolean matchExpected(String message) {
        return isMessageClose(message, "Listar entradas");
    }

    @Override
    protected void process(String message) {
        //TODO criar o comando de listar entradas
    }
}
