# Waves

Waves — это музыкальный плеер с веб-интерфейсом и desktop-оболочкой на Electron.  
Проект сочетает локальный Spring Boot backend, авторизацию через Firebase и систему пользовательских плейлистов.

## Возможности

- просмотр музыкальной библиотеки
- воспроизведение треков во встроенном плеере
- авторизация через Google
- регистрация по e-mail и паролю
- восстановление пароля
- создание и удаление пользовательских плейлистов
- отдельный плейлист **Favorites**
- добавление и удаление треков из плейлистов
- лайк текущего трека
- запуск как в браузере, так и в виде desktop-приложения

## Стек проекта

### Backend
- Java 17
- Spring Boot 4
- Spring MVC
- Spring Data JPA
- Thymeleaf
- H2 Database

### Desktop
- Electron
- electron-builder

### Frontend
- HTML
- CSS
- Vanilla JavaScript

### Auth
- Firebase Authentication

## Архитектура

NeoWaves состоит из двух частей:

1. **Spring Boot backend**
   - хранит данные о треках, пользователях и плейлистах
   - отдает HTML-шаблон и API
   - работает локально на `http://127.0.0.1:8081`

2. **Electron desktop shell**
   - запускает backend как JAR-процесс
   - ждет, пока сервер ответит по `/api/health`
   - открывает приложение в отдельном desktop-окне

## Основные функции API

### Состояние приложения
- `GET /api/health` — проверка, что backend запущен

### Авторизация
- `POST /api/auth/sync` — синхронизация пользователя после Firebase-входа

### Плейлисты
- `GET /playlist/user?email=...` — загрузка плейлистов пользователя
- `POST /playlist/create?name=...&userEmail=...` — создание плейлиста
- `POST /playlist/delete/{id}` — удаление плейлиста
- `GET /playlist/{id}/songs` — треки плейлиста
- `POST /playlist/{playlistId}/add/{songId}` — добавить трек в плейлист
- `POST /playlist/{playlistId}/remove/{songId}` — удалить трек из плейлиста

### Избранное
- `POST /like/{songId}?userEmail=...` — добавить или убрать трек из Favorites

## Структура проекта

```text
NeoWaves/
├─ src/
│  ├─ main/
│  │  ├─ java/com/waveneo/neowaves/
│  │  │  ├─ controller/
│  │  │  ├─ model/
│  │  │  └─ repository/
│  │  └─ resources/
│  │     ├─ static/
│  │     │  └─ css/
│  │     ├─ templates/
│  │     ├─ application.properties
│  │     └─ data.sql
├─ build.gradle
├─ package.json
├─ main.js
└─ README.md
```

## Локальный запуск

### Что нужно
- **Java 17**
- **Node.js**
- **npm**

### 1. Клонирование проекта
```bash
git clone https://github.com/walterkaine0/NeoWaves.git
cd NeoWaves
git checkout const-fix
```

### 2. Запуск backend в браузере
```bash
gradlew.bat bootRun
```

После запуска приложение будет доступно по адресу:

```text
http://127.0.0.1:8081
```

## Запуск desktop-версии

### Важно
Electron в режиме разработки запускает **уже собранный JAR**, поэтому перед `npm start` нужно сначала собрать backend:

```bash
gradlew.bat bootJar
npm install
npm start
```

## Сборка инсталлятора

Для сборки desktop-инсталлятора:

```bash
npm run dist
```

В результате будет создан Windows installer в папке:

```text
dist/
```

## Встроенная Java для инсталлятора

Если нужен запуск на другом ПК без установленной Java, в корне проекта должна быть папка:

```text
jre/
```

Пример структуры:

```text
jre/
├─ bin/
│  └─ java.exe
├─ conf/
├─ lib/
└─ ...
```

Именно эта папка добавляется в desktop-сборку.

## База данных

По умолчанию используется локальная файловая H2-база.

Пример конфигурации:
- порт backend: `8081`
- файл БД: `./data/neowaves`

Если нужно, конфигурацию можно поменять в `src/main/resources/application.properties`.

## Пользовательский сценарий

1. Пользователь открывает NeoWaves
2. Выполняет вход через Google или по e-mail
3. Backend синхронизирует пользователя
4. Автоматически создается плейлист **Favorites**, если его еще нет
5. Пользователь:
   - просматривает библиотеку
   - слушает треки
   - добавляет треки в плейлисты
   - ставит лайки
   - создает собственные подборки

## Текущий статус проекта

Проект находится в активной доработке.  
Сейчас основной акцент сделан на:
- стабилизацию desktop-сборки
- доработку UI
- улучшение логики плейлистов
- улучшение авторизации и синхронизации пользователя

## Планы на развитие

- более удобный поиск по библиотеке
- отдельный экран избранного
- улучшение анимаций интерфейса
- улучшение системы хранения обложек и медиа
- более безопасная серверная валидация Firebase token
- расширение desktop-сборки под другие платформы

## Автор

**walterkaine0**

GitHub:  
https://github.com/walterkaine0/NeoWaves

