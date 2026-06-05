package dev.codeio.helloworld.controller;


import dev.codeio.helloworld.controller.utils.JwtUtil;
import dev.codeio.helloworld.models.User;
import dev.codeio.helloworld.repository.UserRepository;
import dev.codeio.helloworld.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Map<String, String> body){
        String email = body.get("email");
        String password = passwordEncoder.encode(body.get("password"));
        if(userRepository.findByEmail(email).isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }
        userService.createUser(User.builder().email(email).password(password).build());
        return ResponseEntity.status(HttpStatus.CREATED).body("Successfully Registered");


    }

    @PostMapping("/login")
    public ResponseEntity<?> longinUser(@RequestBody Map<String, String> body){
        String email = body.get("email");
        String password = body.get("password");
        var userOptional = userRepository.findByEmail(email);
        if(!userOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not registered");
        }
        User user = userOptional.get();
        if(!passwordEncoder.matches(password, user.getPassword())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Password doesn't matched");
        }
        String token = jwtUtil.generateToken(email);
        return ResponseEntity.ok(Map.of("token", token));

    }
}
