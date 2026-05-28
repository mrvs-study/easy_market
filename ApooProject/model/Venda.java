package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venda {

    private int           id;
    private int           idCliente;
    private int           idFuncionario;
    private double        total;
    private String        status;           // "ATIVA" | "CANCELADA"
    private LocalDateTime dataVenda;
    private List<ItemVenda> itens;

    public Venda() {
        this.itens  = new ArrayList<>();
        this.status = "ATIVA";
    }

    public Venda(int idCliente, int idFuncionario) {
        this();
        this.idCliente     = idCliente;
        this.idFuncionario = idFuncionario;
    }

    public void adicionarItem(ItemVenda item) {
        itens.add(item);
        recalcularTotal();
    }

    public void recalcularTotal() {
        total = itens.stream()
                     .mapToDouble(i -> i.getPrecoUnit() * i.getQuantidade())
                     .sum();
    }

    // Getters e Setters
    public int getId()                              { return id; }
    public void setId(int id)                       { this.id = id; }

    public int getIdCliente()                       { return idCliente; }
    public void setIdCliente(int idCliente)         { this.idCliente = idCliente; }

    public int getIdFuncionario()                   { return idFuncionario; }
    public void setIdFuncionario(int v)             { this.idFuncionario = v; }

    public double getTotal()                        { return total; }
    public void setTotal(double total)              { this.total = total; }

    public String getStatus()                       { return status; }
    public void setStatus(String status)            { this.status = status; }

    public LocalDateTime getDataVenda()             { return dataVenda; }
    public void setDataVenda(LocalDateTime d)       { this.dataVenda = d; }

    public List<ItemVenda> getItens()               { return itens; }
    public void setItens(List<ItemVenda> itens)     { this.itens = itens; recalcularTotal(); }

    @Override
    public String toString() {
        return String.format("[%d] %s | R$ %.2f | %s | %s",
                id, dataVenda, total, status,
                idCliente > 0 ? "Cliente ID: " + idCliente : "Sem cliente");
    }

    // ---- Inner class ItemVenda ----
    public static class ItemVenda {
        private int    id;
        private int    idVenda;
        private int    idProduto;
        private String nomeProduto;
        private int    quantidade;
        private double precoUnit;

        public ItemVenda() {}

        public ItemVenda(int idProduto, String nomeProduto, int quantidade, double precoUnit) {
            this.idProduto   = idProduto;
            this.nomeProduto = nomeProduto;
            this.quantidade  = quantidade;
            this.precoUnit   = precoUnit;
        }

        public int getId()                          { return id; }
        public void setId(int id)                   { this.id = id; }

        public int getIdVenda()                     { return idVenda; }
        public void setIdVenda(int idVenda)         { this.idVenda = idVenda; }

        public int getIdProduto()                   { return idProduto; }
        public void setIdProduto(int idProduto)     { this.idProduto = idProduto; }

        public String getNomeProduto()              { return nomeProduto; }
        public void setNomeProduto(String n)        { this.nomeProduto = n; }

        public int getQuantidade()                  { return quantidade; }
        public void setQuantidade(int quantidade)   { this.quantidade = quantidade; }

        public double getPrecoUnit()                { return precoUnit; }
        public void setPrecoUnit(double precoUnit)  { this.precoUnit = precoUnit; }

        @Override
        public String toString() {
            return String.format("  - %s x%d @ R$ %.2f = R$ %.2f",
                    nomeProduto, quantidade, precoUnit, precoUnit * quantidade);
        }
    }
}
