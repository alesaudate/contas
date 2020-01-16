package br.com.alesaudate.contas.domain;


import javax.persistence.*;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.Optional;

@Entity
@Data
public class Preference {


    @Id
    @GeneratedValue
    Long id;

    String itemName;


    String description;


    @ManyToOne
    Category category;


    Boolean isApproximate = Boolean.FALSE;

    public boolean isSuitable(Entry entry) {
        if (isApproximate) {
            int results = LevenshteinDistance.getDefaultInstance().apply(entry.getItemName(), itemName);
            int avgLength = (entry.getItemName().length() + itemName.length()) / 2;

            if ((results) < avgLength/3) {
                return true;
            }
        }
        else {
            if (itemName.equalsIgnoreCase(entry.getItemName())) {
                return true;
            }
        }
        return false;
    }

    public void apply(Entry entry) {
        Optional.ofNullable(description).ifPresent(entry::setDescription);
        Optional.ofNullable(category).ifPresent(entry::setCategory);
    }

}
