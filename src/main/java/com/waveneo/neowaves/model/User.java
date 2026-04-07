package com.waveneo.neowaves.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Data
@Table(name = "users") // "user" — зарезервированное слово в SQL, лучше назвать таблицу "users"
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;

    @OneToMany(mappedBy = "user")
    private List<Playlist> playlists;
}