package mbtec.com.mz.itemvendatest.controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
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
import mbtec.com.mz.itemvendatest.domain.Venda;
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
    private TableView<Produtos> tableviewProdutoDoSistema;

    @FXML
    private TableColumn<Produtos, Integer> colunaCodigoProdutoDoSistema;

    @FXML
    private TableColumn<Produtos, Integer> colunaEstoqueProdutoDoSistema;

    @FXML
    private TableColumn<Produtos, Double> colunaPrecoProdutoDoSistema;

    @FXML
    private TableColumn<Produtos, String> colunaProdutoProdutoDoSistema;


    @FXML
    private TableView<Itemvenda> tableViewCarrinho;

    @FXML
    private TableColumn<Itemvenda, Double> colunaDescontoCarrinho;

    @FXML
    private TableColumn<Itemvenda, Integer> colunaPrecoUnitarioCarrinho;

    @FXML
    private TableColumn<Itemvenda, String> colunaProdutoCarrinho;


    @FXML
    private TableColumn<Itemvenda, Integer> colunaQTDCarrinho;

    @FXML
    private TableColumn<Itemvenda, Double> colunaTotalCarrinho;


    @FXML
    private JFXComboBox<Cliente> comboBoxClientenoSistema;

    @FXML
    private Label lbDataHora;


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

    private Produtos produto;
    private Venda venda = new Venda();
    private Itemvenda itemvenda;

    private List<Produtos> produtosList;                // lista vinda do DAO
    private ObservableList<Produtos> produtosObservableList; // lista base da TableView
    private FilteredList<Produtos> produtosFilteredList;     // filtro da TableView
    private final ObservableList<Itemvenda> itemvendaObservableList = FXCollections.observableArrayList();
    private List<Itemvenda> itemvendaList;

    private ObservableList<Cliente> clienteObservableList;

    @FXML
    void btnAdicionarCarrinho(ActionEvent event) {

        Produtos produto = tableviewProdutoDoSistema
                .getSelectionModel().getSelectedItem();

        if (produto == null) {
            AlertaUtil.mostrarErro("Produto", "Selecione um produto");
            return;
        }

        int qtd;
        double desconto = 0;

        try {
            qtd = Integer.parseInt(txtQuantidade.getText());
            if (!txtDesconto.getText().isBlank()) {
                desconto = Double.parseDouble(txtDesconto.getText());
            }
        } catch (NumberFormatException e) {
            AlertaUtil.mostrarErro("Erro", "Quantidade ou desconto inválido");
            return;
        }

        if (qtd <= 0 || qtd > produto.getQuantidadeEstoque()) {
            AlertaUtil.mostrarErro("Quantidade", "Quantidade inválida ou sem estoque");
            return;
        }

        itemvenda = new Itemvenda(
                produto,
                qtd,
                produto.getPreco(),
                desconto,
                venda
        );

        venda.adicionarItem(itemvenda);
        itemvendaObservableList.add(itemvenda);

        //atualizarTotalVenda();
        atualizarValoresVenda();
        limparCamposItem();
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
    void checkBoxClienteNaoRegistado(ActionEvent event) {

        comboBoxClientenoSistema.getSelectionModel().clearSelection();

        txtCliente.clear();
        txtNuit.clear();

        txtCliente.setDisable(false);
        txtNuit.setDisable(false);

        venda.setCliente(null);
    }

    @FXML
    void btnRemoverItem(ActionEvent event) {
        Itemvenda itemSelecionado =
                tableViewCarrinho.getSelectionModel().getSelectedItem();

        if (itemSelecionado == null) {
            AlertaUtil.mostrarAviso("Remover item", "Selecione um item do carrinho");
            return;
        }

        venda.getItens().remove(itemSelecionado);
        itemvendaObservableList.remove(itemSelecionado);
        atualizarValoresVenda();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        venda = new Venda();
        lbDataHora.setText(LocalDate.now().toString());
        carregarTableViewProdutosNoSistema();
        carregarProdutosNoSistema();
        pesquisarProdutoPorNome();
        carregarCombboxClienteNoSistema();
        inicializarListeners();
        tableViewCarrinho.setItems(itemvendaObservableList);
        carregarTableViewCarrinho();
    }

    private void carregarTableViewProdutosNoSistema() {
        colunaCodigoProdutoDoSistema.setCellValueFactory(new PropertyValueFactory<>("idProduto"));
        colunaProdutoProdutoDoSistema.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        DateTimeFormatter dataEntrada = DateTimeFormatter.ofPattern("yyy-MM-dd");
        DateTimeFormatter datasaida = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        colunaPrecoProdutoDoSistema.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colunaEstoqueProdutoDoSistema.setCellValueFactory(new PropertyValueFactory<>("quantidadeEstoque"));
    }

    private void carregarTableViewCarrinho() {
        colunaProdutoCarrinho.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getProduto().getDescricao()
                )
        );

        colunaQTDCarrinho.setCellValueFactory(new PropertyValueFactory<>("quantidade"));

        colunaPrecoUnitarioCarrinho.setCellValueFactory(
                new PropertyValueFactory<>("precoUnitario")
        );

        colunaDescontoCarrinho.setCellValueFactory(
                new PropertyValueFactory<>("desconto")
        );

        // total calculado (SEM atributo!)
        colunaTotalCarrinho.setCellValueFactory(data ->
                new SimpleDoubleProperty(
                        data.getValue().getTotalComDesconto()
                ).asObject()
        );
    }

    private void limparCamposItem() {
        txtQuantidade.clear();
        txtDesconto.clear();
        tableviewProdutoDoSistema.getSelectionModel().clearSelection();
    }

    public void carregarCombboxClienteNoSistema() {
        List<Cliente> clienteList = new ClienteDAO().listar();
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

    private void inicializarListeners() {
        listenerPesquisaProduto();
        listenerCliente();
        listenerIVA();
        listenerPagamento();
    }

    private void listenerPesquisaProduto() {
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

    private void listenerPagamento() {
        txtDinheiroPago.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) calcularTroco();
        });

        txtDinheiroPago.focusedProperty().addListener((obs, o, focou) -> {
            if (!focou) calcularTroco();
        });
    }

    private void listenerIVA() {
        checkBoxIVA.selectedProperty().addListener((obs, oldValue, marcado) -> {

            if (marcado) {
                venda.setTaxaIva(0.17);
            } else {
                venda.setTaxaIva(0.0);
            }

            atualizarValoresVenda();
        });

    }

    private void listenerCliente() {

        comboBoxClientenoSistema.valueProperty().addListener((obs, antigo, novo) -> {
            if (novo != null) {
                txtCliente.setText(novo.getNome());
                txtNuit.setText(novo.getNuit());
                txtCliente.setDisable(true);
                txtNuit.setDisable(true);
                venda.setCliente(novo);
            }
        });
    }

    private void calcularTroco() {

        String texto = txtDinheiroPago.getText();

        if (texto == null || texto.isBlank()) {
            txtTroco.clear();
            return;
        }

        try {
            double pago = Double.parseDouble(texto);
            double total = venda.getTotalFinal();

            if (pago < total) {
                AlertaUtil.mostrarAviso(
                        "Valor inválido",
                        "O valor pago não pode ser menor que o total"
                );
                txtTroco.clear();
                return;
            }

            txtTroco.setText(String.format("%.2f", pago - total));

        } catch (NumberFormatException e) {
            AlertaUtil.mostrarErro("Valor inválido", "Digite apenas números");
            txtTroco.clear();
        }
    }

    private void atualizarValoresVenda() {

        txtSubtotal.setText(String.format("%.2f", venda.getSubtotal()));
        txtIVA.setText(String.format("%.2f", venda.getValorIva()));
        txtTotal.setText(String.format("%.2f", venda.getTotalFinal()));

        recalcularTrocoSePossivel();
    }

    private void recalcularTrocoSePossivel() {

        String texto = txtDinheiroPago.getText();

        if (texto == null || texto.isBlank()) {
            txtTroco.clear();
            return;
        }

        try {
            double pago = Double.parseDouble(texto);
            double total = venda.getTotalFinal();

            if (pago >= total) {
                txtTroco.setText(String.format("%.2f", pago - total));
            } else {
                txtTroco.clear();
            }

        } catch (NumberFormatException e) {
            txtTroco.clear();
        }
    }


    //deve ser eliminado
    private void listenerFocusCamposNomeCodigoPesquisa() {


        comboBoxClientenoSistema.valueProperty().addListener((obs, velho, novo) ->
        {
            if (novo != null) {

                venda.setCliente(novo);
                txtCliente.setText(novo.getNome());
                txtNuit.setText(novo.getNuit());
            }
        });

        txtCliente.focusedProperty().addListener((obs, velho, novo) ->
        {
            comboBoxClientenoSistema.getSelectionModel().clearSelection();
        });

        txtNuit.focusedProperty().addListener((obs, velho, novo) ->
        {
            comboBoxClientenoSistema.getSelectionModel().clearSelection();
        });


        txtDinheiroPago.focusedProperty().addListener((obs, antigo, focou) -> {

            // Só calcula quando PERDER o foco
            if (!focou) {

                String texto = txtDinheiroPago.getText();

                if (texto == null || texto.isBlank()) {
                    txtTroco.clear();
                    return;
                }

                try {
                    double pago = Double.parseDouble(texto);
                    double total = venda.getTotalFinal();

                    if (pago < total) {
                        AlertaUtil.mostrarAviso(
                                "Valor inválido",
                                "O valor pago não pode ser menor que o total da venda"
                        );
                        txtTroco.clear();
                        return;
                    }

                    txtTroco.setText(String.format("%.2f", pago - total));

                } catch (NumberFormatException e) {
                    AlertaUtil.mostrarErro(
                            "Valor inválido",
                            "Digite apenas números no valor pago"
                    );
                    txtTroco.clear();
                }
            }
        });

        txtDinheiroPago.setOnKeyPressed(event -> {

            if (event.getCode() != KeyCode.ENTER) {
                return;
            }

            String texto = txtDinheiroPago.getText();

            if (texto == null || texto.isBlank()) {
                txtTroco.clear();
                return;
            }

            try {
                double pago = Double.parseDouble(texto);
                double total = venda.getTotalFinal();

                if (pago < total) {
                    AlertaUtil.mostrarAviso(
                            "Valor inválido",
                            "O valor pago não pode ser menor que o total da venda"
                    );
                    txtTroco.clear();
                    return;
                }

                txtTroco.setText(String.format("%.2f", pago - total));

            } catch (NumberFormatException e) {
                AlertaUtil.mostrarErro(
                        "Valor inválido",
                        "Digite apenas números no valor pago"
                );
                txtTroco.clear();
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

    private void limparVenda() {
        venda = new Venda();
        itemvendaObservableList.clear();
        txtTotal.setText("0.00");
    }

}
