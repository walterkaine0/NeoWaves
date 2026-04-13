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
                    webSecurity: false,
                    nativeWindowOpen: true,
                    allowRunningInsecureContent: true
                }
    });

    win.webContents.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36");
    win.loadURL('http://localhost:8081');
    win.setMenuBarVisibility(false);
}

app.whenReady().then(() => {
    const jarName = 'neo-waves-0.0.1-SNAPSHOT.jar';

    const jarPath = app.isPackaged
        ? path.join(process.resourcesPath, jarName)
        : path.join(__dirname, 'build/libs', jarName);

    console.log(`[NeoWaves] Запуск бэкенда по пути: ${jarPath}`);

    serverProcess = exec(`java -jar "${jarPath}"`, (error) => {
        if (error) {
            console.error(`[NeoWaves] Ошибка запуска JAR: ${error}`);
        }
    });

    serverProcess.stdout.on('data', (data) => {
        console.log(`[Spring Boot]: ${data}`);
    });

    setTimeout(() => {
        createWindow();
    }, 15000);


    app.on('activate', () => {
        if (BrowserWindow.getAllWindows().length === 0) createWindow();
    });
});

app.on('window-all-closed', () => {
    if (serverProcess) {
        console.log("[Waves] Остановка бэкенда...");
        serverProcess.kill();
    }

    if (process.platform !== 'darwin') {
        app.quit();
    }
});
