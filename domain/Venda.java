package mbtec.com.mz.itemvendatest.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * Responsavel em instanciar Venda calculando ou adicionando os
 * itens de venda, somar os itens, calcular total de venda
 * Versao de aprendizagem em Venda e Itens de Venda
 * @version 1.1
 * @author Mbtec subtecnico Zulo Rajabo mitumba
 */
public class Venda {

    private int idVenda;
    private LocalDateTime dataVenda;
    private Cliente cliente;
    private boolean pago;
    private double taxaIva; // ex: 0.17 (17%)
    private List<Itemvenda> itens = new ArrayList<>();

    public Venda() {
        this.dataVenda = LocalDateTime.now();
    }

    public Venda(int idVenda, Cliente cliente, boolean pago, double taxaIva) {
        this();
        this.idVenda = idVenda;
        this.cliente = cliente;
        this.pago = pago;
        this.taxaIva = taxaIva;
    }

    public int getIdVenda() {
        return idVenda;
    }

    public void setIdVenda(int idVenda) {
        this.idVenda = idVenda;
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public boolean isPago() {
        return pago;
    }

    public void setPago(boolean pago) {
        this.pago = pago;
    }

    public double getTaxaIva() {
        return taxaIva;
    }

    public void setTaxaIva(double taxaIva) {
        this.taxaIva = taxaIva;
    }

    public List<Itemvenda> getItens() {
        return itens;
    }

    //adicionar item
    public void adicionarItem(Itemvenda item) {
        itens.add(item);
    }

    //CÁLCULOS
    public double getSubtotal() {
        return itens.stream()
                .mapToDouble(Itemvenda::getTotalComDesconto)
                .sum();
    }

    public double getValorIva() {
        return getSubtotal() * taxaIva;
    }

    public double getTotalFinal() {
        return getSubtotal() + getValorIva();
    }

    @Override
    public String toString() {
        return "Venda #" + idVenda +
                " | Data: " + dataVenda +
                " | Total: " + getTotalFinal();
    }
}

