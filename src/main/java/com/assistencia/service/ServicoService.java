package com.assistencia.service;

import com.assistencia.model.Servico;
import com.assistencia.repository.ServicoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;

    public ServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    @Transactional(readOnly = true)
    public List<Servico> listarTodos() {
        return servicoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Servico buscarPorId(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Servico nao encontrado: " + id));
    }

    @Transactional
    public Servico salvar(Servico servico) {
        validarDados(servico);
        return servicoRepository.save(servico);
    }

    @Transactional
    public Servico atualizar(Long id, Servico dados) {
        validarDados(dados);
        Servico servico = buscarPorId(id);
        servico.setNome(dados.getNome());
        servico.setDescricao(dados.getDescricao());
        servico.setPrecoBase(dados.getPrecoBase());
        return servicoRepository.save(servico);
    }

    @Transactional
    public Servico atualizarPreco(Long id, BigDecimal novoPreco) {
        validarPreco(novoPreco);
        Servico servico = buscarPorId(id);
        servico.setPrecoBase(novoPreco);
        return servicoRepository.save(servico);
    }

    @Transactional
    public void excluir(Long id) {
        Servico servico = buscarPorId(id);
        servicoRepository.delete(servico);
    }

    private void validarDados(Servico servico) {
        if (servico == null || servico.getNome() == null || servico.getNome().isBlank()) {
            throw new IllegalArgumentException("Informe o nome do servico.");
        }
        validarPreco(servico.getPrecoBase());
    }

    private void validarPreco(BigDecimal preco) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O preco-base deve ser zero ou maior.");
        }
    }
}
