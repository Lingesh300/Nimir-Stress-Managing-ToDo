package dev.codeio.helloworld.controller;

import dev.codeio.helloworld.models.Todo;
import dev.codeio.helloworld.repository.UserRepository;
import dev.codeio.helloworld.service.TelegramService;
import dev.codeio.helloworld.service.todoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/todo")
@Slf4j
public class ToDoController {

    @Autowired
    private todoService todoService;


    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }


    @GetMapping
    ResponseEntity<List<Todo>> getTodos() {
        String email = getCurrentUserEmail();
        return new ResponseEntity<>(todoService.getTodosByEmail(email), HttpStatus.OK);
    }


    @PostMapping("/create")
    ResponseEntity<Todo> createTodo(@RequestBody Todo todo) {
        String email = getCurrentUserEmail();
        todo.setUserEmail(email);
        return new ResponseEntity<>(todoService.createTodo(todo), HttpStatus.CREATED);
    }

    @PutMapping
    ResponseEntity<Todo> updateTodo(@RequestBody Todo todo) {
        String email = getCurrentUserEmail();
        Todo existing = todoService.getTodoById(todo.getId());
        if (!existing.getUserEmail().equals(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return new ResponseEntity<>(todoService.updateTodo(todo), HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        String email = getCurrentUserEmail();
        Todo existing = todoService.getTodoById(id);
        if (!existing.getUserEmail().equals(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        todoService.deleteTodoById(id);
        return ResponseEntity.ok().build();
    }

   }