package dao;

import connection.Conexao;
import model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public void cadastrar(Cliente c) {
        String sql = "INSERT INTO cliente (nome, cpf, telefone, email) VALUES (?, ?, ?, ?)";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getCpf());
            ps.setString(3, c.getTelefone());
            ps.setString(4, c.getEmail());
            ps.executeUpdate();
            System.out.println("Cliente cadastrado com sucesso!");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Erro: Cadastro já efetuado (CPF duplicado).");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar cliente: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
    }

    public void alterar(Cliente c) {
        String sql = "UPDATE cliente SET nome=?, cpf=?, telefone=?, email=? WHERE id=?";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getCpf());
            ps.setString(3, c.getTelefone());
            ps.setString(4, c.getEmail());
            ps.setInt(5, c.getId());
            int linhas = ps.executeUpdate();
            System.out.println(linhas > 0 ? "Dados alterados com sucesso!" : "Erro: Cliente não encontrado.");
        } catch (SQLException e) {
            System.out.println("Erro ao alterar cliente: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
    }

    public void remover(int id) {
        String sql = "DELETE FROM cliente WHERE id=?";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int linhas = ps.executeUpdate();
            System.out.println(linhas > 0 ? "Cliente removido com sucesso!" : "Erro: Cliente inexistente.");
        } catch (SQLException e) {
            System.out.println("Erro ao remover cliente: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
    }

    public List<Cliente> listar() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente ORDER BY nome";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
            if (lista.isEmpty()) System.out.println("Nenhum cliente cadastrado.");
        } catch (SQLException e) {
            System.out.println("Erro ao listar clientes: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
        return lista;
    }

    public List<Cliente> buscarPorNomeOuCpf(String termo) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE nome LIKE ? OR cpf LIKE ? ORDER BY nome";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + termo + "%");
            ps.setString(2, "%" + termo + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
            if (lista.isEmpty()) System.out.println("Nenhum cliente encontrado.");
        } catch (SQLException e) {
            System.out.println("Erro ao buscar cliente: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
        return lista;
    }

    public Cliente buscarPorId(int id) {
        String sql = "SELECT * FROM cliente WHERE id=?";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.out.println("Erro ao buscar cliente: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
        return null;
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setNome(rs.getString("nome"));
        c.setCpf(rs.getString("cpf"));
        c.setTelefone(rs.getString("telefone"));
        c.setEmail(rs.getString("email"));
        return c;
    }
}
