package br.com.alesaudate.contas.domain;


import br.com.alesaudate.contas.domain.exceptions.CategoryNotFoundException;

import java.util.List;
import java.util.Optional;

import br.com.alesaudate.contas.domain.exceptions.EntryNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DocumentService {



    EntriesRepository entriesRepository;

    CategoryRepository categoryRepository;

    @Transactional
    public void saveDocumentData(Document document) {

        document.getEntries().stream().forEach(e -> {
            if (e.getCategory() != null && e.getCategory().getId() != null) {
                e.setCategory(categoryRepository.getOne(e.getCategory().getId()));
            }
        });

        List<Entry> entries = entriesRepository.saveAll(document.getEntries());

        document.setEntries(entries);
    }


    public Entry getFirstInstallmentForEntry(Entry entry) throws EntryNotFoundException{
        if (Optional.ofNullable(entry.getTotalNumberOfInstallments()).orElse(1) == 1) {
            return entry;
        }
        else if (Optional.ofNullable(entry.getInstallmentNumber()).orElse(1) == 1) {
            return entry;
        }

        List<Entry> entries = entriesRepository.findByDateAndItemNameAndAmount(entry.getDate(), entry.getItemName(), entry.getAmount());
        return entries.stream().filter(e -> Optional.ofNullable(e.getInstallmentNumber()).orElse(1).equals(1)).findAny().orElseThrow(() -> new EntryNotFoundException());
    }

    public List<Entry> findEntriesByCategoryName(String categoryName) throws CategoryNotFoundException {
        Category category = categoryRepository.findByName(categoryName).orElseThrow(() -> new CategoryNotFoundException(categoryName));
        return entriesRepository.findByCategory(category);
    }

    public boolean isEntryAlreadyIn(Entry entry) {

        List<Entry> entries = entriesRepository.findAll();

        return entriesRepository.findByDateAndItemNameAndAmount(entry.getDate(), entry.getItemName(), entry.getAmount())
                .stream()
                .filter(e -> Optional.ofNullable(e.getInstallmentNumber()).orElse(1).equals(Optional.ofNullable(entry.getInstallmentNumber()).orElse(1)))
                .findAny()
                .isPresent();

    }


    @Transactional
    public Category findOrCreateCategory(final String categoryName) {
        final String catName = categoryName.trim().toLowerCase();


        List<Category> categories = categoryRepository.findAll();
        LevenshteinDistance levenshteinDistance = LevenshteinDistance.getDefaultInstance();

        Optional<Category> optionalCategory = categories.stream()
                .filter(category -> levenshteinDistance.apply(category.getName().trim().toLowerCase(), catName) <= 2)
                .findAny();

        if (optionalCategory.isPresent()) {
            return optionalCategory.get();
        }
        return categoryRepository.saveAndFlush(new Category(categoryName));

    }
}
