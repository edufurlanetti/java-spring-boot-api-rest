package br.edu.furlanetti.manutencao.controller;

import br.edu.furlanetti.manutencao.dto.OrdemDtos.AbrirOrdemRequest;
import br.edu.furlanetti.manutencao.dto.OrdemDtos.CancelarRequest;
import br.edu.furlanetti.manutencao.dto.OrdemDtos.ConcluirRequest;
import br.edu.furlanetti.manutencao.dto.OrdemDtos.IndicadoresResponse;
import br.edu.furlanetti.manutencao.dto.OrdemDtos.IniciarRequest;
import br.edu.furlanetti.manutencao.dto.OrdemDtos.OrdemResponse;
import br.edu.furlanetti.manutencao.model.Prioridade;
import br.edu.furlanetti.manutencao.model.StatusOrdem;
import br.edu.furlanetti.manutencao.service.OrdemServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/ordens")
@Tag(name = "Ordens de serviço", description = "Abertura e ciclo de vida das ordens de manutenção")
public class OrdemServicoController {

    private final OrdemServicoService servico;

    public OrdemServicoController(OrdemServicoService servico) {
        this.servico = servico;
    }

    @GetMapping
    @Operation(summary = "Lista ordens com filtros opcionais e paginação",
            description = "Exemplo: /api/ordens?status=ABERTA&prioridade=CRITICA&page=0&size=10&sort=abertaEm,desc")
    public Page<OrdemResponse> listar(
            @RequestParam(required = false) StatusOrdem status,
            @RequestParam(required = false) Prioridade prioridade,
            @PageableDefault(size = 10, sort = "abertaEm", direction = Sort.Direction.DESC) Pageable pageable) {
        return servico.listar(status, prioridade, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma ordem pelo id")
    public OrdemResponse buscar(@PathVariable Long id) {
        return servico.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Abre uma nova ordem de serviço")
    public ResponseEntity<OrdemResponse> abrir(@RequestBody @Valid AbrirOrdemRequest dados) {
        OrdemResponse criada = servico.abrir(dados);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(local).body(criada);
    }

    // PATCH: altera parcialmente o recurso (apenas o status e campos ligados a ele)

    @PatchMapping("/{id}/iniciar")
    @Operation(summary = "Inicia o atendimento (ABERTA → EM_ANDAMENTO)")
    public OrdemResponse iniciar(@PathVariable Long id, @RequestBody @Valid IniciarRequest dados) {
        return servico.iniciar(id, dados.tecnico());
    }

    @PatchMapping("/{id}/concluir")
    @Operation(summary = "Conclui o atendimento (EM_ANDAMENTO → CONCLUIDA)")
    public OrdemResponse concluir(@PathVariable Long id, @RequestBody @Valid ConcluirRequest dados) {
        return servico.concluir(id, dados.solucao(), dados.horasTrabalhadas());
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancela uma ordem aberta ou em andamento")
    public OrdemResponse cancelar(@PathVariable Long id, @RequestBody @Valid CancelarRequest dados) {
        return servico.cancelar(id, dados.motivo());
    }

    @GetMapping("/indicadores")
    @Operation(summary = "Indicadores de manutenção: ordens por status e média de horas por equipamento")
    public IndicadoresResponse indicadores() {
        return servico.indicadores();
    }
}
