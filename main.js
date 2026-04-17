const { app, BrowserWindow, dialog } = require("electron");
const path = require("path");
const fs = require("fs");
const http = require("http");
const { spawn } = require("child_process");

const PORT = 8081;
const SERVER_URL = `http://127.0.0.1:${PORT}`;
const HEALTH_URL = `${SERVER_URL}/api/health`;
const JAR_NAME = "neo-waves-0.0.1-SNAPSHOT.jar";

let mainWindow = null;
let serverProcess = null;
let quitting = false;

function getJarPath() {
  return app.isPackaged
    ? path.join(process.resourcesPath, "app", "backend", JAR_NAME)
    : path.join(__dirname, "build", "libs", JAR_NAME);
}

function getJavaPath() {
  if (!app.isPackaged) {
    return "java";
  }

  const bundledJava = path.join(
    process.resourcesPath,
    "jre",
    "bin",
    process.platform === "win32" ? "java.exe" : "java"
  );

  return fs.existsSync(bundledJava) ? bundledJava : "java";
}

function waitForServer(timeoutMs = 45000, intervalMs = 700) {
  return new Promise((resolve, reject) => {
    const startedAt = Date.now();

    const ping = () => {
      const req = http.get(HEALTH_URL, (res) => {
        if (res.statusCode === 200) {
          res.resume();
          resolve();
          return;
        }
        res.resume();
        retry();
      });

      req.on("error", retry);

      req.setTimeout(2000, () => {
        req.destroy();
        retry();
      });
    };

    const retry = () => {
      if (Date.now() - startedAt >= timeoutMs) {
        reject(new Error("Spring Boot не успел подняться за отведённое время."));
        return;
      }
      setTimeout(ping, intervalMs);
    };

    ping();
  });
}

function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1280,
    height: 860,
    minWidth: 1024,
    minHeight: 700,
    show: false,
    autoHideMenuBar: true,
    backgroundColor: "#090909",
    title: "Waves Desktop",
    icon: path.join(__dirname, "src", "main", "resources", "static", "favicon.ico"),
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
      sandbox: true,
      webSecurity: true
    }
  });

  mainWindow.once("ready-to-show", () => {
    mainWindow.show();
  });

  mainWindow.on("closed", () => {
    mainWindow = null;
  });

  mainWindow.loadURL(SERVER_URL);
}

function startBackend() {
  return new Promise((resolve, reject) => {
    const javaBinary = getJavaPath();
    const jarPath = getJarPath();

    serverProcess = spawn(javaBinary, ["-jar", jarPath], {
      cwd: app.isPackaged ? process.resourcesPath : __dirname,
      windowsHide: true,
      stdio: ["ignore", "pipe", "pipe"]
    });

    serverProcess.once("error", reject);

    serverProcess.once("spawn", () => {
      resolve();
    });

    serverProcess.stdout.on("data", (data) => {
      console.log(`[SpringBoot] ${data.toString().trim()}`);
    });

    serverProcess.stderr.on("data", (data) => {
      console.error(`[SpringBoot] ${data.toString().trim()}`);
    });
  });
}

function stopBackend() {
  return new Promise((resolve) => {
    if (!serverProcess || serverProcess.killed) {
      resolve();
      return;
    }

    let resolved = false;

    const finish = () => {
      if (!resolved) {
        resolved = true;
        resolve();
      }
    };

    serverProcess.once("exit", finish);
    serverProcess.kill();

    setTimeout(finish, 4000);
  });
}

const gotSingleInstanceLock = app.requestSingleInstanceLock();

if (!gotSingleInstanceLock) {
  app.quit();
} else {
  app.on("second-instance", () => {
    if (mainWindow) {
      if (mainWindow.isMinimized()) {
        mainWindow.restore();
      }
      mainWindow.focus();
    }
  });

  app.whenReady().then(async () => {
    try {
      await startBackend();
      await waitForServer();
      createWindow();
    } catch (error) {
      console.error("[Waves Desktop] Ошибка запуска:", error);

      dialog.showErrorBox(
        "Waves не удалось запустить",
        `${error.message}\n\nПроверь, что доступна Java 17, либо положи bundled JRE в resources/jre.`
      );

      await stopBackend();
      app.quit();
    }
  });

  app.on("before-quit", async (event) => {
    if (quitting) return;

    event.preventDefault();
    quitting = true;

    await stopBackend();
    app.quit();
  });

  app.on("window-all-closed", () => {
    if (process.platform !== "darwin") {
      app.quit();
    }
  });

  app.on("activate", () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });
}