package com.assistencia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Dados recebidos da tela de abertura da OS. IDs são validados novamente no service. */
public class AberturaOrdemServicoDTO {

    @NotNull(message = "Selecione um cliente.")
    private Long clienteId;

    @NotNull(message = "Selecione um equipamento.")
    private Long equipamentoId;

    @NotBlank(message = "Descreva o defeito relatado.")
    @Size(max = 1000, message = "O defeito deve ter no maximo 1000 caracteres.")
    private String defeitoRelatado;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public Long getEquipamentoId() { return equipamentoId; }
    public void setEquipamentoId(Long equipamentoId) { this.equipamentoId = equipamentoId; }

    public String getDefeitoRelatado() { return defeitoRelatado; }
    public void setDefeitoRelatado(String defeitoRelatado) { this.defeitoRelatado = defeitoRelatado; }
}
