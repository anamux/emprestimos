package com.projeto.emprestimo.repository;

import com.projeto.emprestimo.model.Colaborador;
import com.projeto.emprestimo.model.Emprestimo;
import com.projeto.emprestimo.model.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    boolean existsByEquipamento(Long id);

    List<Emprestimo> findByColaborador(Colaborador colaborador);
    List<Emprestimo> findByDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(LocalDate data);
}
