import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelaCadastroUsuario {

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cadastro de Usuário");

        // Campos de entrada
        TextField campoNome = new TextField();
        campoNome.setPromptText("Nome");
        campoNome.getStyleClass().add("campo-texto"); // Adiciona classe CSS

        TextField campoEmail = new TextField();
        campoEmail.setPromptText("Email");
        campoEmail.getStyleClass().add("campo-texto"); // Adiciona classe CSS

        TextField campoConfirmarEmail = new TextField();
        campoConfirmarEmail.setPromptText("Confirmar Email");
        campoConfirmarEmail.getStyleClass().add("campo-texto"); // Adiciona classe CSS

        PasswordField campoSenha = new PasswordField();
        campoSenha.setPromptText("Senha");
        campoSenha.getStyleClass().add("campo-texto"); // Adiciona classe CSS

        // ComboBox para selecionar o nível de acesso
        ComboBox<String> comboNivelAcesso = new ComboBox<>();
        comboNivelAcesso.getItems().addAll("admin", "usuario");
        comboNivelAcesso.setValue("usuario"); // Valor padrão
        comboNivelAcesso.getStyleClass().add("combo-box"); // Adiciona classe CSS

        // HBox para alinhar o ComboBox à esquerda
        HBox hboxCombo = new HBox(comboNivelAcesso);
        hboxCombo.setAlignment(Pos.CENTER_LEFT); // Alinha o ComboBox à esquerda

        // Label para mensagens de erro/sucesso
        Label labelMensagem = new Label();
        labelMensagem.getStyleClass().add("label-mensagem"); // Adiciona classe CSS

        // Botão para cadastrar
        Button botaoCadastrar = new Button("Cadastrar");
        botaoCadastrar.getStyleClass().add("botao"); // Adiciona classe CSS
        botaoCadastrar.setOnAction(e -> {
            String nome = campoNome.getText();
            String email = campoEmail.getText();
            String confirmarEmail = campoConfirmarEmail.getText();
            String senha = campoSenha.getText();
            String nivelAcesso = comboNivelAcesso.getValue();

            // Validação dos campos
            if (nome.isEmpty() || email.isEmpty() || confirmarEmail.isEmpty() || senha.isEmpty()) {
                labelMensagem.setText("Preencha todos os campos!");
                return;
            }

            // Verifica se os emails são iguais
            if (!email.equals(confirmarEmail)) {
                labelMensagem.setText("Os emails não coincidem!");
                return;
            }

            // Validação básica do formato do email
            if (!validarEmail(email)) {
                labelMensagem.setText("Email inválido!");
                return;
            }

            // Cadastra o usuário no banco de dados
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            boolean sucesso = usuarioDAO.adicionarUsuario(nome, email, senha, nivelAcesso);

            if (sucesso) {
                labelMensagem.setText("Usuário cadastrado com sucesso!");
            } else {
                labelMensagem.setText("Erro ao cadastrar usuário.");
            }
        });

        // Layout
        VBox vbox = new VBox(10, campoNome, campoEmail, campoConfirmarEmail, campoSenha, hboxCombo, botaoCadastrar, labelMensagem);
        vbox.setPadding(new Insets(20));
        vbox.getStyleClass().add("vbox"); // Adiciona classe CSS

        // Cena
        Scene scene = new Scene(vbox, 300, 350); // Ajustei a altura para 350px
        scene.getStylesheets().add(getClass().getResource("stylesCadastro.css").toExternalForm()); // Adiciona o arquivo CSS
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Método para validar o formato do email
    private boolean validarEmail(String email) {
        // Expressão regular simples para validar email
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(regex);
    }
}