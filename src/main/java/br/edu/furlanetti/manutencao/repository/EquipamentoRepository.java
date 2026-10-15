package br.edu.furlanetti.manutencao.repository;

import br.edu.furlanetti.manutencao.model.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Spring Data gera a implementação a partir do nome dos métodos. */
public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {

    boolean existsByCodigo(String codigo);

    List<Equipamento> findByAtivoTrueOrderByCodigo();
}
