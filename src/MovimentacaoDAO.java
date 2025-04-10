import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MovimentacaoDAO {

    public void registrarMovimentacao(int produtoId, String tipo, int quantidade) {
        String sql = "INSERT INTO movimentacoes (produto_id, tipo, quantidade) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produtoId);
            stmt.setString(2, tipo);
            stmt.setInt(3, quantidade);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}