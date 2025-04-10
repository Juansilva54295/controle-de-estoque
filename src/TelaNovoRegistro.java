import java.sql.SQLException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelaNovoRegistro {

    private CategoriaDAO categoriaDAO = new CategoriaDAO(); // DAO para categorias

    public TelaNovoRegistro(Stage primaryStage, TableView<Produto> tabelaProdutos, ProdutoDAO produtoDAO) {
        primaryStage.setTitle("Adicionar Novo Produto");

        // Campos de entrada
        Label labelNome = new Label("Nome do Produto:");
        labelNome.getStyleClass().add("label"); // Adiciona classe CSS

        TextField campoNome = new TextField();
        campoNome.setPromptText("Nome do Produto");
        campoNome.getStyleClass().add("campo-texto"); // Adiciona classe CSS

        Label labelCodigo = new Label("Código:");
        labelCodigo.getStyleClass().add("label"); // Adiciona classe CSS

        TextField campoCodigo = new TextField();
        campoCodigo.setPromptText("Código");
        campoCodigo.getStyleClass().add("campo-texto"); // Adiciona classe CSS

        Label labelQuantidade = new Label("Quantidade:");
        labelQuantidade.getStyleClass().add("label"); // Adiciona classe CSS

        TextField campoQuantidade = new TextField();
        campoQuantidade.setPromptText("Quantidade");
        campoQuantidade.getStyleClass().add("campo-texto"); // Adiciona classe CSS

        Label labelPreco = new Label("Preço:");
        labelPreco.getStyleClass().add("label"); // Adiciona classe CSS

        TextField campoPreco = new TextField();
        campoPreco.setPromptText("Preço");
        campoPreco.getStyleClass().add("campo-texto"); // Adiciona classe CSS

        Label labelCategoria = new Label("Categoria:");
        labelCategoria.getStyleClass().add("label"); // Adiciona classe CSS

        ComboBox<String> comboBoxCategoria = new ComboBox<>(); // ComboBox para categorias
        comboBoxCategoria.setPromptText("Selecione uma categoria");
        comboBoxCategoria.getStyleClass().add("combo-box"); // Adiciona classe CSS

        // Carrega as categorias do banco de dados no ComboBox
        carregarCategoriasNoComboBox(comboBoxCategoria);

        // Botão para confirmar
        Button botaoConfirmar = new Button("Adicionar Produto");
        botaoConfirmar.getStyleClass().add("botao"); // Adiciona classe CSS
        botaoConfirmar.setOnAction(e -> {
            try {
                // Validação dos campos
                if (campoNome.getText().isEmpty() || campoCodigo.getText().isEmpty() || campoQuantidade.getText().isEmpty() ||
                    campoPreco.getText().isEmpty() || comboBoxCategoria.getValue() == null) {
                    mostrarAlerta("Erro", "Todos os campos devem ser preenchidos.");
                    return;
                }

                String nome = campoNome.getText();
                String codigo = campoCodigo.getText();
                int quantidade = Integer.parseInt(campoQuantidade.getText());
                double preco = Double.parseDouble(campoPreco.getText());
                String categoria = comboBoxCategoria.getValue(); // Obtém a categoria selecionada

                // Validação adicional para quantidade e preço
                if (quantidade <= 0 || preco <= 0) {
                    mostrarAlerta("Erro", "Quantidade e Preço devem ser números positivos.");
                    return;
                }

                // Adiciona o produto ao banco de dados
                produtoDAO.adicionarProduto(nome, codigo, quantidade, preco, categoria);

                // Atualiza a tabela de produtos
                ObservableList<Produto> produtos = FXCollections.observableArrayList(produtoDAO.listarProdutos());
                tabelaProdutos.setItems(produtos);

                // Exibe mensagem de sucesso
                mostrarAlerta("Sucesso", "Produto adicionado com sucesso!");

                // Fecha a janela
                primaryStage.close();
            } catch (NumberFormatException ex) {
                mostrarAlerta("Erro", "Quantidade e Preço devem ser números válidos.");
            } catch (SQLException ex) {
                mostrarAlerta("Erro", "Ocorreu um erro ao adicionar o produto: " + ex.getMessage());
            } catch (Exception ex) {
                mostrarAlerta("Erro", "Ocorreu um erro inesperado: " + ex.getMessage());
            }
        });

        // Layout
        VBox vbox = new VBox(10, labelNome, campoNome, labelCodigo, campoCodigo, labelQuantidade, campoQuantidade, labelPreco, campoPreco, labelCategoria, comboBoxCategoria, botaoConfirmar);
        vbox.setPadding(new Insets(20));
        vbox.getStyleClass().add("vbox"); // Adiciona classe CSS

        // Cena
        Scene scene = new Scene(vbox, 300, 450); // Ajustei a altura para 450px
        scene.getStylesheets().add(getClass().getResource("stylesAddProd.css").toExternalForm()); // Adiciona o arquivo CSS
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Método para carregar as categorias no ComboBox
    private void carregarCategoriasNoComboBox(ComboBox<String> comboBoxCategoria) {
        try {
            // Busca a lista de categorias do banco de dados
            List<Categoria> categorias = categoriaDAO.listarCategorias();
            
            // Cria uma ObservableList para armazenar os nomes das categorias
            ObservableList<String> nomesCategorias = FXCollections.observableArrayList();
            
            // Adiciona os nomes das categorias à ObservableList
            for (Categoria categoria : categorias) {
                nomesCategorias.add(categoria.getNome());
            }
            
            // Define os itens do ComboBox como a lista de nomes de categorias
            comboBoxCategoria.setItems(nomesCategorias);
        } catch (SQLException e) {
            // Exibe uma mensagem de erro caso ocorra uma exceção
            mostrarAlerta("Erro", "Erro ao carregar categorias: " + e.getMessage());
        }
    }

    // Método para exibir alertas
    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}