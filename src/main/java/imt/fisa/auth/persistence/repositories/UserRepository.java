package imt.fisa.auth.persistence.repositories;


import imt.fisa.auth.persistence.dto.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserEntity, String> {

    Optional<UserEntity> findByIdentifiant(String identifiant);
    Optional<UserEntity> findByIdentifiantAndPassword(String identifiant, String password);
    Optional<UserEntity> findByToken(String token);



}
