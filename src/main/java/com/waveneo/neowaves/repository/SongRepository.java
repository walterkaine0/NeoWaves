package com.waveneo.neowaves.repository;

import com.waveneo.neowaves.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Long> {
}
