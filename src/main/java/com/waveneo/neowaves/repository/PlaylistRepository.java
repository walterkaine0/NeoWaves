package com.waveneo.neowaves.repository;


import com.waveneo.neowaves.model.Playlist; // Убедись, что импорт верный
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    Optional<Playlist> findByNameIgnoreCaseAndUserId(String name, Long userId);

    List<Playlist> findByUserEmail(String email);
}
