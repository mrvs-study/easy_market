package dao;

import connection.Conexao;
import model.Fornecedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FornecedorDAO {

    public void cadastrar(Fornecedor f) {
        String sql = "INSERT INTO fornecedor (nome, cnpj, telefone, email, produtos_fornecidos) VALUES (?, ?, ?, ?, ?)";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getNome());
            ps.setString(2, f.getCnpj());
            ps.setString(3, f.getTelefone());
            ps.setString(4, f.getEmail());
            ps.setString(5, f.getProdutosFornecidos());
            ps.executeUpdate();
            System.out.println("Cadastro realizado com sucesso!");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Erro: Dados inválidos (CNPJ já cadastrado).");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar fornecedor: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
    }

    public void alterar(Fornecedor f) {
        String sql = "UPDATE fornecedor SET nome=?, cnpj=?, telefone=?, email=?, produtos_fornecidos=? WHERE id=?";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getNome());
            ps.setString(2, f.getCnpj());
            ps.setString(3, f.getTelefone());
            ps.setString(4, f.getEmail());
            ps.setString(5, f.getProdutosFornecidos());
            ps.setInt(6, f.getId());
            int linhas = ps.executeUpdate();
            System.out.println(linhas > 0 ? "Fornecedor alterado com sucesso!"
                                          : "Não foi possível concluir alteração - Fornecedor não encontrado.");
        } catch (SQLException e) {
            System.out.println("Erro ao alterar fornecedor: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
    }

    public void remover(int id) {
        String sql = "DELETE FROM fornecedor WHERE id=?";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int linhas = ps.executeUpdate();
            System.out.println(linhas > 0 ? "Fornecedor removido com sucesso!"
                                          : "Fornecedor não encontrado.");
        } catch (SQLException e) {
            System.out.println("Erro durante a remoção do fornecedor: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
    }

    public List<Fornecedor> listar() {
        List<Fornecedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM fornecedor ORDER BY nome";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
            if (lista.isEmpty()) System.out.println("Nenhum fornecedor cadastrado.");
        } catch (SQLException e) {
            System.out.println("Erro ao listar fornecedores: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
        return lista;
    }

    public Fornecedor buscarPorId(int id) {
        String sql = "SELECT * FROM fornecedor WHERE id=?";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.out.println("Erro ao buscar fornecedor: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
        return null;
    }

    private Fornecedor mapear(ResultSet rs) throws SQLException {
        Fornecedor f = new Fornecedor();
        f.setId(rs.getInt("id"));
        f.setNome(rs.getString("nome"));
        f.setCnpj(rs.getString("cnpj"));
        f.setTelefone(rs.getString("telefone"));
        f.setEmail(rs.getString("email"));
        f.setProdutosFornecidos(rs.getString("produtos_fornecidos"));
        return f;
    }
}
