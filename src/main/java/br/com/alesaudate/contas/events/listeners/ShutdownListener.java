package br.com.alesaudate.contas.events.listeners;

import br.com.alesaudate.contas.interfaces.InteractionScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class ShutdownListener extends InputCommandListener {

    @Autowired
    ConfigurableApplicationContext context;

    @Autowired
    InteractionScheme io;

    @Override
    protected boolean matchExpected(String message) {
        return isMessageClose(message, "Desligar") || isMessageClose(message, "Desligar o sistema");
    }

    @Override
    protected void process(String message) {
        io.tell("Encerrando...");
        Future<?> callable = Executors.newFixedThreadPool(1).submit(() -> context.close());
        try {
            callable.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            io.tell("Forçando encerramento.");
            System.exit(-1);
        }
        io.tell("Contexto fechado.");
    }
}
