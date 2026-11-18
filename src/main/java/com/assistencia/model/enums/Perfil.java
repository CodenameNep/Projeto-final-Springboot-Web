package com.assistencia.model.enums;

public enum Perfil {
    ROLE_ADMIN("Administrador"),
    ROLE_TECNICO("Técnico");

    private final String descricao;

    Perfil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
