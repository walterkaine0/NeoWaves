

SET REFERENTIAL_INTEGRITY FALSE;
TRUNCATE TABLE song RESTART IDENTITY;
TRUNCATE TABLE album RESTART IDENTITY;
TRUNCATE TABLE artist RESTART IDENTITY;
-- USERS и PLAYLIST не трогаем, чтобы не удалять твой аккаунт
SET REFERENTIAL_INTEGRITY TRUE;

INSERT INTO artist (id, name, bio) VALUES (1, 'Ken Carson', 'Opium label artist');
INSERT INTO artist (id, name, bio) VALUES (2, 'Lil Uzi Vert', 'American rapper');
INSERT INTO album (id, title, cover_url, artist_id) VALUES (1, 'margiela', 'https://github.com/walterkaine0/Storage1232/blob/main/kencarsonmargiela.jpg?raw=true', 1);
INSERT INTO album (id, title, cover_url, artist_id) VALUES (2, 'Pink Tape', 'https://github.com/walterkaine0/Storage1232/blob/main/chanelboyliluzi.jpg?raw=true', 2);
INSERT INTO song (id, title, duration, s3_url, album_id) VALUES (1, 'margiela', 136, 'https://github.com/walterkaine0/Storage1232/raw/refs/heads/main/Ken%20Carson%20-%20margiela.mp3', 1);
INSERT INTO song (id, title, duration, s3_url, album_id) VALUES (2, 'Chanel Boy', 157, 'https://github.com/walterkaine0/Storage1232/raw/refs/heads/main/Lil%20Uzi%20Vert%20-%20Chanel%20Boy.mp3', 2);