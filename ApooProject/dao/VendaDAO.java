package dao;

import connection.Conexao;
import model.Venda;
import model.Venda.ItemVenda;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VendaDAO {

    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    // ── Registrar venda com transação ──────────────────────────────────────────
    public boolean registrar(Venda venda) {
        if (venda.getItens().isEmpty()) {
            System.out.println("Erro: Venda sem itens.");
            return false;
        }

        String sqlVenda = "INSERT INTO venda (id_cliente, id_funcionario, total, status) VALUES (?, ?, ?, 'ATIVA')";
        String sqlItem  = "INSERT INTO item_venda (id_venda, id_produto, quantidade, preco_unit) VALUES (?, ?, ?, ?)";

        Connection conn = Conexao.conectar();
        try {
            conn.setAutoCommit(false);

            // 1. Verifica e desconta estoque
            for (ItemVenda item : venda.getItens()) {
                boolean ok = produtoDAO.descontarEstoque(item.getIdProduto(), item.getQuantidade(), conn);
                if (!ok) {
                    conn.rollback();
                    System.out.println("Estoque insuficiente para: " + item.getNomeProduto());
                    return false;
                }
            }

            // 2. Insere a venda
            try (PreparedStatement psVenda = conn.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS)) {
                if (venda.getIdCliente() > 0) psVenda.setInt(1, venda.getIdCliente());
                else psVenda.setNull(1, Types.INTEGER);
                psVenda.setInt(2, venda.getIdFuncionario());
                psVenda.setDouble(3, venda.getTotal());
                psVenda.executeUpdate();

                ResultSet keys = psVenda.getGeneratedKeys();
                if (keys.next()) venda.setId(keys.getInt(1));
            }

            // 3. Insere os itens
            try (PreparedStatement psItem = conn.prepareStatement(sqlItem)) {
                for (ItemVenda item : venda.getItens()) {
                    psItem.setInt(1, venda.getId());
                    psItem.setInt(2, item.getIdProduto());
                    psItem.setInt(3, item.getQuantidade());
                    psItem.setDouble(4, item.getPrecoUnit());
                    psItem.addBatch();
                }
                psItem.executeBatch();
            }

            conn.commit();
            System.out.println("Venda realizada com sucesso! ID: " + venda.getId());
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { /* ignora */ }
            System.out.println("Erro ao registrar venda: " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { /* ignora */ }
            Conexao.fechar(conn);
        }
    }

    // ── Cancelar venda ─────────────────────────────────────────────────────────
    public boolean cancelar(int idVenda) {
        String sqlStatus = "UPDATE venda SET status='CANCELADA' WHERE id=? AND status='ATIVA'";
        String sqlItens  = "SELECT id_produto, quantidade FROM item_venda WHERE id_venda=?";

        Connection conn = Conexao.conectar();
        try {
            conn.setAutoCommit(false);

            // 1. Muda status
            int linhas;
            try (PreparedStatement ps = conn.prepareStatement(sqlStatus)) {
                ps.setInt(1, idVenda);
                linhas = ps.executeUpdate();
            }

            if (linhas == 0) {
                conn.rollback();
                System.out.println("Venda não existente ou cancelamento negado.");
                return false;
            }

            // 2. Restaura estoque
            try (PreparedStatement ps = conn.prepareStatement(sqlItens)) {
                ps.setInt(1, idVenda);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    produtoDAO.restaurarEstoque(rs.getInt("id_produto"), rs.getInt("quantidade"), conn);
                }
            }

            conn.commit();
            System.out.println("Venda cancelada com sucesso!");
            return true;

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { /* ignora */ }
            System.out.println("Erro ao cancelar venda: " + e.getMessage());
            return false;
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { /* ignora */ }
            Conexao.fechar(conn);
        }
    }

    // ── Alterar venda (somente total/cliente, status ATIVA) ───────────────────
    public void alterar(Venda v) {
        String sql = "UPDATE venda SET id_cliente=? WHERE id=? AND status='ATIVA'";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (v.getIdCliente() > 0) ps.setInt(1, v.getIdCliente());
            else ps.setNull(1, Types.INTEGER);
            ps.setInt(2, v.getId());
            int linhas = ps.executeUpdate();
            System.out.println(linhas > 0 ? "Venda alterada!"
                                          : "Venda não existente ou dados inválidos.");
        } catch (SQLException e) {
            System.out.println("Dados da venda inválidos: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
    }

    // ── Listar vendas ──────────────────────────────────────────────────────────
    public List<Venda> listar() {
        List<Venda> lista = new ArrayList<>();
        String sql = "SELECT * FROM venda ORDER BY data_venda DESC";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
            if (lista.isEmpty()) System.out.println("Nenhuma venda encontrada na lista.");
        } catch (SQLException e) {
            System.out.println("Erro ao carregar o registro de vendas: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
        return lista;
    }

    public List<Venda> listarPorData(LocalDateTime inicio, LocalDateTime fim) {
        List<Venda> lista = new ArrayList<>();
        String sql = "SELECT * FROM venda WHERE data_venda BETWEEN ? AND ? ORDER BY data_venda DESC";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(inicio));
            ps.setTimestamp(2, Timestamp.valueOf(fim));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.out.println("Erro ao listar vendas por data: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
        return lista;
    }

    // ── Relatório diário ───────────────────────────────────────────────────────
    public void relatorioDiario() {
        LocalDateTime inicio = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime fim    = inicio.plusDays(1).minusSeconds(1);
        gerarRelatorio("RELATÓRIO DIÁRIO", inicio, fim);
    }

    // ── Relatório mensal ───────────────────────────────────────────────────────
    public void relatorioMensal() {
        LocalDateTime agora  = LocalDateTime.now();
        LocalDateTime inicio = agora.toLocalDate().withDayOfMonth(1).atStartOfDay();
        LocalDateTime fim    = inicio.plusMonths(1).minusSeconds(1);
        gerarRelatorio("RELATÓRIO MENSAL", inicio, fim);
    }

    private void gerarRelatorio(String titulo, LocalDateTime inicio, LocalDateTime fim) {
        String sql = "SELECT COUNT(*) AS qtd, SUM(total) AS faturamento " +
                     "FROM venda WHERE status='ATIVA' AND data_venda BETWEEN ? AND ?";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(inicio));
            ps.setTimestamp(2, Timestamp.valueOf(fim));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int    qtd         = rs.getInt("qtd");
                double faturamento = rs.getDouble("faturamento");
                System.out.println("\n========== " + titulo + " ==========");
                System.out.printf("Período   : %s  →  %s%n", inicio.toLocalDate(), fim.toLocalDate());
                if (qtd == 0) {
                    System.out.println("Nenhum dado registrado.");
                } else {
                    System.out.printf("Vendas    : %d%n", qtd);
                    System.out.printf("Faturamento: R$ %.2f%n", faturamento);
                }
                System.out.println("==========================================\n");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gerar relatório: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
    }

    private Venda mapear(ResultSet rs) throws SQLException {
        Venda v = new Venda();
        v.setId(rs.getInt("id"));
        v.setIdCliente(rs.getInt("id_cliente"));
        v.setIdFuncionario(rs.getInt("id_funcionario"));
        v.setTotal(rs.getDouble("total"));
        v.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("data_venda");
        if (ts != null) v.setDataVenda(ts.toLocalDateTime());
        return v;
    }
}
