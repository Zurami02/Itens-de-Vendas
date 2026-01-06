package mbtec.com.mz.itemvendatest.domain;
/**
 * Metodo responsavel em instanciar objeto itens de venda
 * Contendo calculos de Subtotal e desconto
 * Versao de aprendizagem em Venda e Itens de Venda
 * @version 1.1
 * @author Mbtec subtecnico Zulo Rajabo mitumba
 */
public class Itemvenda {

    private int idItemVenda;
    private Produtos produto;
    private int quantidade;
    private double precoUnitario;
    private double desconto;
    private Venda venda;

    public Itemvenda() {}

    public Itemvenda(Produtos produto, int quantidade, double precoUnitario, double desconto, Venda venda) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = produto.getPreco();
        this.desconto = desconto;
        this.venda = venda;
    }

    public int getIdItemVenda() {
        return idItemVenda;
    }

    public void setIdItemVenda(int idItemVenda) {
        this.idItemVenda = idItemVenda;
    }

    public Produtos getProduto() {
        return produto;
    }

    public void setProduto(Produtos produto) {
        this.produto = produto;
        this.precoUnitario = produto.getPreco();
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade inválida");
        }
        this.quantidade = quantidade;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
        if (desconto < 0) {
            throw new IllegalArgumentException("Desconto inválido");
        }
        this.desconto = desconto;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    //CÁLCULOS
    public double getSubtotal() {
        return quantidade * precoUnitario;
    }

    public double getTotalComDesconto() {
        return getSubtotal() - desconto;
    }

    @Override
    public String toString() {
        return produto.getDescricao() +
                " | Qtd: " + quantidade +
                " | Unit: " + precoUnitario +
                " | Desc: " + desconto +
                " | Total: " + getTotalComDesconto();
    }
}