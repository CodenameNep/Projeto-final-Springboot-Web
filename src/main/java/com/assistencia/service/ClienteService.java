package com.assistencia.service;

import com.assistencia.model.Cliente;
import com.assistencia.model.Endereco;
import com.assistencia.repository.ClienteRepository;
import com.assistencia.repository.EquipamentoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EquipamentoRepository equipamentoRepository;

    public ClienteService(ClienteRepository clienteRepository,
                          EquipamentoRepository equipamentoRepository) {
        this.clienteRepository = clienteRepository;
        this.equipamentoRepository = equipamentoRepository;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente nao encontrado: " + id));
    }

    @Transactional
    public Cliente salvar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente dados) {
        Cliente cliente = buscarPorId(id);
        cliente.setNome(dados.getNome());
        cliente.setTelefone(dados.getTelefone());
        cliente.setEmail(dados.getEmail());

        if (dados.getEndereco() != null) {
            if (cliente.getEndereco() == null) {
                cliente.setEndereco(dados.getEndereco());
            } else {
                Endereco atual = cliente.getEndereco();
                Endereco novo = dados.getEndereco();
                atual.setCep(novo.getCep());
                atual.setLogradouro(novo.getLogradouro());
                atual.setBairro(novo.getBairro());
                atual.setCidade(novo.getCidade());
                atual.setUf(novo.getUf());
                atual.setNumero(novo.getNumero());
                atual.setComplemento(novo.getComplemento());
            }
        }

        return clienteRepository.save(cliente);
    }

    @Transactional
    public void excluir(Long id) {
        Cliente cliente = buscarPorId(id);
        if (equipamentoRepository.existsByClienteId(id)) {
            throw new IllegalStateException("Exclua os equipamentos antes de excluir o cliente.");
        }
        clienteRepository.delete(cliente);
    }
}
