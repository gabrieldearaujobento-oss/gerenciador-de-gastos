package br.unipar.poo.repository;

import br.unipar.poo.connection.ConnectionFactory;
import br.unipar.poo.enums.FormaPagamento;
import br.unipar.poo.model.Pagamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagamentoRepository implements Repositorio<Pagamento> {

    private final Connection conn;

    public PagamentoRepository() {
        this.conn = ConnectionFactory.getInstance().getConnection();
    }

    public PagamentoRepository(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Pagamento salvar(Pagamento p) {
        String sql = "INSERT INTO pagamentos (gasto_id, valor_pago, forma, data_pagamento, observacao) VALUES (?,?,?,?,?) RETURNING id, criado_em";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getGastoId());
            ps.setBigDecimal(2, p.getValorPago());
            ps.setString(3, p.getForma().name());
            ps.setDate(4, Date.valueOf(p.getDataPagamento()));
            ps.setString(5, p.getObservacao());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                p.setId(rs.getInt("id"));
                p.setCriadoEm(rs.getTimestamp("criado_em").toLocalDateTime());
            }
            return p;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar pagamento: " + e.getMessage(), e);
        }
    }

    // Versão que usa uma conexão externa (para transação compartilhada)
    public Pagamento salvar(Pagamento p, Connection connExt) {
        String sql = "INSERT INTO pagamentos (gasto_id, valor_pago, forma, data_pagamento, observacao) VALUES (?,?,?,?,?) RETURNING id, criado_em";
        try (PreparedStatement ps = connExt.prepareStatement(sql)) {
            ps.setInt(1, p.getGastoId());
            ps.setBigDecimal(2, p.getValorPago());
            ps.setString(3, p.getForma().name());
            ps.setDate(4, Date.valueOf(p.getDataPagamento()));
            ps.setString(5, p.getObservacao());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                p.setId(rs.getInt("id"));
                p.setCriadoEm(rs.getTimestamp("criado_em").toLocalDateTime());
            }
            return p;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar pagamento: " + e.getMessage(), e);
        }
    }

    @Override
    public Pagamento buscarPorId(int id) {
        String sql = "SELECT * FROM pagamentos WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pagamento: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pagamento> listar() {
        String sql = "SELECT * FROM pagamentos ORDER BY data_pagamento DESC";
        List<Pagamento> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pagamentos: " + e.getMessage(), e);
        }
    }

    public List<Pagamento> listarPorGasto(int gastoId) {
        String sql = "SELECT * FROM pagamentos WHERE gasto_id = ? ORDER BY data_pagamento DESC";
        List<Pagamento> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gastoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pagamentos do gasto: " + e.getMessage(), e);
        }
    }

    @Override
    public Pagamento atualizar(int id, Pagamento dados) {
        // pagamento não é editável — use estorno
        throw new UnsupportedOperationException("Pagamentos não podem ser editados. Use estorno.");
    }

    @Override
    public boolean deletar(int id) {
        String sql = "DELETE FROM pagamentos WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar pagamento: " + e.getMessage(), e);
        }
    }

    public boolean deletar(int id, Connection connExt) {
        String sql = "DELETE FROM pagamentos WHERE id = ?";
        try (PreparedStatement ps = connExt.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar pagamento: " + e.getMessage(), e);
        }
    }

    private Pagamento mapear(ResultSet rs) throws SQLException {
        Pagamento p = new Pagamento(
                rs.getDate("data_pagamento").toLocalDate(),
                FormaPagamento.valueOf(rs.getString("forma")),
                rs.getInt("gasto_id"),
                rs.getString("observacao"),
                rs.getBigDecimal("valor_pago")
        );
        p.setId(rs.getInt("id"));
        p.setCriadoEm(rs.getTimestamp("criado_em").toLocalDateTime());
        return p;
    }
}