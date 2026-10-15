package br.edu.furlanetti.manutencao.model;

import java.util.Set;

/**
 * Ciclo de vida da ordem de serviço (máquina de estados):
 *
 * <pre>
 *   ABERTA ──iniciar──► EM_ANDAMENTO ──concluir──► CONCLUIDA
 *     │                      │
 *     └──────cancelar────────┴────────► CANCELADA
 * </pre>
 */
public enum StatusOrdem {
    ABERTA,
    EM_ANDAMENTO,
    CONCLUIDA,
    CANCELADA;

    public boolean podeIrPara(StatusOrdem destino) {
        return switch (this) {
            case ABERTA -> Set.of(EM_ANDAMENTO, CANCELADA).contains(destino);
            case EM_ANDAMENTO -> Set.of(CONCLUIDA, CANCELADA).contains(destino);
            case CONCLUIDA, CANCELADA -> false;
        };
    }
}
