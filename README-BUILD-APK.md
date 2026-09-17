# To, Laper To v5.8 — APK Android

Project ini membungkus website HTTPS To, Laper To dalam WebView native Android dengan dukungan Firebase Cloud Messaging.

## Perilaku pertama kali
Saat pertama dibuka, APK meminta URL HTTPS website To, Laper To (contoh `https://nama-site.netlify.app`). URL disimpan di perangkat.

## Build dengan Android Studio
Buka folder ini sebagai project, tunggu Gradle sync, lalu Build > Build APK(s).

## Build gratis dengan GitHub Actions
1. Upload seluruh isi folder ini ke repository GitHub.
2. Buka tab **Actions**.
3. Pilih **Build To Laper To Android APK**.
4. Run workflow.
5. Download artifact `To-Laper-To-v5.8-Android`.

APK yang dihasilkan adalah debug APK yang sudah signed dengan debug key dan bisa dipasang langsung untuk penggunaan internal. Untuk distribusi Play Store, buat release signing key milik organisasi.

## Smart Realtime v5.8
APK ini membuka website HTTPS yang sama. Setelah website Netlify di-upgrade ke v5.8, perilaku Smart Realtime (tidak menghapus form saat event masuk), tombol lonceng ringkas, dan floating Cart otomatis ikut aktif tanpa perubahan database tambahan.
