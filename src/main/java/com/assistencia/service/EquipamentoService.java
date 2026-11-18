package com.assistencia.service;

import com.assistencia.model.Cliente;
import com.assistencia.model.Equipamento;
import com.assistencia.repository.ClienteRepository;
import com.assistencia.repository.EquipamentoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EquipamentoService {

    private final EquipamentoRepository equipamentoRepository;
    private final ClienteRepository clienteRepository;

    public EquipamentoService(EquipamentoRepository equipamentoRepository,
                              ClienteRepository clienteRepository) {
        this.equipamentoRepository = equipamentoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<Equipamento> listarTodos() {
        return equipamentoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Equipamento> listarPorCliente(Long clienteId) {
        return equipamentoRepository.findByClienteId(clienteId);
    }

    @Transactional(readOnly = true)
    public Equipamento buscarPorId(Long id) {
        return equipamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipamento nao encontrado: " + id));
    }

    @Transactional
    public Equipamento salvar(Equipamento equipamento) {
        equipamento.setCliente(buscarCliente(equipamento.getCliente()));
        return equipamentoRepository.save(equipamento);
    }

    @Transactional
    public Equipamento atualizar(Long id, Equipamento dados) {
        Equipamento equipamento = buscarPorId(id);
        equipamento.setTipo(dados.getTipo());
        equipamento.setMarca(dados.getMarca());
        equipamento.setModelo(dados.getModelo());
        equipamento.setNumeroSerie(dados.getNumeroSerie());

        if (dados.getCliente() != null) {
            equipamento.setCliente(buscarCliente(dados.getCliente()));
        }

        return equipamentoRepository.save(equipamento);
    }

    @Transactional
    public void excluir(Long id) {
        Equipamento equipamento = buscarPorId(id);
        equipamentoRepository.delete(equipamento);
    }

    private Cliente buscarCliente(Cliente cliente) {
        if (cliente == null || cliente.getId() == null) {
            throw new IllegalArgumentException("Informe um cliente cadastrado para o equipamento.");
        }
        return clienteRepository.findById(cliente.getId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente nao encontrado: " + cliente.getId()));
    }
}
