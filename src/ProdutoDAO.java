import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    // Constantes para status de estoque
    private static final String STATUS_ESTOQUE_BAIXO = "Baixo";
    private static final String STATUS_ESTOQUE_MEDIO = "Médio";
    private static final String STATUS_ESTOQUE_ALTO = "Alto";

    // Método para listar todos os produtos
    public List<Produto> listarProdutos() throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT id, nome, codigo, quantidade, preco, status_estoque, categoria FROM produtos";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Produto produto = new Produto(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("codigo"),
                    rs.getInt("quantidade"),
                    rs.getDouble("preco"),
                    rs.getString("status_estoque"),
                    rs.getString("categoria")
                );
                produtos.add(produto);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar produtos: " + e.getMessage());
            throw e; // Re-lança a exceção para que o chamador possa tratá-la
        }

        return produtos;
    }

    // Método para adicionar um novo produto
    public void adicionarProduto(String nome, String codigo, int quantidade, double preco, String categoria) throws SQLException {
        if (nome == null || nome.trim().isEmpty() || codigo == null || codigo.trim().isEmpty() || categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome, código e categoria não podem ser nulos ou vazios.");
        }

        String sql = "INSERT INTO produtos (nome, codigo, quantidade, preco, status_estoque, categoria) VALUES (?, ?, ?, ?, ?, ?)";
        String statusEstoque = calcularStatusEstoque(quantidade);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, codigo);
            stmt.setInt(3, quantidade);
            stmt.setDouble(4, preco);
            stmt.setString(5, statusEstoque);
            stmt.setString(6, categoria);
            stmt.executeUpdate();
        }
    }

    // Método para atualizar um produto
    public void atualizarProduto(int id, String nome, String codigo, int quantidade, double preco, String categoria) throws SQLException {
        if (nome == null || nome.trim().isEmpty() || codigo == null || codigo.trim().isEmpty() || categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome, código e categoria não podem ser nulos ou vazios.");
        }

        String sql = "UPDATE produtos SET nome = ?, codigo = ?, quantidade = ?, preco = ?, status_estoque = ?, categoria = ? WHERE id = ?";
        String statusEstoque = calcularStatusEstoque(quantidade);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, codigo);
            stmt.setInt(3, quantidade);
            stmt.setDouble(4, preco);
            stmt.setString(5, statusEstoque);
            stmt.setString(6, categoria);
            stmt.setInt(7, id);
            stmt.executeUpdate();
        }
    }

    // Método para remover um produto
    public void removerProduto(int id) throws SQLException {
        String sql = "DELETE FROM produtos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Método para calcular o status do estoque
    public String calcularStatusEstoque(int quantidade) {
        if (quantidade < 10) {
            return STATUS_ESTOQUE_BAIXO;
        } else if (quantidade >= 10 && quantidade < 30) {
            return STATUS_ESTOQUE_MEDIO;
        } else {
            return STATUS_ESTOQUE_ALTO;
        }
    }
}