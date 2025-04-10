import java.sql.SQLException;

import com.itextpdf.text.List;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        // Inicia a aplicação JavaFX
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Cria e exibe a tela de login
            TelaLogin telaLogin = new TelaLogin();
            telaLogin.start(primaryStage);
        } catch (Exception e) {
            // Exibe uma mensagem de erro em caso de falha
            System.err.println("Erro ao iniciar a aplicação: " + e.getMessage());
            e.printStackTrace();
        }
    }

}