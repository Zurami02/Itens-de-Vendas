package mbtec.com.mz.itemvendatest.controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import mbtec.com.mz.itemvendatest.DAO.ItemvendaDAO;
import mbtec.com.mz.itemvendatest.DAO.ProdutosDAO;
import mbtec.com.mz.itemvendatest.DAO.VendaDAO;
import mbtec.com.mz.itemvendatest.domain.Cliente;
import mbtec.com.mz.itemvendatest.domain.Itemvenda;
import mbtec.com.mz.itemvendatest.domain.Produtos;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class VendasController implements Initializable {

    @FXML
    private AnchorPane anchorPaneMain;

    @FXML
    private JFXCheckBox checkBoxIVA;

    @FXML
    private JFXCheckBox checkBoxVD;

    @FXML
    private TableColumn<Produtos, Integer> colunaCodigoProdutoDoSistema;

    @FXML
    private TableColumn<Itemvenda, Double> colunaDescontoCarrinho;

    @FXML
    private TableColumn<Produtos, Integer> colunaEstoqueProdutoDoSistema;

    @FXML
    private TableColumn<Produtos, Double> colunaPrecoProdutoDoSistema;

    @FXML
    private TableColumn<Itemvenda, Integer> colunaPrecoUnitarioCarrinho;

    @FXML
    private TableColumn<Itemvenda, String> colunaProdutoCarrinho;

    @FXML
    private TableColumn<Produtos, String> colunaProdutoProdutoDoSistema;

    @FXML
    private TableColumn<Itemvenda, Integer> colunaQTDCarrinho;

    @FXML
    private TableColumn<Itemvenda, Double> colunaTotalCarrinho;

    @FXML
    private JFXComboBox<Cliente> comboBoxClientenoSistema;

    @FXML
    private Label lbDataHora;

    @FXML
    private TableView<Itemvenda> tableViewCarrinho;

    @FXML
    private TableView<Produtos> tablevieProdutoDoSistema;

    @FXML
    private TextField txtCliente;

    @FXML
    private TextField txtCodigoPesquisa;

    @FXML
    private TextField txtDesconto;

    @FXML
    private TextField txtDinheiroPago;

    @FXML
    private TextField txtIVA;

    @FXML
    private TextField txtNomeClientePesquisa;

    @FXML
    private TextField txtNuit;

    @FXML
    private TextField txtQuantidade;

    @FXML
    private TextField txtSubtotal;

    @FXML
    private TextField txtTotal;

    @FXML
    private TextField txtTroco;

    private final ProdutosDAO produtosDAO = new ProdutosDAO();
    private final ItemvendaDAO itemvendaDAO = new ItemvendaDAO();
    private final VendaDAO vendaDAO = new VendaDAO();
    private Produtos produtos;
    private List<Produtos> produtosList;
    private ObservableList<Produtos> produtosObservableList;

    @FXML
    void btnAdicionarCarrinho(ActionEvent event) {

    }

    @FXML
    void btnFinalizar(ActionEvent event) {

    }

    @FXML
    void btnRemoverItem(ActionEvent event) {

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        carregarTableViewProdutosNoSistema();
    }
    private void carregarTableViewProdutosNoSistema() {
        colunaCodigoProdutoDoSistema.setCellValueFactory(new PropertyValueFactory<>("idProduto"));
        colunaProdutoProdutoDoSistema.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        DateTimeFormatter dataEntrada = DateTimeFormatter.ofPattern("yyy-MM-dd");
        DateTimeFormatter datasaida = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        colunaPrecoProdutoDoSistema.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colunaEstoqueProdutoDoSistema.setCellValueFactory(new PropertyValueFactory<>("quantidadeEstoque"));

        //Listener para txtProcuraNome
        txtNomeClientePesquisa.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                carregarTableViewProdutosNoSistema();
            }
        });

        produtosList = produtosDAO.listar();

        produtosObservableList = FXCollections.observableArrayList(produtosList);
        tablevieProdutoDoSistema.setItems(produtosObservableList);
    }
}
