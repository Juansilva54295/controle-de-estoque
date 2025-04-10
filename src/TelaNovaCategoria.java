import java.sql.SQLException;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelaNovaCategoria {

    private CategoriaDAO categoriaDAO;

    public TelaNovaCategoria(CategoriaDAO categoriaDAO) {
        this.categoriaDAO = categoriaDAO;
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Adicionar Nova Categoria");

        // Campo de texto para a nova categoria
        TextField campoCategoria = new TextField();
        campoCategoria.setPromptText("Digite o nome da categoria");

        // Botão para adicionar a categoria
        Button botaoAdicionar = new Button("Adicionar");
        botaoAdicionar.setOnAction(e -> {
            String nomeCategoria = campoCategoria.getText().trim();
            if (!nomeCategoria.isEmpty()) {
                // Cria um novo objeto Categoria com o nome fornecido
                Categoria novaCategoria = new Categoria();
                novaCategoria.setNome(nomeCategoria);

                try {
                    // Adiciona a categoria ao banco de dados
                    categoriaDAO.adicionarCategoria(novaCategoria);
                    
                    // Limpa o campo de texto após a adição
                    campoCategoria.clear();
                    
                    // Fecha a tela após adicionar
                    primaryStage.close();
                    
                    // Exibe uma mensagem de sucesso (opcional)
                    System.out.println("Categoria adicionada com sucesso!");
                } catch (SQLException ex) {
                    // Trata a exceção (exibe uma mensagem de erro ou registra o erro)
                    System.err.println("Erro ao adicionar categoria: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        // Layout
        VBox vbox = new VBox(10, new Label("Nova Categoria:"), campoCategoria, botaoAdicionar);
        vbox.setPadding(new Insets(10));

        // Cena
        Scene scene = new Scene(vbox, 300, 150);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}