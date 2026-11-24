package com.assistencia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_servico")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome do servico.")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nome;

    @Size(max = 500)
    @Column(length = 500)
    private String descricao;

    @NotNull(message = "Informe o preco-base do servico.")
    @DecimalMin(value = "0.0", message = "O preco nao pode ser negativo.")
    @Digits(integer = 8, fraction = 2, message = "Informe um preco com ate 8 digitos inteiros e 2 decimais.")
    @Column(name = "preco_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoBase;

    public Servico() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPrecoBase() {
        return precoBase;
    }

    public void setPrecoBase(BigDecimal precoBase) {
        this.precoBase = precoBase;
    }
}
