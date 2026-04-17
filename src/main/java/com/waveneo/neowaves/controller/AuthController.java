package com.waveneo.neowaves.controller;

import com.waveneo.neowaves.model.Playlist;
import com.waveneo.neowaves.model.User;
import com.waveneo.neowaves.repository.PlaylistRepository;
import com.waveneo.neowaves.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @PostMapping("/api/auth/sync")
    public ResponseEntity<Map<String, String>> syncUser(@RequestBody Map<String, String> data) {
        try {
            String email = data.get("email");
            String username = data.get("username");

            userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setUsername(username);
                User savedUser = userRepository.save(newUser);

                Playlist favs = new Playlist();
                favs.setName("Favorites");
                favs.setUser(savedUser);
                favs.setSongs(new ArrayList<>());
                playlistRepository.save(favs);

                return savedUser;
            });

            return ResponseEntity.ok(Map.of("status", "success", "userEmail", email));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
