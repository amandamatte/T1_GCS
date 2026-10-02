import java.time.LocalDate;
import java.util.*;

public class App {
    private Scanner entrada;
    private Funcionario usuarioLogado;
    private ArrayList<Pedido> pedidos = new ArrayList<>();
    private int proximoIdPedido = 1;

    public App() {
        entrada = new Scanner(System.in);
    }

    public void executar() {
        int opcao;
        do {
            System.out.println("SISTEMA DE PEDIDOS -- TRABALHO 1 GCS");
            menu();
            System.out.print("Digite a opcao desejada: ");
            opcao = entrada.nextInt();
            entrada.nextLine();
            switch (opcao) {
                case 0:
                    break;
                case 1:
                    List<Departamento> departamentos = Mock.carregarDepartamentos();
                    List<Funcionario> funcionarios = Mock.carregarFuncionarios(departamentos);
                    mudarUsuarioPorId(funcionarios);
                    break;
                case 2:
                    registrarPedido();
                    break;
                case 3:
                    excluirPedido();
                    break;
                case 4:
                    avaliarPedidoAdmin(); 
                    break;
                case 5:
                    menuConsultaAdmin();
                    break;
                case 6:
                    //
                    break;
                default:
                    System.out.println("Opcao invalida. Redigite, por favor.");
            }
        } while (opcao != 0);
    }

    private void menu() {
        System.out.println("Opcoes: ");
        System.out.println("[0] Sair");
        System.out.println("[1] Mudar de usuario por ID");
        System.out.println("[2] Registrar um novo pedido de aquisicao");
        System.out.println("[3] Excluir pedido de aquisicao");
        System.out.println("[4] Avaliar pedido (Apenas Administrador)");
        System.out.println("[5] Consultar pedidos (Apenas Adminstrador)");
        System.out.println("[6] ");
    }

    public void mudarUsuarioPorId(List<Funcionario> funcionarios) {
        System.out.println("Digite o ID: ");
        int id = entrada.nextInt();
        entrada.nextLine();
        for (Funcionario f : funcionarios) {
            if (f.getId() == id) {
                usuarioLogado = f;
                System.out.println("Usuário atual: " + f.getNome());
                return;
            }
        }
        System.out.println("Usuário com ID não encontrado.");
    } 

    public void registrarPedido() {
        if (usuarioLogado == null) {
            System.out.println("Não há usuário logado.");
            return;
        }
        List<ItemPedido> itensDoPedido = new ArrayList<>();
        System.out.print("Quantos itens deseja adicionar ao pedido? ");
        int quantidadeItens = entrada.nextInt();
        entrada.nextLine();
        for (int i = 1; i <= quantidadeItens; i++) {
            System.out.println("Item " + i + ": ");
            System.out.print("Descrição do item/produto: ");
            String descricaoItem = entrada.nextLine();
            System.out.print("Valor unitário: ");
            double valorUnitario = entrada.nextDouble();
            System.out.print("Quantidade: ");
            int quantidade = entrada.nextInt();
            entrada.nextLine();

            itensDoPedido.add(new ItemPedido(descricaoItem, valorUnitario, quantidade));
        }

        Pedido pedidoCriado = new Pedido(proximoIdPedido, usuarioLogado, itensDoPedido);
        pedidos.add(pedidoCriado);
        
        System.out.println("Pedido gerado com sucesso.");
        proximoIdPedido++;
    }

    public void excluirPedido() {
        if (usuarioLogado == null) {
            System.out.println("Não há usuário logado.");
            return;
        }

        System.out.print("Digite o ID do pedido que quer excluir: ");
        int idProcurado = entrada.nextInt();
        entrada.nextLine();

        for (Pedido pedido : pedidos) {
            if (pedido.getId() == idProcurado) {
                if (pedido.getSolicitante().getId() == usuarioLogado.getId()) {
                    pedidos.remove(pedido);
                    System.out.println("Pedido excluído com sucesso.");
                    return; 
                } else {
                    System.out.println("Apenas o funcionário que criou o pedido pode excluir.");
                    return;
                }
            }
        }
        System.out.println("ID do pedido não encontrado.");
    }

