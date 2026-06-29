package br.unipar.poo.service;

import br.unipar.poo.connection.ConnectionFactory;
import br.unipar.poo.enums.FormaPagamento;
import br.unipar.poo.enums.StatusGasto;
import br.unipar.poo.model.Gasto;
import br.unipar.poo.model.Pagamento;
import br.unipar.poo.repository.GastoRepository;
import br.unipar.poo.repository.PagamentoRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class PagamentoService {

    private final GastoRepository gastoRepository;
    private final PagamentoRepository pagamentoRepository;
    private final Connection conn;

    public PagamentoService() {
        this.conn = ConnectionFactory.getInstance().getConnection();
        this.gastoRepository = new GastoRepository(conn);
        this.pagamentoRepository = new PagamentoRepository(conn);
    }

    /**
     * Registra o pagamento de um gasto.
     * Operação transacional: insere pagamento + atualiza status em um único commit.
     */
    public Pagamento pagar(int gastoId, BigDecimal valorPago, FormaPagamento forma) {
        Gasto gasto = gastoRepository.buscarPorId(gastoId);

        // Regras de negócio
        if (gasto == null)
            throw new IllegalArgumentException("Gasto #" + gastoId + " não encontrado.");
        if (gasto.getStatus() != StatusGasto.PENDENTE)
            throw new IllegalStateException("Gasto #" + gastoId + " não está PENDENTE (status atual: " + gasto.getStatus() + ").");
        if (valorPago == null || valorPago.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Valor pago deve ser maior que zero.");
        if (valorPago.compareTo(gasto.getValor()) > 0)
            throw new IllegalArgumentException("Valor pago (R$ " + valorPago + ") não pode ser maior que o valor do gasto (R$ " + gasto.getValor() + ").");

        try {
            conn.setAutoCommit(false); // BEGIN

            Pagamento pagamento = new Pagamento(LocalDate.now(), forma, gastoId, null, valorPago);
            pagamentoRepository.salvar(pagamento, conn);
            gastoRepository.atualizarStatus(gastoId, StatusGasto.PAGO, conn);

            conn.commit(); // COMMIT
            conn.setAutoCommit(true);
            return pagamento;

        } catch (Exception e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException("Falha ao registrar pagamento. Operação desfeita. Detalhe: " + e.getMessage(), e);
        }
    }

    /**
     * Estorna um pagamento: remove o pagamento e volta o gasto para PENDENTE.
     * Também transacional.
     */
    public void estornar(int pagamentoId) {
        Pagamento pagamento = pagamentoRepository.buscarPorId(pagamentoId);
        if (pagamento == null)
            throw new IllegalArgumentException("Pagamento #" + pagamentoId + " não encontrado.");

        Gasto gasto = gastoRepository.buscarPorId(pagamento.getGastoId());
        if (gasto == null)
            throw new IllegalStateException("Gasto vinculado não encontrado.");
        if (gasto.getStatus() != StatusGasto.PAGO)
            throw new IllegalStateException("Não é possível estornar: gasto não está PAGO.");

        try {
            conn.setAutoCommit(false); // BEGIN

            pagamentoRepository.deletar(pagamentoId, conn);
            gastoRepository.atualizarStatus(gasto.getId(), StatusGasto.PENDENTE, conn);

            conn.commit(); // COMMIT
            conn.setAutoCommit(true);

        } catch (Exception e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException("Falha ao estornar pagamento. Operação desfeita. Detalhe: " + e.getMessage(), e);
        }
    }
}