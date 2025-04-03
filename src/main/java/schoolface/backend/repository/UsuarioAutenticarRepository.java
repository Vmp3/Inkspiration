package inkspiration.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inkspiration.backend.entities.UsuarioAutenticar;

@Repository
public interface UsuarioAutenticarRepository extends JpaRepository<UsuarioAutenticar, Long> {
    Optional<UsuarioAutenticar> findByEmail(String email);
}
