package br.unipar.poo.ui;

import br.unipar.poo.enums.FormaPagamento;
import br.unipar.poo.enums.StatusGasto;
import br.unipar.poo.model.Categoria;
import br.unipar.poo.model.Gasto;
import br.unipar.poo.model.Pagamento;
import br.unipar.poo.relatorio.Relatorio;
import br.unipar.poo.repository.CategoriaRepository;
import br.unipar.poo.repository.GastoRepository;
import br.unipar.poo.repository.PagamentoRepository;
import br.unipar.poo.service.PagamentoService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class MenuPrincipal {

    private final Scanner sc = new Scanner(System.in);
    private final CategoriaRepository categoriaRepo = new CategoriaRepository();
    private final GastoRepository gastoRepo         = new GastoRepository();
    private final PagamentoRepository pagamentoRepo  = new PagamentoRepository();
    private final PagamentoService pagamentoService  = new PagamentoService();
    private final Relatorio relatorio                = new Relatorio();

    public void iniciar() {
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║   GERENCIADOR DE GASTOS - v1.0   ║");
        System.out.println("╚══════════════════════════════════╝");

        boolean rodando = true;
        while (rodando) {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1. Categorias");
            System.out.println("2. Gastos");
            System.out.println("3. Pagamentos");
            System.out.println("4. Relatórios");
            System.out.println("0. Sair");
            int op = lerInt("Opção: ");
            switch (op) {
                case 1 -> menuCategorias();
                case 2 -> menuGastos();
                case 3 -> menuPagamentos();
                case 4 -> menuRelatorios();
                case 0 -> rodando = false;
                default -> System.out.println("Opção inválida.");
            }
        }
        System.out.println("Encerrando o sistema. Até logo!");
    }

    private void menuCategorias() {
        System.out.println("\n-- CATEGORIAS --");
        System.out.println("1. Nova categoria");
        System.out.println("2. Listar categorias");
        System.out.println("3. Editar categoria");
        System.out.println("4. Excluir categoria");
        System.out.println("0. Voltar");
        switch (lerInt("Opção: ")) {
            case 1 -> criarCategoria();
            case 2 -> listarCategorias();
            case 3 -> editarCategoria();
            case 4 -> excluirCategoria();
        }
    }

    private void criarCategoria() {
        String nome = lerTexto("Nome da categoria: ");
        String desc = lerTexto("Descrição (Enter para pular): ");
        BigDecimal orc = lerDecimal("Orçamento mensal (R$): ");
        try {
            Categoria c = categoriaRepo.salvar(new Categoria(desc, nome, orc));
            System.out.println("Categoria criada com ID #" + c.getId());
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void listarCategorias() {
        List<Categoria> lista = categoriaRepo.listar();
        if (lista.isEmpty()) { System.out.println("Nenhuma categoria."); return; }
        System.out.printf("  %-5s %-25s %-15s%n", "ID", "NOME", "ORÇAMENTO");
        System.out.println("  " + "-".repeat(50));
        lista.forEach(c -> System.out.printf("  %-5d %-25s R$ %.2f%n",
                c.getId(), c.getNome(), c.getOrcamentoMensal()));
    }

    private void editarCategoria() {
        listarCategorias();
        int id = lerInt("ID da categoria a editar: ");
        Categoria atual = categoriaRepo.buscarPorId(id);
        if (atual == null) { System.out.println("Categoria não encontrada."); return; }
        String nome = lerTexto("Novo nome (" + atual.getNome() + "): ");
        String desc = lerTexto("Nova descrição (" + atual.getDescricao() + "): ");
        BigDecimal orc = lerDecimal("Novo orçamento (atual: " + atual.getOrcamentoMensal() + "): ");
        categoriaRepo.atualizar(id, new Categoria(
                desc.isBlank() ? atual.getDescricao() : desc,
                nome.isBlank() ? atual.getNome() : nome,
                orc));
        System.out.println("Categoria atualizada.");
    }

    private void excluirCategoria() {
        listarCategorias();
        int id = lerInt("ID da categoria a excluir: ");
        try {
            boolean ok = categoriaRepo.deletar(id);
            System.out.println(ok ? "Excluída." : "Categoria não encontrada.");
        } catch (Exception e) {
            System.out.println("Não é possível excluir: " + e.getMessage());
        }
    }

    private void menuGastos() {
        System.out.println("\n-- GASTOS --");
        System.out.println("1. Novo gasto");
        System.out.println("2. Listar todos");
        System.out.println("3. Listar por status");
        System.out.println("4. Cancelar gasto");
        System.out.println("5. Excluir gasto");
        System.out.println("0. Voltar");
        switch (lerInt("Opção: ")) {
            case 1 -> criarGasto();
            case 2 -> listarGastos(gastoRepo.listar());
            case 3 -> filtrarGastosPorStatus();
            case 4 -> cancelarGasto();
            case 5 -> excluirGasto();
        }
    }

    private void criarGasto() {
        listarCategorias();
        int catId = lerInt("ID da categoria: ");
        if (categoriaRepo.buscarPorId(catId) == null) { System.out.println("Categoria não encontrada."); return; }
        String desc  = lerTexto("Descrição do gasto: ");
        BigDecimal val = lerDecimal("Valor (R$): ");
        LocalDate venc = lerData("Data de vencimento (AAAA-MM-DD): ");
        try {
            Gasto g = gastoRepo.salvar(new Gasto(catId, venc, desc, val));
            System.out.println("Gasto criado com ID #" + g.getId());
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void listarGastos(List<Gasto> lista) {
        if (lista.isEmpty()) { System.out.println("Nenhum gasto."); return; }
        System.out.printf("  %-5s %-28s %-10s %-12s %-10s%n", "ID", "DESCRIÇÃO", "VALOR", "VENCIMENTO", "STATUS");
        System.out.println("  " + "-".repeat(72));
        lista.forEach(g -> System.out.printf("  %-5d %-28s R$%-8.2f %-12s %-10s%n",
                g.getId(), g.getDescricao(), g.getValor(), g.getDataVencimento(), g.getStatus()));
    }

    private void filtrarGastosPorStatus() {
        System.out.println("Status: 1-PENDENTE  2-PAGO  3-CANCELADO");
        int op = lerInt("Opção: ");
        StatusGasto status = switch (op) {
            case 1 -> StatusGasto.PENDENTE;
            case 2 -> StatusGasto.PAGO;
            case 3 -> StatusGasto.CANCELADO;
            default -> { System.out.println("Inválido."); yield null; }
        };
        if (status != null) listarGastos(gastoRepo.listarPorStatus(status));
    }

    private void cancelarGasto() {
        listarGastos(gastoRepo.listarPorStatus(StatusGasto.PENDENTE));
        int id = lerInt("ID do gasto a cancelar: ");
        Gasto g = gastoRepo.buscarPorId(id);
        if (g == null) { System.out.println("Gasto não encontrado."); return; }
        if (g.getStatus() != StatusGasto.PENDENTE) { System.out.println("Só gastos PENDENTES podem ser cancelados."); return; }
        gastoRepo.atualizarStatus(id, StatusGasto.CANCELADO, null);
        System.out.println("Gasto cancelado.");
    }

    private void excluirGasto() {
        listarGastos(gastoRepo.listar());
        int id = lerInt("ID do gasto a excluir: ");
        try {
            boolean ok = gastoRepo.deletar(id);
            System.out.println(ok ? "Excluído." : "Gasto não encontrado.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void menuPagamentos() {
        System.out.println("\n-- PAGAMENTOS --");
        System.out.println("1. Registrar pagamento");
        System.out.println("2. Estornar pagamento");
        System.out.println("3. Ver pagamentos de um gasto");
        System.out.println("0. Voltar");
        switch (lerInt("Opção: ")) {
            case 1 -> registrarPagamento();
            case 2 -> estornarPagamento();
            case 3 -> verPagamentosGasto();
        }
    }

    private void registrarPagamento() {
        listarGastos(gastoRepo.listarPorStatus(StatusGasto.PENDENTE));
        int gastoId = lerInt("ID do gasto a pagar: ");
        BigDecimal valor = lerDecimal("Valor a pagar (R$): ");
        System.out.println("Forma: 1-DINHEIRO  2-CARTAO  3-PIX");
        int op = lerInt("Opção: ");
        FormaPagamento forma = switch (op) {
            case 1 -> FormaPagamento.DINHEIRO;
            case 2 -> FormaPagamento.CARTAO;
            case 3 -> FormaPagamento.PIX;
            default -> { System.out.println("Forma inválida."); yield null; }
        };
        if (forma == null) return;
        try {
            Pagamento p = pagamentoService.pagar(gastoId, valor, forma);
            System.out.println("Pagamento registrado com ID #" + p.getId());
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void estornarPagamento() {
        int id = lerInt("ID do pagamento a estornar: ");
        try {
            pagamentoService.estornar(id);
            System.out.println("Pagamento estornado. Gasto voltou para PENDENTE.");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void verPagamentosGasto() {
        int gastoId = lerInt("ID do gasto: ");
        List<Pagamento> lista = pagamentoRepo.listarPorGasto(gastoId);
        if (lista.isEmpty()) { System.out.println("Nenhum pagamento encontrado."); return; }
        System.out.printf("  %-5s %-12s %-10s %-10s%n", "ID", "DATA", "FORMA", "VALOR");
        System.out.println("  " + "-".repeat(45));
        lista.forEach(p -> System.out.printf("  %-5d %-12s %-10s R$ %.2f%n",
                p.getId(), p.getDataPagamento(), p.getForma(), p.getValorPago()));
    }

    private void menuRelatorios() {
        System.out.println("\n-- RELATÓRIOS --");
        System.out.println("1. Relatório mensal");
        System.out.println("2. Gastos por categoria");
        System.out.println("3. Alertas de orçamento");
        System.out.println("0. Voltar");
        switch (lerInt("Opção: ")) {
            case 1 -> { int m = lerInt("Mês (1-12): "); int a = lerInt("Ano: "); relatorio.relatorioMensal(m, a); }
            case 2 -> relatorio.gastosPorCategoria();
            case 3 -> { int m = lerInt("Mês (1-12): "); int a = lerInt("Ano: "); relatorio.alertasOrcamento(m, a); }
        }
    }

    private int lerInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Digite um número inteiro válido."); }
        }
    }

    private BigDecimal lerDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return new BigDecimal(sc.nextLine().trim().replace(",", ".")); }
            catch (NumberFormatException e) { System.out.println("Digite um valor numérico válido (ex: 150.00)."); }
        }
    }

    private String lerTexto(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private LocalDate lerData(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return LocalDate.parse(sc.nextLine().trim()); }
            catch (DateTimeParseException e) { System.out.println("Formato inválido. Use AAAA-MM-DD (ex: 2025-06-15)."); }
        }
    }
}