package mbtec.com.mz.itemvendatest.service;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

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

    public static void piscarVermelho(Control campo) {

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

    public static void piscarSucesso(Control campo) {

        String estiloErro = """
                    -fx-border-color: green;
                    -fx-background-color: #2ecc71;
                    -fx-prompt-text-fill: green;
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

}
