package br.com.alesaudate.contas.domain;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Period {


    @Id
    @GeneratedValue
    Long id;

    @OneToMany
    List<Document> documents;

}
