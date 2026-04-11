package com.waveneo.neowaves.controller;

import com.waveneo.neowaves.model.User;
import com.waveneo.neowaves.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/api/auth/sync")
    public ResponseEntity<String> syncUser(@RequestBody java.util.Map<String, String> data) {
        try {
            String email = data.get("email");
            String username = data.get("username");

            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setEmail(email);
                        newUser.setUsername(username);
                        return userRepository.save(newUser);
                    });
            return ResponseEntity.ok("Synced");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
