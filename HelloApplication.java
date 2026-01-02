package mbtec.com.mz.itemvendatest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mbtec.com.mz.itemvendatest.DAO.ClienteDAO;
import mbtec.com.mz.itemvendatest.DB.ConexaoSQLite;
import mbtec.com.mz.itemvendatest.service.AlertaUtil;

import java.io.IOException;
import java.sql.Connection;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("VendaTest");
        stage.setScene(scene);
        stage.show();

        ClienteDAO dao = new ClienteDAO();
        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&DAO&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
        Connection connection = ConexaoSQLite.getConnection();
        if (connection == null){
            AlertaUtil.mostrarErro("Erro a Conexao", "Tente novamente");
        }
        System.out.println(connection);
        System.out.println(dao.listar());
    }

    public static void main(String[] args) {
        launch();

    }
}