package com.waveneo.neowaves.controller;

import com.waveneo.neowaves.model.Playlist;
import com.waveneo.neowaves.model.Song;
import com.waveneo.neowaves.repository.PlaylistRepository;
import com.waveneo.neowaves.repository.SongRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.waveneo.neowaves.model.Playlist;
import com.waveneo.neowaves.model.Song;
import com.waveneo.neowaves.repository.PlaylistRepository;
import com.waveneo.neowaves.repository.SongRepository;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@Controller
public class SongController {

    private final SongRepository songRepository;
    private final PlaylistRepository playlistRepository; // Добавь это поле

    // Обнови конструктор, чтобы Spring внедрил оба репозитория
    public SongController(SongRepository songRepository, PlaylistRepository playlistRepository) {
        this.songRepository = songRepository;
        this.playlistRepository = playlistRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("songs", songRepository.findAll());
        return "index";
    }

    @PostMapping("/like/{songId}")
    @ResponseBody
    public String likeSong(@PathVariable Long songId) {
        // 1. Ищем или создаем плейлист "Избранное"
        Playlist favorites = playlistRepository.findById(1L).orElseGet(() -> {
            Playlist newPlaylist = new Playlist();
            newPlaylist.setName("Favorites");
            // Если у тебя уже есть юзер в базе, можно его привязать:
            // newPlaylist.setUser(userRepository.findById(1L).orElse(null));
            return playlistRepository.save(newPlaylist);
        });

        // 2. Ищем песню
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found with id: " + songId));

        // 3. Инициализируем список, если он null (защита от ошибок)
        if (favorites.getSongs() == null) {
            favorites.setSongs(new ArrayList<>());
        }

        // 4. Добавляем и сохраняем
        if (!favorites.getSongs().contains(song)) {
            favorites.getSongs().add(song);
            playlistRepository.save(favorites);
            return "Added to Favorites!";
        }
        return "Already in Favorites";
    }
}

