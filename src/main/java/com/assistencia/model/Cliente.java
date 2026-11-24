package com.assistencia.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome do cliente.")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nome;

    @Size(max = 20)
    @Column(length = 20)
    private String telefone;

    @Email(message = "Informe um e-mail valido.")
    @Size(max = 120)
    @Column(length = 120)
    private String email;

    // Cada cliente pode ter um endereco.
    // CascadeType.ALL permite salvar o endereco junto com o cliente.
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id", unique = true)
    private Endereco endereco;

    // Um cliente pode ter varios equipamentos.
    @JsonIgnore
    @OneToMany(mappedBy = "cliente")
    private List<Equipamento> equipamentos = new ArrayList<>();

    public Cliente() {
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public List<Equipamento> getEquipamentos() {
        return equipamentos;
    }

    public void setEquipamentos(List<Equipamento> equipamentos) {
        this.equipamentos = equipamentos;
    }
}
