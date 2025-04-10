import java.util.HashMap;
import java.util.Map;

public class Autenticacao {

    // Simulação de um banco de dados de usuários
    private static final Map<String, String> USUARIOS = new HashMap<>();

    static {
        // Adiciona alguns usuários de exemplo
        USUARIOS.put("admin@exemplo.com", "senha123");
        USUARIOS.put("usuario@exemplo.com", "senha456");
    }

    // Método para autenticar o usuário
    public boolean autenticar(String email, String senha) {
        if (USUARIOS.containsKey(email)) {
            String senhaArmazenada = USUARIOS.get(email);
            return senhaArmazenada.equals(senha);
        }
        return false;
    }
}