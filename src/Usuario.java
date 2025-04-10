public class Usuario {
    private int id;
    private String nome;
    private String email;
    private String senha;
    private String nivelAcesso;

    public Usuario(int id, String nome, String email, String senha, String nivelAcesso) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.nivelAcesso = nivelAcesso;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getNivelAcesso() {
        return nivelAcesso;
    }
}