import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelaLogin extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login");

        // Campos de entrada
        TextField campoEmail = new TextField();
        campoEmail.setPromptText("Email");
        campoEmail.getStyleClass().add("campo-texto"); // Adiciona classe CSS

        PasswordField campoSenha = new PasswordField();
        campoSenha.setPromptText("Senha");
        campoSenha.getStyleClass().add("campo-texto"); // Adiciona classe CSS

        // Label para mensagens de erro
        Label labelMensagem = new Label();
        labelMensagem.getStyleClass().add("label-mensagem"); // Adiciona classe CSS

        // Botão para login
        Button botaoLogin = new Button("Login");
        botaoLogin.getStyleClass().add("botao"); // Adiciona classe CSS
        botaoLogin.setOnAction(e -> {
            String email = campoEmail.getText();
            String senha = campoSenha.getText();

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuario = usuarioDAO.autenticar(email, senha);

            if (usuario != null) {
                // Redireciona para a tela de estoque com o nível de acesso do usuário
                TelaEstoque telaEstoque = new TelaEstoque(usuario.getNivelAcesso());
                Stage stageEstoque = new Stage();
                telaEstoque.start(stageEstoque);
                primaryStage.close(); // Fecha a tela de login
            } else {
                labelMensagem.setText("Email ou senha incorretos.");
            }
        });

        // Botão para cadastrar novo usuário
        Button botaoCadastrar = new Button("Cadastrar Novo Usuário");
        botaoCadastrar.getStyleClass().add("botao"); // Adiciona classe CSS
        botaoCadastrar.setOnAction(e -> {
            Stage stageCadastro = new Stage();
            TelaCadastroUsuario telaCadastro = new TelaCadastroUsuario();
            telaCadastro.start(stageCadastro);
        });

        // Layout
        VBox vbox = new VBox(10, campoEmail, campoSenha, botaoLogin, botaoCadastrar, labelMensagem);
        vbox.setPadding(new Insets(20));
        vbox.getStyleClass().add("vbox"); // Adiciona classe CSS

        // Cena
        Scene scene = new Scene(vbox, 300, 250); // Ajustei a altura para 250px
        scene.getStylesheets().add(getClass().getResource("stylesLogin.css").toExternalForm()); // Adiciona o arquivo CSS
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // Inicia a aplicação JavaFX
    }
}