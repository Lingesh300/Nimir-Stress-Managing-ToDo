package dev.codeio.helloworld;

import dev.codeio.helloworld.models.Todo;
import dev.codeio.helloworld.models.User;
import dev.codeio.helloworld.repository.TodoRepository;
import dev.codeio.helloworld.repository.UserRepository;
import dev.codeio.helloworld.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
@EnableScheduling
public class NimirNotificationScheduler {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TelegramService telegramService;

    // runs every 60 seconds
    @Scheduled(fixedRate = 60000)
    public void checkAndNotify() {
        String today = LocalDate.now().toString();
        String currentTime = LocalTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm"));

        List<Todo> todos = todoRepository.findAll();

        for (Todo todo : todos) {
            if (
                    todo.getIsCompleted() != null && todo.getIsCompleted()
            ) continue;

            if (todo.getDate() == null || todo.getTime() == null) continue;
            if (!todo.getDate().equals(today)) continue;
            if (!todo.getTime().equals(currentTime)) continue;
            if (todo.getUserEmail() == null) continue;


            Optional<User> userOpt = userRepository.findByEmail(todo.getUserEmail());
            if (userOpt.isEmpty()) continue;

            User user = userOpt.get();
            if (user.getTelegramChatId() == null) continue;

            String message = String.format(
                    "✊ Nimir Reminder!\n\n📌 %s\n🔴 Priority: %s\n⏰ Time: %s\n\nYou got this! 💪",
                    todo.getTitle(),
                    todo.getPriority() != null ? todo.getPriority().toUpperCase() : "MEDIUM",
                    todo.getTime()
            );

            telegramService.sendMessage(user.getTelegramChatId(), message);
        }
    }
}