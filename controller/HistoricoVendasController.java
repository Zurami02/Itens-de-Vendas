package mbtec.com.mz.itemvendatest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import mbtec.com.mz.itemvendatest.domain.Itemvenda;
import mbtec.com.mz.itemvendatest.domain.Venda;

import java.net.URL;
import java.util.ResourceBundle;

public class HistoricoVendasController implements Initializable {
    @FXML
    private AnchorPane anchorPaneMain;

    @FXML
    private TableColumn<Itemvenda, Integer> colunaCodigoDetalheVenda;

    @FXML
    private TableColumn<Itemvenda, Integer> colunaQTDDetalheVenda;

    @FXML
    private TableColumn<Itemvenda, Double> colunaPrecoDetalheVenda;

    @FXML
    private TableColumn<Itemvenda, String> colunaProdutoDetalheVenda;

    @FXML
    private TableColumn<Itemvenda, Double> colunaSubtotalDetalheVenda;

    @FXML
    private TableView<Itemvenda> tableViewDetalheVenda;


    @FXML
    private TableColumn<Venda, String> colunaDataVendaHistorico;

    @FXML
    private TableColumn<Venda, Integer> colunaCodigoVendaHistorico;

    @FXML
    private TableColumn<Venda, Double> colunaTotalVendaHistorico;

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

    @FXML
    void btnImprimirRecibo(ActionEvent event) {

    }

    @FXML
    void btnPesquisarVendaHistorico(ActionEvent event) {

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
