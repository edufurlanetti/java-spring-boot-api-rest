package br.edu.furlanetti.manutencao.service;

import br.edu.furlanetti.manutencao.exception.RegraNegocioException;
import br.edu.furlanetti.manutencao.model.Equipamento;
import br.edu.furlanetti.manutencao.model.OrdemServico;
import br.edu.furlanetti.manutencao.model.Prioridade;
import br.edu.furlanetti.manutencao.model.StatusOrdem;
import br.edu.furlanetti.manutencao.model.TipoManutencao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Testes de unidade do domínio: sem Spring e sem banco, executam em milissegundos. */
class OrdemServicoTest {

    private Equipamento torno;

    @BeforeEach
    void preparar() {
        torno = new Equipamento("TORNO-01", "Torno CNC", "Usinagem", "Romi");
    }

    private OrdemServico novaOrdem() {
        return new OrdemServico(torno, "Vibração no cabeçote", TipoManutencao.CORRETIVA, Prioridade.ALTA);
    }

    @Test
    @DisplayName("Ordem nasce ABERTA")
    void nasceAberta() {
        assertEquals(StatusOrdem.ABERTA, novaOrdem().getStatus());
    }

    @Test
    @DisplayName("Fluxo feliz: ABERTA → EM_ANDAMENTO → CONCLUIDA")
    void fluxoCompleto() {
        OrdemServico ordem = novaOrdem();

        ordem.iniciar("Marcos");
        ordem.concluir("Troca de rolamentos", new BigDecimal("3.5"));

        assertEquals(StatusOrdem.CONCLUIDA, ordem.getStatus());
        assertEquals("Marcos", ordem.getTecnico());
        assertNotNull(ordem.getIniciadaEm());
        assertNotNull(ordem.getEncerradaEm());
    }

    @Test
    @DisplayName("Não é possível concluir uma ordem que não foi iniciada")
    void concluirSemIniciar() {
        OrdemServico ordem = novaOrdem();

        assertThrows(RegraNegocioException.class, () -> ordem.concluir("x", BigDecimal.ONE));
    }

    @Test
    @DisplayName("Ordem concluída não pode ser cancelada")
    void cancelarConcluida() {
        OrdemServico ordem = novaOrdem();
        ordem.iniciar("Marcos");
        ordem.concluir("ok", BigDecimal.ONE);

        assertThrows(RegraNegocioException.class, () -> ordem.cancelar("desistência"));
    }

    @Test
    @DisplayName("Não se abre ordem para equipamento inativo")
    void equipamentoInativo() {
        torno.inativar();

        assertThrows(RegraNegocioException.class, this::novaOrdem);
    }
}
