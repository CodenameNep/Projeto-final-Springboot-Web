package com.assistencia.repository;

import com.assistencia.model.OrdemServico;
import com.assistencia.model.enums.StatusOrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    Optional<OrdemServico> findByNumeroProtocolo(String numeroProtocolo);

    List<OrdemServico> findByStatus(StatusOrdemServico status);

    List<OrdemServico> findByClienteId(Long clienteId);

    List<OrdemServico> findByTecnicoResponsavelId(Long tecnicoId);

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

    @Query("SELECT os FROM OrdemServico os " +
           "WHERE os.dataAbertura BETWEEN :dataInicio AND :dataFim " +
           "ORDER BY os.dataAbertura DESC")
    List<OrdemServico> buscarPorIntervaloDatas(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    @Query("SELECT os FROM OrdemServico os " +
           "WHERE LOWER(os.cliente.nome) LIKE LOWER(CONCAT('%', :nome, '%')) " +
           "ORDER BY os.dataAbertura DESC")
    List<OrdemServico> buscarPorNomeClientePersonalizado(@Param("nome") String nome);

    @Query("SELECT COUNT(os) FROM OrdemServico os WHERE os.status = :status")
    long contarPorStatus(@Param("status") StatusOrdemServico status);

    @Query("SELECT SUM(os.valorTotal) FROM OrdemServico os " +
           "WHERE os.status = :status " +
           "AND os.dataFinalizacao >= :inicio " +
           "AND os.dataFinalizacao < :fim")
    BigDecimal somarFaturamentoFinalizadoNoPeriodo(
            @Param("status") StatusOrdemServico status,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    List<OrdemServico> findTop5ByOrderByDataAberturaDesc();
}
