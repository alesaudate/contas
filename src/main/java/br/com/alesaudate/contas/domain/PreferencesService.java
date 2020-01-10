package br.com.alesaudate.contas.domain;


import br.com.alesaudate.contas.interfaces.InteractionScheme;
import br.com.alesaudate.contas.utils.Sets;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class PreferencesService {


    PreferencesRepository repository;

    InteractionScheme scheme;

    SimpleDateFormat simpleDateFormat;

    DocumentService documentService;


    @Transactional
    public void enrich(Entry entry) {

        if (entry.needsEnrichment()) {
            List<Preference> preferences = repository.findAll();
            for (Preference preference : preferences) {
                if (preference.tryToAdequate(entry)) {
                    return;
                };
            }
            fillMissingData(entry);
        }
        else {
            //TODO criar um jeito da entrada ser usada como preferência (em casos onde ela foi criada pelo arquivo, por exemplo)
        }
    }



    protected void fillMissingData(Entry entry) {

        //TODO quando a entrada é uma parcela, tentar localizar os dados das parcelas anteriores e replicar

        if (entry.needsEnrichment()) {
            scheme.tell("Não conseguí localizar dados para enriquecer a entrada %s , do dia %s e valor %s", entry.getItemName(), simpleDateFormat.format(entry.getDate()), entry.getAmount());
            String description = scheme.ask("Qual é a descrição deste item?", Sets.newSet("Não sei", "nao sei", ""));
            if (description != null) {
                entry.setDescription(description);

                String categoryName = scheme.ask("A qual categoria pertence esse item?");

                Category c = documentService.findOrCreateCategory(categoryName);

                entry.setCategory(c);

                boolean makeDefault = scheme.askBoolean("OK. Você quer que essa seja a resposta padrão para itens assim?", Sets.newSet("Sim", "Quero"));
                if (makeDefault) {
                    boolean makeApproximation = scheme.askBoolean("Certo. O texto a ser usado será exatamente assim?", null);
                    Preference newPreference = new Preference();
                    newPreference.setEntry(entry);
                    newPreference.setIsApproximate(!makeApproximation);
                    repository.save(newPreference);
                    scheme.tell("OK, sua preferência foi salva. Itens como esse serão salvos com a descrição \"%s\"", description);
                }
                else {
                    scheme.tell("Certo, vamos continuar.");
                }
            }
            else {
                scheme.tell("Certo, vamos pular este.");
            }
        }
    }

}
