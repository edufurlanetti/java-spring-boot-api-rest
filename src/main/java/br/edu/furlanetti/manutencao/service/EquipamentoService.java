package br.edu.furlanetti.manutencao.service;

import br.edu.furlanetti.manutencao.dto.EquipamentoDtos.EquipamentoRequest;
import br.edu.furlanetti.manutencao.dto.EquipamentoDtos.EquipamentoResponse;
import br.edu.furlanetti.manutencao.exception.RecursoNaoEncontradoException;
import br.edu.furlanetti.manutencao.exception.RegraNegocioException;
import br.edu.furlanetti.manutencao.model.Equipamento;
import br.edu.furlanetti.manutencao.repository.EquipamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EquipamentoService {

    private final EquipamentoRepository repositorio;

    // Injeção de dependência pelo construtor (dispensa @Autowired)
    public EquipamentoService(EquipamentoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<EquipamentoResponse> listarAtivos() {
        return repositorio.findByAtivoTrueOrderByCodigo().stream()
                .map(EquipamentoResponse::de)
                .toList();
    }

    public EquipamentoResponse buscar(Long id) {
        return EquipamentoResponse.de(obter(id));
    }

    @Transactional
    public EquipamentoResponse cadastrar(EquipamentoRequest dados) {
        String codigo = dados.codigo().trim().toUpperCase();
        if (repositorio.existsByCodigo(codigo)) {
            throw new RegraNegocioException("Já existe equipamento com o código " + codigo);
        }
        Equipamento novo = new Equipamento(codigo, dados.nome(), dados.setor(), dados.fabricante());
        return EquipamentoResponse.de(repositorio.save(novo));
    }

    @Transactional
    public EquipamentoResponse atualizar(Long id, EquipamentoRequest dados) {
        Equipamento equipamento = obter(id);
        equipamento.atualizar(dados.nome(), dados.setor(), dados.fabricante());
        return EquipamentoResponse.de(equipamento);   // salvo automaticamente (dirty checking)
    }

    @Transactional
    public void inativar(Long id) {
        obter(id).inativar();                          // exclusão lógica preserva o histórico
    }

    Equipamento obter(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Equipamento", id));
    }
}
