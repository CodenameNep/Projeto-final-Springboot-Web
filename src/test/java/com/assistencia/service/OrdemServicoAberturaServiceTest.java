package com.assistencia.service;

import com.assistencia.model.Cliente;
import com.assistencia.model.Equipamento;
import com.assistencia.model.OrdemServico;
import com.assistencia.model.Usuario;
import com.assistencia.model.enums.StatusOrdemServico;
import com.assistencia.repository.OrdemServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrdemServicoAberturaServiceTest {

    private OrdemServicoRepository repository;
    private OrdemServicoService service;
    private Cliente cliente;
    private Equipamento equipamento;
    private Usuario tecnico;

    @BeforeEach
    void preparar() {
        repository = mock(OrdemServicoRepository.class);
        service = new OrdemServicoService(repository);
        cliente = new Cliente();
        cliente.setId(1L);
        equipamento = new Equipamento();
        equipamento.setId(2L);
        equipamento.setCliente(cliente);
        tecnico = new Usuario();
        tecnico.setId(3L);
        tecnico.setAtivo(true);
        when(repository.save(any(OrdemServico.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void criaOsAbertaComTecnicoLogadoEProtocolo() {
        OrdemServico os = service.abrirOrdem(cliente, equipamento, tecnico, " Tela quebrada ");
        assertSame(cliente, os.getCliente());
        assertSame(equipamento, os.getEquipamento());
        assertSame(tecnico, os.getTecnicoResponsavel());
        assertEquals(StatusOrdemServico.ABERTA, os.getStatus());
        assertEquals("Tela quebrada", os.getDefeitoRelatado());
        assertNotNull(os.getDataAbertura());
        assertTrue(os.getNumeroProtocolo().startsWith("OS-"));
        assertTrue(os.getNumeroProtocolo().length() <= 20);
    }

    @Test
    void rejeitaEquipamentoDeOutroClienteEUsuarioInativo() {
        Cliente outro = new Cliente();
        outro.setId(88L);
        equipamento.setCliente(outro);
        assertThrows(IllegalArgumentException.class,
                () -> service.abrirOrdem(cliente, equipamento, tecnico, "Tela quebrada"));
        equipamento.setCliente(cliente);
        tecnico.setAtivo(false);
        assertThrows(IllegalArgumentException.class,
                () -> service.abrirOrdem(cliente, equipamento, tecnico, "Tela quebrada"));
        verify(repository, never()).save(any(OrdemServico.class));
    }
}
