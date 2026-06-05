package dev.codeio.helloworld.controller;

import dev.codeio.helloworld.models.User;
import dev.codeio.helloworld.repository.UserRepository;
import dev.codeio.helloworld.service.TelegramService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/telegram")
@RequiredArgsConstructor
public class TelegramController {

    private final UserRepository userRepository;
    private final TelegramService telegramService;

    // ✅ generate a unique code for this user
    @PostMapping("/generate-code")
    public ResponseEntity<?> generateCode() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

        User user = userOpt.get();

        // generate short code like "NIMIR-4829"
        String code = "NIMIR-" + UUID.randomUUID()
                .toString()
                .substring(0, 4)
                .toUpperCase();

        user.setTelegramCode(code);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("code", code));
    }

    // check connection status
    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return ResponseEntity.notFound().build();

        boolean connected = userOpt.get().getTelegramChatId() != null;
        return ResponseEntity.ok(Map.of("connected", connected));
    }
}