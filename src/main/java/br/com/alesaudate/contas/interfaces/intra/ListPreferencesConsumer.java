package br.com.alesaudate.contas.interfaces.intra;

import br.com.alesaudate.contas.domain.Entry;
import br.com.alesaudate.contas.domain.Preference;
import br.com.alesaudate.contas.domain.PreferencesRepository;
import br.com.alesaudate.contas.interfaces.InteractionScheme;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.bus.Event;
import reactor.fn.Consumer;

@Component
public class ListPreferencesConsumer implements Consumer<Event<String>> {


    @Autowired
    InteractionScheme io;

    @Autowired
    PreferencesRepository preferencesRepository;


    @Override
    public void accept(Event<String> stringEvent) {

        if (Commands.match(stringEvent.getData(), "Listar preferências")) {
            io.tell("OK, listando preferências atuais:");

            List<Preference> prefs = preferencesRepository.findAll();
            for (Preference preference :  prefs) {
                Entry entry = preference.getEntry();
                if (preference.getIsApproximate()) {
                    io.tell("Quando tiver texto similar a %s , aplica a descrição %s", entry.getItemName(), entry.getDescription());
                }
                else {
                    io.tell("Quando tiver texto igual a %s , aplica a descrição %s", entry.getItemName(), entry.getDescription());
                }
            }
        }
    }
}
