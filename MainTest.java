package mbtec.com.mz.itemvendatest;

import mbtec.com.mz.itemvendatest.domain.Cliente;
import mbtec.com.mz.itemvendatest.domain.Itemvenda;
import mbtec.com.mz.itemvendatest.domain.Produtos;
import mbtec.com.mz.itemvendatest.domain.Venda;

public class MainTest {
    public static void main(String[] args) {

        Venda v1 = getVenda();

        System.out.println("###############################X###############################");

        System.out.println(v1);
        System.out.println("Data de Venda e hora: "+v1.getDataVenda());
        System.out.println("Cliente: "+v1.getCliente().getNome());
        for (Itemvenda item : v1.getItens()){
            System.out.println("Produtos Comprados: "+item);
        }
        System.out.println("Total de venda: "+"IVA 17%: "+v1.getValorIva()+"MZN, \nTotal: "+v1.getTotalFinal()+"MZN");

        System.out.println("###############################X###############################");
        for (Itemvenda item : v1.getItens()){
            System.out.println("Produtos: "+item.getProduto().getDescricao()
                    +" "+item.getProduto().getPreco());
        }



    }

    private static @NotNull Venda getVenda() {
        Cliente cl = new Cliente(1, "Zulo", "9998777Nuit", "Pemba, Cabo Delgado");
        Cliente cl1 = new Cliente(2, "Zito", "7778777Nuit", "Nampula, Nampula");
        Cliente cl2 = new Cliente(3, "Zura", "8888777Nuit", "Cuamba, Niassa");

        Produtos p1 = new Produtos(1, "Teclado HP", 20, 550.0);
        Produtos p2 = new Produtos(1, "Gabinete HP", 20, 5050.0);
        Produtos p3 = new Produtos(1, "Mouse HP", 20, 650.0);
        Produtos p4 = new Produtos(1, "Monitor HP", 20, 3550.0);


        Venda v1 = new Venda(1, cl,true,0.17);

        Itemvenda iv1 = new Itemvenda(p1, 5, 0.0, v1);
        Itemvenda iv2 = new Itemvenda(p4, 5, 0.0, v1);
        Itemvenda iv3 = new Itemvenda(p2, 5, 0.0, v1);
        Itemvenda iv4 = new Itemvenda(p3, 5, 0.0, v1);

        v1.adicionarItem(iv1);
        v1.adicionarItem(iv2);
        v1.adicionarItem(iv3);
        v1.adicionarItem(iv4);

        System.out.println("###############################X###############################");

        System.out.println(v1);
        System.out.println("Data de Venda e hora: "+v1.getDataVenda());
        System.out.println("Cliente: "+v1.getCliente().getNome());
        for (Itemvenda item : v1.getItens()){
            System.out.println("Produtos Comprados: "+item);
        }
        System.out.println("Total de venda: "+"IVA 17%: "+v1.getValorIva()+"MZN, \nTotal: "+v1.getTotalFinal()+"MZN");

        System.out.println("###############################X###############################");
        for (Itemvenda item : v1.getItens()){
            System.out.println("Produtos: "+item.getProduto().getDescricao()
                    +" "+item.getProduto().getPreco());
        }



    }
}
