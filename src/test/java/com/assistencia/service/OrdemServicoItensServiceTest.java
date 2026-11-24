package com.assistencia.service;

import com.assistencia.model.ItemOrdemServico;
import com.assistencia.model.OrdemServico;
import com.assistencia.model.Servico;
import com.assistencia.model.enums.StatusOrdemServico;
import com.assistencia.repository.OrdemServicoRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrdemServicoItensServiceTest {
    private OrdemServicoRepository repository;
    private OrdemServicoService service;
    private OrdemServico ordem;

    @BeforeEach
    void preparar() {
        repository = Mockito.mock(OrdemServicoRepository.class);
        service = new OrdemServicoService(repository);
        ordem = new OrdemServico();
        when(repository.findById(10L)).thenReturn(Optional.of(ordem));
        when(repository.save(ordem)).thenReturn(ordem);
    }

    @Test
    void adicionarItemUsaPrecoAplicadoESomaTotal() {
        Servico catalogo = new Servico();
        catalogo.setId(3L);
        catalogo.setPrecoBase(new BigDecimal("24.50"));
        service.adicionarServico(10L, catalogo, 2);
        catalogo.setPrecoBase(new BigDecimal("50.00"));
        assertEquals(new BigDecimal("49.00"), ordem.getValorTotal());
        assertEquals(new BigDecimal("24.50"), ordem.getItens().get(0).getPrecoUnitario());
    }

    @Test
    void alterarQuantidadeEExcluirRecalculamTotal() {
        ItemOrdemServico item = new ItemOrdemServico();
        item.setId(7L);
        item.setQuantidade(1);
        item.setPrecoUnitario(new BigDecimal("15.00"));
        item.setOrdemServico(ordem);
        ordem.getItens().add(item);
        service.alterarQuantidade(10L, 7L, 3);
        assertEquals(new BigDecimal("45.00"), ordem.getValorTotal());
        service.removerServico(10L, 7L);
        assertEquals(new BigDecimal("0.00"), ordem.getValorTotal());
        assertTrue(ordem.getItens().isEmpty());
    }

    @Test
    void impedeAlteracaoDeOrdemFinalizadaEItemDeOutraOrdem() {
        ordem.setStatus(StatusOrdemServico.FINALIZADA);
        assertThrows(IllegalStateException.class, () -> service.removerServico(10L, 55L));
        ordem.setStatus(StatusOrdemServico.ABERTA);
        assertThrows(IllegalArgumentException.class, () -> service.alterarQuantidade(10L, 55L, 1));
        verify(repository, never()).save(ordem);
    }
}
