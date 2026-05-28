package bin.src;

import dao.*;
import model.*;
import model.Venda.ItemVenda;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    private static final FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    private static final ClienteDAO     clienteDAO     = new ClienteDAO();
    private static final FornecedorDAO  fornecedorDAO  = new FornecedorDAO();
    private static final ProdutoDAO     produtoDAO     = new ProdutoDAO();
    private static final VendaDAO       vendaDAO       = new VendaDAO();

    private static Funcionario usuarioLogado = null;

    // ──────────────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║       EASY MARKET  v1.0          ║");
        System.out.println("╚══════════════════════════════════╝");

        if (!telaLogin()) {
            System.out.println("Sistema encerrado.");
            return;
        }

        menuPrincipal();
        System.out.println("Até logo!");
        sc.close();
    }

    // ── Login / Recuperação de Senha ──────────────────────────────────────────
    private static boolean telaLogin() {
        while (true) {
            System.out.println("\n[LOGIN]");
            System.out.println("0 - Sair");
            System.out.println("1 - Entrar");
            System.out.println("2 - Recuperar senha");
            System.out.print("Opção: ");
            String op = sc.nextLine().trim();

            switch (op) {
                case "0": return false;

                case "1": {
                    System.out.print("E-mail: ");
                    String email = sc.nextLine().trim();
                    System.out.print("Senha : ");
                    String senha = sc.nextLine().trim();

                    if (email.isEmpty() || senha.isEmpty()) {
                        System.out.println("Preencha os campos obrigatórios.");
                        break;
                    }
                    usuarioLogado = funcionarioDAO.login(email, senha);
                    if (usuarioLogado != null) {
                        System.out.println("Login realizado com sucesso! Bem-vindo(a), " + usuarioLogado.getNome() + ".");
                        return true;
                    } else {
                        System.out.println("Erro: usuário ou senha inválidos.");
                    }
                    break;
                }

                case "2": {
                    System.out.print("Informe o e-mail cadastrado: ");
                    String email = sc.nextLine().trim();
                    if (email.isEmpty()) {
                        System.out.println("Campo de e-mail obrigatório.");
                        break;
                    }
                    System.out.print("Nova senha: ");
                    String novaSenha = sc.nextLine().trim();
                    if (novaSenha.isEmpty()) {
                        System.out.println("Campo de e-mail e senha obrigatórios.");
                        break;
                    }
                    boolean ok = funcionarioDAO.recuperarSenha(email, novaSenha);
                    System.out.println(ok ? "Senha atualizada com sucesso!"
                                         : "E-mail não encontrado ou inválido.");
                    break;
                }
                default: System.out.println("Opção inválida.");
            }
        }
    }

    // ── Menu Principal ─────────────────────────────────────────────────────────
    private static void menuPrincipal() {
        while (true) {
            System.out.println("\n╔══════════ MENU PRINCIPAL ══════════╗");
            System.out.println("║  1 - Produtos                      ║");
            System.out.println("║  2 - Clientes                      ║");
            System.out.println("║  3 - Fornecedores                  ║");
            System.out.println("║  4 - Vendas                        ║");
            System.out.println("║  5 - Funcionários                  ║");
            System.out.println("║  6 - Relatórios                    ║");
            System.out.println("║  7 - Controle de Estoque           ║");
            System.out.println("║  0 - Sair                          ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Opção: ");
            switch (sc.nextLine().trim()) {
                case "1": menuProdutos();     break;
                case "2": menuClientes();     break;
                case "3": menuFornecedores(); break;
                case "4": menuVendas();       break;
                case "5": menuFuncionarios(); break;
                case "6": menuRelatorios();   break;
                case "7": controleEstoque();  break;
                case "0": return;
                default:  System.out.println("Opção inválida.");
            }
        }
    }

    // ── Produtos ───────────────────────────────────────────────────────────────
    private static void menuProdutos() {
        while (true) {
            System.out.println("\n[PRODUTOS] 1-Cadastrar  2-Alterar  3-Remover  4-Listar  0-Voltar");
            System.out.print("Opção: ");
            switch (sc.nextLine().trim()) {
                case "1": cadastrarProduto();  break;
                case "2": alterarProduto();    break;
                case "3": removerProduto();    break;
                case "4": listarProdutos();    break;
                case "0": return;
                default:  System.out.println("Opção inválida.");
            }
        }
    }

    private static void cadastrarProduto() {
        System.out.println("\n-- Cadastrar Produto --");
        System.out.print("Nome       : "); String nome = sc.nextLine().trim();
        System.out.print("Categoria  : "); String cat  = sc.nextLine().trim();
        System.out.print("Preço (R$) : "); String preco = sc.nextLine().trim();
        System.out.print("Quantidade : "); String qtd   = sc.nextLine().trim();
        System.out.print("Estoque min: "); String emin  = sc.nextLine().trim();
        System.out.print("ID Forn.(0=nenhum): "); String forn = sc.nextLine().trim();

        if (nome.isEmpty() || cat.isEmpty() || preco.isEmpty() || qtd.isEmpty()) {
            System.out.println("Preencha o campo de Dados do Produto.");
            return;
        }
        try {
            Produto p = new Produto(nome, cat, Double.parseDouble(preco),
                    Integer.parseInt(qtd), emin.isEmpty() ? 5 : Integer.parseInt(emin),
                    forn.isEmpty() ? 0 : Integer.parseInt(forn));
            produtoDAO.cadastrar(p);
        } catch (NumberFormatException e) {
            System.out.println("Erro ao Cadastrar Produto: dados numéricos inválidos.");
        }
    }

    private static void alterarProduto() {
        listarProdutos();
        System.out.print("ID do produto a alterar: ");
        String idStr = sc.nextLine().trim();
        if (idStr.isEmpty()) { System.out.println("Dados do produto inválidos."); return; }
        Produto p = produtoDAO.buscarPorId(Integer.parseInt(idStr));
        if (p == null) { System.out.println("Erro - Produto não Alterado."); return; }

        System.out.println("Deixe em branco para manter o valor atual.");
        System.out.print("Nome [" + p.getNome() + "]: ");          String nome  = sc.nextLine().trim();
        System.out.print("Categoria [" + p.getCategoria() + "]: "); String cat   = sc.nextLine().trim();
        System.out.print("Preço [" + p.getPreco() + "]: ");        String preco = sc.nextLine().trim();
        System.out.print("Qtd [" + p.getQuantidade() + "]: ");     String qtd   = sc.nextLine().trim();

        if (!nome.isEmpty())  p.setNome(nome);
        if (!cat.isEmpty())   p.setCategoria(cat);
        if (!preco.isEmpty()) p.setPreco(Double.parseDouble(preco));
        if (!qtd.isEmpty())   p.setQuantidade(Integer.parseInt(qtd));
        produtoDAO.alterar(p);
    }

    private static void removerProduto() {
        listarProdutos();
        System.out.print("ID do produto a remover (0=cancelar): ");
        String id = sc.nextLine().trim();
        if ("0".equals(id) || id.isEmpty()) { System.out.println("Remoção Cancelada."); return; }
        produtoDAO.remover(Integer.parseInt(id));
    }

    private static void listarProdutos() {
        List<Produto> lista = produtoDAO.listar();
        lista.forEach(System.out::println);
    }

    // ── Clientes ───────────────────────────────────────────────────────────────
    private static void menuClientes() {
        while (true) {
            System.out.println("\n[CLIENTES] 1-Cadastrar  2-Alterar  3-Remover  4-Listar  0-Voltar");
            System.out.print("Opção: ");
            switch (sc.nextLine().trim()) {
                case "1": cadastrarCliente();  break;
                case "2": alterarCliente();    break;
                case "3": removerCliente();    break;
                case "4": listarClientes();    break;
                case "0": return;
                default:  System.out.println("Opção inválida.");
            }
        }
    }

    private static void cadastrarCliente() {
        System.out.println("\n-- Cadastrar Cliente --");
        System.out.print("Nome    : "); String nome = sc.nextLine().trim();
        System.out.print("CPF     : "); String cpf  = sc.nextLine().trim();
        System.out.print("Telefone: "); String tel  = sc.nextLine().trim();
        System.out.print("E-mail  : "); String email = sc.nextLine().trim();
        if (nome.isEmpty() || cpf.isEmpty()) {
            System.out.println("Preencha os Dados Obrigatórios.");
            return;
        }
        clienteDAO.cadastrar(new Cliente(nome, cpf, tel, email));
    }

    private static void alterarCliente() {
        listarClientes();
        System.out.print("ID do cliente a alterar: ");
        String idStr = sc.nextLine().trim();
        if (idStr.isEmpty()) { System.out.println("Preencha os campos vazios."); return; }
        Cliente c = clienteDAO.buscarPorId(Integer.parseInt(idStr));
        if (c == null) { System.out.println("Erro: Cliente não encontrado."); return; }

        System.out.println("Deixe em branco para manter.");
        System.out.print("Nome [" + c.getNome() + "]: ");      String nome = sc.nextLine().trim();
        System.out.print("CPF  [" + c.getCpf()  + "]: ");      String cpf  = sc.nextLine().trim();
        System.out.print("Tel  [" + c.getTelefone() + "]: ");   String tel  = sc.nextLine().trim();
        System.out.print("Email[" + c.getEmail() + "]: ");      String email = sc.nextLine().trim();

        if (!nome.isEmpty())  c.setNome(nome);
        if (!cpf.isEmpty())   c.setCpf(cpf);
        if (!tel.isEmpty())   c.setTelefone(tel);
        if (!email.isEmpty()) c.setEmail(email);
        clienteDAO.alterar(c);
    }

    private static void removerCliente() {
        listarClientes();
        System.out.print("ID do cliente a remover (0=cancelar): ");
        String id = sc.nextLine().trim();
        if ("0".equals(id) || id.isEmpty()) { System.out.println("Operação Cancelada."); return; }
        clienteDAO.remover(Integer.parseInt(id));
    }

    private static void listarClientes() {
        clienteDAO.listar().forEach(System.out::println);
    }

    // ── Fornecedores ───────────────────────────────────────────────────────────
    private static void menuFornecedores() {
        while (true) {
            System.out.println("\n[FORNECEDORES] 1-Cadastrar  2-Alterar  3-Remover  4-Listar  0-Voltar");
            System.out.print("Opção: ");
            switch (sc.nextLine().trim()) {
                case "1": cadastrarFornecedor();  break;
                case "2": alterarFornecedor();    break;
                case "3": removerFornecedor();    break;
                case "4": listarFornecedores();   break;
                case "0": return;
                default:  System.out.println("Opção inválida.");
            }
        }
    }

    private static void cadastrarFornecedor() {
        System.out.println("\n-- Cadastrar Fornecedor --");
        System.out.print("Nome     : "); String nome = sc.nextLine().trim();
        System.out.print("CNPJ     : "); String cnpj = sc.nextLine().trim();
        System.out.print("Telefone : "); String tel  = sc.nextLine().trim();
        System.out.print("E-mail   : "); String email = sc.nextLine().trim();
        System.out.print("Produtos : "); String prod  = sc.nextLine().trim();
        if (nome.isEmpty() || cnpj.isEmpty()) {
            System.out.println("Dados Inválidos.");
            return;
        }
        fornecedorDAO.cadastrar(new Fornecedor(nome, cnpj, tel, email, prod));
    }

    private static void alterarFornecedor() {
        listarFornecedores();
        System.out.print("ID do fornecedor a alterar: ");
        String idStr = sc.nextLine().trim();
        if (idStr.isEmpty()) { System.out.println("Erro ao Alterar Fornecedor."); return; }
        Fornecedor f = fornecedorDAO.buscarPorId(Integer.parseInt(idStr));
        if (f == null) { System.out.println("Não foi possível concluir alteração - Fornecedor não encontrado."); return; }

        System.out.println("Deixe em branco para manter.");
        System.out.print("Nome [" + f.getNome() + "]: ");   String nome = sc.nextLine().trim();
        System.out.print("CNPJ [" + f.getCnpj() + "]: ");   String cnpj = sc.nextLine().trim();
        System.out.print("Tel  [" + f.getTelefone() + "]: ");String tel  = sc.nextLine().trim();

        if (!nome.isEmpty()) f.setNome(nome);
        if (!cnpj.isEmpty()) f.setCnpj(cnpj);
        if (!tel.isEmpty())  f.setTelefone(tel);
        fornecedorDAO.alterar(f);
    }

    private static void removerFornecedor() {
        listarFornecedores();
        System.out.print("ID do fornecedor a remover (0=cancelar): ");
        String id = sc.nextLine().trim();
        if ("0".equals(id) || id.isEmpty()) { System.out.println("Cadastro Cancelado."); return; }
        fornecedorDAO.remover(Integer.parseInt(id));
    }

    private static void listarFornecedores() {
        fornecedorDAO.listar().forEach(System.out::println);
    }

    // ── Vendas ─────────────────────────────────────────────────────────────────
    private static void menuVendas() {
        while (true) {
            System.out.println("\n[VENDAS] 1-Registrar  2-Alterar  3-Cancelar  4-Listar  0-Voltar");
            System.out.print("Opção: ");
            switch (sc.nextLine().trim()) {
                case "1": registrarVenda();  break;
                case "2": alterarVenda();    break;
                case "3": cancelarVenda();   break;
                case "4": listarVendas();    break;
                case "0": return;
                default:  System.out.println("Opção inválida.");
            }
        }
    }

    private static void registrarVenda() {
        System.out.println("\n-- Registrar Venda --");
        System.out.print("ID do cliente (0=sem cliente): ");
        int idCliente = Integer.parseInt(sc.nextLine().trim());
        Venda venda = new Venda(idCliente, usuarioLogado.getId());

        while (true) {
            System.out.println("\nProdutos disponíveis:");
            listarProdutos();
            System.out.print("ID do produto (0=finalizar): ");
            String idProdStr = sc.nextLine().trim();
            if ("0".equals(idProdStr)) break;

            Produto p = produtoDAO.buscarPorId(Integer.parseInt(idProdStr));
            if (p == null) { System.out.println("Produto não existente."); continue; }
            if (p.getQuantidade() == 0) { System.out.println("Estoque insuficiente para: " + p.getNome()); continue; }

            System.out.print("Quantidade: ");
            int qtd = Integer.parseInt(sc.nextLine().trim());
            if (qtd > p.getQuantidade()) {
                System.out.println("Estoque insuficiente. Disponível: " + p.getQuantidade());
                continue;
            }
            venda.adicionarItem(new ItemVenda(p.getId(), p.getNome(), qtd, p.getPreco()));
        }

        if (venda.getItens().isEmpty()) {
            System.out.println("Venda não registrada.");
            return;
        }
        System.out.printf("Total: R$ %.2f%n", venda.getTotal());
        System.out.print("Confirmar venda? (s/n): ");
        if (!"s".equalsIgnoreCase(sc.nextLine().trim())) {
            System.out.println("Venda não registrada.");
            return;
        }
        vendaDAO.registrar(venda);
    }

    private static void alterarVenda() {
        listarVendas();
        System.out.print("ID da venda a alterar: ");
        String idStr = sc.nextLine().trim();
        if (idStr.isEmpty()) { System.out.println("Venda não existente."); return; }
        System.out.print("Novo ID de cliente (0=sem cliente): ");
        String idCli = sc.nextLine().trim();
        if (idCli.isEmpty()) { System.out.println("Dados da venda Inválidos."); return; }
        Venda v = new Venda(Integer.parseInt(idCli), usuarioLogado.getId());
        v.setId(Integer.parseInt(idStr));
        vendaDAO.alterar(v);
    }

    private static void cancelarVenda() {
        listarVendas();
        System.out.print("ID da venda a cancelar (0=voltar): ");
        String id = sc.nextLine().trim();
        if ("0".equals(id) || id.isEmpty()) { System.out.println("Operação cancelada."); return; }
        vendaDAO.cancelar(Integer.parseInt(id));
    }

    private static void listarVendas() {
        vendaDAO.listar().forEach(System.out::println);
    }

    // ── Funcionários ───────────────────────────────────────────────────────────
    private static void menuFuncionarios() {
        while (true) {
            System.out.println("\n[FUNCIONÁRIOS] 1-Cadastrar  2-Alterar  3-Remover  4-Listar  0-Voltar");
            System.out.print("Opção: ");
            switch (sc.nextLine().trim()) {
                case "1": cadastrarFuncionario();  break;
                case "2": alterarFuncionario();    break;
                case "3": removerFuncionario();    break;
                case "4": listarFuncionarios();    break;
                case "0": return;
                default:  System.out.println("Opção inválida.");
            }
        }
    }

    private static void cadastrarFuncionario() {
        System.out.println("\n-- Cadastrar Funcionário --");
        System.out.print("Nome : "); String nome  = sc.nextLine().trim();
        System.out.print("CPF  : "); String cpf   = sc.nextLine().trim();
        System.out.print("Cargo: "); String cargo  = sc.nextLine().trim();
        System.out.print("Email: "); String email  = sc.nextLine().trim();
        System.out.print("Senha: "); String senha  = sc.nextLine().trim();

        if (nome.isEmpty() || cpf.isEmpty() || cargo.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            System.out.println("Funcionário não cadastrado.");
            return;
        }
        funcionarioDAO.cadastrar(new Funcionario(nome, cpf, cargo, email, senha));
    }

    private static void alterarFuncionario() {
        listarFuncionarios();
        System.out.print("ID do funcionário a alterar: ");
        String idStr = sc.nextLine().trim();
        if (idStr.isEmpty()) { System.out.println("Dados de funcionário inválidos."); return; }
        Funcionario f = funcionarioDAO.buscarPorId(Integer.parseInt(idStr));
        if (f == null) { System.out.println("Funcionário Inexistente."); return; }

        System.out.println("Deixe em branco para manter.");
        System.out.print("Nome  [" + f.getNome()  + "]: ");  String nome  = sc.nextLine().trim();
        System.out.print("Cargo [" + f.getCargo() + "]: ");  String cargo = sc.nextLine().trim();
        System.out.print("Email [" + f.getEmail() + "]: ");  String email = sc.nextLine().trim();

        if (!nome.isEmpty())  f.setNome(nome);
        if (!cargo.isEmpty()) f.setCargo(cargo);
        if (!email.isEmpty()) f.setEmail(email);
        funcionarioDAO.alterar(f);
    }

    private static void removerFuncionario() {
        listarFuncionarios();
        System.out.print("ID do funcionário a remover (0=cancelar): ");
        String id = sc.nextLine().trim();
        if ("0".equals(id) || id.isEmpty()) { System.out.println("Operação cancelada."); return; }
        if (Integer.parseInt(id) == usuarioLogado.getId()) {
            System.out.println("Funcionário não pode ser removido (usuário logado).");
            return;
        }
        funcionarioDAO.remover(Integer.parseInt(id));
    }

    private static void listarFuncionarios() {
        funcionarioDAO.listar().forEach(System.out::println);
    }

    // ── Relatórios ─────────────────────────────────────────────────────────────
    private static void menuRelatorios() {
        System.out.println("\n[RELATÓRIOS] 1-Diário  2-Mensal  0-Voltar");
        System.out.print("Opção: ");
        switch (sc.nextLine().trim()) {
            case "1": vendaDAO.relatorioDiario();  break;
            case "2": vendaDAO.relatorioMensal();  break;
            case "0": break;
            default:  System.out.println("Opção inválida.");
        }
    }

    // ── Controle de Estoque Mínimo ─────────────────────────────────────────────
    private static void controleEstoque() {
        System.out.println("\n=== CONTROLE DE ESTOQUE ===");
        List<Produto> baixo = produtoDAO.listarEstoqueBaixo();
        if (baixo.isEmpty()) {
            System.out.println("Estoque normalizado. Nenhum produto abaixo do mínimo.");
        } else {
            System.out.println("⚠  ALERTA - Estoque baixo:");
            baixo.forEach(System.out::println);
        }
    }
}
