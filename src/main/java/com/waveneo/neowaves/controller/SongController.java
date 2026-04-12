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

@CrossOrigin(origins = "*") // Добавь это обязательно!
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
        // Песни показываем всем
        model.addAttribute("songs", songRepository.findAll());

        // Если email не передан, ПРИНУДИТЕЛЬНО очищаем список плейлистов
        if (userEmail == null || userEmail.trim().isEmpty() || userEmail.equals("null")) {
            model.addAttribute("playlists", new ArrayList<Playlist>());
        } else {
            User user = userRepository.findByEmail(userEmail).orElse(null);
            if (user != null) {
                // Фильтруем плейлисты именно этого пользователя
                List<Playlist> userPlaylists = playlistRepository.findAll().stream()
                        .filter(p -> p.getUser() != null && p.getUser().getEmail().equalsIgnoreCase(userEmail))
                        .toList();
                model.addAttribute("playlists", userPlaylists);
            } else {
                model.addAttribute("playlists", new ArrayList<Playlist>());
            }
        }
        return "index";
    }


    // 2. ИСПРАВЛЕННЫЙ МЕТОД ЛАЙКА
    @PostMapping("/like/{songId}")
    @ResponseBody
    @Transactional
    public String likeSong(@PathVariable Long songId, @RequestParam String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Используем новый метод репозитория
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

    // 3. МЕТОД ДЛЯ ЗАГРУЗКИ ПЕСЕН ПЛЕЙЛИСТА
    @GetMapping("/playlist/{id}/songs")
    @ResponseBody
    @Transactional(readOnly = true)
    public List<Song> getPlaylistSongs(@PathVariable Long id) {
        return playlistRepository.findById(id)
                .map(p -> {
                    p.getSongs().size(); // Принудительный "прогрев" списка
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

    @GetMapping("/playlist/user") // Убрали {email} из пути
    @ResponseBody
    public List<Playlist> getUserPlaylists(@RequestParam String email) {
        return playlistRepository.findByUserEmail(email);
    }

    @PostMapping("/playlist/create")
    @ResponseBody
    @Transactional
    public String createPlaylist(@RequestParam String name, @RequestParam String userEmail) {
        if (userEmail == null || userEmail.trim().isEmpty()) {
            return "Ошибка: Email пользователя не указан!";
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Пользователь с email " + userEmail + " не найден в базе"));

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

        // Запрещаем удалять избранное
        if ("Favorites".equalsIgnoreCase(playlist.getName())) {
            return ResponseEntity.badRequest().body("Нельзя удалить Избранное");
        }

        // Сначала очищаем связи с песнями, чтобы не было ошибок внешнего ключа
        playlist.getSongs().clear();
        playlistRepository.delete(playlist);

        return ResponseEntity.ok("Плейлист удален");
    }
}
