package mbtec.com.mz.itemvendatest.service;

import mbtec.com.mz.itemvendatest.DAO.ItemvendaDAO;
import mbtec.com.mz.itemvendatest.DAO.ProdutosDAO;
import mbtec.com.mz.itemvendatest.DAO.VendaDAO;
import mbtec.com.mz.itemvendatest.DB.ConexaoSQLite;
import mbtec.com.mz.itemvendatest.domain.Itemvenda;
import mbtec.com.mz.itemvendatest.domain.Venda;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class VendaService {

    private VendaDAO vendaDAO = new VendaDAO();
    private ItemvendaDAO itemVendaDAO = new ItemvendaDAO();
    private ProdutosDAO produtoDAO = new ProdutosDAO();
    private boolean anuladaVenda;

    public boolean isAnuladaVenda() {
        return anuladaVenda;
    }

    public void setAnuladaVenda(boolean anuladaVenda) {
        this.anuladaVenda = anuladaVenda;
    }

    public void anularVenda(@NotNull Venda venda) {
        Connection conn = null;

        try {
            conn = ConexaoSQLite.getConnection();
            conn.setAutoCommit(false);

            //Buscar itens da venda
            List<Itemvenda> itens = itemVendaDAO.buscarPorVenda(venda.getIdVenda(), conn);

            //Devolver stock
            for (Itemvenda item : itens) {
                produtoDAO.adicionarStock(
                        item.getProduto().getIdProduto(),
                        item.getQuantidade(),
                        conn
                );
            }

            //Anular venda
            vendaDAO.anularVenda(venda.getIdVenda(), conn);

            //Commit
            conn.commit();

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("Erro ao anular venda", e);

        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        venda.setStatus("ANULADA");
    }
}

