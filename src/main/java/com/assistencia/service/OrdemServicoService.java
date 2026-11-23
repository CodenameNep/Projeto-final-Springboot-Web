package com.assistencia.service;

import com.assistencia.model.ItemOrdemServico;
import com.assistencia.model.OrdemServico;
import com.assistencia.model.enums.StatusOrdemServico;
import com.assistencia.repository.OrdemServicoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
