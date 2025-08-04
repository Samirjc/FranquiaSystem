package ufjf_dcc025.franquiasystem.models;

public abstract class Usuario {
    protected int id;
    protected String nome;
    protected String senha;
    protected String cpf;
    protected String email;
    
    public Usuario(int id, String nome, String senha, String cpf, String email) {
        this.id = id;
        this.nome = nome;
        this.senha = senha;
        this.cpf = cpf;
        this.email = email;
    }
    
    public Usuario(String nome, String senha, String cpf, String email) {
        this.nome = nome;
        this.senha = senha;
        this.cpf = cpf;
        this.email = email;
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
    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    
    public abstract String getTipo();
    
    @Override
    public String toString() {
        return this.nome;
    }
}
