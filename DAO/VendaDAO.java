package mbtec.com.mz.itemvendatest.DAO;

import mbtec.com.mz.itemvendatest.DB.ConexaoSQLite;
import mbtec.com.mz.itemvendatest.domain.Cliente;
import mbtec.com.mz.itemvendatest.domain.Venda;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VendaDAO {
    public int salvarVenda(Connection conn, @NotNull Venda venda) throws SQLException {

        String sql = """
                    INSERT INTO venda 
                    (datavenda, idcliente, nomecliente, nuitCliente, pago, taxaiva, valortotal, vd)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
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
            ps.setBoolean(8, venda.isVd());

            ps.executeUpdate();

            //BUSCAR O ID GERADO NO SQLITE
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT last_insert_rowid()")) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            throw new SQLException("Erro ao gerar ID da venda.");
        }
    }

    public List<Venda> historicoVendas(
            LocalDate dataInicial,
            LocalDate dataFinal,
            String textoPesquisa
    ) {

        List<Venda> lista = new ArrayList<>();

        String sql = """
        SELECT
            v.idvenda,
            v.datavenda,
            v.valortotal,
            v.pago,
            v.taxaiva,
            c.idcliente,
            c.nome        AS nome_registado,
            v.nomecliente AS nome_nao_registado,
            v.nuitcliente
        FROM venda v
        LEFT JOIN cliente c ON c.idcliente = v.idcliente
        WHERE DATE(v.datavenda) BETWEEN ? AND ?
        AND (
            ? IS NULL
            OR c.nome LIKE ?
            OR v.nomecliente LIKE ?
        )
        ORDER BY v.datavenda DESC
    """;

        try (Connection conn = ConexaoSQLite.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Datas
            ps.setString(1, dataInicial.toString());
            ps.setString(2, dataFinal.toString());

            // Texto de pesquisa (opcional)
            if (textoPesquisa == null || textoPesquisa.isBlank()) {
                ps.setNull(3, Types.VARCHAR);
                ps.setNull(4, Types.VARCHAR);
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(3, textoPesquisa);
                ps.setString(4, "%" + textoPesquisa + "%");
                ps.setString(5, "%" + textoPesquisa + "%");
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Venda venda = new Venda();
                venda.setIdVenda(rs.getInt("idvenda"));
                venda.setPago(rs.getBoolean("pago"));
                venda.setTaxaIva(rs.getDouble("taxaiva"));
                venda.setDataVenda(
                        rs.getTimestamp("datavenda").toLocalDateTime()
                );

                // Cliente registado
                int idCliente = rs.getInt("idcliente");
                if (!rs.wasNull()) {
                    Cliente c = new Cliente();
                    c.setIdcliente(idCliente);
                    c.setNome(rs.getString("nome_registado"));
                    venda.setCliente(c);
                } else {
                    // Cliente não registado
                    venda.setNomeCliente(rs.getString("nome_nao_registado"));
                    venda.setNuitCliente(rs.getString("nuitcliente"));
                }

                lista.add(venda);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar histórico de vendas", e);
        }

        return lista;
    }

}
