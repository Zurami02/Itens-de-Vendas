package mbtec.com.mz.itemvendatest.DB;

import mbtec.com.mz.itemvendatest.service.AlertaUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Responsavel pela comunicacao de Banco de dados local com as DAOs do sistema
 *
 */
public class ConexaoSQLite {

    private static Connection connection;
    private static final String DB_NAME = "Sistemambtec.db";

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {

                String localAppData = System.getenv("LOCALAPPDATA");
                if (localAppData == null)
                    localAppData = System.getProperty("user.home");

                File pastaApp = new File(localAppData, "MBTEC");
                File arquivoDestino = new File(pastaApp, DB_NAME);

                if (!arquivoDestino.exists()) {
                    pastaApp.mkdirs();
                    try (InputStream in =
                                 ConexaoSQLite.class.getResourceAsStream("/database/" + DB_NAME)) {
                        if (in == null)
                            throw new RuntimeException("Banco não encontrado no JAR!");
                        Files.copy(in, arquivoDestino.toPath(),
                                StandardCopyOption.REPLACE_EXISTING);

                    } catch (IOException e) {
                        AlertaUtil.mostrarErro("Erro a conexao",
                                "Banco de nao encontrado"+e.getMessage());
                    }
                }

                String url = "jdbc:sqlite:" + arquivoDestino.getAbsolutePath();
                connection = DriverManager.getConnection(url);
            }
        }catch (SQLException e){
            throw new RuntimeException("Erro ao conectar ao banco", e);
        }
        return connection;
    }

    public static void fecharConexao() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            AlertaUtil.mostrarErro("Erro ao fechar conexao","Contacte Assistencia tecnica");
        }
    }
}
