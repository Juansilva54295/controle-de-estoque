import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    // Método para autenticar o usuário
    public Usuario autenticar(String email, String senha) {
        return buscarUsuario(email, senha);
    }

    // Método para buscar um usuário pelo email e senha
    public Usuario buscarUsuario(String email, String senha) {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";
        Usuario usuario = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                usuario = new Usuario(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getString("senha"),
                    rs.getString("nivel_acesso")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuario; // Retorna o usuário encontrado ou null se não encontrado
    }

    // Método para adicionar um novo usuário
    public boolean adicionarUsuario(String nome, String email, String senha, String nivelAcesso) {
        // Verifica se já existe um admin
        if (nivelAcesso.equals("admin") && existeAdmin()) {
            System.out.println("Já existe um usuário admin cadastrado.");
            return false;
        }

        // Verifica se o email já está cadastrado
        if (emailJaCadastrado(email)) {
            System.out.println("Email já cadastrado.");
            return false;
        }

        String sql = "INSERT INTO usuarios (nome, email, senha, nivel_acesso) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, senha);
            stmt.setString(4, nivelAcesso);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para verificar se já existe um admin
    private boolean existeAdmin() {
        String sql = "SELECT * FROM usuarios WHERE nivel_acesso = 'admin'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            return rs.next(); // Retorna true se encontrar um admin
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para verificar se o email já está cadastrado
    private boolean emailJaCadastrado(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Retorna true se o email já estiver cadastrado
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método para verificar se o usuário é admin
    public boolean isAdmin(String email, String senha) {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ? AND nivel_acesso = 'admin'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // Retorna true se encontrar um admin com o email e senha fornecidos
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}