-- Сначала чистим таблицы (важно при пересоздании структуры)
TRUNCATE TABLE song, album, artist RESTART IDENTITY CASCADE;

-- 1. Добавляем Артистов
INSERT INTO artist (name, bio) VALUES ('Ken Carson', 'Opium label artist');
INSERT INTO artist (name, bio) VALUES ('Lil Uzi Vert', 'American rapper and singer');

-- 2. Добавляем Альбомы (привязываем к id артистов)
-- Предположим, Ken Carson - id 1, Lil Uzi - id 2
INSERT INTO album (title, cover_url, artist_id)
VALUES ('margiela', 'https://github.com/walterkaine0/Storage1232/blob/main/kencarsonmargiela.jpg?raw=true', 1);

INSERT INTO album (title, cover_url, artist_id)
VALUES ('Pink Tape', 'https://github.com/walterkaine0/Storage1232/blob/main/chanelboyliluzi.jpg?raw=true', 2);

-- 3. Добавляем Песни (привязываем к id альбомов)
INSERT INTO song (title, duration, s3_url, album_id)
VALUES ('margiela', 136, 'https://github.com/walterkaine0/Storage1232/raw/refs/heads/main/Ken%20Carson%20-%20margiela.mp3', 1);

INSERT INTO song (title, duration, s3_url, album_id)
VALUES ('Chanel Boy', 157, 'https://github.com/walterkaine0/Storage1232/raw/refs/heads/main/Lil%20Uzi%20Vert%20-%20Chanel%20Boy.mp3', 2);

-- Создаем тестового пользователя
INSERT INTO users (username, email)
VALUES ('WalterKaine', 'demo@neowaves.com') ON CONFLICT DO NOTHING;

-- Создаем плейлист "Favorites" для этого пользователя (id = 1)
INSERT INTO playlist (name, user_id)
VALUES ('Favorites', 1) ON CONFLICT DO NOTHING;