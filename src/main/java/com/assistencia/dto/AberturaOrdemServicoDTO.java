package com.assistencia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class AberturaOrdemServicoDTO {

    @NotNull(message = "Selecione um cliente.")
    @Positive(message = "Selecione um cliente valido.")
    private Long clienteId;

    @NotNull(message = "Selecione um equipamento.")
    @Positive(message = "Selecione um equipamento valido.")
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
