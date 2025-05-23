package inkspiration.backend.dto;

import inkspiration.backend.entities.Usuario;

public class UsuarioSeguroDTO {
    private Long idUsuario;
    private String nome;
    private String cpfMascarado;
    private String imagemPerfil;
    private String role;
    private Long idEndereco;

    public UsuarioSeguroDTO() {
    }

    public UsuarioSeguroDTO(Long idUsuario, String nome, String cpfMascarado, String imagemPerfil, String role, Long idEndereco) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.cpfMascarado = cpfMascarado;
        this.imagemPerfil = imagemPerfil;
        this.role = role;
        this.idEndereco = idEndereco;
    }
    
    public static UsuarioSeguroDTO fromUsuario(Usuario usuario) {
        // Mascara o CPF, deixando apenas os últimos 3 dígitos visíveis
        String cpfMascarado = null;
        if (usuario.getCpf() != null && usuario.getCpf().length() > 3) {
            String cpf = usuario.getCpf().replaceAll("[^0-9]", "");
            int len = cpf.length();
            if (len >= 3) {
                cpfMascarado = "***." + "***." + "***-" + cpf.substring(len - 2);
            } else {
                cpfMascarado = "***.***.***-**";
            }
        }
        
        // Obtém o ID do endereço se existir
        Long idEndereco = usuario.getEndereco() != null ? usuario.getEndereco().getIdEndereco() : null;
        
        return new UsuarioSeguroDTO(
            usuario.getIdUsuario(),
            usuario.getNome(),
            cpfMascarado,
            usuario.getImagemPerfil(),
            usuario.getRole(),
            idEndereco
        );
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpfMascarado() {
        return cpfMascarado;
    }

    public void setCpfMascarado(String cpfMascarado) {
        this.cpfMascarado = cpfMascarado;
    }

    public String getImagemPerfil() {
        return imagemPerfil;
    }

    public void setImagemPerfil(String imagemPerfil) {
        this.imagemPerfil = imagemPerfil;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getIdEndereco() {
        return idEndereco;
    }

    public void setIdEndereco(Long idEndereco) {
        this.idEndereco = idEndereco;
    }
} 