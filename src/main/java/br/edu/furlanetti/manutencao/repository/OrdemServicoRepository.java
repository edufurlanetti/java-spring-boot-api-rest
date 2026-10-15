package br.edu.furlanetti.manutencao.repository;

import br.edu.furlanetti.manutencao.model.OrdemServico;
import br.edu.furlanetti.manutencao.model.Prioridade;
import br.edu.furlanetti.manutencao.model.StatusOrdem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    /**
     * Filtros opcionais: parâmetro nulo significa "não filtrar".
     * O {@code @EntityGraph} carrega o equipamento na mesma consulta,
     * evitando o problema N+1.
     */
    @EntityGraph(attributePaths = "equipamento")
    @Query("""
            select o from OrdemServico o
            where (:status is null or o.status = :status)
              and (:prioridade is null or o.prioridade = :prioridade)
            """)
    Page<OrdemServico> filtrar(@Param("status") StatusOrdem status,
                               @Param("prioridade") Prioridade prioridade,
                               Pageable pageable);

    /** Projeção baseada em interface: o Spring cria a implementação. */
    interface ContagemPorStatus {
        StatusOrdem getStatus();

        Long getTotal();
    }

    @Query("select o.status as status, count(o) as total from OrdemServico o group by o.status")
    List<ContagemPorStatus> contarPorStatus();

    interface HorasPorEquipamento {
        String getEquipamento();

        Long getOrdens();

        Double getMediaHoras();
    }

    @Query("""
            select o.equipamento.codigo as equipamento,
                   count(o) as ordens,
                   avg(o.horasTrabalhadas) as mediaHoras
            from OrdemServico o
            where o.status = br.edu.furlanetti.manutencao.model.StatusOrdem.CONCLUIDA
            group by o.equipamento.codigo
            order by avg(o.horasTrabalhadas) desc
            """)
    List<HorasPorEquipamento> horasPorEquipamento();
}
