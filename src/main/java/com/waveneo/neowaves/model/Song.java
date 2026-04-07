package com.waveneo.neowaves.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    @ToString.Exclude // Чтобы Lombok не зациклился
    private Album album;
}

