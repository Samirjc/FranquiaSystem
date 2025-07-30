package ufjf_dcc025.franquiasystem.models;

import java.util.ArrayList;
import java.util.List;

public class Franquia {
    private int id;
    private String nome;
    private String endereco;
    private Gerente gerente;
    private List<Vendedor> vendedores;

    public Franquia(String nome, String endereco, Gerente gerente) {
        this.nome = nome;
        this.endereco = endereco;
        this.gerente = gerente;
        this.vendedores = new ArrayList<>();
    }

    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getEndereco() {
        return endereco;
    }
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    public Gerente getGerente() {
        return gerente;
    }
    public void setGerente(Gerente gerente) {
        this.gerente = gerente;
    }
    public List<Vendedor> getVendedores() {
        return vendedores;
    }
    public void setVendedores(List<Vendedor> vendedores) {
        this.vendedores = vendedores;
    }
    public void adicionarVendedor(Vendedor vendedor) {
        this.vendedores.add(vendedor);
    }
}
