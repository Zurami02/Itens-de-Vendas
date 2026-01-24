package mbtec.com.mz.itemvendatest.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mbtec.com.mz.itemvendatest.DAO.ItemvendaDAO;
import mbtec.com.mz.itemvendatest.DAO.VendaDAO;
import mbtec.com.mz.itemvendatest.domain.Itemvenda;
import mbtec.com.mz.itemvendatest.domain.Produtos;
import mbtec.com.mz.itemvendatest.domain.Venda;
import mbtec.com.mz.itemvendatest.service.AlertaUtil;
import mbtec.com.mz.itemvendatest.service.VendaService;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @version 1.1
 * class controller de Historico venda responsavel apenas para exibicao de vendas feitas
 */
public class HistoricoVendasController implements Initializable {
    @FXML
    private AnchorPane anchorPaneMain;

    @FXML
    private TableColumn<Itemvenda, Integer> colunaCodigoDetalheVenda;

    @FXML
    private TableColumn<Itemvenda, Integer> colunaQTDDetalheVenda;

    @FXML
    private TableColumn<Itemvenda, String> colunaPrecoDetalheVenda;

    @FXML
    private TableColumn<Itemvenda, Double> colunaDescontoDetalheVenda;

    @FXML
    private TableColumn<Itemvenda, String> colunaProdutoDetalheVenda;

    @FXML
    private TableColumn<Itemvenda, String> colunaSubtotalDetalheVenda;

    @FXML
    private TableView<Itemvenda> tableViewDetalheVenda;


    @FXML
    private TableColumn<Venda, String> colunaDataVendaHistorico;

    @FXML
    private TableColumn<Venda, Integer> colunaCodigoVendaHistorico;

    @FXML
    private TableColumn<Venda, Double> colunaTotalVendaHistorico;

    @FXML
    private TableColumn<Venda, Double> colunaIVAVendaHistorico;

    @FXML
    private TableColumn<Venda, String> colunaclienteVendaHistorico;

    @FXML
    private TableView<Venda> tableviewVendaHistorico;


    @FXML
    private DatePicker datPickerFinalHistorico;

    @FXML
    private DatePicker datPickerinicialHistorico;

    @FXML
    private Label lbFeedBack;

    @FXML
    private Label lbTAXAIVAVendas;

    @FXML
    private TextField txtCodigoVendaHistorico;

    @FXML
    private TextField txtDataVendaHistorico;

    @FXML
    private TextField txtNomeClienteHistorico;

    @FXML
    private TextField txtTotalVendaHistorico;

    @FXML
    private TextField txtPesquisaNomeClienteHistorico;

    @FXML
    private CheckBox checkBoxPesqNomeHistorico;

    private Venda venda = new Venda();
    private Itemvenda itemvenda;
    private VendaService vs = new VendaService();

    private VendaDAO vendaDAO = new VendaDAO();
    private ItemvendaDAO itemvendaDAO = new ItemvendaDAO();

    private List<Produtos> produtosList;                // lista vinda do DAO
    private ObservableList<Venda> vendaObservableList = FXCollections.observableArrayList();
    private FilteredList<Produtos> produtosFilteredList;     // filtro da TableView
    private final ObservableList<Itemvenda> itemvendaObservableList = FXCollections.observableArrayList();
    private List<Itemvenda> itemvendaList;
    private List<Venda> vendaList;

    @FXML
    void btnImprimirRecibo(ActionEvent event) {

    }

