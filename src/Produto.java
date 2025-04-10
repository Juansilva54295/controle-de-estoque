import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Produto {
    private int id;
    private String nome;
    private String codigo;
    private int quantidade;
    private double preco;
    private String statusEstoque;
    private String categoria; // Novo campo
    private BooleanProperty selecionado; // Propriedade para o CheckBox

    // Construtor
    public Produto(int id, String nome, String codigo, int quantidade, double preco, String statusEstoque, String categoria) {
        this.id = id;
        this.nome = nome;
        this.codigo = codigo;
        this.quantidade = quantidade;
        this.preco = preco;
        this.statusEstoque = statusEstoque;
        this.categoria = categoria;
        this.selecionado = new SimpleBooleanProperty(false); // Inicializa como não selecionado
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getPreco() {
        return preco;
    }

    public String getStatusEstoque() {
        return statusEstoque;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public void setStatusEstoque(String statusEstoque) {
        this.statusEstoque = statusEstoque;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
    
    public boolean isSelecionado() {
        return selecionado.get();
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado.set(selecionado);
    }

    // Propriedade para o CheckBox
    public BooleanProperty selecionadoProperty() {
        return selecionado;
    }

    public String calcularStatusEstoque() {
        if (quantidade > 50) {
            return "Alto";
        } else if (quantidade >=20 && quantidade <=50) {
            return "Medio";
        } else {
            return "Baixo";
        }
    }
    @Override
    public String toString() {
        return "Produto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", codigo='" + codigo + '\'' +
                ", quantidade=" + quantidade +
                ", preco=" + preco +
                ", statusEstoque='" + statusEstoque + '\'' +
                ", categoria='" + categoria + '\'' +
                ", selecionado=" + selecionado.get() +
                '}';
    }
}