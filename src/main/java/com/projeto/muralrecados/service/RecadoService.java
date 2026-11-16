package com.projeto.muralrecados.service;

import com.projeto.muralrecados.model.Recado;
import com.projeto.muralrecados.repository.RecadosRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecadoService {
    private final RecadosRepository repository;

    public RecadoService(RecadosRepository repository){
        this.repository = repository;
    }

    public List<Recado>listar(){
        return repository.findAll();
    }
    public Recado cadastrar (Recado recado){
        return repository.save(recado);
    }
}
