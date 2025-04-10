import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class TelaAutenticacaoAdmin {

    private TextField campoEmail;
    private PasswordField campoSenha;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public boolean mostrar(Stage stage) {
        stage.setTitle("Autenticação de Admin");

        // Campos de entrada
        campoEmail = new TextField();
        campoEmail.setPromptText("Digite seu email");

        campoSenha = new PasswordField();
        campoSenha.setPromptText("Digite sua senha");

        // Botão de autenticação
        Button botaoAutenticar = new Button("Autenticar");
        botaoAutenticar.setOnAction(e -> {
            if (autenticar()) {
                stage.close();
            }
        });

        // Layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.add(new Label("Email:"), 0, 0);
        grid.add(campoEmail, 1, 0);
        grid.add(new Label("Senha:"), 0, 1);
        grid.add(campoSenha, 1, 1);
        grid.add(botaoAutenticar, 1, 2);

        // Aplicando estilos CSS
        grid.getStyleClass().add("grid-pane"); // Adiciona a classe CSS ao GridPane
        campoEmail.getStyleClass().add("text-field"); // Adiciona a classe CSS ao campo de email
        campoSenha.getStyleClass().add("password-field"); // Adiciona a classe CSS ao campo de senha
        botaoAutenticar.getStyleClass().add("button"); // Adiciona a classe CSS ao botão

        // Cena
        Scene scene = new Scene(grid, 300, 200);
        scene.getStylesheets().add(getClass().getResource("stylesAutenticacaoAdmin.css").toExternalForm()); // Carrega o
                                                                                                            // arquivo
                                                                                                            // CSS
        stage.setScene(scene);
        stage.showAndWait(); // Aguarda o fechamento da janela

        // Retorna true se a autenticação foi bem-sucedida
        return autenticar();
    }

    private boolean autenticar() {
        String email = campoEmail.getText();
        String senha = campoSenha.getText();

        // Busca o usuário no banco de dados
        Usuario usuario = usuarioDAO.buscarUsuario(email, senha);

        if (usuario != null && usuario.getNivelAcesso().equals("user")) {
            return true; // Autenticação bem-sucedida

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Email ou senha incorretos, ou nível de acesso insuficiente!");
            alert.getDialogPane().getStyleClass().add("alert"); // Aplica o estilo CSS ao Alert
            alert.show();
            return false; // Autenticação falhou
        }
    }
}