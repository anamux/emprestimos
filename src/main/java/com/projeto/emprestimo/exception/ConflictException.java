package com.projeto.emprestimo.exception;

public class ConflictException extends RuntimeException{
    public ConflictException (String mensagem){
        super(mensagem);
    }
}
