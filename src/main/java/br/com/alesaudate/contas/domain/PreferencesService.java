package br.com.alesaudate.contas.domain;


import br.com.alesaudate.contas.interfaces.InteractionScheme;
import br.com.alesaudate.contas.utils.Sets;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor(onConstructor_ = @Autowired)
public class PreferencesService {


    PreferencesRepository repository;

    CategoryRepository categoryRepository;

    InteractionScheme io;

    SimpleDateFormat simpleDateFormat;

    DocumentService documentService;


    @Transactional
    public void autoCreatePreferences(List<Entry> entries) {

        for (Entry entry : entries) {
            Preference preference = findSuitablePreference(entry);
            if (preference == null && entry.getCategory() != null) {
                preference = new Preference();
                preference.setItemName(entry.getItemName());
                preference.setIsApproximate(true);
                preference.setCategory(documentService.findOrCreateCategory(entry.getCategoryName()));
                repository.save(preference);
            }
        }
    }


    @Transactional
    public void enrich(Entry entry) {

        Preference preference = findSuitablePreference(entry);
        if (entry.needsEnrichment()) {
            if (preference != null) {
                preference.apply(entry);
            }
        }

        boolean preferenceSaved = fillMissingData(entry);


        if (preference == null && !preferenceSaved) {
            boolean useAsPreference = io.askBoolean("Podemos aproveitar os dados do item %s (%s) (Categoria: %s) como preferência em algo?", Sets.newSet("pode", "pode sim", "sim"), entry.getItemName(), entry.getDescription(), entry.getCategoryName());
            if (useAsPreference) {
                String description = null;
                Category category = categoryRepository.getOne(entry.getCategory().getId());


                boolean definite = io.askBoolean("O item sempre terá um descritivo assim (com o texto igualzinho a %s)?", Sets.newSet("sim", "sempre", "vai"), entry.getItemName());
                boolean useDescription = io.askBoolean("Vamos usar o descritivo (%s) como um padrão para esse tipo de item?", Sets.newSet("sim", "vamos", "vamos sim"), entry.getDescription());
                boolean useCategory = io.askBoolean("Vamos usar a categoria (%s) como um padrão para esse tipo de item?", Sets.newSet("sim", "vamos"), entry.getCategoryName());

                if (useDescription) {
                    if (StringUtils.isNotBlank(entry.getDescription())) {
                        description = entry.getDescription();
                    }
                }

                entry.setCategory(category);
                Preference newPref = new Preference();

                newPref.setItemName(entry.getItemName());
                if (useCategory) {
                    newPref.setCategory(category);
                }
                newPref.setDescription(description);
                newPref.setIsApproximate(!definite);
                repository.saveAndFlush(newPref);
            }
        }

    }


    protected Preference findSuitablePreference(Entry entry) {
        List<Preference> preferences = repository.findAll();
        for (Preference preference : preferences) {
            if (preference.isSuitable(entry)) {
                return preference;
            }
        }
        return null;
    }


    protected boolean fillMissingData(Entry entry) {

        //TODO quando a entrada é uma parcela, tentar localizar os dados das parcelas anteriores e replicar

        if (entry.needsEnrichment()) {
            io.tell("Não conseguí localizar dados para enriquecer a entrada %s , do dia %s e valor %s", entry.getItemName(), simpleDateFormat.format(entry.getDate()), entry.getAmount());

            if (StringUtils.isBlank(entry.getDescription())) {
                String description = io.ask("Qual é a descrição deste item?", Sets.newSet("Não sei", "nao sei", ""));
                entry.setDescription(description);
            }

            if (entry.getCategory() == null) {
                String categoryName = io.ask("A qual categoria pertence esse item?");
                Category c = documentService.findOrCreateCategory(categoryName);
                entry.setCategory(c);
            }


            boolean makeDefault = io.askBoolean("OK. Você quer que essa seja a resposta padrão para itens assim?", Sets.newSet("Sim", "Quero"));
            if (makeDefault) {
                boolean makeApproximation = io.askBoolean("Certo. O texto a ser usado será exatamente assim (%s)?", Sets.newSet("Vai"), entry.getItemName());
                Preference newPreference = new Preference();
                newPreference.setDescription(entry.getDescription());
                newPreference.setItemName(entry.getItemName());
                newPreference.setCategory(entry.getCategory());
                newPreference.setIsApproximate(makeApproximation);
                repository.save(newPreference);
                io.tell("OK, sua preferência foi salva. Vamos continuar.");
                return true;
            } else {
                io.tell("Certo, vamos continuar.");
            }
        }
        return false;
    }

}
