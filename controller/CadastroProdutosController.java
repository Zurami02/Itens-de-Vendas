package mbtec.com.mz.itemvendatest.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import mbtec.com.mz.itemvendatest.DAO.CategoriaDAO;
import mbtec.com.mz.itemvendatest.DAO.ProdutosDAO;
import mbtec.com.mz.itemvendatest.domain.Categoria;
import mbtec.com.mz.itemvendatest.domain.Produtos;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CadastroProdutosController implements Initializable {

    @FXML
    private TableColumn<Produtos, Integer> colunaCodigo;

    @FXML
    private TableColumn<Produtos, String> colunaDescricao;

    @FXML
    private TableColumn<Produtos, Integer> colunaQTD;

    @FXML
    private TableColumn<Produtos, String> colunacategoria;

    @FXML
    private TableColumn<Produtos, Double> colunapreco;

    @FXML
    private TableView<Produtos> tableviewProdutos;

    @FXML
    private ComboBox<Categoria> txtcomboboxCategoriaProduto;

    @FXML
    private TextField txtdescricao;

    @FXML
    private TextField txtpesquisa;

    @FXML
    private TextField txtquantidade;

    @FXML
    private TextField txtpreco;

    @FXML
    private TextField txtcategoria;

    private final ProdutosDAO produtosDAO = new ProdutosDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private Produtos produto;
    private List<Produtos> produtosList;
    private ObservableList<Produtos> observableProdutosList;
    private List<Categoria> categoriaList;
    private ObservableList<Categoria> observableCategoriaList;

    @FXML
    void btnPesquisaProduto(ActionEvent event) {
        String pesquisa = txtpesquisa.getText();
        List<Produtos> resultadoencontrado = produtosDAO.buscarPorNome(pesquisa);
        tableviewProdutos.getItems().clear();
        limparCampos();
        if (!resultadoencontrado.isEmpty()) {
            tableviewProdutos.getItems().addAll(resultadoencontrado);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Produto não encontrado");
            alert.setHeaderText(null);
            alert.setContentText("Nenhum produto encontrado com esse nome.");
            alert.showAndWait();
        }
    }

    @FXML
    void btndeletarProduto(ActionEvent event) {
        produto = tableviewProdutos.getSelectionModel().getSelectedItem();
        if (produto != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmacao de exclusao");
            alert.setHeaderText("Voce esta prestes a excluir o produto");
            alert.setContentText("Tem certeza que deseja excluir " + produto.getDescricao() + "?");

            Optional<ButtonType> resultado = alert.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                produtosDAO.remover(produto);
                carregarTableViewProdutos();
                limparCampos();
            }
        }
    }

    @FXML
    void btneditarProduto(ActionEvent event) {
        produto = tableviewProdutos.getSelectionModel().getSelectedItem();
        if (produto != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmação de Atualização");
            alert.setHeaderText("Você está prestes a atualizar o produto");
            alert.setContentText("Tem certeza que deseja atualizar " + produto.getDescricao() + "?");

            Optional<ButtonType> resultado = alert.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                String produtoAtual = produto.getDescricao(); // Armazena o produto atual
                String produtoNovo = txtdescricao.getText();

                if (!produtoAtual.equals(produtoNovo) && produtosDAO.existeProduto(produtoNovo)) {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro no Cadastro");
                    alert.setHeaderText("Campo produto inválido");
                    alert.setContentText("Produto já existe!");
                    alert.show();
                    return;
                }

                // Verifica se houve alguma alteração antes de editar
                if (!produtoAtual.equals(produtoNovo) ||
                        produto.getQuantidadeEstoque() != Integer.parseInt(txtquantidade.getText()) ||
                        produto.getPreco() != Double.parseDouble(txtpreco.getText()) ||
                        !produto.getCategoria().equals(txtcategoria.getText())) {

                    produto.setDescricao(txtdescricao.getText());
                    produto.setQuantidadeEstoque(Integer.parseInt(txtquantidade.getText()));
                    produto.setPreco(Double.parseDouble(txtpreco.getText()));
                    produto.setCategoria(txtcomboboxCategoriaProduto.getValue());

                    produtosDAO.editar(produto);
                    carregarTableViewProdutos();
                    limparCampos();
                } else {
                    // Se não houve mudanças, você pode mostrar uma mensagem ou simplesmente retornar
                    Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                    infoAlert.setTitle("Nenhuma Alteração");
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Nenhuma alteração foi feita no produto.");
                    infoAlert.show();
                }
            }
        }
    }

    @FXML
    void btnsalvarProduto(ActionEvent event) {
        if (validarEntradadedados()) {
            Categoria categoriaSelecionada = txtcomboboxCategoriaProduto.getValue();
            String produtoInserido = txtdescricao.getText();
            if (produtosDAO.existeProduto(produtoInserido)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro no Cadastro");
                alert.setHeaderText("Campo descricao invalido");
                alert.setContentText("Produto ja existe!");
                alert.show();
                return;
            }
            produto = new Produtos();
            produto.setDescricao(txtdescricao.getText());
            produto.setQuantidadeEstoque(Integer.parseInt(txtquantidade.getText()));
            produto.setPreco(Double.parseDouble(txtpreco.getText()));
            produto.setCategoria(categoriaSelecionada);
            produtosDAO.inserir(produto);
            limparCampos();
            carregarTableViewProdutos();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        carregarCombboxProdutosAutoCompletado();
        carregarTableViewProdutos();
        tableviewProdutos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtdescricao.setText(String.valueOf(newSelection.getDescricao()));
                txtquantidade.setText(String.valueOf(newSelection.getQuantidadeEstoque()));
                txtpreco.setText(String.valueOf(newSelection.getPreco()));
                txtcomboboxCategoriaProduto.setValue(newSelection.getCategoria());
            }
        });
    }

    //Validar entrada de Dados no Cadastro
    private boolean validarEntradadedados() {
        String erroMessage = "";
        if (txtdescricao.getText() == null || txtdescricao.getText().isEmpty()) {
            erroMessage += "Plano de conta invalido!\n";
        }
        if (txtquantidade.getText() == null || txtquantidade.getText().isEmpty()) {
            erroMessage += "A quantidade invalida!\n";
        }
        if (txtpreco.getText() == null || txtpreco.getText().isEmpty()) {
            erroMessage += "O preco invalido!\n";
        }
        if (txtcomboboxCategoriaProduto.getValue() == null || txtcomboboxCategoriaProduto.getValue() == null) {
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

    public void carregarComboBoxProdutos() {

        categoriaList = categoriaDAO.listar(); // ou outro método que lista produtos
        observableCategoriaList = FXCollections.observableArrayList(categoriaList);
        // Cria uma lista filtrável
        FilteredList<Categoria> categoriasFiltrados = new FilteredList<>(observableCategoriaList, p -> true);
        txtcomboboxCategoriaProduto.setItems(categoriasFiltrados);

        // Define os itens no ComboBox
        txtcomboboxCategoriaProduto.setItems(categoriasFiltrados);

        // Permite edição
        txtcomboboxCategoriaProduto.setEditable(true);

        // Listener para texto digitado
        txtcomboboxCategoriaProduto.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            final TextField editor = txtcomboboxCategoriaProduto.getEditor();
            final Categoria selected = txtcomboboxCategoriaProduto.getSelectionModel().getSelectedItem();

            // Atualiza a filtragem
            categoriasFiltrados.setPredicate(categoria -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lower = newValue.toLowerCase();
                return categoria.getDescricaocategoria().toLowerCase().contains(lower);
            });

            // Se o item selecionado não for o mesmo que o texto digitado, limpa seleção
            if (selected == null || !selected.getDescricaocategoria().equals(editor.getText())) {
                txtcomboboxCategoriaProduto.getSelectionModel().clearSelection();
            }
        });

        txtcomboboxCategoriaProduto.setConverter(new StringConverter<Categoria>() {
            @Override
            public String toString(Categoria categoria) {
                return categoria != null ? categoria.toString() : "";
            }

            @Override
            public Categoria fromString(String string) {
                return txtcomboboxCategoriaProduto.getItems().stream()
                        .filter(c -> c.toString().equals(string))
                        .findFirst().orElse(null);
            }
        });

    }

    public void carregarCombboxProdutosAutoCompletado() {
        // Converte a lista de produtos em uma lista observável
        categoriaList = new CategoriaDAO().listar(); // ou seu método
        observableCategoriaList = FXCollections.observableArrayList(categoriaList);
        txtcomboboxCategoriaProduto.setItems(observableCategoriaList);

        // Define um filtro dinâmico
        FilteredList<Categoria> categoriaFiltrados = new FilteredList<>(observableCategoriaList, c -> true);

        txtcomboboxCategoriaProduto.setItems(categoriaFiltrados);

        // Adiciona um listener para o editor de texto do ComboBox
        txtcomboboxCategoriaProduto.setEditable(true);
        txtcomboboxCategoriaProduto.getEditor().textProperty().addListener((obs,
                                                                            oldValue, newValue) -> {
            final String filtro = newValue.toLowerCase();

            // Aplica filtro
            categoriaFiltrados.setPredicate(categoria -> {
                if (filtro == null || filtro.isEmpty()) {
                    return true;
                }
                return categoria.getDescricaocategoria().toLowerCase().contains(filtro);
            });

            // Mostra o menu dropdown automaticamente
            if (!txtcomboboxCategoriaProduto.isShowing()) {
                txtcomboboxCategoriaProduto.show();
            }
        });

        // Corrige o comportamento de seleção para manter o objeto Produtos real
        txtcomboboxCategoriaProduto.setConverter(new StringConverter<Categoria>() {
            @Override
            public String toString(Categoria categoria) {
                return categoria != null ? categoria.getDescricaocategoria() : "";
            }

            @Override
            public Categoria fromString(String string) {
                return observableCategoriaList.stream()
                        .filter(c -> c.getDescricaocategoria().equals(string))
                        .findFirst().orElse(null);
            }
        });
    }

    private void carregarTableViewProdutos() {
        colunaCodigo.setCellValueFactory(new PropertyValueFactory<>("idproduto"));
        colunaDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colunaQTD.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colunapreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        //colunacategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colunacategoria.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCategoria().getDescricaocategoria()));


        //Listener para txtProcuraNome
        txtpesquisa.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                carregarTableViewProdutos();
            }
        });

        produtosList = produtosDAO.listar();

        observableProdutosList = FXCollections.observableArrayList(produtosList);
        tableviewProdutos.setItems(observableProdutosList);
    }

    private void limparCampos() {
        txtdescricao.clear();
        txtquantidade.clear();
        txtpreco.clear();
        txtcomboboxCategoriaProduto.setValue(null);
    }
}
