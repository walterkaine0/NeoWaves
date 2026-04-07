package com.waveneo.neowaves.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.List;

@NoArgsConstructor
@Entity
@Data
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String coverUrl;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @OneToMany(mappedBy = "album")
    @JsonIgnore // Чтобы не тянуть песни обратно при сериализации
    @ToString.Exclude // Чтобы Lombok не зациклился
    private List<Song> songs;
}

