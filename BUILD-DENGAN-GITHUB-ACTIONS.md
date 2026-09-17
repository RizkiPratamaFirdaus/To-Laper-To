# Build APK tanpa Android Studio

Folder `android/` sudah dilengkapi workflow GitHub Actions.

1. Buat repository GitHub baru.
2. Upload seluruh **isi** folder `android/` ke root repository (termasuk folder `.github`).
3. Buka tab **Actions** → **Build To Laper To APK** → **Run workflow**.
4. Setelah job sukses, buka bagian **Artifacts** dan download `To-Laper-To-v4.8-Push-debug`.
5. Di dalam artifact terdapat `To-Laper-To-v4.8-Push-debug.apk`.

Workflow memakai Java 17, Gradle 8.13, dan Android Gradle Plugin 8.11.1. AGP 8.11 mendukung compileSdk/API 36.

## Catatan

APK debug cocok untuk instalasi internal/testing. Untuk distribusi produksi atau Play Store, buat signing keystore release dan build varian release.
