package ufjf_dcc025.franquiasystem.models;

public abstract class Usuario {
    protected int id;
    protected String nome;
    protected String senha;
    
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
    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public abstract String getTipo();
}
