package dev.codeio.helloworld.service;

import dev.codeio.helloworld.models.User;
import dev.codeio.helloworld.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository UserRepository;

    public User createUser(User User) {
        return UserRepository.save(User);
    }

    @Transactional
    public User getUserById(Long id) {
        return UserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

}