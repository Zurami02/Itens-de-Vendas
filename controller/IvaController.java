package mbtec.com.mz.itemvendatest.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import mbtec.com.mz.itemvendatest.DAO.ConfiguracaoDAO;
import mbtec.com.mz.itemvendatest.domain.Configuracao;
import mbtec.com.mz.itemvendatest.service.AlertaUtil;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class IvaController implements Initializable {
    @FXML
    private Label lbIVA;

    @FXML
    private TextField txtNomeIVA;

    @FXML
    private TextField txtValorIVA;

    @FXML
    void btnAdiconarIVA(ActionEvent event) {
        if (txtNomeIVA.getText().isBlank() || txtValorIVA.getText().isBlank()){
            AlertaUtil.mostrarErro("Falha","Campos invalidos");
            return;
        }
        String imposto = txtNomeIVA.getText().trim();
        String valorIVA = txtValorIVA.getText().trim();
        Configuracao config = new Configuracao(imposto,valorIVA);
       ConfiguracaoDAO.salvar(config);
        AlertaUtil.mostrarConfirmacao("Sucesso","IVA cadastrado");
        txtValorIVA.clear();
        txtNomeIVA.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String nomeIVA = "IVA";
        String buscado = String.valueOf(Objects.requireNonNull(ConfiguracaoDAO.buscarPorChave(nomeIVA)).getValor());
        if (buscado != null) lbIVA.setText(buscado+"%");
    }
}
