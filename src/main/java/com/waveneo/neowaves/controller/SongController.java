package com.waveneo.neowaves.controller;

import com.waveneo.neowaves.model.*;
import com.waveneo.neowaves.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SongController {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index(Model model, @RequestParam(required = false) String userEmail) {
        model.addAttribute("songs", songRepository.findAll());

        if (userEmail != null && !userEmail.isEmpty()) {
            User user = userRepository.findByEmail(userEmail).orElse(null);
            if (user != null) {
                model.addAttribute("playlists", playlistRepository.findAll().stream()
                        .filter(p -> p.getUser() != null && p.getUser().getId().equals(user.getId()))
                        .toList());
            } else {
                model.addAttribute("playlists", new ArrayList<>());
            }
        } else {
            model.addAttribute("playlists", new ArrayList<>());
        }
        return "index";
    }

    @PostMapping("/like/{songId}")
    @ResponseBody
    @Transactional
    public String likeSong(@PathVariable Long songId, @RequestParam String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Playlist favorites = playlistRepository.findByNameIgnoreCaseAndUserId("Favorites", user.getId())
                .orElseGet(() -> {
                    Playlist p = new Playlist();
                    p.setName("Favorites");
                    p.setUser(user);
                    p.setSongs(new ArrayList<>());
                    return playlistRepository.save(p);
                });

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        if (favorites.getSongs() == null) favorites.setSongs(new ArrayList<>());

        if (favorites.getSongs().contains(song)) {
            favorites.getSongs().remove(song);
            playlistRepository.save(favorites);
            return "Removed from Favorites";
        } else {
            favorites.getSongs().add(song);
            playlistRepository.save(favorites);
            return "Added to Favorites!";
        }
    }

    @GetMapping("/playlist/{id}/songs")
    @ResponseBody
    @Transactional(readOnly = true)
    public List<Song> getPlaylistSongs(@PathVariable Long id) {
        return playlistRepository.findById(id)
                .map(p -> {
                    p.getSongs().size();
                    return p.getSongs();
                })
                .orElse(new ArrayList<>());
    }

    @PostMapping("/playlist/{playlistId}/add/{songId}")
    @ResponseBody
    @Transactional
    public String addSongToPlaylist(@PathVariable Long playlistId, @PathVariable Long songId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Плейлист не найден"));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Песня не найдена"));

        if (!playlist.getSongs().contains(song)) {
            playlist.getSongs().add(song);
            playlistRepository.save(playlist);
            return "Добавлено в " + playlist.getName();
        }
        return "Уже в плейлисте";
    }

    @PostMapping("/playlist/create")
    @ResponseBody
    @Transactional
    public String createPlaylist(@RequestParam String name, @RequestParam String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.setUser(user);
        playlist.setSongs(new ArrayList<>());
        playlistRepository.save(playlist);

        return "Плейлист '" + name + "' создан!";
    }

    @PostMapping("/playlist/{playlistId}/remove/{songId}")
    @ResponseBody
    @Transactional
    public String removeSongFromPlaylist(@PathVariable Long playlistId, @PathVariable Long songId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Плейлист не найден"));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Песня не найдена"));

        if (playlist.getSongs().contains(song)) {
            playlist.getSongs().remove(song);
            playlistRepository.save(playlist);
            return "Удалено из плейлиста";
        }
        return "Песни нет в этом плейлисте";
    }

    @PostMapping("/playlist/delete/{id}")
    @ResponseBody
    @Transactional
    public ResponseEntity<String> deletePlaylist(@PathVariable Long id) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Плейлист не найден"));

        if ("Favorites".equalsIgnoreCase(playlist.getName())) {
            return ResponseEntity.badRequest().body("Нельзя удалить Избранное");
        }

        playlist.getSongs().clear();
        playlistRepository.delete(playlist);

        return ResponseEntity.ok("Плейлист удален");
    }
}
