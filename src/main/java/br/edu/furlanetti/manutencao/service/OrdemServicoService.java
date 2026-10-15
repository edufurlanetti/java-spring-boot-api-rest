package br.edu.furlanetti.manutencao.service;

import br.edu.furlanetti.manutencao.dto.OrdemDtos.AbrirOrdemRequest;
import br.edu.furlanetti.manutencao.dto.OrdemDtos.IndicadorEquipamento;
import br.edu.furlanetti.manutencao.dto.OrdemDtos.IndicadoresResponse;
import br.edu.furlanetti.manutencao.dto.OrdemDtos.OrdemResponse;
import br.edu.furlanetti.manutencao.exception.RecursoNaoEncontradoException;
import br.edu.furlanetti.manutencao.model.Equipamento;
import br.edu.furlanetti.manutencao.model.OrdemServico;
import br.edu.furlanetti.manutencao.model.Prioridade;
import br.edu.furlanetti.manutencao.model.StatusOrdem;
import br.edu.furlanetti.manutencao.repository.OrdemServicoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class OrdemServicoService {

    private final OrdemServicoRepository ordens;
    private final EquipamentoService equipamentos;

    public OrdemServicoService(OrdemServicoRepository ordens, EquipamentoService equipamentos) {
        this.ordens = ordens;
        this.equipamentos = equipamentos;
    }

    public Page<OrdemResponse> listar(StatusOrdem status, Prioridade prioridade, Pageable pageable) {
        return ordens.filtrar(status, prioridade, pageable).map(OrdemResponse::de);
    }

    public OrdemResponse buscar(Long id) {
        return OrdemResponse.de(obter(id));
    }

    @Transactional
    public OrdemResponse abrir(AbrirOrdemRequest dados) {
        Equipamento equipamento = equipamentos.obter(dados.equipamentoId());
        OrdemServico ordem = new OrdemServico(equipamento, dados.descricao(), dados.tipo(), dados.prioridade());
        return OrdemResponse.de(ordens.save(ordem));
    }

    @Transactional
    public OrdemResponse iniciar(Long id, String tecnico) {
        OrdemServico ordem = obter(id);
        ordem.iniciar(tecnico);
        return OrdemResponse.de(ordem);
    }

    @Transactional
    public OrdemResponse concluir(Long id, String solucao, BigDecimal horas) {
        OrdemServico ordem = obter(id);
        ordem.concluir(solucao, horas);
        return OrdemResponse.de(ordem);
    }

    @Transactional
    public OrdemResponse cancelar(Long id, String motivo) {
        OrdemServico ordem = obter(id);
        ordem.cancelar(motivo);
        return OrdemResponse.de(ordem);
    }

    public IndicadoresResponse indicadores() {
        Map<StatusOrdem, Long> porStatus = new EnumMap<>(StatusOrdem.class);
        for (StatusOrdem status : StatusOrdem.values()) {
            porStatus.put(status, 0L);
        }
        ordens.contarPorStatus().forEach(c -> porStatus.put(c.getStatus(), c.getTotal()));

        List<IndicadorEquipamento> porEquipamento = ordens.horasPorEquipamento().stream()
                .map(h -> new IndicadorEquipamento(
                        h.getEquipamento(),
                        h.getOrdens(),
                        Math.round(h.getMediaHoras() * 100.0) / 100.0))
                .toList();

        return new IndicadoresResponse(porStatus, porEquipamento);
    }

    private OrdemServico obter(Long id) {
        return ordens.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ordem de serviço", id));
    }
}
