package ufjf_dcc025.franquiasystem.models;

import java.util.Map;

public class Pedido {
    private int id;
    private String nomeCliente;
    private String formaPagamento;
    private Map<Produto, Integer> produtos;
    private double taxas;
    private double descontos;
    private String modalidadeEntrega;

    public Pedido(int id, String nomeCliente, String formaPagamento, Map<Produto, Integer> produtos, double taxas, double descontos, String modalidadeEntrega) {
        this.id = id;
        this.nomeCliente = nomeCliente;
        this.formaPagamento = formaPagamento;
        this.produtos = produtos;
        this.taxas = taxas;
        this.descontos = descontos;
        this.modalidadeEntrega = modalidadeEntrega;
    }

    public Pedido(String nomeCliente, String formaPagamento, Map<Produto, Integer> produtos, double taxas, double descontos, String modalidadeEntrega) {
        this.nomeCliente = nomeCliente;
        this.formaPagamento = formaPagamento;
        this.produtos = produtos;
        this.taxas = taxas;
        this.descontos = descontos;
        this.modalidadeEntrega = modalidadeEntrega;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public Map<Produto, Integer> getProdutos() {
        return produtos;
    }

    public void setProdutos(Map<Produto, Integer> produtos) {
        this.produtos = produtos;
    }

    public double getTaxas() {
        return taxas;
    }

    public void setTaxas(double taxas) {
        this.taxas = taxas;
    }

    public double getDescontos() {
        return descontos;
    }

    public void setDescontos(double descontos) {
        this.descontos = descontos;
    }

    public String getModalidadeEntrega() {
        return modalidadeEntrega;
    }

    public void setModalidadeEntrega(String modalidadeEntrega) {
        this.modalidadeEntrega = modalidadeEntrega;
    }
}
