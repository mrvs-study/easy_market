package model;

public class Funcionario {

    private int    id;
    private String nome;
    private String cpf;
    private String cargo;
    private String email;
    private String senha;
    private boolean ativo;

    public Funcionario() {}

    public Funcionario(String nome, String cpf, String cargo, String email, String senha) {
        this.nome  = nome;
        this.cpf   = cpf;
        this.cargo = cargo;
        this.email = email;
        this.senha = senha;
        this.ativo = true;
    }

    // Getters e Setters
    public int getId()                  { return id; }
    public void setId(int id)           { this.id = id; }

    public String getNome()             { return nome; }
    public void setNome(String nome)    { this.nome = nome; }

    public String getCpf()              { return cpf; }
    public void setCpf(String cpf)      { this.cpf = cpf; }

    public String getCargo()            { return cargo; }
    public void setCargo(String cargo)  { this.cargo = cargo; }

    public String getEmail()            { return email; }
    public void setEmail(String email)  { this.email = email; }

    public String getSenha()            { return senha; }
    public void setSenha(String senha)  { this.senha = senha; }

    public boolean isAtivo()            { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        return String.format("[%d] %s | %s | %s | %s", id, nome, cpf, cargo, email);
    }
}
