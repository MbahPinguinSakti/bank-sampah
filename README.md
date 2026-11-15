# bank-sampah
Bank sampah management system with java

## Setup

1. **JavaFX SDK 26.0.1** (beda per OS, tidak disimpan di repo). Download dari https://gluonhq.com/products/javafx/ sesuai OS, lalu ekstrak sehingga file jar-nya ada di `bank_sampah/javafx/lib/`:
   ```
   bank_sampah/javafx/lib/javafx.controls.jar
   bank_sampah/javafx/lib/javafx.graphics.jar
   ...  (+ file .dll di Windows / .so di Linux)
   ```
   Caranya: ekstrak zip SDK, lalu ganti nama folder `javafx-sdk-26.0.1` menjadi `javafx` dan taruh di dalam `bank_sampah/`.
2. **Database:** jalankan `sql/bank_sampah.sql` sebagai admin MariaDB/MySQL (Linux: `sudo mariadb < sql/bank_sampah.sql`).
3. **Jalankan aplikasi:** `./run.sh` (Linux/macOS) atau `run.bat` (Windows). Bisa juga lewat tombol Run di VS Code dengan membuka folder `bank_sampah/`.

Folder `lib/` hanya berisi jar Java murni yang jalan di semua OS (MySQL Connector/J, gson, itextpdf).

Analisis Penggunaan Struktur Data (Collection Framework):

Data Warga (HashMap<String, Warga>): Penggunaan HashMap dengan ID Warga sebagai key memungkinkan pencarian data warga dengan kompleksitas waktu O(1). Hal ini sangat efisien dibandingkan ArrayList yang membutuhkan waktu O(n) saat mencari warga pada proses penimbangan dan penukaran poin. Sifat Map yang tidak menerima key ganda juga secara otomatis menjamin ID warga selalu unik.   

Riwayat Transaksi (ArrayList<Transaksi>): Riwayat transaksi hanya memerlukan operasi penambahan data di akhir (append) secara kronologis, sehingga ArrayList sangat ideal. Pencarian riwayat warga spesifik cukup dilakukan menggunakan fungsi .stream().filter(). ArrayList juga didukung secara native oleh FXCollections.observableArrayList() untuk kebutuhan GUI JavaFX (Tabel) di fase selanjutnya karena memiliki akses indeks O(1).   

Jenis Sampah (List<String>): List statis berisi "Plastik", "Kertas", dan "Logam" digunakan karena kebutuhan datanya yang tetap (tidak berubah) dan akan langsung di-binding (dihubungkan) ke komponen ComboBox pada layar penimbangan di fase GUI.