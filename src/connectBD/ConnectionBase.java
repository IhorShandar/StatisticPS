package connectBD;

import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ConnectionBase {

    private static Connection connection;

    public static Connection getConnection() {

        Alert alert = new Alert(Alert.AlertType.ERROR, "Connection failed!");
        alert.setTitle("Error");
        alert.setHeaderText("Attention!");

        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(Paths.get("database.properties"))) {
            properties.load(inputStream);
        } catch (IOException e) {
            alert.setContentText("File 'database.properties' not found or it is damaged");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = properties.getProperty("url");
            String userName = properties.getProperty("userName");
            String password = properties.getProperty("password");
            connection = DriverManager.getConnection(url, userName, password);
        }catch (Exception e) {
            alert.showAndWait();
        }

        return connection;
    }
}
