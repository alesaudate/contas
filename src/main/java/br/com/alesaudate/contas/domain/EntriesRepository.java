package br.com.alesaudate.contas.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EntriesRepository extends JpaRepository<Entry, Long> {
}
