package inkspiration.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inkspiration.backend.entities.Profissional;
import inkspiration.backend.entities.Usuario;

@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {
    Optional<Profissional> findByUsuario(Usuario usuario);
    Optional<Profissional> findByUsuario_IdUsuario(Long idUsuario);
    Page<Profissional> findAll(Pageable pageable);
    boolean existsByUsuario(Usuario usuario);
    boolean existsByUsuario_IdUsuario(Long idUsuario);
    
    // Consultas que excluem usuários com role DELETED
    List<Profissional> findByUsuarioRoleNot(String role);
    Page<Profissional> findByUsuarioRoleNot(String role, Pageable pageable);
} 