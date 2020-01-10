package br.com.alesaudate.contas.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface EntriesRepository extends JpaRepository<Entry, Long> {

    List<Entry> findByCategory(Category category);

    List<Entry> findByDateAndItemNameAndAmount(Date date, String itemName, BigDecimal amount);
}
