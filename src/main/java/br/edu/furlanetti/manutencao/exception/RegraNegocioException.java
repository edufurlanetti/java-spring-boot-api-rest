package br.edu.furlanetti.manutencao.exception;

/** Violação de regra de negócio: vira HTTP 422 (Unprocessable Entity). */
public class RegraNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
