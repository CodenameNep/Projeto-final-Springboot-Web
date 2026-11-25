package com.assistencia.dto;

import com.assistencia.model.OrdemServico;
import java.math.BigDecimal;
import java.util.List;

public class DashboardResumoDTO {

    private final long abertas;
    private final long emAndamento;
    private final long aguardandoPecas;
    private final long finalizadas;
    private final long total;
    private final BigDecimal faturamentoMes;
    private final String competencia;
    private final List<OrdemServico> ultimasOrdens;

    public DashboardResumoDTO(long abertas, long emAndamento, long aguardandoPecas,
                               long finalizadas, long total, BigDecimal faturamentoMes,
                               String competencia, List<OrdemServico> ultimasOrdens) {
        this.abertas = abertas;
        this.emAndamento = emAndamento;
        this.aguardandoPecas = aguardandoPecas;
        this.finalizadas = finalizadas;
        this.total = total;
        this.faturamentoMes = faturamentoMes;
        this.competencia = competencia;
        this.ultimasOrdens = ultimasOrdens;
    }

    public long getAbertas() { return abertas; }
    public long getEmAndamento() { return emAndamento; }
    public long getAguardandoPecas() { return aguardandoPecas; }
    public long getFinalizadas() { return finalizadas; }
    public long getTotal() { return total; }
    public BigDecimal getFaturamentoMes() { return faturamentoMes; }
    public String getCompetencia() { return competencia; }
    public List<OrdemServico> getUltimasOrdens() { return ultimasOrdens; }
}
