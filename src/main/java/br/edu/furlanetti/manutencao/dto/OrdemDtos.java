package br.edu.furlanetti.manutencao.dto;

import br.edu.furlanetti.manutencao.model.OrdemServico;
import br.edu.furlanetti.manutencao.model.Prioridade;
import br.edu.furlanetti.manutencao.model.StatusOrdem;
import br.edu.furlanetti.manutencao.model.TipoManutencao;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/** DTOs das ordens de serviço. */
public final class OrdemDtos {

    private OrdemDtos() {
    }

    public record AbrirOrdemRequest(
            @NotNull Long equipamentoId,
            @NotBlank @Size(max = 500) String descricao,
            @NotNull TipoManutencao tipo,
            @NotNull Prioridade prioridade) {
    }

    public record IniciarRequest(@NotBlank @Size(max = 100) String tecnico) {
    }

    public record ConcluirRequest(
            @NotBlank @Size(max = 450) String solucao,
            @NotNull @Positive @Digits(integer = 4, fraction = 2) BigDecimal horasTrabalhadas) {
    }

    public record CancelarRequest(@NotBlank @Size(max = 400) String motivo) {
    }

    public record OrdemResponse(
            Long id,
            String equipamentoCodigo,
            String equipamentoNome,
            String descricao,
            TipoManutencao tipo,
            Prioridade prioridade,
            StatusOrdem status,
            String tecnico,
            String solucao,
            BigDecimal horasTrabalhadas,
            LocalDateTime abertaEm,
            LocalDateTime iniciadaEm,
            LocalDateTime encerradaEm) {

        public static OrdemResponse de(OrdemServico o) {
            return new OrdemResponse(
                    o.getId(),
                    o.getEquipamento().getCodigo(),
                    o.getEquipamento().getNome(),
                    o.getDescricao(),
                    o.getTipo(),
                    o.getPrioridade(),
                    o.getStatus(),
                    o.getTecnico(),
                    o.getSolucao(),
                    o.getHorasTrabalhadas(),
                    o.getAbertaEm(),
                    o.getIniciadaEm(),
                    o.getEncerradaEm());
        }
    }

    public record IndicadorEquipamento(String equipamento, long ordensConcluidas, double mediaHoras) {
    }

    public record IndicadoresResponse(Map<StatusOrdem, Long> ordensPorStatus,
                                      List<IndicadorEquipamento> horasPorEquipamento) {
    }
}
