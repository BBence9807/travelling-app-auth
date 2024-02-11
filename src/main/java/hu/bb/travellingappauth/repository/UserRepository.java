package hu.bb.travellingappauth.repository;

import hu.bb.travellingappauth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Boolean existsByEmail(String email);

    User getUserByEmail(String email);
}
