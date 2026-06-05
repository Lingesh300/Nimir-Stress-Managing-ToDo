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

    @RestController
    @RequestMapping("/telegram")
    public static class TelegramWebhookController {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private TelegramService telegramService;

        @PostMapping("/webhook")
        public void handleWebhook(@RequestBody Map<String, Object> body) {
            try {
                Map<?, ?> message = (Map<?, ?>) body.get("message");
                if (message == null) return;

                Map<?, ?> chat = (Map<?, ?>) message.get("chat");
                String chatId = chat.get("id").toString();
                String text = ((String) message.get("text")).trim();

                if ("/start".equals(text)) {
                    telegramService.sendMessage(chatId,
                            "✊ Welcome to Nimir!\n\nSend your unique code from the app to connect your account."
                    );
                    return;
                }

                // ✅ user sends code like "NIMIR-4829"
                if (text.startsWith("NIMIR-")) {
                    var userOpt = userRepository.findByTelegramCode(text);

                    if (userOpt.isEmpty()) {
                        telegramService.sendMessage(chatId,
                                "❌ Invalid code. Please generate a new one from the Nimir app."
                        );
                        return;
                    }

                    var user = userOpt.get();
                    user.setTelegramChatId(chatId);
                    user.setTelegramCode(null); // clear code after use
                    userRepository.save(user);

                    telegramService.sendMessage(chatId,
                            "✅ Connected successfully!\n\n✊ Nimir will now remind you even when your laptop is off! 🔥"
                    );
                }

            } catch (Exception e) {
                System.out.println("Webhook error: " + e.getMessage());
            }
        }
    }
}