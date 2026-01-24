package mbtec.com.mz.itemvendatest.DAO;

import mbtec.com.mz.itemvendatest.DB.ConexaoSQLite;
import mbtec.com.mz.itemvendatest.domain.Categoria;
import mbtec.com.mz.itemvendatest.domain.Produtos;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ProdutosDAO {
    public boolean inserir(@NotNull Produtos produto) {
        String sql = "INSERT INTO produtos (descricao, quantidade, preco, idcategoria) VALUES (?,?,?,?)";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, produto.getDescricao());
            stmt.setInt(2, produto.getQuantidadeEstoque());
            stmt.setDouble(3, produto.getPreco());
            stmt.setInt(4, produto.getCategoria().getIdcategoria());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(ProdutosDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public boolean existeProduto(String produto) {
        String sql = "SELECT COUNT(*) FROM produtos WHERE descricao = ?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            //PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, produto);
            ResultSet resultado = stmt.executeQuery();
            if (resultado.next()) {
                return resultado.getInt(1) > 0;//Retorna True se houver pelo menos um registo.
            }
        } catch (SQLException e) {
            Logger.getLogger(ProdutosDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return false;
    }

    public List<Produtos> listar() {
        String sql = "SELECT p.*, c.idcategoria, c.descricao_categoria AS nome_categoria " +
                "FROM produtos p " +
                "JOIN categoria c ON p.idcategoria = c.idcategoria";
        List<Produtos> retorno = new ArrayList<>();
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            while (resultado.next()) {

                // primeiro Cria o objeto Categoria
                Categoria categoria = new Categoria();
                categoria.setIdcategoria(resultado.getInt("idcategoria")); // da tabela categoria
                categoria.setDescricaocategoria(resultado.getString("nome_categoria"));

                // depois Cria o objeto Produto e associa
                Produtos produto = new Produtos();
                produto.setIdProduto(resultado.getInt("idproduto"));
                produto.setDescricao(resultado.getString("descricao"));
                produto.setQuantidadeEstoque(resultado.getInt("quantidade"));
                produto.setPreco(resultado.getDouble("preco"));
                produto.setCategoria(categoria);

                retorno.add(produto);
            }
        } catch (SQLException e) {
            Logger.getLogger(ProdutosDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return retorno;
    }


    public boolean editar(@NotNull Produtos produto) {
        String sql = "UPDATE produtos SET descricao=?, quantidade=?,  preco=?, idcategoria=? WHERE idproduto=?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            //PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, produto.getDescricao());
            stmt.setInt(2, produto.getQuantidadeEstoque());
            stmt.setDouble(3, produto.getPreco());
            stmt.setInt(4, produto.getCategoria().getIdcategoria());//observe
            stmt.setInt(5, produto.getIdProduto());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(ProdutosDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public boolean remover(@NotNull Produtos produto) {
        String sql = "DELETE FROM produtos WHERE idproduto=?";
        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            //PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, produto.getIdProduto());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(ProdutosDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public List<Produtos> buscarPorNome(String nome) {
        String sql = "SELECT * FROM produtos WHERE descricao LIKE ?";
        List<Produtos> produtosList = new ArrayList<>();

        CategoriaDAO categoriaDAO = new CategoriaDAO();
        Map<Integer, Categoria> categoriasMap = categoriaDAO.mapearPorId();

        try (Connection connection = ConexaoSQLite.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");
            ResultSet resultado = stmt.executeQuery();

            while (resultado.next()) {
                Produtos produto = new Produtos();
                produto.setIdProduto(resultado.getInt("idproduto"));
                produto.setDescricao(resultado.getString("descricao"));
                produto.setQuantidadeEstoque(resultado.getInt("quantidade"));
                produto.setPreco(resultado.getDouble("preco"));

                int idCategoria = resultado.getInt("idcategoria");
                Categoria categoria = categoriasMap.get(idCategoria);
                produto.setCategoria(categoria);

                produtosList.add(produto);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ProdutosDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return produtosList;
    }

    public boolean temEstoqueSuficiente(int idProduto, int quantidade) {

        String sql = "SELECT quantidade FROM produtos WHERE idproduto = ?";

        try (Connection conn = ConexaoSQLite.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProduto);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("quantidade") >= quantidade;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void baixarEstoque(@NotNull Connection conn, int idProduto, int quantidadeVendida)
            throws SQLException {

        String sql = """
        UPDATE produtos
        SET quantidade = quantidade - ?
        WHERE idproduto = ?
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantidadeVendida);
            ps.setInt(2, idProduto);
            ps.executeUpdate();
        }
    }

    public void baixarEstoqueControlador(@NotNull Connection conn, int idProduto, int quantidadeVendida)
            throws SQLException {

        String sql = """
                    UPDATE produtos
                    SET quantidade = quantidade - ?
                    WHERE idproduto = ?
                    AND quantidade >=?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantidadeVendida);
            ps.setInt(2, idProduto);
            ps.setInt(3, quantidadeVendida);

            int linhas = ps.executeUpdate();

            if (linhas == 0){
                throw new IllegalStateException("Estoque insuficiente para o produto");
            }
        }
    }


    public void adicionarStock(int idProduto, int quantidade, Connection conn) {
        String sql = "UPDATE produtos SET quantidade = quantidade + ? WHERE idproduto = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantidade);
            ps.setInt(2, idProduto);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao devolver stock", e);
        }
    }
}
