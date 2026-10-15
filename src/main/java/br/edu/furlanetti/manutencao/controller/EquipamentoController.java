package br.edu.furlanetti.manutencao.controller;

import br.edu.furlanetti.manutencao.dto.EquipamentoDtos.EquipamentoRequest;
import br.edu.furlanetti.manutencao.dto.EquipamentoDtos.EquipamentoResponse;
import br.edu.furlanetti.manutencao.service.EquipamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/equipamentos")
@Tag(name = "Equipamentos", description = "Cadastro dos equipamentos da planta")
public class EquipamentoController {

    private final EquipamentoService servico;

    public EquipamentoController(EquipamentoService servico) {
        this.servico = servico;
    }

    @GetMapping
    @Operation(summary = "Lista os equipamentos ativos")
    public List<EquipamentoResponse> listar() {
        return servico.listarAtivos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um equipamento pelo id")
    public EquipamentoResponse buscar(@PathVariable Long id) {
        return servico.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Cadastra um equipamento")
    public ResponseEntity<EquipamentoResponse> cadastrar(@RequestBody @Valid EquipamentoRequest dados) {
        EquipamentoResponse criado = servico.cadastrar(dados);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(local).body(criado);        // HTTP 201 + cabeçalho Location
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza nome, setor e fabricante")
    public EquipamentoResponse atualizar(@PathVariable Long id, @RequestBody @Valid EquipamentoRequest dados) {
        return servico.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativa o equipamento (exclusão lógica)")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        servico.inativar(id);
        return ResponseEntity.noContent().build();                // HTTP 204
    }
}
