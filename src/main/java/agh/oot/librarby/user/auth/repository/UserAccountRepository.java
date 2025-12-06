package agh.oot.librarby.user.auth.repository;

import agh.oot.librarby.user.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<UserAccount> findByUsername(String username);
}
