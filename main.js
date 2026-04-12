const { app, BrowserWindow } = require('electron');
const path = require('path');
const { exec } = require('child_process');

let serverProcess;

/**
 * Создание окна браузера
 */
function createWindow() {
    const win = new BrowserWindow({
        width: 1200,
        height: 800,
        title: "Waves Desktop",
        // Путь к иконке (убедись, что файл существует по этому пути)
        icon: path.join(__dirname, 'src/main/resources/static/favicon.ico'),
        webPreferences: {
            partition: 'persist:google-session',
            nodeIntegration: false,
            contextIsolation: false,
            webSecurity: false
        }
    });

    // Маскируемся под обычный браузер для корректной работы Google Auth
    win.webContents.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

    // Загружаем адрес твоего Spring Boot сервера
    win.loadURL('http://localhost:8081');

    // Отключаем верхнее меню
    win.setMenuBarVisibility(false);

    // Если нужно открыть консоль для отладки, раскомментируй строку ниже:
    // win.webContents.openDevTools();
}

/**
 * Логика запуска бэкенда и приложения
 */
app.whenReady().then(() => {
    const jarName = 'neo-waves-0.0.1-SNAPSHOT.jar';

    // Определяем путь к JAR:
    // Если приложение собрано в .exe — берем из папки resources
    // Если запускаем через npm start — берем из build/libs (как на твоем скриншоте)
    const jarPath = app.isPackaged
        ? path.join(process.resourcesPath, jarName)
        : path.join(__dirname, 'build/libs', jarName);

    console.log(`[NeoWaves] Запуск бэкенда по пути: ${jarPath}`);

    // Запуск JAR процесса
    serverProcess = exec(`java -jar "${jarPath}"`, (error) => {
        if (error) {
            console.error(`[NeoWaves] Ошибка запуска JAR: ${error}`);
        }
    });

    // Выводим логи бэкенда в консоль Electron для контроля
    serverProcess.stdout.on('data', (data) => {
        console.log(`[Spring Boot]: ${data}`);
    });

    // Ждем 5 секунд, чтобы Spring Boot успел инициализировать БД и подняться
    setTimeout(() => {
        createWindow();
    }, 5000);

    app.on('activate', () => {
        if (BrowserWindow.getAllWindows().length === 0) createWindow();
    });
});

/**
 * Корректное завершение работы
 */
app.on('window-all-closed', () => {
    // Убиваем процесс бэкенда, чтобы порт 8081 освободился
    if (serverProcess) {
        console.log("[Waves] Остановка бэкенда...");
        serverProcess.kill();
    }

    if (process.platform !== 'darwin') {
        app.quit();
    }
});
