package br.unipar.poo.relatorio;

import br.unipar.poo.model.Categoria;
import br.unipar.poo.model.Gasto;
import br.unipar.poo.model.Pagamento;
import br.unipar.poo.repository.CategoriaRepository;
import br.unipar.poo.repository.GastoRepository;
import br.unipar.poo.repository.PagamentoRepository;

import java.math.BigDecimal;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class Relatorio {

    private final GastoRepository gastoRepository;
    private final PagamentoRepository pagamentoRepository;
    private final CategoriaRepository categoriaRepository;

    public Relatorio() {
        this.gastoRepository = new GastoRepository();
        this.pagamentoRepository = new PagamentoRepository();
        this.categoriaRepository = new CategoriaRepository();
    }

    public void relatorioMensal(int mes, int ano) {
        String nomeMes = Month.of(mes).getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
        System.out.println("\n========== RELATÓRIO MENSAL: " + nomeMes.toUpperCase() + "/" + ano + " ==========");

        List<Gasto> gastos = gastoRepository.listar().stream()
                .filter(g -> g.getDataVencimento().getMonthValue() == mes
                        && g.getDataVencimento().getYear() == ano)
                .toList();

        if (gastos.isEmpty()) {
            System.out.println("  Nenhum gasto encontrado para este período.");
            return;
        }

        BigDecimal totalGastos = BigDecimal.ZERO;
        BigDecimal totalPago   = BigDecimal.ZERO;

        System.out.printf("  %-5s %-30s %-12s %-10s %-12s%n", "ID", "DESCRIÇÃO", "VALOR", "STATUS", "VENCIMENTO");
        System.out.println("  " + "-".repeat(75));

        for (Gasto g : gastos) {
            System.out.printf("  %-5d %-30s R$%-10.2f %-10s %-12s%n",
                    g.getId(), g.getDescricao(), g.getValor(),
                    g.getStatus(), g.getDataVencimento());
            totalGastos = totalGastos.add(g.getValor());

            List<Pagamento> pagamentos = pagamentoRepository.listarPorGasto(g.getId());
            for (Pagamento p : pagamentos) {
                System.out.printf("    └─ Pago em %s via %-8s R$ %.2f%n",
                        p.getDataPagamento(), p.getForma(), p.getValorPago());
                totalPago = totalPago.add(p.getValorPago());
            }
        }

        System.out.println("  " + "-".repeat(75));
        System.out.printf("  Total de gastos: R$ %.2f%n", totalGastos);
        System.out.printf("  Total pago:      R$ %.2f%n", totalPago);
        System.out.printf("  Em aberto:       R$ %.2f%n", totalGastos.subtract(totalPago));
    }

    public void gastosPorCategoria() {
        System.out.println("\n========== GASTOS POR CATEGORIA ==========");
        List<Categoria> categorias = categoriaRepository.listar();

        if (categorias.isEmpty()) {
            System.out.println("  Nenhuma categoria cadastrada.");
            return;
        }

        System.out.printf("  %-25s %-15s %-15s %-10s%n", "CATEGORIA", "ORÇAMENTO", "GASTO TOTAL", "SITUAÇÃO");
        System.out.println("  " + "-".repeat(70));

        for (Categoria cat : categorias) {
            List<Gasto> gastos = gastoRepository.listarPorCategoria(cat.getId());
            BigDecimal total = gastos.stream()
                    .map(Gasto::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String situacao = total.compareTo(cat.getOrcamentoMensal()) > 0 ? "⚠ EXCEDIDO" : "OK";
            System.out.printf("  %-25s R$%-13.2f R$%-13.2f %-10s%n",
                    cat.getNome(), cat.getOrcamentoMensal(), total, situacao);
        }
    }

    public void alertasOrcamento(int mes, int ano) {
        System.out.println("\n========== ALERTAS DE ORÇAMENTO ==========");
        List<Categoria> categorias = categoriaRepository.listar();
        boolean algumAlerta = false;

        for (Categoria cat : categorias) {
            BigDecimal gasto = categoriaRepository.somarGastosDoMes(cat.getId(), mes, ano);
            if (gasto.compareTo(cat.getOrcamentoMensal()) > 0) {
                BigDecimal excesso = gasto.subtract(cat.getOrcamentoMensal());
                System.out.printf("  ⚠  %-20s Orçamento: R$ %.2f | Gasto: R$ %.2f | Excesso: R$ %.2f%n",
                        cat.getNome(), cat.getOrcamentoMensal(), gasto, excesso);
                algumAlerta = true;
            }
        }

        if (!algumAlerta)
            System.out.println("  Nenhuma categoria ultrapassou o orçamento neste mês.");
    }
}