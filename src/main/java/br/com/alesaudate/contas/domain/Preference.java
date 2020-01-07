package br.com.alesaudate.contas.domain;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

@Entity
@Data
public class Preference {


    @Id
    @GeneratedValue
    Long id;

    @OneToOne(cascade = CascadeType.PERSIST)
    Entry entry;


    Boolean isApproximate = Boolean.FALSE;

    public boolean tryToAdequate(Entry entry) {
        if (isApproximate) {
            int results = LevenshteinDistance.getDefaultInstance().apply(entry.getItemName(), getEntry().getItemName());
            int avgLength = (entry.getItemName().length() + getEntry().getItemName().length()) / 2;

            if ((results) < avgLength/3) {
                entry.setDescription(getEntry().getDescription());
                entry.setCategory(getEntry().getCategory());
                return true;
            }
        }
        else {
            if (getEntry().getItemName().equalsIgnoreCase(entry.getItemName())) {
                entry.setDescription(getEntry().getDescription());
                return true;
            }
        }
        return false;
    }

}
