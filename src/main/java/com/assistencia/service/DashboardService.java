package com.assistencia.service;

import com.assistencia.dto.DashboardResumoDTO;
import com.assistencia.model.enums.StatusOrdemServico;
import com.assistencia.repository.OrdemServicoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final OrdemServicoRepository ordemServicoRepository;

    public DashboardService(OrdemServicoRepository ordemServicoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResumoDTO obterResumo() {
        long abertas = ordemServicoRepository.contarPorStatus(StatusOrdemServico.ABERTA);
        long emAndamento = ordemServicoRepository.contarPorStatus(StatusOrdemServico.EM_ANDAMENTO);
        long aguardandoPecas = ordemServicoRepository.contarPorStatus(StatusOrdemServico.AGUARDANDO_PECAS);
        long finalizadas = ordemServicoRepository.contarPorStatus(StatusOrdemServico.FINALIZADA);

        YearMonth mesAtual = YearMonth.now();
        LocalDateTime inicio = mesAtual.atDay(1).atStartOfDay();
        LocalDateTime fimExclusivo = inicio.plusMonths(1);
        BigDecimal faturamento = ordemServicoRepository.somarFaturamentoFinalizadoNoPeriodo(
                StatusOrdemServico.FINALIZADA, inicio, fimExclusivo);
        if (faturamento == null) {
            faturamento = BigDecimal.ZERO;
        }

        String competencia = mesAtual.format(DateTimeFormatter.ofPattern(
                "MMMM 'de' yyyy", new Locale("pt", "BR")));

        return new DashboardResumoDTO(abertas, emAndamento, aguardandoPecas,
                finalizadas, ordemServicoRepository.count(), faturamento,
                competencia, ordemServicoRepository.findTop5ByOrderByDataAberturaDesc());
    }
}
