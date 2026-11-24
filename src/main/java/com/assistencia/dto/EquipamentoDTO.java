package com.assistencia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class EquipamentoDTO {

    private Long id;

    @NotBlank(message = "Informe o tipo do aparelho (ex: Smartphone, Notebook, Tablet).")
    @Size(max = 80, message = "O tipo deve ter no maximo 80 caracteres.")
    private String tipo;

    @NotBlank(message = "Informe a marca (ex: Dell, Apple, Samsung).")
    @Size(max = 80, message = "A marca deve ter no maximo 80 caracteres.")
    private String marca;

    @NotBlank(message = "Informe o modelo (ex: Inspiron 15, iPhone 13).")
    @Size(max = 100, message = "O modelo deve ter no maximo 100 caracteres.")
    private String modelo;

    @Size(max = 100, message = "O numero de serie deve ter no maximo 100 caracteres.")
    private String numeroSerie;

    @NotNull(message = "Selecione o cliente proprietario do equipamento.")
    @Positive(message = "Selecione um cliente valido.")
    private Long clienteId;

    public EquipamentoDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
}
