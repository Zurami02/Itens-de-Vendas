package mbtec.com.mz.itemvendatest.DAO;

import mbtec.com.mz.itemvendatest.DB.ConexaoSQLite;
import mbtec.com.mz.itemvendatest.domain.Cliente;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteDAO {
    public void inserir(@NotNull Cliente cliente){
        String sql = "INSERT INTO cliente (nome, nuit, endereco) VALUES  (?,?,?)";
        try(Connection connection = ConexaoSQLite.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getNuit());
            stmt.setString(3, cliente.getEndereco());
            stmt.execute();

        } catch (SQLException e) {
            Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public List<Cliente> listar(){
        String sql = "SELECT * FROM cliente";
        List<Cliente> retorno = new ArrayList<>();
        try(Connection connection = ConexaoSQLite.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setIdcliente(rs.getInt("idcliente"));
                cliente.setNome(rs.getString("nome"));
                cliente.setNuit(rs.getString("nuit"));
                cliente.setEndereco(rs.getString("endereco"));
                retorno.add(cliente);
            }

        } catch (SQLException e) {
            Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return retorno;
    }

    public void renover(@NotNull Cliente cliente){
        String sql = "DELETE FROM cliente WHERE idcliente =?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1,cliente.getIdcliente());
            stmt.execute();
        } catch (SQLException e) {
            Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public boolean atualizar(@NotNull Cliente cliente){
        String sql = "UPDATE cliente SET nome=?, nuit=?, endereco=? WHERE idcliente=?";
        try(Connection connection = ConexaoSQLite.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getNuit());
            stmt.setString(2, cliente.getEndereco());
            stmt.execute();
            return true;

        } catch (SQLException e) {
            Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public boolean existeCliente(String nuit) {
        String sql = "SELECT COUNT(*) FROM cliente WHERE nuit = ? COLLATE NOCASE";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nuit);
            ResultSet resultado = stmt.executeQuery();
            if (resultado.next()) {
                return resultado.getInt(1) > 0;//Retorna True se houver pelo menos um registo.
            }
        } catch (SQLException e) {
            Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public Cliente buscarPorId(int id) {
        String sql = "SELECT * FROM cliente WHERE idcliente = ?";
        try (Connection conn = ConexaoSQLite.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Cliente c = new Cliente();
                c.setIdcliente(rs.getInt("idcliente"));
                c.setNome(rs.getString("nome"));
                c.setEndereco(rs.getString("endereco"));
                c.setNuit(rs.getString("nuit"));
                return c;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
