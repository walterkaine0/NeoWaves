(() => {
  const firebaseConfig = {
    apiKey: "AIzaSyDJhWJDrW-rN2a3zFp_hAL5EWFn5UT9B28",
    authDomain: "neowaves-824c3.firebaseapp.com",
    projectId: "neowaves-824c3",
    storageBucket: "neowaves-824c3.firebasestorage.app",
    messagingSenderId: "398266414799",
    appId: "1:398266414799:web:f58c71fe879b531f8b29ba",
    measurementId: "G-YS6MJR4KBX"
  };

  const friendlyError = (error) => {
    const code = error?.code || "";
    const map = {
      "auth/invalid-email": "Неверный формат e-mail.",
      "auth/user-disabled": "Этот аккаунт отключён.",
      "auth/user-not-found": "Аккаунт с таким e-mail не найден.",
      "auth/wrong-password": "Неверный пароль.",
      "auth/invalid-credential": "Неверный e-mail или пароль.",
      "auth/email-already-in-use": "Этот e-mail уже используется.",
      "auth/weak-password": "Пароль должен быть не короче 6 символов.",
      "auth/popup-blocked": "Браузер заблокировал окно входа. Повторите попытку.",
      "auth/network-request-failed": "Сетевой запрос не удался. Проверь подключение."
    };
    return map[code] || error?.message || "Не удалось выполнить операцию авторизации.";
  };

  document.addEventListener("DOMContentLoaded", () => {
    if (!window.firebase) {
      console.error("[NeoWaves Auth] Firebase CDN не загружен");
      return;
    }

    if (!firebase.apps.length) {
      firebase.initializeApp(firebaseConfig);
    }

    const auth = firebase.auth();
    const googleProvider = new firebase.auth.GoogleAuthProvider();
    googleProvider.setCustomParameters({ prompt: "select_account" });

    const refs = {
      shell: document.getElementById("auth-shell"),
      closeBtn: document.getElementById("auth-close-btn"),
      loginBtn: document.getElementById("login-btn"),
      logoutBtn: document.getElementById("logout-btn"),
      userName: document.getElementById("user-name"),
      createPlaylistBtn: document.getElementById("create-playlist-btn"),
      playlistsContainer: document.getElementById("playlists-container"),

      tabs: Array.from(document.querySelectorAll(".nw-auth-tab")),
      panels: Array.from(document.querySelectorAll("[data-auth-panel]")),

      error: document.getElementById("auth-error"),
      success: document.getElementById("auth-success"),

      googleBtn: document.getElementById("google-auth-btn"),

      loginForm: document.getElementById("login-form"),
      registerForm: document.getElementById("register-form"),
      resetForm: document.getElementById("reset-form"),

      loginEmail: document.getElementById("login-email"),
      loginPassword: document.getElementById("login-password"),

      registerName: document.getElementById("register-name"),
      registerEmail: document.getElementById("register-email"),
      registerPassword: document.getElementById("register-password"),
      registerPasswordConfirm: document.getElementById("register-password-confirm"),

      resetEmail: document.getElementById("reset-email"),

      switchers: Array.from(document.querySelectorAll("[data-switch-to]"))
    };

    const clearPlaylistUi = () => {
      if (refs.playlistsContainer) refs.playlistsContainer.innerHTML = "";
      document.querySelectorAll(".playlist-selector-container").forEach((div) => {
        div.innerHTML = "";
        div.style.display = "none";
      });
    };

    const hideMessages = () => {
      if (refs.error) {
        refs.error.hidden = true;
        refs.error.textContent = "";
      }
      if (refs.success) {
        refs.success.hidden = true;
        refs.success.textContent = "";
      }
    };

    const showError = (message) => {
      hideMessages();
      if (refs.error) {
        refs.error.hidden = false;
        refs.error.textContent = message;
      }
    };

    const showSuccess = (message) => {
      hideMessages();
      if (refs.success) {
        refs.success.hidden = false;
        refs.success.textContent = message;
      }
    };

    const setMode = (mode) => {
      refs.tabs.forEach((tab) => {
        tab.classList.toggle("is-active", tab.dataset.mode === mode);
      });

      refs.panels.forEach((panel) => {
        panel.classList.toggle("is-visible", panel.dataset.authPanel === mode);
      });

      hideMessages();

      if (mode === "reset" && refs.loginEmail?.value && refs.resetEmail) {
        refs.resetEmail.value = refs.loginEmail.value.trim();
      }
    };

    const open = (mode = "login") => {
      setMode(mode);
      refs.shell?.classList.add("is-open");
      document.body.classList.add("auth-open");
    };

    const close = () => {
      // Если пользователь не вошёл, не даём закрыть стартовый экран
      if (!auth.currentUser && refs.shell?.classList.contains("force-open")) {
        return;
      }
      refs.shell?.classList.remove("is-open");
      document.body.classList.remove("auth-open");
      hideMessages();
    };

    const syncUser = async (user) => {
      if (!user?.email) return;

      const idToken = await user.getIdToken(true);

      const response = await fetch("/api/auth/sync", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          token: idToken,
          email: user.email,
          username: user.displayName || user.email.split("@")[0]
        })
      });

      if (!response.ok) {
        throw new Error("Сервер не подтвердил пользовательскую сессию.");
      }
    };

    const applyLoggedInUi = async (user) => {
      if (refs.userName) {
        refs.userName.textContent = user.displayName || user.email;
      }

      if (refs.loginBtn) {
        refs.loginBtn.style.setProperty("display", "none", "important");
      }

      if (refs.logoutBtn) {
        refs.logoutBtn.style.setProperty("display", "inline-flex", "important");
      }

      if (refs.createPlaylistBtn) {
        refs.createPlaylistBtn.style.display = "block";
      }

      refs.shell?.classList.remove("force-open");
      refs.shell?.classList.remove("is-open");
      document.body.classList.remove("auth-open");
      hideMessages();

      if (typeof window.loadSidebar === "function") {
        window.loadSidebar();
      }

      if (typeof window.updateAllSelects === "function") {
        window.updateAllSelects();
      }
    };

    const applyLoggedOutUi = () => {
      if (refs.userName) {
        refs.userName.textContent = "";
      }

      if (refs.loginBtn) {
        refs.loginBtn.style.setProperty("display", "inline-flex", "important");
      }

      if (refs.logoutBtn) {
        refs.logoutBtn.style.setProperty("display", "none", "important");
      }

      if (refs.createPlaylistBtn) {
        refs.createPlaylistBtn.style.display = "none";
      }

      clearPlaylistUi();
      refs.shell?.classList.add("force-open");
      open("login");
    };

    refs.tabs.forEach((tab) => {
      tab.addEventListener("click", () => setMode(tab.dataset.mode));
    });

    refs.switchers.forEach((btn) => {
      btn.addEventListener("click", () => setMode(btn.dataset.switchTo));
    });

    refs.closeBtn?.addEventListener("click", close);

    refs.loginBtn?.addEventListener("click", () => open("login"));
    refs.logoutBtn?.addEventListener("click", () => auth.signOut());

    refs.googleBtn?.addEventListener("click", async () => {
      hideMessages();

      try {
        await auth.signInWithPopup(googleProvider);
      } catch (error) {
        if (error?.code === "auth/popup-blocked" || error?.code === "auth/cancelled-popup-request") {
          await auth.signInWithRedirect(googleProvider);
          return;
        }
        showError(friendlyError(error));
      }
    });

    refs.loginForm?.addEventListener("submit", async (e) => {
      e.preventDefault();
      hideMessages();

      const email = refs.loginEmail?.value?.trim();
      const password = refs.loginPassword?.value || "";

      try {
        await auth.signInWithEmailAndPassword(email, password);
      } catch (error) {
        showError(friendlyError(error));
      }
    });

    refs.registerForm?.addEventListener("submit", async (e) => {
      e.preventDefault();
      hideMessages();

      const name = refs.registerName?.value?.trim();
      const email = refs.registerEmail?.value?.trim();
      const password = refs.registerPassword?.value || "";
      const confirmPassword = refs.registerPasswordConfirm?.value || "";

      if (!name) {
        showError("Укажи имя для профиля.");
        return;
      }

      if (password !== confirmPassword) {
        showError("Пароли не совпадают.");
        return;
      }

      try {
        const credential = await auth.createUserWithEmailAndPassword(email, password);

        if (credential.user) {
          await credential.user.updateProfile({ displayName: name });
        }

        showSuccess("Аккаунт создан. Выполняется вход...");
      } catch (error) {
        showError(friendlyError(error));
      }
    });

    refs.resetForm?.addEventListener("submit", async (e) => {
      e.preventDefault();
      hideMessages();

      const email = refs.resetEmail?.value?.trim();

      try {
        await auth.sendPasswordResetEmail(email);
        showSuccess("Письмо для сброса пароля отправлено.");
      } catch (error) {
        showError(friendlyError(error));
      }
    });

    auth.getRedirectResult().catch((error) => {
      showError(friendlyError(error));
    });

    auth.onAuthStateChanged(async (user) => {
      try {
        if (user) {
          await syncUser(user);
          await applyLoggedInUi(user);
        } else {
          applyLoggedOutUi();
        }
      } catch (error) {
        console.error("[Waves Auth]", error);
        if (user) {
          await applyLoggedInUi(user);
          showError("Вход выполнен, но профиль не удалось синхронизировать с NeoWaves.");
        } else {
          applyLoggedOutUi();
        }
      }
    });

    window.handleLogin = () => open("login");
    window.handleLogout = () => auth.signOut();

    window.AuthUI = {
      open,
      close,
      setMode,
      logout: () => auth.signOut()
    };
  });
})();