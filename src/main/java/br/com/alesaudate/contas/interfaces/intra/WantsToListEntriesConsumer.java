package br.com.alesaudate.contas.interfaces.intra;

import br.com.alesaudate.contas.domain.Entry;
import br.com.alesaudate.contas.interfaces.InteractionScheme;
import br.com.alesaudate.contas.interfaces.intra.events.WantsToListEntriesEvent;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.bus.Event;
import reactor.fn.Consumer;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class WantsToListEntriesConsumer implements Consumer<Event<WantsToListEntriesEvent>> {

    private static final String ACCEPTED_ANSWERS[] = {"sim", "quero", "s"};


    SimpleDateFormat dateFormat;

    InteractionScheme interactionScheme;

    @Override
    public void accept(Event<WantsToListEntriesEvent> wantsToListEntriesEventEvent) {
        String response = wantsToListEntriesEventEvent.getData().getAnswer();
        boolean yes = Arrays.stream(ACCEPTED_ANSWERS).filter(r -> response.trim().equalsIgnoreCase(r)).findAny().isPresent();

        if (yes) {

            interactionScheme.tell("Ok, listando...");
            List<Entry> entryList = wantsToListEntriesEventEvent.getData().getDocument().getEntries();
            BigDecimal sum = entryList.stream().map(Entry::getNormalizedAmount).reduce(BigDecimal::add).get();

            entryList.stream().forEach(e -> {
                interactionScheme.tell("Data: %s Entrada: %s  Descrição: %s Valor: %s", dateFormat.format(e.getDate()), e.getItemName(), e.getDescription(), e.getAmount());
            });

            interactionScheme.tell("A soma dos valores é %s.", sum);

        }

    }




}
