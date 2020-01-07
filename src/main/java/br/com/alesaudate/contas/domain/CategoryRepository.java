package br.com.alesaudate.contas.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {



    public Optional<Category> findByName(String category);


}
