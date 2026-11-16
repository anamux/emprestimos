package com.projeto.muralrecados.repository;

import com.projeto.muralrecados.model.Recado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecadosRepository extends JpaRepository<Recado, Long> {
}
