package br.com.alesaudate.contas.domain;


import br.com.alesaudate.contas.domain.exceptions.CategoryNotFoundException;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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


    public List<Entry> findEntriesByCategoryName(String categoryName) throws CategoryNotFoundException {
        Category category = categoryRepository.findByName(categoryName).orElseThrow(() -> new CategoryNotFoundException(categoryName));
        return entriesRepository.findByCategory(category);
    }

    public boolean isEntryAlreadyIn(Entry entry) {
        return !entriesRepository.findByDateAndItemNameAndAmount(entry.getDate(), entry.getItemName(), entry.getAmount()).isEmpty();
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
