package mbtec.com.mz.itemvendatest.DAO;

import mbtec.com.mz.itemvendatest.DB.ConexaoSQLite;
import mbtec.com.mz.itemvendatest.domain.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CategoriaDAO {

    public boolean inserir(Categoria categoria) {
        String sql = "INSERT INTO categoria (descricao_categoria) VALUES (?)";
        try (Connection connection = ConexaoSQLite.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, categoria.getDescricaocategoria());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(CategoriaDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public boolean existeCategoria(String categoria, String descricao_categoria) {
        String sql = "SELECT COUNT(*) FROM categoria WHERE categoria = ? AND descricao_categoria = ?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)){

            stmt.setString(1, categoria);
            stmt.setString(2, descricao_categoria);
            ResultSet resultado = stmt.executeQuery();
            if (resultado.next()) {
                return resultado.getInt(1) > 0;//Retorna True se houver pelo menos um registo.
            }
        } catch (SQLException e) {
            Logger.getLogger(CategoriaDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public boolean descricaoProdutoUnica(String descricaoCategoria, String categoriaId) {
        String sql = "SELECT COUNT(*) FROM categoria WHERE descricao_categoria = ? AND idcategoria = ?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, descricaoCategoria);
            stmt.setString(2, categoriaId);
            ResultSet resultado = stmt.executeQuery();
            if (resultado.next()) {
                return resultado.getInt(1) == 0; // Retorna True se não houver registros com a mesma descrição para o mesmo fornecedor.
            }
        } catch (SQLException e) {
            Logger.getLogger(ProdutosDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public boolean existeDescricaoCategoria(String descricao_categoria) {
        String sql = "SELECT COUNT(*) FROM categoria WHERE descricao_categoria = ?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, descricao_categoria);
            ResultSet resultado = stmt.executeQuery();
            if (resultado.next()) {
                return resultado.getInt(1) > 0;//Retorna True se houver pelo menos um registo.
            }
        } catch (SQLException e) {
            Logger.getLogger(CategoriaDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public List<Categoria> listar() {
        String sql = "SELECT * FROM categoria";
        List<Categoria> retorno = new ArrayList<>();
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()){
            while (resultado.next()) {
                Categoria categoria = new Categoria();
                categoria.setIdcategoria(resultado.getInt("idcategoria"));
                categoria.setDescricaocategoria(resultado.getString("descricao_categoria"));
                retorno.add(categoria);
            }
        } catch (SQLException e) {
            Logger.getLogger(CategoriaDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return retorno;
    }

    public boolean editar(Categoria categoria) {
        String sql = "UPDATE categoria SET descricao_categoria=? WHERE idcategoria=?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, categoria.getDescricaocategoria());
            stmt.setInt(2, categoria.getIdcategoria());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(CategoriaDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public boolean remover(Categoria categoria) {
        String sql = "DELETE FROM categoria WHERE idcategoria=?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, categoria.getIdcategoria());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(CategoriaDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

//    public Fornecedores buscar(Fornecedores fornecedores) {
//        String sql = "SELECT * FROM fornecedor WHERE idfornecedor=?";
//        Fornecedores retorno = new Fornecedores();
//        try (Connection connection = ConexaoSQLite.getConnection();
//             PreparedStatement stmt = connection.prepareStatement(sql)){
//            stmt.setInt(1, fornecedores.getIdfornecedor());
//            ResultSet resultado = stmt.executeQuery();
//            if (resultado.next()) {
//                retorno.setIdfornecedor(resultado.getInt("idfornecedor"));
//                retorno.setDescricaoProduto(resultado.getString("descricao_produto"));
//                retorno.setQuantidade(resultado.getInt("quantidade"));
//                retorno.setFornecedor(resultado.getString("fornecedor"));
//                retorno.setPreco(resultado.getDouble("preco"));
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(CategoriaDAO.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return retorno;
//    }

    public List<Categoria> buscarPorNome(String nome) {
        String sql = "SELECT * FROM categoria WHERE descricao_categoria LIKE ?";
        List<Categoria> categoriasList = new ArrayList<>();

        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, "%" + nome + "%"); // Usando LIKE para permitir buscas parciais
            ResultSet resultado = stmt.executeQuery();

            while (resultado.next()) {
                Categoria categoria = new Categoria();
                categoria.setIdcategoria(resultado.getInt("idcategoria"));
                categoria.setDescricaocategoria(resultado.getString("descricao_categoria"));
                categoriasList.add(categoria);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CategoriaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return categoriasList;
    }

    //Usado para edicao so
    public boolean existeOutroCombinacao(String fornecedor, String descricaoProduto, int idAtual) {
        String sql = "SELECT COUNT(*) FROM fornecedor WHERE fornecedor = ? AND descricao_produto = ? AND idfornecedor != ?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fornecedor);
            stmt.setString(2, descricaoProduto);
            stmt.setInt(3, idAtual);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Categoria buscarPorId(int id) {
        String sql = "SELECT * FROM categoria WHERE idcategoria = ?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setIdcategoria(rs.getInt("idcategoria"));
                categoria.setDescricaocategoria(rs.getString("descricao_categoria"));
                return categoria;
            }

        } catch (SQLException e) {
            Logger.getLogger(CategoriaDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    public Map<Integer, Categoria> mapearPorId() {
        String sql = "SELECT * FROM categoria";
        Map<Integer, Categoria> mapa = new HashMap<>();

        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setIdcategoria(rs.getInt("idcategoria"));
                categoria.setDescricaocategoria(rs.getString("descricao_categoria"));
                mapa.put(categoria.getIdcategoria(), categoria);
            }

        } catch (SQLException ex) {
            Logger.getLogger(CategoriaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return mapa;
    }


}
