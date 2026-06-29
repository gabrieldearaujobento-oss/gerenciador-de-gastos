package br.unipar.poo.repository;

import br.unipar.poo.connection.ConnectionFactory;
import br.unipar.poo.enums.StatusGasto;
import br.unipar.poo.model.Gasto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GastoRepository implements Repositorio<Gasto> {

    private final Connection conn;

    public GastoRepository() {
        this.conn = ConnectionFactory.getInstance().getConnection();
    }

    public GastoRepository(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Gasto salvar(Gasto g) {
        String sql = "INSERT INTO gastos (descricao, valor, categoria_id, data_vencimento, status) VALUES (?,?,?,?,?) RETURNING id, criado_em";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, g.getDescricao());
            ps.setBigDecimal(2, g.getValor());
            ps.setInt(3, g.getCategoriaID());
            ps.setDate(4, Date.valueOf(g.getDataVencimento()));
            ps.setString(5, g.getStatus().name());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                g.setId(rs.getInt("id"));
                g.setCriadoEm(rs.getTimestamp("criado_em").toLocalDateTime());
            }
            return g;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar gasto: " + e.getMessage(), e);
        }
    }

    @Override
    public Gasto buscarPorId(int id) {
        String sql = "SELECT * FROM gastos WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar gasto: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Gasto> listar() {
        String sql = "SELECT * FROM gastos ORDER BY data_vencimento";
        List<Gasto> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar gastos: " + e.getMessage(), e);
        }
    }

    public List<Gasto> listarPorStatus(StatusGasto status) {
        String sql = "SELECT * FROM gastos WHERE status = ? ORDER BY data_vencimento";
        List<Gasto> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar por status: " + e.getMessage(), e);
        }
    }

    public List<Gasto> listarPorCategoria(int categoriaId) {
        String sql = "SELECT * FROM gastos WHERE categoria_id = ? ORDER BY data_vencimento";
        List<Gasto> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoriaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar por categoria: " + e.getMessage(), e);
        }
    }

    @Override
    public Gasto atualizar(int id, Gasto dados) {
        String sql = "UPDATE gastos SET descricao=?, valor=?, categoria_id=?, data_vencimento=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dados.getDescricao());
            ps.setBigDecimal(2, dados.getValor());
            ps.setInt(3, dados.getCategoriaID());
            ps.setDate(4, Date.valueOf(dados.getDataVencimento()));
            ps.setInt(5, id);
            ps.executeUpdate();
            return buscarPorId(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar gasto: " + e.getMessage(), e);
        }
    }

    public void atualizarStatus(int id, StatusGasto status, Connection connExt) {
        Connection c = (connExt != null) ? connExt : this.conn;
        String sql = "UPDATE gastos SET status = ? WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deletar(int id) {
        String sql = "DELETE FROM gastos WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar gasto: " + e.getMessage(), e);
        }
    }

    private Gasto mapear(ResultSet rs) throws SQLException {
        Gasto g = new Gasto(
                rs.getInt("categoria_id"),
                rs.getDate("data_vencimento").toLocalDate(),
                rs.getString("descricao"),
                rs.getBigDecimal("valor")
        );
        g.setId(rs.getInt("id"));
        g.setStatus(StatusGasto.valueOf(rs.getString("status")));
        g.setCriadoEm(rs.getTimestamp("criado_em").toLocalDateTime());
        return g;
    }
}