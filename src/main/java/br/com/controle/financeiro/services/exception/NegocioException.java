package br.com.controle.financeiro.services.exception;

public class NegocioException extends RuntimeException {

    public NegocioException() {
        super();
    }

    public NegocioException(String message) {
        super(message);
    }
}
