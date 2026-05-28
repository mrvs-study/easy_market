package model;

public class Produto {

    private int    id;
    private String nome;
    private String categoria;
    private double preco;
    private int    quantidade;
    private int    estoqueMinimo;
    private int    idFornecedor;

    public Produto() {}

    public Produto(String nome, String categoria, double preco,
                   int quantidade, int estoqueMinimo, int idFornecedor) {
        this.nome          = nome;
        this.categoria     = categoria;
        this.preco         = preco;
        this.quantidade    = quantidade;
        this.estoqueMinimo = estoqueMinimo;
        this.idFornecedor  = idFornecedor;
    }

    public int getId()                              { return id; }
    public void setId(int id)                       { this.id = id; }

    public String getNome()                         { return nome; }
    public void setNome(String nome)                { this.nome = nome; }

    public String getCategoria()                    { return categoria; }
    public void setCategoria(String categoria)      { this.categoria = categoria; }

    public double getPreco()                        { return preco; }
    public void setPreco(double preco)              { this.preco = preco; }

    public int getQuantidade()                      { return quantidade; }
    public void setQuantidade(int quantidade)       { this.quantidade = quantidade; }

    public int getEstoqueMinimo()                   { return estoqueMinimo; }
    public void setEstoqueMinimo(int estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }

    public int getIdFornecedor()                    { return idFornecedor; }
    public void setIdFornecedor(int idFornecedor)   { this.idFornecedor = idFornecedor; }

    public boolean estoqueBaixo() {
        return quantidade <= estoqueMinimo;
    }

    @Override
    public String toString() {
        String alerta = estoqueBaixo() ? " ⚠ ESTOQUE BAIXO" : "";
        return String.format("[%d] %s | %s | R$ %.2f | Qtd: %d%s",
                id, nome, categoria, preco, quantidade, alerta);
    }
}
