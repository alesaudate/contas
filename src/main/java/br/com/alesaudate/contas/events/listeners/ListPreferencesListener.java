package br.com.alesaudate.contas.events.listeners;

import br.com.alesaudate.contas.domain.Entry;
import br.com.alesaudate.contas.domain.Preference;
import br.com.alesaudate.contas.domain.PreferencesRepository;
import br.com.alesaudate.contas.interfaces.InteractionScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListPreferencesListener extends InputCommandListener {

    @Autowired
    InteractionScheme io;

    @Autowired
    PreferencesRepository preferencesRepository;

    @Override
    protected boolean matchExpected(String message) {
        return isMessageClose(message, "Listar preferências");
    }

    @Override
    protected void process(String message) {
        List<Preference> preferences = preferencesRepository.findAll();
        if (preferences.isEmpty()) {
            io.tell("Nenhuma preferência encontrada");
        }
        else {
            preferences.stream().map(this::formatPreference).forEach(io::tell);
        }
        getEventsProducerService().publishReadyForEvents();
    }


    private String formatPreference(Preference preference) {
        Entry entry = preference.getEntry();
        if (preference.getIsApproximate()) {
            return String.format("Quando a entrada for similar a %s , aplicar a descrição %s e a categoria %s", entry.getItemName(), entry.getDescription(), entry.getCategoryName());
        }
        else {
            return String.format("Quando a entrada tiver o texto igual a %s, aplicar a descrição %s e a categoria %s", entry.getItemName(), entry.getDescription(), entry.getCategoryName());
        }
    }

}
