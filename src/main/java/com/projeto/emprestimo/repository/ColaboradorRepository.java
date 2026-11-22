package com.projeto.emprestimo.repository;

import com.projeto.emprestimo.model.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ColaboradorRepository extends JpaRepository<Colaborador, Long> {
    List<Colaborador> findAll();

    boolean existsByEmail(String email);
}
