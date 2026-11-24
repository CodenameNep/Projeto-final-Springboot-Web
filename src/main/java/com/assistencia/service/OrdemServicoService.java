package com.assistencia.service;

import com.assistencia.model.Cliente;
import com.assistencia.model.Equipamento;
import com.assistencia.model.Usuario;
import com.assistencia.model.Servico;
import com.assistencia.model.ItemOrdemServico;
import com.assistencia.model.OrdemServico;
import com.assistencia.model.enums.StatusOrdemServico;
import com.assistencia.repository.OrdemServicoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;

    public OrdemServicoService(OrdemServicoRepository ordemServicoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
    }

    @Transactional(readOnly = true)
    public OrdemServico buscarPorId(Long id) {
        return ordemServicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ordem de servico nao encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<OrdemServico> listarTodas() {
        return ordemServicoRepository.findAll();
    }

    /** Abre a ordem com os relacionamentos validados, técnico logado e protocolo único. */
    @Transactional
    public OrdemServico abrirOrdem(Cliente cliente, Equipamento equipamento,
                                  Usuario tecnicoLogado, String defeitoRelatado) {
        if (cliente == null || cliente.getId() == null) {
            throw new IllegalArgumentException("Selecione um cliente cadastrado.");
        }
        if (equipamento == null || equipamento.getId() == null
                || equipamento.getCliente() == null
                || !cliente.getId().equals(equipamento.getCliente().getId())) {
            throw new IllegalArgumentException("O equipamento deve pertencer ao cliente selecionado.");
        }
        if (tecnicoLogado == null || tecnicoLogado.getId() == null
                || !Boolean.TRUE.equals(tecnicoLogado.getAtivo())) {
            throw new IllegalArgumentException("O usuario logado deve estar ativo.");
        }
        if (defeitoRelatado == null || defeitoRelatado.isBlank()
                || defeitoRelatado.trim().length() > 1000) {
            throw new IllegalArgumentException("Informe o defeito relatado (ate 1000 caracteres).");
        }

        OrdemServico ordem = new OrdemServico();
        ordem.setNumeroProtocolo("OS-" + UUID.randomUUID().toString()
                .replace("-", "").substring(0, 16).toUpperCase());
        ordem.setCliente(cliente);
        ordem.setEquipamento(equipamento);
        ordem.setTecnicoResponsavel(tecnicoLogado);
        ordem.setDefeitoRelatado(defeitoRelatado.trim());
        return ordemServicoRepository.save(ordem);
    }

    /** Registra o preço do catálogo no momento da inclusão (histórico imutável de preço). */
    @Transactional
    public OrdemServico adicionarServico(Long ordemId, Servico servico, Integer quantidade) {
        OrdemServico ordem = buscarPorId(ordemId);
        exigirEditavel(ordem);
        validarQuantidade(quantidade);
        if (servico == null || servico.getId() == null || servico.getPrecoBase() == null
                || servico.getPrecoBase().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Selecione um servico cadastrado com preco valido.");
        }
        ItemOrdemServico item = new ItemOrdemServico(
                ordem, servico, quantidade, servico.getPrecoBase());
        ordem.getItens().add(item);
        calcularValorTotal(ordem);
        return ordemServicoRepository.save(ordem);
    }

    @Transactional
    public OrdemServico alterarQuantidade(Long ordemId, Long itemId, Integer quantidade) {
        OrdemServico ordem = buscarPorId(ordemId);
        exigirEditavel(ordem);
        validarQuantidade(quantidade);
        ItemOrdemServico item = buscarItemDaOrdem(ordem, itemId);
        item.setQuantidade(quantidade);
        calcularValorTotal(ordem);
        return ordemServicoRepository.save(ordem);
    }

    @Transactional
    public OrdemServico removerServico(Long ordemId, Long itemId) {
        OrdemServico ordem = buscarPorId(ordemId);
        exigirEditavel(ordem);
        ItemOrdemServico item = buscarItemDaOrdem(ordem, itemId);
        ordem.getItens().remove(item); // orphanRemoval exclui o registro associado.
        calcularValorTotal(ordem);
        return ordemServicoRepository.save(ordem);
    }

    private void validarQuantidade(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
    }

    private ItemOrdemServico buscarItemDaOrdem(OrdemServico ordem, Long itemId) {
        return ordem.getItens().stream()
                .filter(i -> i.getId() != null && i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item nao encontrado nesta OS."));
    }

    private void exigirEditavel(OrdemServico ordem) {
        if (ordem.getStatus() == StatusOrdemServico.FINALIZADA
                || ordem.getStatus() == StatusOrdemServico.CANCELADA) {
            throw new IllegalStateException("Nao e permitido alterar uma OS encerrada.");
        }
    }

    // Calcula o valor do item usando o preco registrado na OS, nao o preco atual do catalogo.
    public BigDecimal calcularSubtotal(ItemOrdemServico item) {
        if (item == null) {
            throw new IllegalArgumentException("Informe o item da ordem de servico.");
        }
        if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
            throw new IllegalArgumentException("A quantidade do item deve ser maior que zero.");
        }
        if (item.getPrecoUnitario() == null
                || item.getPrecoUnitario().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O preco unitario deve ser zero ou maior.");
        }

        BigDecimal subtotal = item.getPrecoUnitario()
                .multiply(BigDecimal.valueOf(item.getQuantidade()))
                .setScale(2, RoundingMode.HALF_UP);
        item.setSubtotal(subtotal);
        return subtotal;
    }

    // Recalcula cada subtotal e atualiza o valor total em memoria.
    public BigDecimal calcularValorTotal(OrdemServico ordemServico) {
        if (ordemServico == null || ordemServico.getItens() == null) {
            throw new IllegalArgumentException("Informe a ordem de servico e seus itens.");
        }

        BigDecimal total = BigDecimal.ZERO.setScale(2);
        for (ItemOrdemServico item : ordemServico.getItens()) {
            total = total.add(calcularSubtotal(item));
        }
        ordemServico.setValorTotal(total);
        return total;
    }

    // Grava os subtotais e o valor total na mesma transacao.
    @Transactional
    public OrdemServico recalcularTotal(Long ordemServicoId) {
        OrdemServico ordemServico = buscarPorId(ordemServicoId);
        if (ordemServico.getStatus() == StatusOrdemServico.FINALIZADA
                || ordemServico.getStatus() == StatusOrdemServico.CANCELADA) {
            throw new IllegalStateException("Nao e permitido alterar uma OS encerrada.");
        }
        calcularValorTotal(ordemServico);
        return ordemServicoRepository.save(ordemServico);
    }

    // So permite avancos validos no fluxo da ordem de servico.
    @Transactional
    public OrdemServico atualizarStatus(Long ordemServicoId, StatusOrdemServico novoStatus) {
        if (novoStatus == null) {
            throw new IllegalArgumentException("Informe o novo status da ordem de servico.");
        }

        OrdemServico ordemServico = buscarPorId(ordemServicoId);
        StatusOrdemServico statusAtual = ordemServico.getStatus();
        if (statusAtual == novoStatus) {
            return ordemServico; // Repetir o mesmo status nao altera o banco.
        }
        if (!transicaoPermitida(statusAtual, novoStatus)) {
            throw new IllegalStateException(
                    "Transicao de status nao permitida: " + statusAtual + " -> " + novoStatus);
        }

        ordemServico.setStatus(novoStatus);
        return ordemServicoRepository.save(ordemServico);
    }

    private boolean transicaoPermitida(StatusOrdemServico atual, StatusOrdemServico novo) {
        if (atual == null) {
            return false;
        }
        return switch (atual) {
            case ABERTA -> novo == StatusOrdemServico.EM_ANDAMENTO
                    || novo == StatusOrdemServico.CANCELADA;
            case EM_ANDAMENTO -> novo == StatusOrdemServico.AGUARDANDO_PECAS
                    || novo == StatusOrdemServico.FINALIZADA
                    || novo == StatusOrdemServico.CANCELADA;
            case AGUARDANDO_PECAS -> novo == StatusOrdemServico.EM_ANDAMENTO
                    || novo == StatusOrdemServico.CANCELADA;
            case FINALIZADA, CANCELADA -> false;
        };
    }
}
