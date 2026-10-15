package br.edu.furlanetti.manutencao.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "equipamento")
public class Equipamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 60)
    private String setor;

    @Column(length = 60)
    private String fabricante;

    @Column(nullable = false)
    private boolean ativo = true;

    protected Equipamento() {
        // exigido pela JPA
    }

    public Equipamento(String codigo, String nome, String setor, String fabricante) {
        this.codigo = codigo;
        this.nome = nome;
        this.setor = setor;
        this.fabricante = fabricante;
    }

    public void atualizar(String nome, String setor, String fabricante) {
        this.nome = nome;
        this.setor = setor;
        this.fabricante = fabricante;
    }

    public void inativar() {
        this.ativo = false;
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public String getSetor() {
        return setor;
    }

    public String getFabricante() {
        return fabricante;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
