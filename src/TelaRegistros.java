import java.time.LocalDate;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelaRegistros extends Application {

    private TableView<Venda> tabelaVendasRegistradas;
    private TableView<Venda> tabelaVendasExcluidas;
    private ObservableList<Venda> vendasRegistradas;
    private ObservableList<Venda> vendasExcluidas;

    public TelaRegistros(ObservableList<Venda> vendasRegistradas, ObservableList<Venda> vendasExcluidas) {
        this.vendasRegistradas = vendasRegistradas;
        this.vendasExcluidas = vendasExcluidas;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Registros de Vendas");

        // Tabela de vendas registradas
        tabelaVendasRegistradas = new TableView<>();
        configurarTabela(tabelaVendasRegistradas);
        tabelaVendasRegistradas.setItems(vendasRegistradas);

        // Tabela de vendas excluídas
        tabelaVendasExcluidas = new TableView<>();
        configurarTabela(tabelaVendasExcluidas);
        tabelaVendasExcluidas.setItems(vendasExcluidas);

        // Layout
        VBox vboxRegistradas = new VBox(10, new Label("Vendas Registradas"), tabelaVendasRegistradas);
        VBox vboxExcluidas = new VBox(10, new Label("Vendas Excluídas"), tabelaVendasExcluidas);
        VBox vboxPrincipal = new VBox(20, vboxRegistradas, vboxExcluidas);
        vboxPrincipal.setPadding(new Insets(10));

        // Cena
        Scene scene = new Scene(vboxPrincipal, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void configurarTabela(TableView<Venda> tabela) {
        TableColumn<Venda, Integer> colunaId = new TableColumn<>("ID");
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Venda, String> colunaCodigoProduto = new TableColumn<>("Código do Produto");
        colunaCodigoProduto.setCellValueFactory(new PropertyValueFactory<>("codigoProduto"));

        TableColumn<Venda, String> colunaNomeProduto = new TableColumn<>("Nome do Produto");
        colunaNomeProduto.setCellValueFactory(new PropertyValueFactory<>("nomeProduto"));

        TableColumn<Venda, Integer> colunaQuantidade = new TableColumn<>("Quantidade");
        colunaQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));

        TableColumn<Venda, Double> colunaValorTotal = new TableColumn<>("Valor Total");
        colunaValorTotal.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));

        TableColumn<Venda, String> colunaCategoria = new TableColumn<>("Categoria");
        colunaCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        TableColumn<Venda, LocalDate> colunaDataVenda = new TableColumn<>("Data da Venda");
        colunaDataVenda.setCellValueFactory(new PropertyValueFactory<>("dataVenda"));

        tabela.getColumns().addAll(colunaId, colunaCodigoProduto, colunaNomeProduto, colunaQuantidade, colunaValorTotal, colunaCategoria, colunaDataVenda);
    }

    public static void main(String[] args) {
        launch(args);
    }
}