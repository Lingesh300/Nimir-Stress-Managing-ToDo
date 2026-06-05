package dev.codeio.helloworld.service;

import dev.codeio.helloworld.models.Todo;
import dev.codeio.helloworld.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class todoService {

    @Autowired
    private TodoRepository todorepository;

    public Todo createTodo(Todo todo) {
        return todorepository.save(todo);
    }

    @Transactional
    public Todo getTodoById(Long id) {
        return todorepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + id));
    }

    public List<Todo> getTodos() {
        return todorepository.findAll();
    }

    public List<Todo> getTodosByEmail(String email) {
        return todorepository.findByUserEmail(email);
    }

    public Todo updateTodo(Todo todo) {
        return todorepository.save(todo);
    }

    public void deleteTodoById(Long id) {
        todorepository.delete(getTodoById(id));
    }
}