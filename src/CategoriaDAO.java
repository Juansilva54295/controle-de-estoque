import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {
    public void adicionarCategoria(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO categorias (nome) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, categoria.getNome());
            stmt.executeUpdate();

            // Recupera o ID gerado
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    categoria.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    // Lista todas as categorias
    public List<Categoria> listarCategorias() throws SQLException {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categorias";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Categoria categoria = new Categoria(rs.getInt("id"), rs.getString("nome"));
                categorias.add(categoria);
            }
        }
        return categorias;
    }

    // Busca uma categoria pelo nome
    public Categoria buscarCategoriaPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM categorias WHERE nome = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Categoria(rs.getInt("id"), rs.getString("nome"));
                }
            }
        }
        return null;
    }

    // Remove uma categoria
    public void removerCategoria(Categoria categoria) throws SQLException {
        String sql = "DELETE FROM categorias WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoria.getId());
            stmt.executeUpdate();
        }
    }
}