package com.projeto.emprestimo.repository;

import com.projeto.emprestimo.model.Emprestimo;
import com.projeto.emprestimo.model.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    boolean existsByEquipamento(Long id);
}
