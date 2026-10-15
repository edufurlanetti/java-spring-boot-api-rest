package br.edu.furlanetti.manutencao.model;

import br.edu.furlanetti.manutencao.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade rica: as regras de transição de status ficam no próprio domínio,
 * e não espalhadas pelo controller ou pelo service.
 */
@Entity
@Table(name = "ordem_servico")
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipamento_id")
    private Equipamento equipamento;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private TipoManutencao tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Prioridade prioridade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusOrdem status = StatusOrdem.ABERTA;

    @Column(length = 100)
    private String tecnico;

    @Column(length = 500)
    private String solucao;

    @Column(precision = 6, scale = 2)
    private BigDecimal horasTrabalhadas;

    @Column(nullable = false)
    private LocalDateTime abertaEm = LocalDateTime.now();

    private LocalDateTime iniciadaEm;

    private LocalDateTime encerradaEm;

    protected OrdemServico() {
        // exigido pela JPA
    }

    public OrdemServico(Equipamento equipamento, String descricao, TipoManutencao tipo, Prioridade prioridade) {
        if (!equipamento.isAtivo()) {
            throw new RegraNegocioException("Não é possível abrir ordem para equipamento inativo");
        }
        this.equipamento = equipamento;
        this.descricao = descricao;
        this.tipo = tipo;
        this.prioridade = prioridade;
    }

    public void iniciar(String tecnico) {
        mudarStatus(StatusOrdem.EM_ANDAMENTO);
        this.tecnico = tecnico;
        this.iniciadaEm = LocalDateTime.now();
    }

    public void concluir(String solucao, BigDecimal horasTrabalhadas) {
        mudarStatus(StatusOrdem.CONCLUIDA);
        this.solucao = solucao;
        this.horasTrabalhadas = horasTrabalhadas;
        this.encerradaEm = LocalDateTime.now();
    }

    public void cancelar(String motivo) {
        mudarStatus(StatusOrdem.CANCELADA);
        this.solucao = "Cancelada: " + motivo;
        this.encerradaEm = LocalDateTime.now();
    }

    private void mudarStatus(StatusOrdem destino) {
        if (!status.podeIrPara(destino)) {
            throw new RegraNegocioException(
                    "Transição inválida: ordem " + status + " não pode passar para " + destino);
        }
        this.status = destino;
    }

    public Long getId() {
        return id;
    }

    public Equipamento getEquipamento() {
        return equipamento;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoManutencao getTipo() {
        return tipo;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public StatusOrdem getStatus() {
        return status;
    }

    public String getTecnico() {
        return tecnico;
    }

    public String getSolucao() {
        return solucao;
    }

    public BigDecimal getHorasTrabalhadas() {
        return horasTrabalhadas;
    }

    public LocalDateTime getAbertaEm() {
        return abertaEm;
    }

    public LocalDateTime getIniciadaEm() {
        return iniciadaEm;
    }

    public LocalDateTime getEncerradaEm() {
        return encerradaEm;
    }
}