    public void avaliarPedidoAdmin() {
        if (usuarioLogado == null) {
            System.out.println("Não há usuário logado.");
            return;
        }

        if (usuarioLogado.getTipo() != TipoFuncionario.ADMINISTRADOR) {
            System.out.println("Apenas administradores podem avaliar pedidos.");
            return;
        }
        

        System.out.println("LISTA DE PEDIDOS PENDENTES");
        boolean temPendentes = false;
        for (Pedido p : pedidos) {
            if (p.getStatus() == StatusPedido.PENDENTE) {
                System.out.println(p);
                temPendentes = true;
            }
        }

        if (!temPendentes) {
            System.out.println("Nenhum pedido pendente encontrado.");
            return;
        } 

        System.out.println("Digite o ID do pedido que deseja avaliar: ");
        int idPedido = entrada.nextInt();
        entrada.nextLine();

        Pedido pedidoAlvo = null;
        for (Pedido p : pedidos) {
            if (p.getId() == idPedido) {
                pedidoAlvo = p;
                p.toString();
                break;
            }
        }

        if (pedidoAlvo == null) {
            System.out.println("Pedido não encontrado.");
            return;
        }

        if (pedidoAlvo.getStatus() != StatusPedido.PENDENTE) {
            System.out.println("Este pedido já foi avaliado.");
            return;
        }

        System.out.print("Deseja [1] APROVAR ou [2] REPROVAR este pedido? ");
        int escolha = entrada.nextInt();
        entrada.nextLine();

        if (escolha == 1) {
            pedidoAlvo.setStatus(StatusPedido.APROVADO);
            pedidoAlvo.setDataConclusao(LocalDate.now());
            System.out.println("Pedido aprovado com sucesso!");
        } else if (escolha == 2) {
            pedidoAlvo.setStatus(StatusPedido.REPROVADO);
            pedidoAlvo.setDataConclusao(LocalDate.now());
            System.out.println("Pedido reprovado.");
        } else {
            System.out.println("Opção inválida.");
        } }

        public void menuConsultaAdmin(){
            System.out.println("MENU DE CONSULTAS (ADMINISTRADOR)");
        System.out.println("[1] Listar pedidos entre duas datas");
        System.out.println("[2] Buscar pedidos por funcionário solicitante por ID");
        System.out.println("[3] Buscar pedidos por descrição de item");
        System.out.print("Escolha a opção de busca: ");
        int op = entrada.nextInt();
        entrada.nextLine();

        switch (op) {
            case 1:
            System.out.println("Digite a data inicial (AAAA-MM-DD): ");
            LocalDate inicio = LocalDate.parse(entrada.nextLine());
            System.out.print("Digite a data final (AAAA-MM-DD): ");
            LocalDate fim = LocalDate.parse(entrada.nextLine());

            System.out.println("Pedidos entre " + inicio + " e " + fim);
            boolean achou = false;
            for (Pedido p : pedidos) {
                if (!p.getDataPedido().isBefore(inicio) && !p.getDataPedido().isAfter(fim)) {
                    System.out.println(p);
                    achou = true;
                }
            }
            if (!achou) System.out.println("Nenhum pedido encontrado.");
                break;

            case 2: 
            System.out.print("Digite o ID do funcionário solicitante: ");
            int idFunc = entrada.nextInt();
            entrada.nextLine();

            System.out.println("\n--- Pedidos do funcionário ID " + idFunc + " ---");
            boolean achouID = false;
            for (Pedido p : pedidos) {
                if (p.getSolicitante().getId() == idFunc) {
                    System.out.println(p);
                    achouID = true;
                }
            }
            if (!achouID) System.out.println("Nenhum pedido encontrado para este funcionário.");
            break;

            case 3:
            System.out.print("Digite pelo menos uma palavra da descrição do item: ");
            String termo = entrada.nextLine().toLowerCase();

            System.out.println("Pedidos contendo a descrição:");
            boolean achouItem = false;
            for (Pedido p : pedidos) {
                for (ItemPedido item : p.getItens()) {
                    if (item.getDescricao().toLowerCase().contains(termo)) {
                        System.out.println(p + " contém: " + item);
                        achouItem = true;
                        break;
                    }
                }
            }
            if (!achouItem) System.out.println("Nenhum pedido encontrado.");
            break;

            default:
            System.out.println("Opção inválida.");
        } 
        }
    }