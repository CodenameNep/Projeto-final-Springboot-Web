package com.assistencia.repository;

import com.assistencia.model.OrdemServico;
import com.assistencia.model.enums.StatusOrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    Optional<OrdemServico> findByNumeroProtocolo(String numeroProtocolo);

    List<OrdemServico> findByStatus(StatusOrdemServico status);

    List<OrdemServico> findByClienteId(Long clienteId);

    List<OrdemServico> findByTecnicoResponsavelId(Long tecnicoId);

    // Consulta JPQL customizada para busca avançada com múltiplos filtros opcionais combinados
    @Query("SELECT os FROM OrdemServico os " +
           "WHERE (:status IS NULL OR os.status = :status) " +
           "AND (:clienteNome IS NULL OR LOWER(os.cliente.nome) LIKE LOWER(CONCAT('%', :clienteNome, '%'))) " +
           "AND (:dataInicio IS NULL OR os.dataAbertura >= :dataInicio) " +
           "AND (:dataFim IS NULL OR os.dataAbertura <= :dataFim) " +
           "ORDER BY os.dataAbertura DESC")
    List<OrdemServico> buscarComFiltros(
            @Param("status") StatusOrdemServico status,
            @Param("clienteNome") String clienteNome,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    // Consulta JPQL para filtrar ordens por intervalo de datas de abertura
    @Query("SELECT os FROM OrdemServico os " +
           "WHERE os.dataAbertura BETWEEN :dataInicio AND :dataFim " +
           "ORDER BY os.dataAbertura DESC")
    List<OrdemServico> buscarPorIntervaloDatas(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    // Consulta JPQL para busca por nome do cliente (case-insensitive)
    @Query("SELECT os FROM OrdemServico os " +
           "WHERE LOWER(os.cliente.nome) LIKE LOWER(CONCAT('%', :nome, '%')) " +
           "ORDER BY os.dataAbertura DESC")
    List<OrdemServico> buscarPorNomeClientePersonalizado(@Param("nome") String nome);
}
