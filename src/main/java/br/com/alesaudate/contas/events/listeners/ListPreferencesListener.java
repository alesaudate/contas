package br.com.alesaudate.contas.events.listeners;

import br.com.alesaudate.contas.domain.Category;
import br.com.alesaudate.contas.domain.Entry;
import br.com.alesaudate.contas.domain.Preference;
import br.com.alesaudate.contas.domain.PreferencesRepository;
import br.com.alesaudate.contas.interfaces.InteractionScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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
        String itemName = preference.getItemName();
        String description = preference.getDescription();
        String categoryName = Optional.ofNullable(preference.getCategory()).orElse(new Category()).getName();
        if (preference.getIsApproximate()) {
            return String.format("Quando a entrada for similar a %s , aplicar a descrição %s e a categoria %s", itemName, description, categoryName);
        }
        else {
            return String.format("Quando a entrada tiver o texto igual a %s, aplicar a descrição %s e a categoria %s", itemName, description, categoryName);
        }
    }

}
