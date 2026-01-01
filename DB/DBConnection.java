package mbtec.com.mz.itemvendatest.DB;

import mbtec.com.mz.itemvendatest.service.AlertaUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection connector(){
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:Sistemambtec.db");
            return conn;
        } catch (SQLException | ClassNotFoundException e) {
            AlertaUtil.mostrarErro("Erro de conexao ",e.getMessage());
        }

        return null;
    }


}
