package br.edu.furlanetti.manutencao.exception;

/** Recurso inexistente: vira HTTP 404 (Not Found). */
public class RecursoNaoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(recurso + " com id " + id + " não encontrado(a)");
    }
}
