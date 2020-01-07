package br.com.alesaudate.contas.domain;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Entry {


    @Id
    @GeneratedValue
    Long id;

    Date date;


    @ManyToOne(cascade = CascadeType.PERSIST)
    Category category;


    @Enumerated(EnumType.STRING)
    EntryType entryType;

    String itemName;
    BigDecimal amount;
    String description;
    Integer installmentNumber;
    Integer totalNumberOfInstallments;


    public boolean needsEnrichment() {
        return description == null || category == null;
    }


    public BigDecimal getNormalizedAmount() {
        return entryType.getNormalizedAmount(amount);
    }

}
