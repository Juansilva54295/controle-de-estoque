import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelaAutenticacao {

    private boolean autenticado = false;
    private String nivelAcessoRequerido; // Nível de acesso necessário (admin ou usuario)

    public TelaAutenticacao(String nivelAcessoRequerido) {
        this.nivelAcessoRequerido = nivelAcessoRequerido;
    }

    public boolean mostrar(Stage primaryStage) {
        primaryStage.setTitle("Autenticação");

        // Campos de entrada
        TextField campoEmail = new TextField();
        campoEmail.setPromptText("Email");

        PasswordField campoSenha = new PasswordField();
        campoSenha.setPromptText("Senha");

        // Botão para confirmar
        Button botaoConfirmar = new Button("Confirmar");
        botaoConfirmar.setOnAction(e -> {
            String email = campoEmail.getText();
            String senha = campoSenha.getText();

            // Verifica a autenticação
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuario = usuarioDAO.autenticar(email, senha);

            if (usuario != null && usuario.getNivelAcesso().equals(nivelAcessoRequerido)) {
                autenticado = true;
                primaryStage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Email ou senha incorretos, ou o usuário não tem permissão.");
                alert.show();
            }
        });

        // Layout
        VBox vbox = new VBox(10, campoEmail, campoSenha, botaoConfirmar);
        vbox.setPadding(new Insets(10));

        // Cena
        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.showAndWait(); // Espera o usuário fechar a janela

        return autenticado;
    }
}