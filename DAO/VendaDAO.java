package mbtec.com.mz.itemvendatest.DAO;

import mbtec.com.mz.itemvendatest.domain.Venda;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

public class VendaDAO {
    public int salvarVenda(Connection conn, @NotNull Venda venda) throws SQLException {

        String sql = """
        INSERT INTO venda 
        (datavenda, idcliente, nomecliente, nuitCliente, pago, taxaiva, valortotal)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, venda.getDataVenda().toString());

            if (venda.getCliente() != null) {
                ps.setInt(2, venda.getCliente().getIdcliente());
                ps.setNull(3, Types.VARCHAR);
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setNull(2, Types.INTEGER);
                ps.setString(3, venda.getNomeCliente());
                ps.setString(4, venda.getNuitCliente());
            }

            ps.setBoolean(5, venda.isPago());
            ps.setDouble(6, venda.getTaxaIva());
            ps.setDouble(7, venda.getTotalFinal());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            throw new SQLException("Erro ao gerar ID da venda.");
        }
    }
}
