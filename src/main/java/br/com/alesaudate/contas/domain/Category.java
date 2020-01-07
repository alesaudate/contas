package br.com.alesaudate.contas.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Category {



    @Id
    @GeneratedValue
    Long id;


    @Column(unique = true)
    String name;


    public Category(String name) {
        this.name = name;
    }

}
