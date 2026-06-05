package dev.codeio.helloworld.repository;

import dev.codeio.helloworld.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByTelegramCode(String telegramCode);
}