import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VendaDAO {

    
    // Método para registrar uma nova venda (atualizado para incluir a data)
    public void registrarVenda(String codigoProduto, String nomeProduto, int quantidade, double valorTotal, String categoria, LocalDate dataVenda) {
        String sql = "INSERT INTO vendas (codigo_produto, nome_produto, quantidade, valor_total, categoria, data_venda) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigoProduto);
            stmt.setString(2, nomeProduto);
            stmt.setInt(3, quantidade);
            stmt.setDouble(4, valorTotal);
            stmt.setString(5, categoria);
            stmt.setDate(6, Date.valueOf(dataVenda)); // Novo campo
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para listar todas as vendas (atualizado para incluir a data)
    public List<Venda> listarVendas() {
        List<Venda> vendas = new ArrayList<>();
        String sql = "SELECT id, codigo_produto, nome_produto, quantidade, valor_total, categoria, data_venda FROM vendas";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Venda venda = new Venda(
                    rs.getInt("id"),
                    rs.getString("codigo_produto"),
                    rs.getString("nome_produto"),
                    rs.getInt("quantidade"),
                    rs.getDouble("valor_total"),
                    rs.getString("categoria"),
                    rs.getDate("data_venda").toLocalDate() // Novo campo
                );
                vendas.add(venda);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vendas;
    }

    // Método para excluir uma venda pelo ID
    public void excluirVenda(int id) {
        String sql = "DELETE FROM vendas WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para excluir todas as vendas
    public void excluirTodasVendas() {
        String sql = "DELETE FROM vendas";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
}