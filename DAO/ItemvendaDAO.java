package mbtec.com.mz.itemvendatest.DAO;

import mbtec.com.mz.itemvendatest.domain.Itemvenda;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.List;

public class ItemvendaDAO {
    public void salvarItens(Connection conn, int idVenda, @NotNull List<Itemvenda> itens)
            throws SQLException {

        String sql = """
            INSERT INTO item_venda
            (id_venda, id_produto, quantidade, preco_unitario, desconto)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Itemvenda item : itens) {
                ps.setInt(1, idVenda);
                ps.setInt(2, item.getProduto().getIdProduto());
                ps.setInt(3, item.getQuantidade());
                ps.setDouble(4, item.getPrecoUnitario());
                ps.setDouble(5, item.getDesconto());
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }


}
