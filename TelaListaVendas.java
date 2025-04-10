import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TelaListaVendas {


    private TableView<Venda> tabelaVendas;
    private VendaDAO vendaDAO = new VendaDAO();
    private Label labelValorTotalVendas;
    private Label labelProdutoMaisVendido;
    private ComboBox<String> comboFiltros;
    private LocalDate dataInicioFiltro; // Armazena a data inicial do filtro
    private LocalDate dataFimFiltro; // Armazena a data final do filtro
    private Button botaoConfirmar;

    // Coluna de seleção (CheckBox)
    private TableColumn<Venda, Boolean> colunaSelecao;

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Lista de Vendas");

        // Inicializa os componentes
        labelValorTotalVendas = new Label();
        labelProdutoMaisVendido = new Label();
        comboFiltros = new ComboBox<>();
        comboFiltros.getItems().addAll(
            "Nome (A-Z)", "Mais Vendido", "Menor Preço", "Maior Preço",
            "Neste Dia", "Última Semana", "Último Mês", "Últimos 3 Meses", "Últimos 6 Meses", "Último Ano", "Período Específico"
        );
        comboFiltros.setValue("Nome (A-Z)"); // Valor padrão

        // Barra de pesquisa
        TextField campoPesquisa = new TextField();
        campoPesquisa.setPromptText("Pesquisar...");

        ComboBox<String> comboTipoPesquisa = new ComboBox<>();
        comboTipoPesquisa.getItems().addAll("Nome", "ID", "Código", "Categoria");
        comboTipoPesquisa.setValue("Nome"); // Valor padrão

        Button botaoPesquisar = new Button("Pesquisar");
        botaoPesquisar.setOnAction(e -> pesquisarVendas(campoPesquisa.getText(), comboTipoPesquisa.getValue()));

        Button botaoExcluirSelecionada = new Button("Excluir Venda Selecionada");
        botaoExcluirSelecionada.setOnAction(e -> excluirVendaSelecionada());

        Button botaoExcluirSelecionadas = new Button("Excluir Vendas Selecionadas");
        Button botaoConfirmar = new Button("Confirmar");
        botaoConfirmar.setVisible(false); // Inicialmente oculto
        botaoConfirmar.setOnAction(e -> confirmarExclusao());

        botaoExcluirSelecionadas.setOnAction(e -> {
            // Mostra as caixinhas de seleção
            colunaSelecao.setVisible(true);
            // Mostra o botão "Confirmar"
            botaoConfirmar.setVisible(true);
        });

        Button botaoExcluirTodas = new Button("Excluir Todas as Vendas");
        botaoExcluirTodas.setOnAction(e -> excluirTodasVendas());

        // Botão para aplicar o filtro
        Button botaoAplicarFiltro = new Button("Aplicar Filtro");
        botaoAplicarFiltro.setOnAction(e -> aplicarFiltro());

        // Botão para gerar PDF
        Button botaoGerarPDF = new Button("Gerar PDF");
        botaoGerarPDF.setOnAction(e -> gerarPDF());

        // Colunas da tabela
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

        // Nova coluna para a data da venda
        TableColumn<Venda, LocalDate> colunaDataVenda = new TableColumn<>("Data da Venda");
        colunaDataVenda.setCellValueFactory(new PropertyValueFactory<>("dataVenda"));

        // Coluna de seleção (CheckBox)
        colunaSelecao = new TableColumn<>("Selecionar");
        colunaSelecao.setCellValueFactory(cellData -> cellData.getValue().selecionadoProperty());
        colunaSelecao.setCellFactory(CheckBoxTableCell.forTableColumn(colunaSelecao));
        colunaSelecao.setEditable(true); // Permite editar a coluna (marcar/desmarcar)
        colunaSelecao.setVisible(false); // Inicialmente oculta

        // Tabela de vendas
        tabelaVendas = new TableView<>();
        tabelaVendas.setEditable(true); // Habilita a edição da tabela
        tabelaVendas.getColumns().addAll(colunaSelecao, colunaId, colunaCodigoProduto, colunaNomeProduto, colunaQuantidade, colunaValorTotal, colunaCategoria, colunaDataVenda);

        // Carrega as vendas do banco de dados ao iniciar
        carregarVendas();

        // Adicionar o botão ao layout
        HBox hboxBotoes = new HBox(10, botaoExcluirSelecionada, botaoExcluirSelecionadas, botaoExcluirTodas, botaoConfirmar);
        hboxBotoes.setPadding(new Insets(10));
        // Layout dos filtros, pesquisa e botão de PDF
        HBox hboxPesquisa = new HBox(10, campoPesquisa, comboTipoPesquisa, botaoPesquisar);
        hboxPesquisa.setPadding(new Insets(10));

        HBox hboxFiltros = new HBox(10, comboFiltros, botaoAplicarFiltro, botaoGerarPDF);
        hboxFiltros.setPadding(new Insets(10));

        // Layout dos botões de exclusão e confirmação
        HBox hboxExclusao = new HBox(10, botaoExcluirSelecionada, botaoExcluirSelecionadas, botaoExcluirTodas, botaoConfirmar);
        hboxExclusao.setPadding(new Insets(10));

        // Layout principal
        VBox vbox = new VBox(10, hboxPesquisa, hboxFiltros, tabelaVendas, labelValorTotalVendas, labelProdutoMaisVendido, hboxExclusao);
        vbox.setPadding(new Insets(10));

        // Cena
        Scene scene = new Scene(vbox, 900, 500); // Aumentei a largura para acomodar a nova coluna

        // Aplicar o arquivo CSS à cena
        scene.getStylesheets().add(getClass().getResource("stylesListavenda.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void confirmarExclusao() {
        // Recupera as vendas selecionadas
        ObservableList<Venda> vendasSelecionadas = tabelaVendas.getItems().filtered(Venda::isSelecionado);

        if (!vendasSelecionadas.isEmpty()) {
            // Solicita autenticação do administrador
            if (autenticarAdmin()) {
                // Exclui as vendas selecionadas
                for (Venda venda : vendasSelecionadas) {
                    vendaDAO.excluirVenda(venda.getId());
                }
                // Recarrega as vendas após a exclusão
                carregarVendas();
                // Oculta as caixinhas e o botão "Confirmar"
                colunaSelecao.setVisible(false);
                botaoConfirmar.setVisible(false);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Nenhuma venda selecionada!");
            alert.show();
        }
    }

    
    // Método para carregar as vendas do banco de dados
    private void carregarVendas() {
        List<Venda> vendas = vendaDAO.listarVendas();
        tabelaVendas.getItems().clear(); // Limpa a tabela antes de carregar os dados
        tabelaVendas.getItems().addAll(vendas);
        atualizarValorTotalVendas();
        mostrarProdutoMaisVendido();
    }

    // Método para aplicar o filtro selecionado
    private void aplicarFiltro() {
        String filtroSelecionado = comboFiltros.getValue();
        ObservableList<Venda> vendas = FXCollections.observableArrayList(vendaDAO.listarVendas()); // Recarrega os dados do banco

        switch (filtroSelecionado) {
            case "Nome (A-Z)":
                vendas.sort(Comparator.comparing(Venda::getNomeProduto));
                break;
            case "Mais Vendido":
                vendas.sort((v1, v2) -> Integer.compare(v2.getQuantidade(), v1.getQuantidade()));
                break;
            case "Menor Preço":
                vendas.sort(Comparator.comparing(Venda::getValorTotal));
                break;
            case "Maior Preço":
                vendas.sort((v1, v2) -> Double.compare(v2.getValorTotal(), v1.getValorTotal()));
                break;
            case "Neste Dia":
                filtrarPorPeriodo(vendas, 0, ChronoUnit.DAYS);
                break;
            case "Última Semana":
                filtrarPorPeriodo(vendas, 1, ChronoUnit.WEEKS);
                break;
            case "Último Mês":
                filtrarPorPeriodo(vendas, 1, ChronoUnit.MONTHS);
                break;
            case "Últimos 3 Meses":
                filtrarPorPeriodo(vendas, 3, ChronoUnit.MONTHS);
                break;
            case "Últimos 6 Meses":
                filtrarPorPeriodo(vendas, 6, ChronoUnit.MONTHS);
                break;
            case "Último Ano":
                filtrarPorPeriodo(vendas, 1, ChronoUnit.YEARS);
                break;
            case "Período Específico":
                filtrarPorPeriodoEspecifico(vendas);
                break;
        }

        tabelaVendas.setItems(vendas); // Atualiza a tabela com os dados filtrados
    }

    // Método para filtrar por período (dias, semanas, meses, anos)
    private void filtrarPorPeriodo(ObservableList<Venda> vendas, int quantidade, ChronoUnit unidade) {
        LocalDate dataAtual = LocalDate.now();
        LocalDate dataInicio = dataAtual.minus(quantidade, unidade);

        vendas.removeIf(venda -> venda.getDataVenda() == null || venda.getDataVenda().isBefore(dataInicio) || venda.getDataVenda().isAfter(dataAtual));
    }

    // Método para filtrar por período específico
    private void filtrarPorPeriodoEspecifico(ObservableList<Venda> vendas) {
        // Janela para selecionar o período
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Selecionar Período");
        dialog.setHeaderText("Escolha o período para filtrar as vendas");

        // Campos de data
        DatePicker dataInicio = new DatePicker();
        dataInicio.setPromptText("Data Inicial");
        DatePicker dataFim = new DatePicker();
        dataFim.setPromptText("Data Final");

        // Layout
        VBox vbox = new VBox(10, new Label("Data Inicial:"), dataInicio, new Label("Data Final:"), dataFim);
        vbox.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(vbox);

        // Botões
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Resultado
        dialog.showAndWait().ifPresent(resultado -> {
            if (resultado == ButtonType.OK) {
                dataInicioFiltro = dataInicio.getValue();
                dataFimFiltro = dataFim.getValue();

                if (dataInicioFiltro != null && dataFimFiltro != null) {
                    vendas.removeIf(venda -> venda.getDataVenda() == null || venda.getDataVenda().isBefore(dataInicioFiltro) || venda.getDataVenda().isAfter(dataFimFiltro));

                    // Atualiza o nome do filtro para incluir as datas
                    comboFiltros.setValue("Período Específico: " +
                        dataInicioFiltro.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " a " +
                        dataFimFiltro.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            }
        });
    }

    // Método para gerar o PDF
    private void gerarPDF() {
        List<Venda> vendas = tabelaVendas.getItems();
        String filtroSelecionado = comboFiltros.getValue();

        // Define o nome do arquivo PDF com base no filtro
        String nomeArquivo;
        if (filtroSelecionado.startsWith("Período Específico")) {
            // Se o filtro for "Período Específico", use as datas selecionadas
            nomeArquivo = String.format("relatorio_vendas_%s_a_%s.pdf",
                dataInicioFiltro.format(DateTimeFormatter.ofPattern("ddMMyyyy")),
                dataFimFiltro.format(DateTimeFormatter.ofPattern("ddMMyyyy")));
        } else {
            // Para outros filtros, use o nome do filtro
            nomeArquivo = "relatorio_vendas_" + filtroSelecionado.replace(" ", "_") + ".pdf";
        }

        String caminhoArquivo = nomeArquivo; // Caminho onde o PDF será salvo
        GeradorPDF.gerarPDF(vendas, caminhoArquivo, filtroSelecionado);

        // Exibir mensagem de sucesso
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("PDF Gerado");
        alert.setHeaderText(null);
        alert.setContentText("PDF gerado com sucesso em: " + caminhoArquivo);
        alert.showAndWait();
    }

    // Método para calcular e atualizar o valor total de todas as vendas
    private void atualizarValorTotalVendas() {
        double valorTotal = tabelaVendas.getItems().stream()
            .mapToDouble(Venda::getValorTotal)
            .sum();
        labelValorTotalVendas.setText(String.format("Valor Total de Todas as Vendas: R$ %.2f", valorTotal));
    }

    // Método para mostrar o produto mais vendido
    private void mostrarProdutoMaisVendido() {
        Map<String, Integer> quantidadePorProduto = new HashMap<>();
        Map<String, Double> valorTotalPorProduto = new HashMap<>();

        for (Venda venda : tabelaVendas.getItems()) {
            quantidadePorProduto.put(venda.getNomeProduto(),
                quantidadePorProduto.getOrDefault(venda.getNomeProduto(), 0) + venda.getQuantidade());
            valorTotalPorProduto.put(venda.getNomeProduto(),
                valorTotalPorProduto.getOrDefault(venda.getNomeProduto(), 0.0) + venda.getValorTotal());
        }

        String produtoMaisVendido = quantidadePorProduto.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("Nenhum produto vendido");

        int quantidadeMaisVendida = quantidadePorProduto.getOrDefault(produtoMaisVendido, 0);
        double valorTotalMaisVendido = valorTotalPorProduto.getOrDefault(produtoMaisVendido, 0.0);

        labelProdutoMaisVendido.setText(String.format(
            "Produto Mais Vendido: %s | Quantidade: %d | Valor Total: R$ %.2f",
            produtoMaisVendido, quantidadeMaisVendida, valorTotalMaisVendido
        ));
    }

    // Método para pesquisar vendas
    private void pesquisarVendas(String termoPesquisa, String tipoPesquisa) {
        List<Venda> vendas = vendaDAO.listarVendas();
        ObservableList<Venda> vendasFiltradas = FXCollections.observableArrayList();

        for (Venda venda : vendas) {
            switch (tipoPesquisa) {
                case "Nome":
                    if (venda.getNomeProduto().toLowerCase().contains(termoPesquisa.toLowerCase())) {
                        vendasFiltradas.add(venda);
                    }
                    break;
                case "ID":
                    if (String.valueOf(venda.getId()).contains(termoPesquisa)) {
                        vendasFiltradas.add(venda);
                    }
                    break;
                case "Código":
                    if (venda.getCodigoProduto().toLowerCase().contains(termoPesquisa.toLowerCase())) {
                        vendasFiltradas.add(venda);
                    }
                    break;
                case "Categoria":
                    if (venda.getCategoria().toLowerCase().contains(termoPesquisa.toLowerCase())) {
                        vendasFiltradas.add(venda);
                    }
                    break;
            }
        }

        tabelaVendas.setItems(vendasFiltradas);
        atualizarValorTotalVendas();
        mostrarProdutoMaisVendido();
    }

 
private void excluirVendaSelecionada() {
    Venda vendaSelecionada = tabelaVendas.getSelectionModel().getSelectedItem();
    if (vendaSelecionada != null) {
        if (autenticarAdmin()) {
            vendaDAO.excluirVenda(vendaSelecionada.getId());
            carregarVendas(); // Recarrega as vendas após a exclusão
        }
    } else {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Nenhuma venda selecionada!");
        alert.show();
    }
}

private void excluirVendasSelecionadas() {
    // Recupera as vendas selecionadas
    ObservableList<Venda> vendasSelecionadas = tabelaVendas.getSelectionModel().getSelectedItems();

    if (!vendasSelecionadas.isEmpty()) {
        // Verifica a autenticação do administrador
        if (autenticarAdmin()) {
            // Exclui cada venda selecionada
            for (Venda venda : vendasSelecionadas) {
                vendaDAO.excluirVenda(venda.getId());
            }
            // Recarrega as vendas após a exclusão
            carregarVendas();
        }
    } else {
        // Exibe um alerta se nenhuma venda estiver selecionada
        Alert alert = new Alert(Alert.AlertType.WARNING, "Nenhuma venda selecionada!");
        alert.show();
    }
}

private void excluirTodasVendas() {
    if (autenticarAdmin()) {
        vendaDAO.excluirTodasVendas();
        carregarVendas(); // Recarrega as vendas após a exclusão
    }
}

private boolean autenticarAdmin() {
    TelaAutenticacaoAdmin telaAutenticacao = new TelaAutenticacaoAdmin();
    return telaAutenticacao.mostrar(new Stage());
}

}