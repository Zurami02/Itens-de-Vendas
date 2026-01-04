package mbtec.com.mz.itemvendatest.controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import mbtec.com.mz.itemvendatest.DAO.ClienteDAO;
import mbtec.com.mz.itemvendatest.DAO.ItemvendaDAO;
import mbtec.com.mz.itemvendatest.DAO.ProdutosDAO;
import mbtec.com.mz.itemvendatest.DAO.VendaDAO;
import mbtec.com.mz.itemvendatest.domain.Cliente;
import mbtec.com.mz.itemvendatest.domain.Itemvenda;
import mbtec.com.mz.itemvendatest.domain.Produtos;
import mbtec.com.mz.itemvendatest.service.AlertaUtil;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
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
    private TableView<Produtos> tableviewProdutoDoSistema;

    @FXML
    private TextField txtCliente;

    @FXML
    private TextField txtCodigoProdutoPesquisa;

    @FXML
    private TextField txtDesconto;

    @FXML
    private TextField txtDinheiroPago;

    @FXML
    private TextField txtIVA;

    @FXML
    private TextField txtNomeProdutoPesquisa;

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

    private List<Produtos> produtosList;                // lista vinda do DAO
    private ObservableList<Produtos> produtosObservableList; // lista base da TableView
    private FilteredList<Produtos> produtosFilteredList;     // filtro da TableView

    private List<Cliente> clienteList;
    private ObservableList<Cliente> clienteObservableList;

    @FXML
    void btnAdicionarCarrinho(ActionEvent event) {

    }

    @FXML
    void pesquisarProdutoPorCodigo(KeyEvent event) {

        String texto = txtCodigoProdutoPesquisa.getText();
        if (texto.isEmpty()) {
            if (produtosFilteredList != null) {
                produtosFilteredList.setPredicate(p -> true); // mostra todos
                tableviewProdutoDoSistema.getSelectionModel().clearSelection();
            }
            return;
        }
        if (event.getCode() != KeyCode.ENTER) return;

        if (texto.isBlank()) return;

        int codigo;
        try {
            codigo = Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            AlertaUtil.mostrarErro("Código inválido", "Digite apenas números");
            return;
        }

        boolean encontrado = false;

        for (Produtos p : produtosList) {
            if (p.getIdProduto() == codigo) {
                produtosFilteredList.setPredicate(prod ->
                        prod.getIdProduto() == codigo);

                tableviewProdutoDoSistema.getSelectionModel().select(p);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            AlertaUtil.mostrarErro("Produto não encontrado",
                    "Nenhum produto com esse código");
        }
    }

    @FXML
    void btnFinalizar(ActionEvent event) {

    }

    @FXML
    void btnRemoverItem(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lbDataHora.setText(LocalDate.now().toString());
        carregarTableViewProdutosNoSistema();
        carregarProdutosNoSistema();
        pesquisarProdutoPorNome();
        carregarCombboxClienteNoSistema();
        listenerFocusCamposNomeCodigoPesquisa();
    }

    private void carregarTableViewProdutosNoSistema() {
        colunaCodigoProdutoDoSistema.setCellValueFactory(new PropertyValueFactory<>("idProduto"));
        colunaProdutoProdutoDoSistema.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        DateTimeFormatter dataEntrada = DateTimeFormatter.ofPattern("yyy-MM-dd");
        DateTimeFormatter datasaida = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        colunaPrecoProdutoDoSistema.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colunaEstoqueProdutoDoSistema.setCellValueFactory(new PropertyValueFactory<>("quantidadeEstoque"));
    }

    public void carregarCombboxClienteNoSistema() {
        clienteList = new ClienteDAO().listar(); // ou seu método
        clienteObservableList = FXCollections.observableArrayList(clienteList);
        comboBoxClientenoSistema.setItems(clienteObservableList);
        // Define um filtro dinâmico
        FilteredList<Cliente> clienteFiltrados = new FilteredList<>(clienteObservableList, c -> true);

        comboBoxClientenoSistema.setItems(clienteFiltrados);

        // Adiciona um listener para o editor de texto do ComboBox
        comboBoxClientenoSistema.setEditable(true);
        comboBoxClientenoSistema.getEditor().textProperty().addListener((obs,
                                                                         oldValue, newValue) -> {
            final String filtro = newValue.toLowerCase().trim();

            clienteFiltrados.setPredicate(cliente -> {
                if (filtro.isEmpty()) {
                    return true;
                }
                return cliente.getNome().toLowerCase().contains(filtro);
            });

            if (!comboBoxClientenoSistema.isShowing()) {
                comboBoxClientenoSistema.show();
            }
        });

        // Corrige o comportamento de seleção para manter o objeto Cliente real
        comboBoxClientenoSistema.setConverter(new StringConverter<Cliente>() {
            @Override
            public String toString(Cliente cliente) {
                return cliente != null ? cliente.getNome() : "";
            }

            @Override
            public Cliente fromString(String string) {
                return clienteObservableList.stream()
                        .filter(c -> c.getNome().equals(string))
                        .findFirst().orElse(null);
            }
        });
    }

    //pesquisar produtos usando nome
    private void pesquisarProdutoPorNome() {

        txtNomeProdutoPesquisa.textProperty().addListener((obs, oldValue, newValue) -> {

            produtosFilteredList.setPredicate(produto -> {

                if (newValue == null || newValue.isBlank()) {
                    return true;
                }

                return produto.getDescricao()
                        .toLowerCase()
                        .contains(newValue.toLowerCase());
            });
        });
    }

    private void listenerFocusCamposNomeCodigoPesquisa() {
        txtNomeProdutoPesquisa.focusedProperty().addListener((obs, old, focou) -> {
            if (focou) {
                txtCodigoProdutoPesquisa.clear();
            }
        });

        txtCodigoProdutoPesquisa.focusedProperty().addListener((obs, old, focou) -> {
            if (focou) {
                txtNomeProdutoPesquisa.clear();
            }
        });

    }

    private void limparFiltro() {
        produtosFilteredList.setPredicate(p -> true);
    }

    //carregar os produtos no sistema
    private void carregarProdutosNoSistema() {

        produtosList = produtosDAO.listar();

        produtosObservableList =
                FXCollections.observableArrayList(produtosList);

        produtosFilteredList =
                new FilteredList<>(produtosObservableList, p -> true);

        tableviewProdutoDoSistema.setItems(produtosFilteredList);
    }




}
