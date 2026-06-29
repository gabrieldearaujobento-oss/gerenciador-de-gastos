package br.unipar.poo.model;

import br.unipar.poo.enums.StatusGasto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class Gasto extends BaseModel {
    private String descricao;
    private BigDecimal valor;
    private int categoriaID;
    private LocalDate dataVencimento;
    private StatusGasto status;

    @Override
    public Map<String, Object> toDict() {
        return Map.of();
    }

    //Construtor
    public Gasto(int categoriaID, LocalDate dataVencimento, String descricao,  BigDecimal valor) {
        super();
        this.categoriaID = categoriaID;
        this.dataVencimento = dataVencimento;
        this.descricao = descricao;
        this.status = StatusGasto.PENDENTE;
        this.valor = valor;
    }

    //Getters e setters


    public int getCategoriaID() {
        return categoriaID;
    }

    public void setCategoriaID(int categoriaID) {
        this.categoriaID = categoriaID;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public StatusGasto getStatus() {
        return status;
    }

    public void setStatus(StatusGasto status) {
        this.status = status;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    //metodos
    public void criar(){}
    public void listar(){}
    public void listarPorStatus(){}
    public void listarPorCategoria(){}
    public void cancelar(){}
    public void deletar(){}
    private void validarValor(){}
    private void validarCancelavel(){}
}
