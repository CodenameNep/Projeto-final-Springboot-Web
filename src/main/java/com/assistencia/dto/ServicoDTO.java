package com.assistencia.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class ServicoDTO {

    private Long id;

    @NotBlank(message = "Informe o nome do servico.")
    @Size(max = 100, message = "O nome deve ter no maximo 100 caracteres.")
    private String nome;

    @Size(max = 500, message = "A descricao deve ter no maximo 500 caracteres.")
    private String descricao;

    @NotNull(message = "Informe o preco-base do servico.")
    @DecimalMin(value = "0.0", inclusive = true, message = "O preco deve ser igual ou maior que zero.")
    @Digits(integer = 8, fraction = 2, message = "O preco aceita ate 8 digitos inteiros e 2 decimais.")
    private BigDecimal precoBase;

    public ServicoDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getPrecoBase() { return precoBase; }
    public void setPrecoBase(BigDecimal precoBase) { this.precoBase = precoBase; }
}
