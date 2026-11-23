package com.assistencia.model.enums;

public enum StatusOrdemServico {
    ABERTA("Aberta"),
    EM_ANDAMENTO("Em Andamento"),
    AGUARDANDO_PECAS("Aguardando Peças"),
    FINALIZADA("Finalizada"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusOrdemServico(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
