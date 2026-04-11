package com.waveneo.neowaves.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Entity
@Data
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private Integer duration;
    private String s3Url;

    @ManyToOne
    @JoinColumn(name = "album_id")
    @ToString.Exclude
    private Album album;

    @ManyToMany(mappedBy = "songs")
    @JsonBackReference
    @ToString.Exclude
    private List<Playlist> playlists;
}


