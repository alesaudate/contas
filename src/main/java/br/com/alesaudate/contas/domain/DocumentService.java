package br.com.alesaudate.contas.domain;


import br.com.alesaudate.contas.interfaces.InteractionScheme;
import br.com.alesaudate.contas.interfaces.intra.EventsProducerService;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DocumentService {




    DocumentRepository documentRepository;

    PreferencesService preferencesService;

    EventsProducerService eventsProducerService;

    EntriesRepository entriesRepository;

    SimpleDateFormat dateFormat;

    InteractionScheme interactionScheme;

    @Transactional
    public void saveDocument(Document document) {

        interactionScheme.tell("Acabei de detectar %d novas entradas: ", document.getEntries().size());
        for (int i = 0; i < document.getEntries().size(); i++) {
            interactionScheme.tell("%d ) %s", i, document.getEntries().get(i).getItemName());
        }
        String whichToRemove = interactionScheme.ask("Tem algum que você queira apagar? Se sim, digite os números separados por vírgula; se não, apenas siga em frente");
        List<Entry> entries = removeEntries(document.getEntries(), whichToRemove);
        document.setEntries(entries);

        document.getEntries().stream().forEach(preferencesService::enrich);
        entriesRepository.saveAll(document.getEntries());
        documentRepository.save(document);
        eventsProducerService.publishDocumentCreated(document);
    }


    private List<Entry> removeEntries(List<Entry> entries, String whichToDelete) {
        if (StringUtils.isBlank(whichToDelete)) {
            return entries;
        }


        try {
            Arrays.stream(whichToDelete.split(","))
                    .map(String::trim)
                    .map(Integer::valueOf)
                    .distinct()
                    .sorted(Comparator.reverseOrder())
                    .forEach(i -> entries.remove(i.intValue()));
            return entries;
        }
        catch (Exception e) {
            return entries;
        }


    }



}
