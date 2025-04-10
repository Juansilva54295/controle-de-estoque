import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import java.time.LocalDate;

public class TelaRegistrarVenda {

    public TelaRegistrarVenda(Stage primaryStage, TableView<Produto> tabelaProdutos, ProdutoDAO produtoDAO, VendaDAO vendaDAO) {
        primaryStage.setTitle("Registrar Venda");

        // Campo para buscar produto por código
        TextField campoBuscaCodigo = new TextField();
        campoBuscaCodigo.setPromptText("Digite o código do produto");

        // Botão para buscar produto por código
        Button botaoBuscarPorCodigo = new Button("Buscar por Código");
        botaoBuscarPorCodigo.setOnAction(e -> buscarProdutoPorCodigo(campoBuscaCodigo.getText(), tabelaProdutos));

        // Campo para quantidade
        TextField campoQuantidade = new TextField();
        campoQuantidade.setPromptText("Quantidade");

        // Campo para valor da venda
        TextField campoValorVenda = new TextField();
        campoValorVenda.setPromptText("Valor da Venda");

        // Campo para selecionar a data da venda
        DatePicker campoDataVenda = new DatePicker();
        campoDataVenda.setPromptText("Data da Venda");
        campoDataVenda.setValue(LocalDate.now()); // Define a data atual como padrão

        // Botão para confirmar
        Button botaoConfirmar = new Button("Registrar Venda");
        botaoConfirmar.setOnAction(e -> {
            try {
                Produto produtoSelecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
                if (produtoSelecionado == null) {
                    mostrarAlerta("Aviso", "Nenhum produto selecionado!");
                    return;
                }

                // Validação dos campos
                if (campoQuantidade.getText().isEmpty() || campoValorVenda.getText().isEmpty() || campoDataVenda.getValue() == null) {
                    mostrarAlerta("Erro", "Todos os campos devem ser preenchidos.");
                    return;
                }

                int quantidade = Integer.parseInt(campoQuantidade.getText());
                double valorVenda = Double.parseDouble(campoValorVenda.getText());
                LocalDate dataVenda = campoDataVenda.getValue(); // Obtém a data selecionada

                if (quantidade <= 0 || valorVenda <= 0) {
                    mostrarAlerta("Erro", "Quantidade e Valor da Venda devem ser maiores que zero.");
                    return;
                }

                if (quantidade > produtoSelecionado.getQuantidade()) {
                    mostrarAlerta("Erro", "Quantidade insuficiente em estoque!");
                    return;
                }

                // Registra a venda no banco de dados
                vendaDAO.registrarVenda(
                    produtoSelecionado.getCodigo(),
                    produtoSelecionado.getNome(),
                    quantidade,
                    valorVenda,
                    produtoSelecionado.getCategoria(),
                    dataVenda // Novo campo
                );

                // Atualiza a quantidade do produto no estoque
                int novaQuantidade = produtoSelecionado.getQuantidade() - quantidade;
                produtoDAO.atualizarProduto(
                    produtoSelecionado.getId(),
                    produtoSelecionado.getNome(),
                    produtoSelecionado.getCodigo(),
                    novaQuantidade,
                    produtoSelecionado.getPreco(),
                    produtoSelecionado.getCategoria()
                );

                // Atualiza o status do estoque
                produtoSelecionado.setQuantidade(novaQuantidade);
                produtoSelecionado.setStatusEstoque(produtoDAO.calcularStatusEstoque(novaQuantidade)); // Atualiza o status do estoque
                tabelaProdutos.refresh();

                // Fecha a janela
                primaryStage.close();
            } catch (NumberFormatException ex) {
                mostrarAlerta("Erro", "Quantidade e Valor da Venda devem ser números válidos.");
            } catch (Exception ex) {
                mostrarAlerta("Erro", "Ocorreu um erro ao registrar a venda: " + ex.getMessage());
            }
        });

        // Layout
        VBox vbox = new VBox(10, campoBuscaCodigo, botaoBuscarPorCodigo, campoQuantidade, campoValorVenda, campoDataVenda, botaoConfirmar);
        vbox.setPadding(new Insets(10));

        // Cena
        Scene scene = new Scene(vbox, 300, 300); // Aumentei a altura para acomodar o novo campo
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Método para buscar produto por código
    private void buscarProdutoPorCodigo(String codigo, TableView<Produto> tabelaProdutos) {
        ObservableList<Produto> produtos = tabelaProdutos.getItems();
        for (Produto produto : produtos) {
            if (produto.getCodigo().equals(codigo)) {
                tabelaProdutos.getSelectionModel().select(produto);
                return;
            }
        }
        mostrarAlerta("Aviso", "Produto com código " + codigo + " não encontrado.");
    }

    // Método para exibir alertas
    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}