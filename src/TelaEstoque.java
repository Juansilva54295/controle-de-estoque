import java.sql.SQLException;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TelaEstoque extends Application {

    private TableView<Produto> tabelaProdutos;
    private ProdutoDAO produtoDAO = new ProdutoDAO();
    private VendaDAO vendaDAO = new VendaDAO();
    private Label labelValorTotalEstoque;
    private String nivelAcessoUsuario; // Nível de acesso do usuário logado
    private ComboBox<String> comboFiltroCategorias; // ComboBox para filtro de categorias
    private TextField campoPesquisa; // Campo de pesquisa
    private ComboBox<String> comboTipoPesquisa; // ComboBox para selecionar o tipo de pesquisa
    private FilteredList<Produto> produtosFiltrados; // Lista filtrada de produtos
    private CategoriaDAO categoriaDAO = new CategoriaDAO(); // DAO para categorias
    private CheckBox checkBoxSelecionarTodos; // CheckBox para selecionar todos os produtos
    private Button botaoConfirmarSelecao; // Botão para confirmar a seleção
    private Button botaoCancelarRemocao; // Botão para cancelar a remoção

    public TelaEstoque(String nivelAcessoUsuario) {
        this.nivelAcessoUsuario = nivelAcessoUsuario;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Controle de Estoque");

        labelValorTotalEstoque = new Label("Valor Total do Estoque: R$ 0.00");

        // Colunas da tabela
        TableColumn<Produto, Integer> colunaId = new TableColumn<>("ID");
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Produto, String> colunaCodigo = new TableColumn<>("Código");
        colunaCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));

        TableColumn<Produto, String> colunaNome = new TableColumn<>("Nome");
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Produto, Integer> colunaQuantidade = new TableColumn<>("Quantidade");
        colunaQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));

        TableColumn<Produto, Double> colunaPreco = new TableColumn<>("Preço");
        colunaPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));

        TableColumn<Produto, Double> colunaValorTotal = new TableColumn<>("Valor Total");
        colunaValorTotal.setCellValueFactory(cellData -> {
            Produto produto = cellData.getValue();
            double valorTotal = produto.getQuantidade() * produto.getPreco();
            return javafx.beans.binding.Bindings.createObjectBinding(() -> valorTotal);
        });

        // Coluna de status do estoque
        TableColumn<Produto, String> colunaStatusEstoque = new TableColumn<>("Status Estoque");
        colunaStatusEstoque.setCellValueFactory(cellData -> {
            Produto produto = cellData.getValue();
            return new SimpleStringProperty(produto.calcularStatusEstoque()); // Calcula o status dinamicamente
        });

        colunaStatusEstoque.setCellFactory(column -> new TableCell<Produto, String>() {
            @Override
            protected void updateItem(String statusEstoque, boolean empty) {
                super.updateItem(statusEstoque, empty);
                if (empty || statusEstoque == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(statusEstoque);
                    switch (statusEstoque) {
                        case "Alto":
                            setStyle("-fx-background-color: lightgreen; -fx-border-color: #000000;");
                            break;
                        case "Medio":
                            setStyle("-fx-background-color: lightyellow; -fx-border-color: #000000;");
                            break;
                        case "Baixo":
                            setStyle("-fx-background-color: lightcoral; -fx-border-color: #000000;");
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                }
            }
        });

        // Coluna de categoria
        TableColumn<Produto, String> colunaCategoria = new TableColumn<>("Categoria");
        colunaCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        // Coluna de seleção (CheckBox)
        TableColumn<Produto, Boolean> colunaSelecao = new TableColumn<>("Selecionar");
        colunaSelecao.setCellValueFactory(cellData -> cellData.getValue().selecionadoProperty());
        colunaSelecao.setCellFactory(CheckBoxTableCell.forTableColumn(colunaSelecao)); // Permite edição
        colunaSelecao.setEditable(true); // Torna a coluna editável
        colunaSelecao.setVisible(false); // Inicialmente invisível

        // Tabela de produtos
        tabelaProdutos = new TableView<>();
        tabelaProdutos.setEditable(true); // Habilita a edição da tabela
        tabelaProdutos.getColumns().addAll(colunaSelecao, colunaId, colunaCodigo, colunaNome, colunaQuantidade, colunaPreco, colunaValorTotal, colunaStatusEstoque, colunaCategoria);

        // Efeito de sombra na tabela
        DropShadow shadow = new DropShadow();
        shadow.setRadius(5);
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);
        shadow.setColor(Color.color(0, 0, 0, 0.2));
        tabelaProdutos.setEffect(shadow);

        carregarProdutos();

        campoPesquisa = new TextField();
        campoPesquisa.setPromptText("Pesquisar...");

        comboTipoPesquisa = new ComboBox<>();
        comboTipoPesquisa.getItems().addAll("Nome", "ID", "Código", "Categoria");
        comboTipoPesquisa.setValue("Nome");

        Button botaoPesquisar = new Button("Pesquisar");
        botaoPesquisar.setOnAction(e -> pesquisarProdutos());

        comboFiltroCategorias = new ComboBox<>();
        comboFiltroCategorias.setPromptText("Filtrar por Categoria");
        carregarCategoriasNoComboBox(); // Carrega as categorias do banco de dados

        // Adiciona um listener para filtrar os produtos quando a categoria selecionada mudar
        comboFiltroCategorias.valueProperty().addListener((obs, oldVal, newVal) -> {
            filtrarPorCategoria();
        });

        Button botaoAdicionarCategoria = new Button("Adicionar Categoria");
        botaoAdicionarCategoria.setOnAction(e -> adicionarNovaCategoria());

        // Botões fixos na lateral
        VBox botoesLaterais = new VBox(10);
        botoesLaterais.setPadding(new Insets(20));
        botoesLaterais.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: none; -fx-border-width: 0;");

        Button botaoAdicionarProduto = new Button("Adicionar Produto");
        Button botaoAtualizarProduto = new Button("Atualizar Produto");
        Button botaoRemoverProduto = new Button("Remover Produtos");
        Button botaoRegistrarVenda = new Button("Registrar Venda");
        Button botaoListaVendas = new Button("Lista de Vendas");

        botoesLaterais.getChildren().addAll(botaoAdicionarProduto, botaoAtualizarProduto, botaoRemoverProduto, botaoRegistrarVenda, botaoListaVendas);

        botaoAdicionarProduto.setOnAction(e -> adicionarProduto());
        botaoAtualizarProduto.setOnAction(e -> atualizarProduto());
        botaoRemoverProduto.setOnAction(e -> {
            colunaSelecao.setVisible(true); // Mostra a coluna de seleção
            checkBoxSelecionarTodos.setVisible(true); // Mostra a CheckBox de selecionar todos
            botaoConfirmarSelecao.setVisible(true); // Mostra o botão de confirmar seleção
            botaoCancelarRemocao.setVisible(true); // Mostra o botão de cancelar remoção
        });
        botaoRegistrarVenda.setOnAction(e -> registrarVenda());
        botaoListaVendas.setOnAction(e -> abrirListaVendas());

        HBox hboxBotoes = new HBox(10, comboFiltroCategorias, botaoAdicionarCategoria);
        hboxBotoes.setPadding(new Insets(10));

        HBox hboxPesquisa = new HBox(10, campoPesquisa, comboTipoPesquisa, botaoPesquisar);
        hboxPesquisa.setPadding(new Insets(10));

        HBox hboxTopo = new HBox(10, labelValorTotalEstoque, hboxPesquisa);
        hboxTopo.setPadding(new Insets(10));

        checkBoxSelecionarTodos = new CheckBox("Selecionar Todos");
        checkBoxSelecionarTodos.setVisible(false); // Inicialmente invisível
        checkBoxSelecionarTodos.setOnAction(e -> {
            boolean selecionar = checkBoxSelecionarTodos.isSelected();
            tabelaProdutos.getItems().forEach(produto -> produto.setSelecionado(selecionar));
        });

        botaoConfirmarSelecao = new Button("Confirmar Seleção");
        botaoConfirmarSelecao.setVisible(false); // Inicialmente invisível
        botaoConfirmarSelecao.setOnAction(e -> confirmarSelecao());

        botaoCancelarRemocao = new Button("Cancelar");
        botaoCancelarRemocao.setVisible(false); // Inicialmente invisível
        botaoCancelarRemocao.setOnAction(e -> cancelarRemocao());

        // HBox para os botões de confirmar e cancelar
        HBox hboxBotoesRemocao = new HBox(10, botaoConfirmarSelecao, botaoCancelarRemocao);
        hboxBotoesRemocao.setPadding(new Insets(10));

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));

        borderPane.setTop(hboxTopo);

        VBox vboxTabela = new VBox(10, checkBoxSelecionarTodos, tabelaProdutos, hboxBotoesRemocao);
        borderPane.setCenter(vboxTabela);

        borderPane.setBottom(hboxBotoes);

        borderPane.setRight(botoesLaterais);

        Scene scene = new Scene(borderPane, 1100, 400);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Método para confirmar a seleção e autenticar o administrador
    private void confirmarSelecao() {
        ObservableList<Produto> produtosSelecionados = tabelaProdutos.getItems().filtered(Produto::isSelecionado);

        if (!produtosSelecionados.isEmpty()) {
            // Verifica a autenticação do administrador
            Stage stageAutenticacao = new Stage();
            TelaAutenticacaoAdmin telaAutenticacao = new TelaAutenticacaoAdmin();
            boolean autenticado = telaAutenticacao.mostrar(stageAutenticacao);

            if (autenticado) {
                try {
                    // Remove cada produto selecionado
                    for (Produto produto : produtosSelecionados) {
                        produtoDAO.removerProduto(produto.getId());
                    }
                    // Recarrega os produtos após a exclusão
                    carregarProdutos();
                    // Oculta a coluna de seleção e os botões de confirmar/cancelar
                    colunaSelecao.setVisible(false);
                    checkBoxSelecionarTodos.setVisible(false);
                    botaoConfirmarSelecao.setVisible(false);
                    botaoCancelarRemocao.setVisible(false);
                } catch (SQLException e) {
                    Alert alertErro = new Alert(Alert.AlertType.ERROR, "Erro ao remover produtos: " + e.getMessage());
                    alertErro.show();
                    e.printStackTrace();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Nenhum produto selecionado!");
            alert.show();
        }
    }

    // Método para cancelar a remoção
    private void cancelarRemocao() {
        // Desmarca todos os produtos selecionados
        tabelaProdutos.getItems().forEach(produto -> produto.setSelecionado(false));
        
        // Oculta a coluna de seleção e os botões de confirmar/cancelar
        colunaSelecao.setVisible(false);
        checkBoxSelecionarTodos.setVisible(false);
        botaoConfirmarSelecao.setVisible(false);
        botaoCancelarRemocao.setVisible(false);
    }

    // Método para carregar as categorias no ComboBox
    private void carregarCategoriasNoComboBox() {
        try {
            // Busca a lista de categorias do banco de dados
            List<Categoria> categorias = categoriaDAO.listarCategorias();
            
            // Cria uma ObservableList para armazenar os nomes das categorias
            ObservableList<String> nomesCategorias = FXCollections.observableArrayList();
            
            // Adiciona a opção "Todas as Categorias" como primeiro item
            nomesCategorias.add("Todas as Categorias");
            
            // Adiciona os nomes das categorias à ObservableList
            for (Categoria categoria : categorias) {
                nomesCategorias.add(categoria.getNome());
            }
            
            // Define os itens do ComboBox como a lista de nomes de categorias
            comboFiltroCategorias.setItems(nomesCategorias);
        } catch (SQLException e) {
            // Exibe uma mensagem de erro caso ocorra uma exceção
            Alert alertErro = new Alert(Alert.AlertType.ERROR, "Erro ao carregar categorias: " + e.getMessage());
            alertErro.show();
            e.printStackTrace();
        }
    }

    // Método para adicionar uma nova categoria
    private void adicionarNovaCategoria() {
        // Abre a tela para adicionar uma nova categoria
        Stage stageAdicionarCategoria = new Stage();
        TelaNovaCategoria telaNovaCategoria = new TelaNovaCategoria(categoriaDAO);
        telaNovaCategoria.start(stageAdicionarCategoria);

        // Atualiza o ComboBox após adicionar uma nova categoria
        stageAdicionarCategoria.setOnHidden(e -> carregarCategoriasNoComboBox());
    }

    // Método para carregar os produtos do banco de dados
    private void carregarProdutos() {
        try {
            List<Produto> produtos = produtoDAO.listarProdutos();
            ObservableList<Produto> listaProdutos = FXCollections.observableArrayList(produtos);
            produtosFiltrados = new FilteredList<>(listaProdutos, p -> true);
            tabelaProdutos.setItems(produtosFiltrados);
            atualizarValorTotalEstoque();
        } catch (SQLException e) {
            Alert alertErro = new Alert(Alert.AlertType.ERROR, "Erro ao carregar produtos: " + e.getMessage());
            alertErro.show();
            e.printStackTrace();
        }
    }

    // Método para pesquisar produtos
    private void pesquisarProdutos() {
        String termoPesquisa = campoPesquisa.getText().toLowerCase();
        String tipoPesquisa = comboTipoPesquisa.getValue();

        produtosFiltrados.setPredicate(produto -> {
            if (termoPesquisa == null || termoPesquisa.isEmpty()) {
                return true; // Mostra todos os produtos se o campo de pesquisa estiver vazio
            }

            switch (tipoPesquisa) {
                case "Nome":
                    return produto.getNome().toLowerCase().contains(termoPesquisa);
                case "ID":
                    return String.valueOf(produto.getId()).contains(termoPesquisa);
                case "Código":
                    return produto.getCodigo().toLowerCase().contains(termoPesquisa);
                case "Categoria":
                    return produto.getCategoria().toLowerCase().contains(termoPesquisa);
                default:
                    return true; // Mostra todos os produtos se o tipo de pesquisa for desconhecido
            }
        });
    }

    // Método para filtrar os produtos por categoria
    private void filtrarPorCategoria() {
        String categoriaSelecionada = comboFiltroCategorias.getValue();
        if (categoriaSelecionada == null || categoriaSelecionada.equals("Todas as Categorias")) {
            produtosFiltrados.setPredicate(produto -> true); // Mostra todos os produtos
        } else {
            produtosFiltrados.setPredicate(produto -> produto.getCategoria().equals(categoriaSelecionada));
        }
    }

    // Método para calcular e atualizar o valor total do estoque
    private void atualizarValorTotalEstoque() {
        double valorTotal = tabelaProdutos.getItems().stream()
            .mapToDouble(p -> p.getQuantidade() * p.getPreco())
            .sum();
        labelValorTotalEstoque.setText(String.format("Valor Total do Estoque: R$ %.2f", valorTotal));
    }

    // Método para adicionar um produto
    private void adicionarProduto() {
        Stage stageAdicionar = new Stage();
        TelaNovoRegistro telaNovoRegistro = new TelaNovoRegistro(stageAdicionar, tabelaProdutos, produtoDAO);
        stageAdicionar.setOnHidden(e -> carregarProdutos()); // Atualiza a tabela após fechar a tela
    }

    // Método para atualizar um produto
    private void atualizarProduto() {
        Produto produtoSelecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (produtoSelecionado != null) {
            Stage stageAtualizar = new Stage();
            TelaAtualizarProduto telaAtualizarProduto = new TelaAtualizarProduto(stageAtualizar, produtoSelecionado, tabelaProdutos, produtoDAO);
            stageAtualizar.setOnHidden(e -> carregarProdutos()); // Atualiza a tabela após fechar a tela
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Nenhum produto selecionado!");
            alert.show();
        }
    }

    // Método para registrar uma venda
    private void registrarVenda() {
        Stage stageRegistrarVenda = new Stage();
        TelaRegistrarVenda telaRegistrarVenda = new TelaRegistrarVenda(stageRegistrarVenda, tabelaProdutos, produtoDAO, vendaDAO);
        stageRegistrarVenda.setOnHidden(e -> carregarProdutos()); // Atualiza a tabela após fechar a tela
    }

    // Método para abrir a lista de vendas
    private void abrirListaVendas() {
        Stage stageListaVendas = new Stage();
        TelaListaVendas telaListaVendas = new TelaListaVendas();
        telaListaVendas.start(stageListaVendas);
    }

    public static void main(String[] args) {
        launch(args);
    }
}