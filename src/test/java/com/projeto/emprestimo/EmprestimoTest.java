package com.projeto.emprestimo;

import com.projeto.emprestimo.model.Emprestimo;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertTrue;

public class EmprestimoTest {
    @Test
    void emprestimoVencidoENaoDevolvidoEstaAtrasado(){
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().minusDays(1));

        assertTrue(emprestimo.isAtrasado());
    }
}
