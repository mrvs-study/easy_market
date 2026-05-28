package dao;

import connection.Conexao;
import model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public void cadastrar(Produto p) {
        String sql = "INSERT INTO produto (nome, categoria, preco, quantidade, estoque_minimo, id_fornecedor) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getCategoria());
            ps.setDouble(3, p.getPreco());
            ps.setInt(4, p.getQuantidade());
            ps.setInt(5, p.getEstoqueMinimo());
            if (p.getIdFornecedor() > 0) ps.setInt(6, p.getIdFornecedor());
            else ps.setNull(6, Types.INTEGER);
            ps.executeUpdate();
            System.out.println("Produto cadastrado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar produto: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
    }

    public void alterar(Produto p) {
        String sql = "UPDATE produto SET nome=?, categoria=?, preco=?, quantidade=?, estoque_minimo=?, id_fornecedor=? WHERE id=?";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getCategoria());
            ps.setDouble(3, p.getPreco());
            ps.setInt(4, p.getQuantidade());
            ps.setInt(5, p.getEstoqueMinimo());
            if (p.getIdFornecedor() > 0) ps.setInt(6, p.getIdFornecedor());
            else ps.setNull(6, Types.INTEGER);
            ps.setInt(7, p.getId());
            int linhas = ps.executeUpdate();
            System.out.println(linhas > 0 ? "Produto alterado com sucesso!"
                                          : "Erro - Produto não alterado.");
        } catch (SQLException e) {
            System.out.println("Dados do produto inválidos: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
    }

    public void remover(int id) {
        String sql = "DELETE FROM produto WHERE id=?";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int linhas = ps.executeUpdate();
            System.out.println(linhas > 0 ? "Produto removido com sucesso!"
                                          : "Erro - Produto não existente.");
        } catch (SQLException e) {
            System.out.println("Erro ao remover produto: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
    }

    public List<Produto> listar() {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produto ORDER BY nome";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
            if (lista.isEmpty()) System.out.println("Nenhum produto encontrado.");
        } catch (SQLException e) {
            System.out.println("Erro durante listagem do produto: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
        return lista;
    }

    public List<Produto> buscarPorNomeOuCategoria(String termo) {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produto WHERE nome LIKE ? OR categoria LIKE ? ORDER BY nome";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + termo + "%");
            ps.setString(2, "%" + termo + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
            if (lista.isEmpty()) System.out.println("Nenhum produto encontrado.");
        } catch (SQLException e) {
            System.out.println("Erro ao buscar produto: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
        return lista;
    }

    public Produto buscarPorId(int id) {
        String sql = "SELECT * FROM produto WHERE id=?";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.out.println("Erro ao buscar produto: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
        return null;
    }

    /** Retorna produtos com quantidade <= estoque_minimo */
    public List<Produto> listarEstoqueBaixo() {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produto WHERE quantidade <= estoque_minimo ORDER BY quantidade";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.out.println("Erro ao verificar o estoque: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
        return lista;
    }

    /** Desconta quantidade do estoque (usado na venda) */
    public boolean descontarEstoque(int idProduto, int quantidade, Connection conn) throws SQLException {
        String sql = "UPDATE produto SET quantidade = quantidade - ? WHERE id=? AND quantidade >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantidade);
            ps.setInt(2, idProduto);
            ps.setInt(3, quantidade);
            return ps.executeUpdate() > 0;
        }
    }

    /** Restaura quantidade do estoque (cancelamento de venda) */
    public void restaurarEstoque(int idProduto, int quantidade, Connection conn) throws SQLException {
        String sql = "UPDATE produto SET quantidade = quantidade + ? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantidade);
            ps.setInt(2, idProduto);
            ps.executeUpdate();
        }
    }

    private Produto mapear(ResultSet rs) throws SQLException {
        Produto p = new Produto();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setCategoria(rs.getString("categoria"));
        p.setPreco(rs.getDouble("preco"));
        p.setQuantidade(rs.getInt("quantidade"));
        p.setEstoqueMinimo(rs.getInt("estoque_minimo"));
        p.setIdFornecedor(rs.getInt("id_fornecedor"));
        return p;
    }
}
