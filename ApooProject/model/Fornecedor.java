package model;

public class Fornecedor {

    private int    id;
    private String nome;
    private String cnpj;
    private String telefone;
    private String email;
    private String produtosFornecidos;

    public Fornecedor() {}

    public Fornecedor(String nome, String cnpj, String telefone,
                      String email, String produtosFornecidos) {
        this.nome               = nome;
        this.cnpj               = cnpj;
        this.telefone           = telefone;
        this.email              = email;
        this.produtosFornecidos = produtosFornecidos;
    }

    public int getId()                                    { return id; }
    public void setId(int id)                             { this.id = id; }

    public String getNome()                               { return nome; }
    public void setNome(String nome)                      { this.nome = nome; }

    public String getCnpj()                               { return cnpj; }
    public void setCnpj(String cnpj)                      { this.cnpj = cnpj; }

    public String getTelefone()                           { return telefone; }
    public void setTelefone(String telefone)              { this.telefone = telefone; }

    public String getEmail()                              { return email; }
    public void setEmail(String email)                    { this.email = email; }

    public String getProdutosFornecidos()                 { return produtosFornecidos; }
    public void setProdutosFornecidos(String pf)          { this.produtosFornecidos = pf; }

    @Override
    public String toString() {
        return String.format("[%d] %s | CNPJ: %s | Tel: %s | Email: %s",
                id, nome, cnpj, telefone, email);
    }
}
