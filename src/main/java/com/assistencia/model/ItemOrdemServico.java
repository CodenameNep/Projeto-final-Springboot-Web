package com.assistencia.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "tb_item_ordem_servico")
public class ItemOrdemServico implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    private OrdemServico ordemServico;

    @ManyToOne(optional = false)
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    @NotNull(message = "Informe a quantidade.")
    @Positive(message = "A quantidade deve ser maior que zero.")
    @Column(nullable = false)
    private Integer quantidade = 1;

    @NotNull(message = "Informe o preco unitario.")
    @DecimalMin(value = "0.0", message = "O preco unitario nao pode ser negativo.")
    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    @NotNull(message = "Informe o subtotal.")
    @DecimalMin(value = "0.0", message = "O subtotal nao pode ser negativo.")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    public ItemOrdemServico() {
    }

    public ItemOrdemServico(OrdemServico ordemServico, Servico servico, Integer quantidade, BigDecimal precoUnitario) {
        this.ordemServico = ordemServico;
        this.servico = servico;
        this.quantidade = quantidade != null ? quantidade : 1;
        this.precoUnitario = precoUnitario;
        if (precoUnitario != null) {
            this.subtotal = precoUnitario.multiply(BigDecimal.valueOf(this.quantidade));
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public OrdemServico getOrdemServico() { return ordemServico; }
    public void setOrdemServico(OrdemServico ordemServico) { this.ordemServico = ordemServico; }

    public Servico getServico() { return servico; }
    public void setServico(Servico servico) { this.servico = servico; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemOrdemServico that = (ItemOrdemServico) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
