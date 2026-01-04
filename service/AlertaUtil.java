package mbtec.com.mz.itemvendatest.service;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.Optional;

public class AlertaUtil {
    public static void mostrarErro(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
        stage.getIcons().add(
                new Image(Objects.requireNonNull(AlertaUtil.class.
                        getResourceAsStream("/mbtec/com/mz/itemvendatest/icones/mbtecShort.png")))
        );
        alerta.showAndWait();
    }

    public static void mostrarInfo(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
        stage.getIcons().add(
                new Image(Objects.requireNonNull(AlertaUtil.class.
                        getResourceAsStream("/mbtec/com/mz/itemvendatest/icones/mbtecShort.png")))
        );
        alerta.showAndWait();
    }

    public static void mostrarAviso(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
        stage.getIcons().add(
                new Image(Objects.requireNonNull(AlertaUtil.class.
                        getResourceAsStream("/mbtec/com/mz/itemvendatest/icones/mbtecShort.png")))
        );
        alerta.showAndWait();
    }

    public static Optional<ButtonType> mostrarConfirmacao(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
        stage.getIcons().add(
                new Image(Objects.requireNonNull(AlertaUtil.class.
                        getResourceAsStream("/mbtec/com/mz/itemvendatest/icones/mbtecShort.png")))
        );
        return alerta.showAndWait();

    }
}
