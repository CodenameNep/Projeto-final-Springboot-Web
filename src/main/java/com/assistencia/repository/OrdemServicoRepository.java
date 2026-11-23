package com.assistencia.repository;

import com.assistencia.model.OrdemServico;
import com.assistencia.model.enums.StatusOrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    Optional<OrdemServico> findByNumeroProtocolo(String numeroProtocolo);

    List<OrdemServico> findByStatus(StatusOrdemServico status);

    List<OrdemServico> findByClienteId(Long clienteId);

    List<OrdemServico> findByTecnicoResponsavelId(Long tecnicoId);
}
