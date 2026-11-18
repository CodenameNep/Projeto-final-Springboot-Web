package com.assistencia.repository;

import com.assistencia.model.Equipamento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {

    List<Equipamento> findByClienteId(Long clienteId);

    boolean existsByClienteId(Long clienteId);
}
