package utn.crud.tpfinal.Repository.MONGO;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Repository;
import utn.crud.tpfinal.Models.LoginCredential;
import utn.crud.tpfinal.Models.Persona;

import java.util.Optional;

@Repository
public interface PersonaMongoRepository extends org.springframework.data.mongodb.repository.MongoRepository<LoginCredential, String> {
    Optional<LoginCredential> findByEmail(String email);


    Optional<LoginCredential> findByEmailAndPassword(String email, String password);

    Optional<LoginCredential> findByPersonaId(Long personaId);

    void deleteByPersonaId(Long personaId);

    @CacheEvict(value = "loginCache", key = "#email")
    void deleteByEmail(String email);
}