    @FXML
    private void btnAnularVenda() {
        Venda venda = tableviewVendaHistorico.getSelectionModel().getSelectedItem();

        if (venda == null) {
            AlertaUtil.mostrarErro("Erro","Selecione uma venda.");
            return;
        }

        if ("ANULADA".equals(venda.getStatus())) {
            AlertaUtil.mostrarInfo("Importante","Esta venda já está anulada.");
            return;
        }
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION,"Tem certeza que deseja anular a venda?");
        Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
        stage.getIcons().add(
                new Image(Objects.requireNonNull(AlertaUtil.class.//linha 146
                        getResourceAsStream("/mbtec/com/mz/itemvendatest/icones/mbtecShort.png")))
        );
        ButtonType btnSim = new ButtonType("Sim");
        ButtonType btnNao = new ButtonType("Não", ButtonBar.ButtonData.CANCEL_CLOSE);
        alerta.getButtonTypes().setAll(btnSim, btnNao);

        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == btnSim) {
            vs.anularVenda(venda);
        }
        carregarTableViewVendasHistorico();
        pintarAnulada();
    }


    @FXML
    void checkboxMarcado(ActionEvent event) {
        boolean pesquisarPorNome = checkBoxPesqNomeHistorico.isSelected();

        datPickerinicialHistorico.setDisable(pesquisarPorNome);
        datPickerFinalHistorico.setDisable(pesquisarPorNome);

        if (pesquisarPorNome) {
            txtPesquisaNomeClienteHistorico.setDisable(false);
            datPickerinicialHistorico.setValue(null);
            datPickerFinalHistorico.setValue(null);
        }
    }

    @FXML
    void btnPesquisarVendaHistorico(ActionEvent event) {
        boolean porNome = checkBoxPesqNomeHistorico.isSelected();

        LocalDate dataInicial = porNome ? null : datPickerinicialHistorico.getValue();
        LocalDate dataFinal = porNome ? null : datPickerFinalHistorico.getValue();
        String textoPesquisa = txtPesquisaNomeClienteHistorico.getText().trim();

        if (checkBoxPesqNomeHistorico.isSelected()) {
            if (textoPesquisa.isBlank()) {
                AlertaUtil.piscarVermelho(txtPesquisaNomeClienteHistorico);
                return;
            }
        } else {
            if (dataInicial == null) {
                AlertaUtil.piscarVermelho(datPickerinicialHistorico);
                return;
            }
        }

        if (!porNome) {
            if (dataInicial == null || dataFinal == null) {
                AlertaUtil.mostrarErro("Pesquisa", "Informe o período.");
                return;
            }

            if (dataFinal.isBefore(dataInicial)) {
                AlertaUtil.mostrarErro("Pesquisa", "Data final menor que a inicial.");
                return;
            }
        }

        List<Venda> vendas = vendaDAO.historicoVendas(
                dataInicial,
                dataFinal,
                textoPesquisa
        );

        tableviewVendaHistorico.setItems(FXCollections.observableArrayList(vendas));
        System.out.println(vendas);
        tableViewDetalheVenda.getItems().clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtPesquisaNomeClienteHistorico.setDisable(true);
        carregarTableViewVendasHistorico();
        carregarTableViewItensvendas();
        datPickerinicialHistorico.focusedProperty().addListener(
                (obs, old, newV) ->
                {
                    if (newV != null) {
                        txtPesquisaNomeClienteHistorico.setDisable(true);
                        txtPesquisaNomeClienteHistorico.clear();
                    }

                }
        );
        pintarAnulada();
        carregarItensVendaSelecionadaListener();
    }

    private void pintarAnulada(){
        tableviewVendaHistorico.setRowFactory(tabela->
                new TableRow<>()
                {
                    @Override
                    protected void updateItem(Venda venda, boolean empty){
                        super.updateItem(venda, empty);
                        if(venda == null || empty){
                            setStyle("");
                        }else if (venda.isAnulada()){
                            setStyle("""
                                    -fx-background-color: #F08080;
                                    -fx-text-fill: #ebebeb;
                                    -fx-font-style: italic;
                                    """);
                        }else {
                            setStyle("");
                        }
                    }
                });
    }

    private void carregarItensVendaSelecionadaListener() {
        tableviewVendaHistorico.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, vendaselecionada) -> {
                            if (vendaselecionada != null) {
                                carregarItensDaVenda(vendaselecionada.getIdVenda());
                            } else {
                                tableViewDetalheVenda.getItems().clear();
                            }
                        }
                );
    }

    private void carregarItensDaVenda(int idVenda) {

            List<Itemvenda> itens =
                    itemvendaDAO.listarPorVenda(idVenda);
        tableViewDetalheVenda.setItems(
                FXCollections.observableArrayList(itens)
        );
    }

    private void carregarTableViewVendasHistorico() {
        colunaCodigoVendaHistorico.setCellValueFactory(new PropertyValueFactory<>("idVenda"));
        colunaclienteVendaHistorico.setCellValueFactory(cell -> {
            Venda v = cell.getValue();
            if (v.getCliente() != null) {
                return new SimpleStringProperty(v.getCliente().getNome());
            } else {
                return new SimpleStringProperty(v.getNomeCliente());
            }
        });

        colunaDataVendaHistorico.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getDataVenda().format(DateTimeFormatter.ofPattern(
                                "dd/MM/yyyy HH:HH"
                        ))
                )
        );

        colunaTotalVendaHistorico.setCellValueFactory(
                new PropertyValueFactory<>("totalDb")
        );

        colunaIVAVendaHistorico.setCellValueFactory(
                new PropertyValueFactory<>("valorIVA")
        );
    }

    private void carregarTableViewItensvendas() {
        colunaCodigoDetalheVenda.setCellValueFactory(cell ->
                new SimpleIntegerProperty(
                        cell.getValue().getProduto().getIdProduto()
                ).asObject()
        );
        colunaProdutoDetalheVenda.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getProduto().getDescricao()
                )
        );

        colunaQTDDetalheVenda.setCellValueFactory(new PropertyValueFactory<>("quantidade")
        );

        colunaPrecoDetalheVenda.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        String.format("%.2f", cell.getValue().getPrecoUnitario())
                )
        );

        colunaSubtotalDetalheVenda.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        String.format("%.2f", cell.getValue().getTotalComDesconto())
                )
        );

        colunaDescontoDetalheVenda.setCellValueFactory(cell ->
                new SimpleDoubleProperty(
                        cell.getValue().getDesconto()
                ).asObject()
        );
    }
}
