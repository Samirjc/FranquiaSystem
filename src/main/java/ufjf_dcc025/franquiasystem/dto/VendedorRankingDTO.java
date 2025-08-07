package ufjf_dcc025.franquiasystem.dto;

public class VendedorRankingDTO {
    private int id;
    private String nome;
    private String email;
    private int quantidadeVendas;
    private Double valorTotalVendas;

    public VendedorRankingDTO(int id, String nome, String email, int quantidadeVendas, Double valorTotalVendas) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.quantidadeVendas = quantidadeVendas;
        this.valorTotalVendas = valorTotalVendas;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getQuantidadeVendas() {
        return quantidadeVendas;
    }

    public void setQuantidadeVendas(int quantidadeVendas) {
        this.quantidadeVendas = quantidadeVendas;
    }

    public Double getValorTotalVendas() {
        return valorTotalVendas;
    }

    public void setValorTotalVendas(Double valorTotalVendas) {
        this.valorTotalVendas = valorTotalVendas;
    }
    
    
}
