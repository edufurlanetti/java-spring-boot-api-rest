package br.edu.furlanetti.manutencao.dto;

import br.edu.furlanetti.manutencao.model.Equipamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTOs (Data Transfer Objects) de equipamento, escritos como records.
 * Separar DTO de entidade evita expor o modelo do banco na API e
 * impede que o cliente altere campos que não deveria (ex.: id, ativo).
 */
public final class EquipamentoDtos {

    private EquipamentoDtos() {
    }

    public record EquipamentoRequest(
            @NotBlank @Size(max = 20) String codigo,
            @NotBlank @Size(max = 100) String nome,
            @NotBlank @Size(max = 60) String setor,
            @Size(max = 60) String fabricante) {
    }

    public record EquipamentoResponse(
            Long id, String codigo, String nome, String setor, String fabricante, boolean ativo) {

        public static EquipamentoResponse de(Equipamento e) {
            return new EquipamentoResponse(
                    e.getId(), e.getCodigo(), e.getNome(), e.getSetor(), e.getFabricante(), e.isAtivo());
        }
    }
}
