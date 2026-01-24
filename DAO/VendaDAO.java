package mbtec.com.mz.itemvendatest.DAO;

import mbtec.com.mz.itemvendatest.DB.ConexaoSQLite;
import mbtec.com.mz.itemvendatest.domain.Cliente;
import mbtec.com.mz.itemvendatest.domain.Venda;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            DateTimeFormatter fmt =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            ps.setString(1, venda.getDataVenda().format(fmt));

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
            String textoCliente
    ) {

        List<Venda> lista = new ArrayList<>();

        String sql = """
        SELECT
            v.idvenda,
            v.datavenda,
            v.valortotal,
            v.pago,
            v.taxaiva,
            v.idcliente,
            v.status,
            c.nome        AS nome_registado,
            v.nomecliente AS nome_nao_registado,
            v.nuitcliente
        FROM venda v
        LEFT JOIN cliente c ON c.idcliente = v.idcliente
        WHERE
            ( ? IS NULL OR DATE(v.datavenda) >= ? )
        AND ( ? IS NULL OR DATE(v.datavenda) <= ? )
        AND (
               ? IS NULL
            OR c.nome LIKE ?
            OR v.nomecliente LIKE ?
        )
        ORDER BY v.datavenda DESC
    """;

        try (Connection conn = ConexaoSQLite.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (dataInicial == null) {
                ps.setNull(1, Types.DATE);
                ps.setNull(2, Types.DATE);
            } else {
                ps.setString(1, dataInicial.toString());
                ps.setString(2, dataInicial.toString());
            }

            if (dataFinal == null) {
                ps.setNull(3, Types.DATE);
                ps.setNull(4, Types.DATE);
            } else {
                ps.setString(3, dataFinal.toString());
                ps.setString(4, dataFinal.toString());
            }

            if (textoCliente == null || textoCliente.isBlank()) {
                ps.setNull(5, Types.VARCHAR);
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.VARCHAR);
            } else {
                String like = "%" + textoCliente + "%";
                ps.setString(5, textoCliente);
                ps.setString(6, like);
                ps.setString(7, like);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Venda venda = new Venda();
                venda.setIdVenda(rs.getInt("idvenda"));
                venda.setPago(rs.getBoolean("pago"));
                venda.setValorIVA(rs.getDouble("taxaiva"));

                LocalDateTime dataPura = LocalDateTime.parse(
                        rs.getString("datavenda").replace(" ", "T"));
                LocalDateTime dataLimpa =dataPura.withNano(0);
                //String dataStr = rs.getString("datavenda");
                //LocalDateTime data = LocalDateTime.parse(dataStr.replace(" ", "T"));
                System.out.println("Data de db: "+dataLimpa);
                venda.setDataVenda(dataLimpa);

                // Cliente registado ou não
                int idCliente = rs.getInt("idcliente");
                if (!rs.wasNull()) {
                    Cliente c = new Cliente();
                    c.setIdcliente(idCliente);
                    c.setNome(rs.getString("nome_registado"));
                    venda.setCliente(c);
                } else {
                    venda.setNomeCliente(rs.getString("nome_nao_registado"));
                    venda.setNuitCliente(rs.getString("nuitcliente"));
                }
                venda.setStatus(rs.getString("status"));
                venda.setTotalDb(rs.getDouble("valortotal"));

                lista.add(venda);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar histórico de vendas", e);
        }

        return lista;
    }

    public void anularVenda(int idvenda, Connection conn){
        String sql = "UPDATE venda SET status = 'ANULADA' WHERE idvenda =?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, idvenda);
            ps.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException("Erro ao anular venda", e);
        }
    }


}
