package mbtec.com.mz.itemvendatest.controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;
import javafx.util.StringConverter;
import mbtec.com.mz.itemvendatest.DAO.ClienteDAO;
import mbtec.com.mz.itemvendatest.DAO.ItemvendaDAO;
import mbtec.com.mz.itemvendatest.DAO.ProdutosDAO;
import mbtec.com.mz.itemvendatest.DAO.VendaDAO;
import mbtec.com.mz.itemvendatest.DB.ConexaoSQLite;
import mbtec.com.mz.itemvendatest.domain.Cliente;
import mbtec.com.mz.itemvendatest.domain.Itemvenda;
import mbtec.com.mz.itemvendatest.domain.Produtos;
import mbtec.com.mz.itemvendatest.domain.Venda;
import mbtec.com.mz.itemvendatest.service.AlertaUtil;

import java.net.URL;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @version 1.1
 * Metodo responsavel para cadastrar a venda
 */

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
    private JFXCheckBox checkBoxClienteNaoRegistado;

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
    private VendaDAO vendaDAO = new VendaDAO();

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
            AlertaUtil.piscarVermelho(tableviewProdutoDoSistema);
            //AlertaUtil.mostrarErro("Produto", "Selecione um produto");
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
            AlertaUtil.piscarVermelho(txtQuantidade);
            AlertaUtil.piscarVermelho(txtDesconto);
            //AlertaUtil.mostrarErro("Erro", "Quantidade ou desconto inválido");
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
            AlertaUtil.piscarVermelho(txtCodigoProdutoPesquisa);
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
            AlertaUtil.piscarVermelho(txtCodigoProdutoPesquisa);
            AlertaUtil.mostrarErro("Produto não encontrado",
                    "Nenhum produto com esse código");
        }
    }

    @FXML
    void btnFinalizar(ActionEvent event) {

        if (txtDinheiroPago.getText().isBlank()) {
            piscarVermelho(txtDinheiroPago);
            txtDinheiroPago.setPromptText("Falta pagamento");
            //AlertaUtil.
                    //mostrarAviso("Falta pagamento",
                            //"Certifique que o Cliente pagou");
            return;
        }

        if (venda.getItens().isEmpty()) {
            AlertaUtil.mostrarErro("Venda", "Carrinho vazio.");
            return;
        }

        if (!validarCliente()) return;

        try (Connection conn = ConexaoSQLite.getConnection()) {

            conn.setAutoCommit(false);

            VendaDAO vendaDAO = new VendaDAO();
            ItemvendaDAO itemDAO = new ItemvendaDAO();

            int idVenda = vendaDAO.salvarVenda(conn, venda);
            itemDAO.salvarItens(conn, idVenda, venda.getItens());

            conn.commit();

            AlertaUtil.mostrarInfo("Venda", "Venda finalizada com sucesso.");
            System.out.println(venda);
            if (venda.isVd()) {
                imprimirVD(venda);
            }
            limparFormulario();

        } catch (Exception e) {
            e.printStackTrace();
            AlertaUtil.mostrarErro("Erro", "Erro ao salvar venda.");
        }
    }

    @FXML
    void checkBoxClienteNaoRegistado(ActionEvent event) {
        controloClienteNaoRegistado();
    }

    @FXML
    void btnRemoverItem(ActionEvent event) {
        Itemvenda itemSelecionado =
                tableViewCarrinho.getSelectionModel().getSelectedItem();

        if (itemSelecionado == null) {
            AlertaUtil.piscarVermelho(tableViewCarrinho);
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

    private void controloClienteNaoRegistado(){
        boolean marcado = checkBoxClienteNaoRegistado.isSelected();

        if (marcado) {
            comboBoxClientenoSistema.hide();
            comboBoxClientenoSistema.setDisable(true);
            comboBoxClientenoSistema.getSelectionModel().clearSelection();

            txtCliente.clear();
            txtNuit.clear();

            txtCliente.setDisable(false);
            txtNuit.setDisable(false);

            venda.setCliente(null);

        } else {
            comboBoxClientenoSistema.setDisable(false);

            txtCliente.clear();
            txtNuit.clear();

            txtCliente.setDisable(true);
            txtNuit.setDisable(true);
        }
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

    /**
     * Piscando vermelho para dar erro a um campo textField
     * @param campo
     */

    private void piscarVermelho(Control campo) {

        String estiloErro = """
        -fx-border-color: red;
        -fx-background-color: #ffeeee;
        -fx-prompt-text-fill: red;
    """;

        String estiloNormal = "";

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> campo.setStyle(estiloErro)),
                new KeyFrame(Duration.seconds(1), e -> campo.setStyle(estiloNormal))
        );

        timeline.setCycleCount(3);
        timeline.setAutoReverse(true);
        timeline.play();
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
        txtCliente.setDisable(true);
        txtNuit.setDisable(true);
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
                AlertaUtil.piscarVermelho(txtDinheiroPago);
//                AlertaUtil.mostrarAviso(
//                        "Valor inválido",
//                        "O valor pago não pode ser menor que o total"
//                );
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

    private boolean validarCliente() {

        // Cliente registado
        if (venda.getCliente() != null) {
            venda.setNomeCliente(null);
            venda.setNuitCliente(null);
            return true;
        }

        // Cliente não registado
        String nome = txtCliente.getText().trim();
        String nuit = txtNuit.getText().trim();

        if (nome.isEmpty()) {
            AlertaUtil.piscarVermelho(comboBoxClientenoSistema);
            AlertaUtil.piscarVermelho(txtCliente);
            //AlertaUtil.mostrarErro("Cliente", "Informe o nome do cliente.");
            return false;
        }

        if (nuit.isEmpty()) {
            // regra comum: consumidor final
            nuit = "6660002";
        }

        venda.setNomeCliente(nome);
        venda.setNuitCliente(nuit);
        venda.setCliente(null);

        return true;
    }

    private void limparFormulario() {
        venda = new Venda();
        tableViewCarrinho.getItems().clear();
        itemvendaObservableList.clear();
        txtTotal.setText("0.00");
        txtSubtotal.clear();
        txtCliente.clear();
        txtNuit.clear();
        txtDinheiroPago.clear();
        txtIVA.clear();
        txtTroco.clear();

        comboBoxClientenoSistema.getSelectionModel().clearSelection();
    }

    private void imprimirVD(Venda venda) {
        System.out.println("Imprimindo VD da venda " + venda.getIdVenda());
        // futuramente:
        // JasperFillManager.fillReport(...)
        // JasperViewer.viewReport(...)
    }


}
