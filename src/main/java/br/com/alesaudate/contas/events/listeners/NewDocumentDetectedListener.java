package br.com.alesaudate.contas.events.listeners;

import br.com.alesaudate.contas.domain.Document;
import br.com.alesaudate.contas.domain.DocumentService;
import br.com.alesaudate.contas.domain.Entry;
import br.com.alesaudate.contas.domain.PreferencesService;
import br.com.alesaudate.contas.events.EventsProducerService;
import br.com.alesaudate.contas.events.SystemLock;
import br.com.alesaudate.contas.events.definitions.DocumentFileEvent;
import br.com.alesaudate.contas.interfaces.InteractionScheme;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class NewDocumentDetectedListener extends GenericMessageListener<DocumentFileEvent> {

    InteractionScheme io;

    PreferencesService preferencesService;

    DocumentService documentService;

    @Override
    protected void doAccept(DocumentFileEvent event) throws Exception {
        Document data = event.getDocument();
        cleanDocument(data);

        boolean review = io.askBoolean("Novo documento detectado. Você quer revisar as entradas?", io.asSet("Sim", "Quero"));
        if (review) {
            List<Entry> entries = data.getEntries();
            for (int i = 0; i < entries.size(); i++) {
                io.tell("%d) %s => Valor: %s Descrição: %s Categoria: %s", (i+1), entries.get(i).getItemName(), entries.get(i).getAmount(), entries.get(i).getDescription(), entries.get(i).getCategoryName());
            }
            String numbers = io.ask("Tem alguma que você deseja remover? Se sim, coloque os números separados por vírgula. Se não, apenas prossiga.");
            numbers = numbers.replace(" ", "");
            Set<Integer> numbersSet = Arrays.stream(numbers.split(",")).map(this::mapInt).filter(Objects::nonNull).sorted(Comparator.reverseOrder()).collect(Collectors.toSet());
            for (Integer n : numbersSet) {
                entries.remove(n - 1);
            }
        }
        io.tell("Certo. Prosseguindo agora para enriquecimento de dados.");
        data.getEntries().stream().forEach(preferencesService::enrich);
        getEventsProducerService().publishDocumentReady(event);
    }



    private void cleanDocument(Document document) {
        List<Entry> entries = document.getEntries();
        Iterator<Entry> it = entries.iterator();
        while (it.hasNext()) {
            Entry entry = it.next();
            if (documentService.isEntryAlreadyIn(entry)) {
                it.remove();
            }
            else if (entry.getCategory() != null) {
                entry.setCategory(documentService.findOrCreateCategory(entry.getCategoryName()));
            }
        }
    }

    private Integer mapInt(String number) {
        try {
            return Integer.valueOf(number);
        }
        catch (Exception e) {
            return null;
        }
    }

}
