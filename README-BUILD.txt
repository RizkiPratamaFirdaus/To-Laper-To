To, Laper To Android wrapper + FCM push
=======================================
Package: id.to.laperto.app
Compile SDK: 36
Android Gradle Plugin: 8.11.1
Gradle: 8.13
Java: 17
Firebase BoM: 34.19.0

APK memakai konfigurasi Firebase runtime dari website push-config.js.
Konfigurasi tersebut disimpan di SharedPreferences native dan diinisialisasi lagi oleh ToLaperApplication saat process Android dibangunkan, sehingga FCM tetap dapat bekerja setelah app di-swipe/ditutup.

google-services.json tidak dibutuhkan untuk build project ini.

BUILD PALING MUDAH:
Gunakan .github/workflows/build-apk.yml dan ikuti BUILD-DENGAN-GITHUB-ACTIONS.md.

Jika build lokal dan Gradle 8.13 sudah terpasang:
  gradle :app:assembleDebug
Output:
  app/build/outputs/apk/debug/app-debug.apk

Build release memerlukan signing keystore sendiri sebelum distribusi luas/Play Store.


FINAL FIREBASE CONFIG:
- google-services.json sudah ada di app/google-services.json
- package id.to.laperto.app sudah cocok
- tidak perlu mengisi Firebase Android client lagi.
