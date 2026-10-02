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
            System.out.println("\n--- SISTEMA DE PEDIDOS -- TRABALHO 1 GCS ---");
            if (usuarioLogado != null) {
                System.out.println("Usuário atual: " + usuarioLogado.getNome() + " (" + usuarioLogado.getTipo() + ")");
            } else {
                System.out.println("Nenhum usuário logado.");
            }
            menu();
            System.out.print("Digite a opcao desejada: ");
            opcao = entrada.nextInt();
            entrada.nextLine();
            switch (opcao) {
                case 0:
                    System.out.println("Encerrando o sistema...");
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
                 //(Buscas/Filtros)
                    System.out.println("esperando implementação...");
                    break;
                case 6:
                    //(Estatísticas)
                    System.out.println("esperando implementação...");
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
        System.out.println("[3] Excluir pedido de aquisicao (Apenas o criador)");
        System.out.println("[4] Avaliar pedido (Apenas Administrador: Aprovar/Reprovar)");
        System.out.println("[5] Consultas e Buscas (Camile)");
        System.out.println("[6] Estatísticas Gerais (Camile)");
    }

    public void mudarUsuarioPorId(List<Funcionario> funcionarios) {
        System.out.print("Digite o ID do funcionário: ");
        int id = entrada.nextInt();
        entrada.nextLine();
        for (Funcionario f : funcionarios) {
            if (f.getId() == id) {
                usuarioLogado = f;
                System.out.println("Usuário alterado com sucesso para: " + f.getNome());
                return;
            }
        }
        System.out.println("Usuário com ID não encontrado.");
    } 

    public void registrarPedido() {
        if (usuarioLogado == null) {
            System.out.println("Erro: Não há usuário logado. Mude de usuário primeiro.");
            return;
        }

        List<ItemPedido> itensDoPedido = new ArrayList<>();
        System.out.print("Quantos itens deseja adicionar ao pedido? ");
        int quantidadeItens = entrada.nextInt();
        entrada.nextLine();
        
        for (int i = 1; i <= quantidadeItens; i++) {
            System.out.println("--- Item " + i + " ---");
            System.out.print("Descrição do item/produto: ");
            String descricaoItem = entrada.nextLine();
            System.out.print("Valor unitário (R$): ");
            double valorUnitario = entrada.nextDouble();
            System.out.print("Quantidade: ");
            int quantidade = entrada.nextInt();
            entrada.nextLine();

            itensDoPedido.add(new ItemPedido(descricaoItem, valorUnitario, quantidade));
        }

        Pedido pedidoTemp = new Pedido(proximoIdPedido, usuarioLogado, itensDoPedido);
        double valorTotal = pedidoTemp.getValorTotalPedido();
        double limiteDepartamento = usuarioLogado.getDepartamento().getLimiteMaximoPedido();

        if (valorTotal > limiteDepartamento) {
            System.out.println("Erro: O valor total do pedido (R$ " + valorTotal + 
                               ") ultrapassa o limite máximo permitido pelo departamento " + 
                               usuarioLogado.getDepartamento().getNome() + " (R$ " + limiteDepartamento + "). Pedido não cadastrado.");
            return;
        }

        pedidos.add(pedidoTemp);
        System.out.println("Pedido #" + proximoIdPedido + " registrado com sucesso!");
        proximoIdPedido++;
    }

    public void excluirPedido() {
        if (usuarioLogado == null) {
            System.out.println("Erro: Não há usuário logado.");
            return;
        }

        System.out.print("Digite o ID do pedido que quer excluir: ");
        int idProcurado = entrada.nextInt();
        entrada.nextLine();

        for (Pedido pedido : pedidos) {
            if (pedido.getId() == idProcurado) {
                if (pedido.getSolicitante().getId() == usuarioLogado.getId()) {
                    if (pedido.getStatus() == StatusPedido.PENDENTE) {
                        pedidos.remove(pedido);
                        System.out.println("Pedido excluído com sucesso.");
                    } else {
                        System.out.println("Erro: Este pedido não está mais pendente e não pode ser excluído.");
                    }
                    return; 
                } else {
                    System.out.println("Erro: Somente o funcionário que criou o pedido pode excluí-lo.");
                    return;
                }
            }
        }
        System.out.println("ID do pedido não encontrado.");
    }

    public void avaliarPedidoAdmin() {
        if (usuarioLogado == null) {
            System.out.println("Erro: Não há usuário logado.");
            return;
        }

        if (usuarioLogado.getTipo() != TipoFuncionario.ADMINISTRADOR) {
            System.out.println("Erro: Somente funcionários administradores podem avaliar pedidos.");
            return;
        }

        System.out.println("\n--- LISTA DE PEDIDOS ABERTOS (PENDENTES) ---");
        boolean temAbertos = false;
        for (Pedido p : pedidos) {
            if (p.getStatus() == StatusPedido.PENDENTE) {
                System.out.println(p);
                temAbertos = true;
            }
        }

        if (!temAbertos) {
            System.out.println("Não há pedidos pendentes no momento.");
            return;
        }

        System.out.print("\nDigite o ID do pedido que deseja avaliar: ");
        int idPedido = entrada.nextInt();
        entrada.nextLine();

        Pedido pedidoAlvo = null;
        for (Pedido p : pedidos) {
            if (p.getId() == idPedido) {
                pedidoAlvo = p;
                break;
            }
        }

        if (pedidoAlvo == null) {
            System.out.println("Pedido não encontrado.");
            return;
        }

        if (pedidoAlvo.getStatus() != StatusPedido.PENDENTE) {
            System.out.println("Erro: Este pedido já foi avaliado anteriormente (" + pedidoAlvo.getStatus() + ") e não pode ser alterado.");
            return;
        }

        System.out.println("Detalhes do Pedido:");
        System.out.println("Solicitante: " + pedidoAlvo.getSolicitante().getNome());
        System.out.println("Departamento: " + pedidoAlvo.getDepartamentoSolicitante().getNome());
        System.out.println("Valor Total: R$ " + pedidoAlvo.getValorTotalPedido());
        System.out.println("Itens:");
        for (ItemPedido item : pedidoAlvo.getItens()) {
            System.out.println(" - " + item);
        }

        System.out.print("Deseja [1] APROVAR ou [2] REPROVAR este pedido? ");
        int escolha = entrada.nextInt();
        entrada.nextLine();

        if (escolha == 1) {
            pedidoAlvo.setStatus(StatusPedido.APROVADO);
            pedidoAlvo.setDataConclusao(LocalDate.now());
            System.out.println("Pedido aprovado com sucesso! Data de conclusão registrada.");
        } else if (escolha == 2) {
            pedidoAlvo.setStatus(StatusPedido.REPROVADO);
            pedidoAlvo.setDataConclusao(LocalDate.now());
            System.out.println("Pedido reprovado.");
        } else {
            System.out.println("Opção inválida. Nenhuma alteração realizada.");
        }
    }
}