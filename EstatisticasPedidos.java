import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EstatisticasPedidos {

    public static int totalPedidos(List<Pedido> pedidos) {
        return pedidos.size();
    }

    public static int quantidadeAprovados(List<Pedido> pedidos) {
        int cont = 0;
        for (Pedido p : pedidos) {
            if (p.getStatus() == StatusPedido.APROVADO) {
                cont++;
            }
        }
        return cont;
    }

    public static int quantidadeReprovados(List<Pedido> pedidos) {
        int cont = 0;
        for (Pedido p : pedidos) {
            if (p.getStatus() == StatusPedido.REPROVADO) {
                cont++;
            }
        }
        return cont;
    }

    public static List<Pedido> pedidos30Dias(List<Pedido> pedidos) {
        LocalDate limite = LocalDate.now().minusDays(30); //localdate biblioteca
        List<Pedido> filtrados = new ArrayList<>();
        for (Pedido p : pedidos) {
            if (!p.getDataPedido().isBefore(limite)) {
                filtrados.add(p);
            }
        }
        return filtrados;
    }

    public static double valorMedio30Dias(List<Pedido> pedidos) {
        List<Pedido> ultimos30 = pedidos30Dias(pedidos);
        if (ultimos30.isEmpty()) {
            return 0.0;
        }
        double soma = 0;
        for (Pedido p : ultimos30) {
            soma += p.getValorTotalPedido();
        }
        return soma / ultimos30.size();
    }

    public static Optional<Pedido> pedidoMaiorValor(List<Pedido> pedidos) {
        Pedido maior = null;
        double maiorValor = -1.0;
        for (Pedido p : pedidos) {
            if (p.getStatus() == StatusPedido.PENDENTE) {
                if (p.getValorTotalPedido() > maiorValor) {
                    maiorValor = p.getValorTotalPedido();
                    maior = p;
                }
            }
        }
        return Optional.ofNullable(maior);
    }
}