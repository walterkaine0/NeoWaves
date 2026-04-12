const { app, BrowserWindow } = require('electron');
const path = require('path');

function createWindow() {
    // Создаем основное окн
    const win = new BrowserWindow({
        width: 1200,
        height: 800,
        title: "NeoWaves Desktop",
        // Правильный путь к иконке (используем path.join для надежности)
        icon: path.join(__dirname, 'src/main/resources/static/favicon.ico'),
        webPreferences: {
            // Сохраняем сессию Google, чтобы не вылетало при перезапуске
            partition: 'persist:google-session',
            // Включаем интеграцию Node.js
            nodeIntegration: false,
            // Выключаем изоляцию, чтобы работали твои функции из script.js (openPlaylist и т.д.)
            contextIsolation: false,
            // ВАЖНО: разрешаем запросы к локальному бэкенду (отключаем CORS блокировки)
            webSecurity: false
        }
    });

    // Добавь это ПЕРЕД loadURL
    win.webContents.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

    // Загружаем твое Spring Boot приложение
    // Если плейлисты привязаны к email, убедись, что ты залогинен
    win.loadURL('http://localhost:8081');

    // Скрываем стандартное верхнее меню (File, Edit, etc.)
    win.setMenuBarVisibility(false);

    // Опционально: открываем инструменты разработчика сразу (для отладки)
    // win.webContents.openDevTools();
}

// Запуск приложения
app.whenReady().then(() => {
    createWindow();

    app.on('activate', () => {
        // На macOS важно пересоздавать окно, если все закрыты
        if (BrowserWindow.getAllWindows().length === 0) createWindow();
    });
});

// Закрытие приложения
app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') {
        app.quit();
    }
});
