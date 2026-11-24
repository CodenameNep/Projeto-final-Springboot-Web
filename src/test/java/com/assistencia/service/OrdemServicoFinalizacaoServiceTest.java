package com.assistencia.service;

import com.assistencia.model.OrdemServico;
import com.assistencia.model.enums.StatusOrdemServico;
import com.assistencia.repository.OrdemServicoRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrdemServicoFinalizacaoServiceTest {

    private OrdemServicoRepository repository;
    private OrdemServicoService service;
    private OrdemServico ordem;

    @BeforeEach
    void preparar() {
        repository = mock(OrdemServicoRepository.class);
        service = new OrdemServicoService(repository);
        ordem = new OrdemServico();
        when(repository.findById(1L)).thenReturn(Optional.of(ordem));
        when(repository.save(ordem)).thenReturn(ordem);
    }

    @Test
    void finalizaSomenteComLaudoEStatusCorreto() {
        ordem.setStatus(StatusOrdemServico.EM_ANDAMENTO);
        service.finalizarOrdem(1L, " Diagnostico concluido ");
        assertEquals(StatusOrdemServico.FINALIZADA, ordem.getStatus());
        assertEquals("Diagnostico concluido", ordem.getLaudoTecnico());
        assertNotNull(ordem.getDataFinalizacao());
        verify(repository).save(ordem);
    }

    @Test
    void impedeFinalizarSemLaudoOuSemIniciar() {
        assertThrows(IllegalStateException.class, () -> service.finalizarOrdem(1L, "Feito"));
        ordem.setStatus(StatusOrdemServico.EM_ANDAMENTO);
        assertThrows(IllegalArgumentException.class, () -> service.finalizarOrdem(1L, "   "));
        assertThrows(IllegalStateException.class,
                () -> service.atualizarStatus(1L, StatusOrdemServico.FINALIZADA));
        verify(repository, never()).save(ordem);
    }

    @Test
    void impedeEdicoesEReaberturaDepoisDeFinalizar() {
        ordem.setStatus(StatusOrdemServico.EM_ANDAMENTO);
        service.finalizarOrdem(1L, "Resolvido");
        assertThrows(IllegalStateException.class, () -> service.removerServico(1L, 10L));
        assertThrows(IllegalStateException.class, () -> service.alterarQuantidade(1L, 10L, 2));
        assertThrows(IllegalStateException.class,
                () -> service.atualizarStatus(1L, StatusOrdemServico.EM_ANDAMENTO));
    }
}
