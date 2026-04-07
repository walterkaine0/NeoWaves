package com.waveneo.neowaves.repository;


import com.waveneo.neowaves.model.Playlist; // Убедись, что импорт верный
import org.springframework.data.jpa.repository.JpaRepository;

// Было: JpaRepository<Song, Long>
// Нужно: JpaRepository<Playlist, Long>
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
}
