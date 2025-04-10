import java.time.LocalDate;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;


public class Venda {
    private int id;
    private String codigoProduto;
    private String nomeProduto;
    private int quantidade;
    private double valorTotal;
    private String categoria;
    private LocalDate dataVenda; // Novo campo

    // Construtor atualizado
    public Venda(int id, String codigoProduto, String nomeProduto, int quantidade, double valorTotal, String categoria, LocalDate dataVenda) {
        this.id = id;
        this.codigoProduto = codigoProduto;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.valorTotal = valorTotal;
        this.categoria = categoria;
        this.dataVenda = dataVenda; // Inicializa o novo campo
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(String codigoProduto) {
        this.codigoProduto = codigoProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    // Getter e Setter para a data da venda
    public LocalDate getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDate dataVenda) {
        this.dataVenda = dataVenda;
    }
    private BooleanProperty selecionado = new SimpleBooleanProperty(false);

    public BooleanProperty selecionadoProperty() {
        return selecionado;
    }

    public boolean isSelecionado() {
        return selecionado.get();
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado.set(selecionado);
    }
    
}