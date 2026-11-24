package com.assistencia.service;

import com.assistencia.model.ItemOrdemServico;
import com.assistencia.model.OrdemServico;
import com.assistencia.model.enums.StatusOrdemServico;
import com.assistencia.repository.OrdemServicoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrdemServicoServiceTest {

    private OrdemServicoRepository repository;
    private OrdemServicoService service;

    @BeforeEach
    void preparar() {
        repository = Mockito.mock(OrdemServicoRepository.class);
        service = new OrdemServicoService(repository);
    }

    @Test
    void deveCalcularSubtotalPelaQuantidadeEPrecoPraticado() {
        ItemOrdemServico item = item(3, "25.50");

        assertEquals(new BigDecimal("76.50"), service.calcularSubtotal(item));
        assertEquals(new BigDecimal("76.50"), item.getSubtotal());
    }

    @Test
    void deveRejeitarQuantidadeInvalidaEPrecoNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> service.calcularSubtotal(item(0, "25.00")));
        assertThrows(IllegalArgumentException.class,
                () -> service.calcularSubtotal(item(-1, "25.00")));
        assertThrows(IllegalArgumentException.class,
                () -> service.calcularSubtotal(item(2, "-1.00")));
        assertThrows(IllegalArgumentException.class,
                () -> service.calcularSubtotal(item(1, null)));
    }

    @Test
    void deveSomarItensEAtualizarSubtotaisETotal() {
        OrdemServico os = new OrdemServico();
        ItemOrdemServico primeiro = item(2, "40.00");
        ItemOrdemServico segundo = item(1, "15.50");
        os.setItens(List.of(primeiro, segundo));

        assertEquals(new BigDecimal("95.50"), service.calcularValorTotal(os));
        assertEquals(new BigDecimal("80.00"), primeiro.getSubtotal());
        assertEquals(new BigDecimal("15.50"), segundo.getSubtotal());
        assertEquals(new BigDecimal("95.50"), os.getValorTotal());
    }

    @Test
    void deveSalvarCalculosNaOrdemDeServico() {
        OrdemServico os = new OrdemServico();
        os.setItens(List.of(item(2, "50.00"), item(1, "25.00")));
        when(repository.findById(7L)).thenReturn(Optional.of(os));
        when(repository.save(os)).thenReturn(os);

        OrdemServico resultado = service.recalcularTotal(7L);

        assertSame(os, resultado);
        assertEquals(new BigDecimal("125.00"), resultado.getValorTotal());
        verify(repository).save(os);
    }

    @Test
    void naoDeveSalvarCasoUmItemSejaInvalido() {
        OrdemServico os = new OrdemServico();
        os.setItens(List.of(item(1, "10.00"), item(0, "15.00")));
        when(repository.findById(7L)).thenReturn(Optional.of(os));

        assertThrows(IllegalArgumentException.class, () -> service.recalcularTotal(7L));
        verify(repository, never()).save(any(OrdemServico.class));
    }

    @Test
    void devePermitirStatusEmAndamentoEFinalizada() {
        OrdemServico os = new OrdemServico();
        when(repository.findById(7L)).thenReturn(Optional.of(os));
        when(repository.save(os)).thenReturn(os);

        service.atualizarStatus(7L, StatusOrdemServico.EM_ANDAMENTO);
        service.finalizarOrdem(7L, "Troca de tela concluida e testada.");

        assertEquals(StatusOrdemServico.FINALIZADA, os.getStatus());
        assertEquals("Troca de tela concluida e testada.", os.getLaudoTecnico());
        org.junit.jupiter.api.Assertions.assertNotNull(os.getDataFinalizacao());
        verify(repository, Mockito.times(2)).save(os);
    }

    @Test
    void devePermitirAguardarPecasERetomarAndamento() {
        OrdemServico os = new OrdemServico();
        os.setStatus(StatusOrdemServico.EM_ANDAMENTO);
        when(repository.findById(7L)).thenReturn(Optional.of(os));
        when(repository.save(os)).thenReturn(os);

        service.atualizarStatus(7L, StatusOrdemServico.AGUARDANDO_PECAS);
        service.atualizarStatus(7L, StatusOrdemServico.EM_ANDAMENTO);

        assertEquals(StatusOrdemServico.EM_ANDAMENTO, os.getStatus());
    }

    @Test
    void naoDevePularEtapasNemReabrirOsFinalizada() {
        OrdemServico os = new OrdemServico();
        when(repository.findById(7L)).thenReturn(Optional.of(os));

        assertThrows(IllegalStateException.class,
                () -> service.atualizarStatus(7L, StatusOrdemServico.FINALIZADA));
        os.setStatus(StatusOrdemServico.FINALIZADA);
        assertThrows(IllegalStateException.class,
                () -> service.atualizarStatus(7L, StatusOrdemServico.EM_ANDAMENTO));
        verify(repository, never()).save(any(OrdemServico.class));
    }

    @Test
    void naoDeveRecalcularOsEncerrada() {
        OrdemServico os = new OrdemServico();
        os.setStatus(StatusOrdemServico.CANCELADA);
        when(repository.findById(7L)).thenReturn(Optional.of(os));

        assertThrows(IllegalStateException.class, () -> service.recalcularTotal(7L));
        verify(repository, never()).save(any(OrdemServico.class));
    }

    @Test
    void deveAvisarQuandoOrdemNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.recalcularTotal(99L));
    }

    private ItemOrdemServico item(int quantidade, String preco) {
        ItemOrdemServico item = new ItemOrdemServico();
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(preco == null ? null : new BigDecimal(preco));
        return item;
    }
}
