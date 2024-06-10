package com.kashan.security.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// The repository is responsible for communicating with the DB
// Extends the generic JpaRepository, provide User and ID classes
public interface UserRepository extends JpaRepository<User, Integer> {
    // Using the email since it is a unique identifier
    Optional<User> findByEmail(String email);
}
