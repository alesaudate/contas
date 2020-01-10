package br.com.alesaudate.contas.events.listeners;


import br.com.alesaudate.contas.domain.EntriesRepository;
import br.com.alesaudate.contas.interfaces.InteractionScheme;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AverageSpendingListener extends InputCommandListener {


    InteractionScheme io;

    EntriesRepository entriesRepository;

    @Override
    protected boolean matchExpected(String message) {
        return isMessageClose(message, "Listar média de gastos");
    }

    @Override
    protected void process(String message) {

        //TODO listar média de gastos por categoria
        String categoria = io.ask("Em qual categoria?", io.asSet("Nenhuma", "Deixa pra lá", "Esquece"));
        if (categoria != null) {

        }
        getEventsProducerService().publishReadyForEvents();
    }

}
