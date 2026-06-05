package dev.codeio.helloworld.repository;

import dev.codeio.helloworld.models.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByUserEmail(String userEmail);
}