package dev.codeio.helloworld.controller;

import dev.codeio.helloworld.repository.UserRepository;
import dev.codeio.helloworld.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/telegram")
public class TelegramWebhookController {

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

            if (message.get("text") == null) return;
            String text = ((String) message.get("text")).trim();

            if ("/start".equals(text)) {
                telegramService.sendMessage(chatId,
                        "✊ Welcome to Nimir!\n\nSend your unique code from the app to connect your account."
                );
                return;
            }

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
                user.setTelegramCode(null);
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