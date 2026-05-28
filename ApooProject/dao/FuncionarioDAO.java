package dao;

import connection.Conexao;
import model.Funcionario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {

    // ── Cadastrar ──────────────────────────────────────────────────────────────
    public void cadastrar(Funcionario f) {
        String sql = "INSERT INTO funcionario (nome, cpf, cargo, email, senha) VALUES (?, ?, ?, ?, ?)";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getNome());
            ps.setString(2, f.getCpf());
            ps.setString(3, f.getCargo());
            ps.setString(4, f.getEmail());
            ps.setString(5, f.getSenha());
            ps.executeUpdate();
            System.out.println("Funcionário cadastrado com sucesso!");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Erro: Funcionário já existe (CPF ou e-mail duplicado).");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar funcionário: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
    }

    // ── Alterar ────────────────────────────────────────────────────────────────
    public void alterar(Funcionario f) {
        String sql = "UPDATE funcionario SET nome=?, cpf=?, cargo=?, email=? WHERE id=? AND ativo=1";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getNome());
            ps.setString(2, f.getCpf());
            ps.setString(3, f.getCargo());
            ps.setString(4, f.getEmail());
            ps.setInt(5, f.getId());
            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                System.out.println("Funcionário alterado com sucesso!");
            } else {
                System.out.println("Erro: Funcionário inexistente ou inativo.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao alterar funcionário: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
    }

    // ── Remover (soft-delete) ──────────────────────────────────────────────────
    public void remover(int id) {
        String sql = "UPDATE funcionario SET ativo=0 WHERE id=?";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                System.out.println("Funcionário removido com sucesso!");
            } else {
                System.out.println("Erro: Funcionário não encontrado.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao remover funcionário: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
    }

    // ── Listar ─────────────────────────────────────────────────────────────────
    public List<Funcionario> listar() {
        List<Funcionario> lista = new ArrayList<>();
        String sql = "SELECT * FROM funcionario WHERE ativo=1 ORDER BY nome";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            if (lista.isEmpty()) {
                System.out.println("Não há nenhum funcionário cadastrado.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar funcionários: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
        return lista;
    }

    // ── Buscar por ID ──────────────────────────────────────────────────────────
    public Funcionario buscarPorId(int id) {
        String sql = "SELECT * FROM funcionario WHERE id=? AND ativo=1";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.out.println("Erro ao buscar funcionário: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
        return null;
    }

    // ── Login ──────────────────────────────────────────────────────────────────
    public Funcionario login(String email, String senha) {
        String sql = "SELECT * FROM funcionario WHERE email=? AND senha=? AND ativo=1";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, senha);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.out.println("Erro ao realizar login: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
        return null;
    }

    // ── Recuperar senha (reset simples) ───────────────────────────────────────
    public boolean recuperarSenha(String email, String novaSenha) {
        String sql = "UPDATE funcionario SET senha=? WHERE email=? AND ativo=1";
        Connection conn = Conexao.conectar();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, novaSenha);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao recuperar senha: " + e.getMessage());
        } finally {
            Conexao.fechar(conn);
        }
        return false;
    }

    // ── Mapear ResultSet → Funcionario ─────────────────────────────────────────
    private Funcionario mapear(ResultSet rs) throws SQLException {
        Funcionario f = new Funcionario();
        f.setId(rs.getInt("id"));
        f.setNome(rs.getString("nome"));
        f.setCpf(rs.getString("cpf"));
        f.setCargo(rs.getString("cargo"));
        f.setEmail(rs.getString("email"));
        f.setSenha(rs.getString("senha"));
        f.setAtivo(rs.getBoolean("ativo"));
        return f;
    }
}
