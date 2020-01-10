package br.com.alesaudate.contas.events.listeners;

import br.com.alesaudate.contas.domain.Document;
import br.com.alesaudate.contas.domain.Entry;
import br.com.alesaudate.contas.interfaces.InteractionScheme;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WantsToListEntriesListener extends GenericMessageListener<Document>{

    private static final String ACCEPTED_ANSWERS[] = {"sim", "quero", "s"};

    InteractionScheme io;

    SimpleDateFormat dateFormat;

    @Override
    public void doAccept(Document document) throws Exception {

        String response = io.ask("Documento lido com sucesso. Quer rever as entradas dele?");
        boolean yes = Arrays.stream(ACCEPTED_ANSWERS).filter(r -> response.trim().equalsIgnoreCase(r)).findAny().isPresent();

        if (yes) {

            io.tell("Ok, listando...");
            List<Entry> entryList = document.getEntries();
            BigDecimal sum = entryList.stream().map(Entry::getNormalizedAmount).reduce(BigDecimal::add).get();

            entryList.stream().forEach(e -> {
                io.tell("Data: %s Entrada: %s  Descrição: %s Valor: %s", dateFormat.format(e.getDate()), e.getItemName(), e.getDescription(), e.getAmount());
            });

            io.tell("A soma dos valores é %s.", sum);

        }

    }

}
