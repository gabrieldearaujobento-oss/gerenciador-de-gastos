package br.unipar.poo.repository;

import br.unipar.poo.connection.ConnectionFactory;
import br.unipar.poo.model.Categoria;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaRepository implements Repositorio<Categoria> {

    private final Connection conn;

    public CategoriaRepository() {
        this.conn = ConnectionFactory.getInstance().getConnection();
    }

    @Override
    public Categoria salvar(Categoria c) {
        String sql = "INSERT INTO categorias (nome, descricao, orcamento_mensal) VALUES (?,?,?) RETURNING id, criado_em";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getDescricao());
            ps.setBigDecimal(3, c.getOrcamentoMensal());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                c.setId(rs.getInt("id"));
                c.setCriadoEm(rs.getTimestamp("criado_em").toLocalDateTime());
            }
            return c;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar categoria: " + e.getMessage(), e);
        }
    }

    @Override
    public Categoria buscarPorId(int id) {
        String sql = "SELECT * FROM categorias WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar categoria: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Categoria> listar() {
        String sql = "SELECT * FROM categorias ORDER BY nome";
        List<Categoria> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar categorias: " + e.getMessage(), e);
        }
    }

    @Override
    public Categoria atualizar(int id, Categoria dados) {
        String sql = "UPDATE categorias SET nome=?, descricao=?, orcamento_mensal=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dados.getNome());
            ps.setString(2, dados.getDescricao());
            ps.setBigDecimal(3, dados.getOrcamentoMensal());
            ps.setInt(4, id);
            ps.executeUpdate();
            return buscarPorId(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar categoria: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deletar(int id) {
        String sql = "DELETE FROM categorias WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar categoria: " + e.getMessage(), e);
        }
    }

    public BigDecimal somarGastosDoMes(int categoriaId, int mes, int ano) {
        String sql = """
            SELECT COALESCE(SUM(g.valor), 0)
            FROM gastos g
            WHERE g.categoria_id = ?
              AND EXTRACT(MONTH FROM g.data_vencimento) = ?
              AND EXTRACT(YEAR  FROM g.data_vencimento) = ?
              AND g.status <> 'CANCELADO'
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoriaId);
            ps.setInt(2, mes);
            ps.setInt(3, ano);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getBigDecimal(1);
            return BigDecimal.ZERO;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao somar gastos: " + e.getMessage(), e);
        }
    }

    private Categoria mapear(ResultSet rs) throws SQLException {
        Categoria c = new Categoria(
                rs.getString("descricao"),
                rs.getString("nome"),
                rs.getBigDecimal("orcamento_mensal")
        );
        c.setId(rs.getInt("id"));
        c.setCriadoEm(rs.getTimestamp("criado_em").toLocalDateTime());
        return c;
    }
}