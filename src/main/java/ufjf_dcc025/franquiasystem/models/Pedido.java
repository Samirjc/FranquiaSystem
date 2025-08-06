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
    private Vendedor vendedor;
    private Franquia franquia;

    public Pedido(int id, String nomeCliente, String formaPagamento, Map<Produto, Integer> produtos, double taxas, double descontos, String modalidadeEntrega, Vendedor vendedor, Franquia franquia) {
        this.id = id;
        this.nomeCliente = nomeCliente;
        this.formaPagamento = formaPagamento;
        this.produtos = produtos;
        this.taxas = taxas;
        this.descontos = descontos;
        this.modalidadeEntrega = modalidadeEntrega;
        this.vendedor = vendedor;
        this.franquia = franquia;
    }

    public Pedido(String nomeCliente, String formaPagamento, Map<Produto, Integer> produtos, double taxas, double descontos, String modalidadeEntrega, Vendedor vendedor, Franquia franquia) {
        this.nomeCliente = nomeCliente;
        this.formaPagamento = formaPagamento;
        this.produtos = produtos;
        this.taxas = taxas;
        this.descontos = descontos;
        this.modalidadeEntrega = modalidadeEntrega;
        this.vendedor = vendedor;
        this.franquia = franquia;
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
    public Vendedor getVendedor() {
        return vendedor;
    }
    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }
    public Franquia getFranquia() {
        return franquia;
    }
    public void setFranquia(Franquia franquia) {
        this.franquia = franquia;
    }
}
