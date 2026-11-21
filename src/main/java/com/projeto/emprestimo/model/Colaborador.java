package com.projeto.emprestimo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Colaborador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório.")
    @Column(nullable = false)
    private String nome;

    @NotNull(message = "O email é obrigatorio.")
    @Column(nullable = false)
    private String email;

    @NotNull(message = "O cargo é obrigatorio.")
    @Column(nullable = false)
    private String cargo;
}
