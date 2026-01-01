package mbtec.com.mz.itemvendatest.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import mbtec.com.mz.itemvendatest.DAO.CategoriaDAO;
import mbtec.com.mz.itemvendatest.domain.Categoria;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CadastroCategoriaController implements Initializable {

    @FXML
    private TableColumn<Categoria, Integer> colunaCodigoCategoria;

    @FXML
    private TableColumn<Categoria, String> colunaDescricaoCategoria;

    @FXML
    private TableView<Categoria> tableviewCategoria;

    @FXML
    private TextField txtdescricaoCategoria;

    @FXML
    private TextField txtpesquisaCategoria;

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private Categoria categoria;
    private List<Categoria> categoriaList;
    private ObservableList<Categoria> observableCategoriaList;

    @FXML
    void btnPesquisaCategoria(ActionEvent event) {
        String pesquisa = txtpesquisaCategoria.getText();
        List<Categoria> resultadoencontrado = categoriaDAO.buscarPorNome(pesquisa);
        tableviewCategoria.getItems().clear();
        limparCampos();
        if (!resultadoencontrado.isEmpty()) {
            tableviewCategoria.getItems().addAll(resultadoencontrado);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Categoria não encontrada");
            alert.setHeaderText(null);
            alert.setContentText("Nenhuma categoria encontrada com o nome " + pesquisa.toUpperCase());
            alert.showAndWait();
        }
    }

    @FXML
    void btndeletarCategoria(ActionEvent event) {
        categoria = tableviewCategoria.getSelectionModel().getSelectedItem();
        if (categoria != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmacao de exclusao");
            alert.setHeaderText("Voce esta prestes a excluir a categoria");
            alert.setContentText("Tem certeza que deseja excluir " + categoria.getDescricaocategoria() + "?");

            Optional<ButtonType> resultado = alert.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                categoriaDAO.remover(categoria);
                carregarTableViewCategoria();
                limparCampos();
            }
        } else {
            Alert info = new Alert(Alert.AlertType.ERROR);
            info.setTitle("Erro na exclusao");
            info.setHeaderText(null);
            info.setContentText("Por favor selecione a categoria para completar a operacao!");
            info.show();
        }
    }

    @FXML
    void btneditarCategoria(ActionEvent event) {
        categoria = tableviewCategoria.getSelectionModel().getSelectedItem();
        if (categoria == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação de Atualização");
        alert.setHeaderText("Você está prestes a atualizar a categoria");
        alert.setContentText("Tem certeza que deseja atualizar a categoria: " + categoria.getDescricaocategoria() + "?");

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            // Dados atuais
            int idAtual = categoria.getIdcategoria();
            String categoriaAtual = categoria.getDescricaocategoria();

            // Dados novos do formulário
            String categoriaNova = txtdescricaoCategoria.getText().trim();

            // Verifica se houve alteração
            boolean houveAlteracao =
                    !categoriaAtual.equals(categoriaNova);

            if (houveAlteracao) {
                // Atualiza o objeto
                categoria.setDescricaocategoria(categoriaNova);

                // Salva no banco
                categoriaDAO.editar(categoria);
                carregarTableViewCategoria();
                limparCampos();
            } else {
                // Nenhuma alteração detectada
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Nenhuma Alteração");
                info.setHeaderText(null);
                info.setContentText("Nenhuma alteração foi feita na categoria " + categoriaAtual);
                info.show();
            }
        }
    }

    @FXML
    void btnsalvarCategoria(ActionEvent event) {
        if (validarEntradadedados()) {
            // Dados novos do formulário
            String categoriaNovo = txtdescricaoCategoria.getText().trim();
            // Verifica se já existe outro registro com mesma combinação fornecedor + descrição
            if (categoriaDAO.existeDescricaoCategoria(categoriaNovo)) {
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setTitle("Erro de Duplicação");
                erro.setHeaderText("A categoria cadastrada");
                erro.setContentText("A categoria " + txtdescricaoCategoria.getText() + " Já existe.");
                erro.show();
                return;
            }
            categoria = new Categoria();
            categoria.setDescricaocategoria(txtdescricaoCategoria.getText());
            categoriaDAO.inserir(categoria);
            limparCampos();
            carregarTableViewCategoria();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        carregarTableViewCategoria();
        tableviewCategoria.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtdescricaoCategoria.setText(String.valueOf(newSelection.getDescricaocategoria()));
            }
        });

    }

    private void limparCampos() {
        txtdescricaoCategoria.clear();
    }

    private boolean validarEntradadedados() {
        String erroMessage = "";
        if (txtdescricaoCategoria.getText() == null || txtdescricaoCategoria.getText().isEmpty()) {
            erroMessage += "A categoria invalida!\n";
        }


        if (erroMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro no Cadastro");
            alert.setHeaderText("Campos invalidos, por favor, corrija...");
            alert.setContentText(erroMessage);
            alert.show();
            return false;
        }
    }

    private void carregarTableViewCategoria() {
        colunaCodigoCategoria.setCellValueFactory(new PropertyValueFactory<>("idcategoria"));
        colunaDescricaoCategoria.setCellValueFactory(new PropertyValueFactory<>("descricaocategoria"));

        //Listener para txtProcuraNome
        txtpesquisaCategoria.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                carregarTableViewCategoria();
            }
        });

        categoriaList = categoriaDAO.listar();

        observableCategoriaList = FXCollections.observableArrayList(categoriaList);
        tableviewCategoria.setItems(observableCategoriaList);
    }
}
