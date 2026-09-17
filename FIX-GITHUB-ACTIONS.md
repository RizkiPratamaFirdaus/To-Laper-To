# Fix build GitHub Actions — To, Laper To v5.8

Workflow sudah diperbarui agar tidak memakai action lama berbasis Node.js 20:

- `actions/checkout@v7`
- `actions/setup-java@v6`
- `android-actions/setup-android@v4`
- `gradle/actions/setup-gradle@v4`
- `actions/upload-artifact@v4`

Konfigurasi build Android tetap memakai kombinasi yang kompatibel:

- JDK 17
- Android Gradle Plugin 8.11.1
- Gradle 8.13
- compileSdk / targetSdk 36
- Android Build Tools 35.0.0

Firebase pada APK To, Laper To diinisialisasi saat runtime dari konfigurasi website, jadi plugin Gradle `com.google.gms.google-services` tidak diperlukan untuk proses compile dan dihapus dari root `build.gradle` untuk mengurangi dependency build yang tidak perlu.

## Cara menjalankan

1. Ganti isi repository GitHub dengan source pada ZIP ini, atau minimal ganti `.github/workflows/build-apk.yml` dan `build.gradle`.
2. Commit/push ke branch `main`.
3. Buka **Actions → Build To Laper To Android APK → Run workflow**.
4. Setelah job hijau, buka bagian **Artifacts** di bawah run.
5. Download `To-Laper-To-v5.8-Android` lalu ekstrak; di dalamnya ada `To-Laper-To-v5.8-Android.apk`.

Catatan: warning Node.js 20 yang sebelumnya muncul berasal dari action lama. Warning itu bukan error compile Android, tetapi workflow baru sudah menghilangkannya.
