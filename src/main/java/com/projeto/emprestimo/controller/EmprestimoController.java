package com.projeto.emprestimo.controller;

import com.projeto.emprestimo.dto.EmprestimoRequest;
import com.projeto.emprestimo.model.Colaborador;
import com.projeto.emprestimo.model.Emprestimo;
import com.projeto.emprestimo.model.Equipamento;
import com.projeto.emprestimo.model.TipoEquipamento;
import com.projeto.emprestimo.service.EmprestimoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.LogManager;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {
    private final EmprestimoService service;
    public EmprestimoController(EmprestimoService service){ this.service = service;}

    @GetMapping("/colaborador/{colaboradorId}")
    public List<Emprestimo> listarColaborador(Colaborador colaborador){
        return service.listarColaborador(colaborador.getId());
    }
    @GetMapping("/{id}")
    public Emprestimo buscar(@PathVariable Long id){
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Emprestimo emprestar(@RequestBody @Valid EmprestimoRequest pedido){
        return service.emprestar(pedido);
    }

    @PutMapping("/{id}/devolucao")
    public Emprestimo devolver(@PathVariable Long id){
        return service.devolver(id);
    }

    @GetMapping("/emprestimos/atrasados")
    public List<Emprestimo> listar(@RequestParam (required = false) Boolean atrasados){
        return service.listar(atrasados);
    }
}
