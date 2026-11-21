package com.projeto.emprestimo.service;

import com.projeto.emprestimo.exception.ConflictException;
import com.projeto.emprestimo.exception.NaoEncontradoException;
import com.projeto.emprestimo.model.Colaborador;
import com.projeto.emprestimo.repository.ColaboradorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColaboradorService {
    private final ColaboradorRepository repository;
    public ColaboradorService(ColaboradorRepository repository){
        this.repository = repository;
    }
    public List<Colaborador> listar(){
        return repository.findAll();
    }
    public Colaborador buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new NaoEncontradoException("Colaborador não encontrado"));
    }
    public Colaborador cadastrar (Colaborador colaborador){
        if (repository.existsByEmail(colaborador.getEmail())){
            throw new ConflictException("Email não cadastrado.");
        }
        return repository.save(colaborador);
    }
}
