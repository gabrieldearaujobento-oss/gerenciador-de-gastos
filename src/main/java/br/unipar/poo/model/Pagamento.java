package br.unipar.poo.model;

import br.unipar.poo.enums.FormaPagamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class Pagamento extends BaseModel{
    private int gastoId;
    private BigDecimal valorPago;
    private FormaPagamento forma;
    private LocalDate dataPagamento;
    private String observacao;


    //construtor

    public Pagamento(LocalDate dataPagamento, FormaPagamento forma, int gastoId, String observacao, BigDecimal valorPago) {
        super();
        this.dataPagamento = dataPagamento;
        this.forma = forma;
        this.gastoId = gastoId;
        this.observacao = observacao;
        this.valorPago = valorPago;
    }

    //getters e setters


    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public FormaPagamento getForma() {
        return forma;
    }

    public void setForma(FormaPagamento forma) {
        this.forma = forma;
    }

    public int getGastoId() {
        return gastoId;
    }

    public void setGastoId(int gastoId) {
        this.gastoId = gastoId;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    //metodos

    public void registrar(){}
    public void estornar(){}
    public void listarPorGastos(){}
    private void validarGastoPagavel(){}
    private void validarValor(){}
    private void atualizarStatus(){}
    private void rollback(){}

    @Override
    public Map<String, Object> toDict() {
        return Map.of();
    }
}
