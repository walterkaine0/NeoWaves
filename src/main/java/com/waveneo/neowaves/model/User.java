package com.waveneo.neowaves.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users") // Переименовываем таблицу, чтобы избежать конфликта с системным словом USER
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // Явно указываем имя колонки
    private Long id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "username")
    private String username;

    @OneToMany(mappedBy = "user")
    @com.fasterxml.jackson.annotation.JsonIgnore
    @ToString.Exclude
    private List<Playlist> playlists;
}

