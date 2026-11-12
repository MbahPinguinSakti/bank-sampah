# bank-sampah
Bank sampah management system with java

Analisis Penggunaan Struktur Data (Collection Framework):

Data Warga (HashMap<String, Warga>): Penggunaan HashMap dengan ID Warga sebagai key memungkinkan pencarian data warga dengan kompleksitas waktu O(1). Hal ini sangat efisien dibandingkan ArrayList yang membutuhkan waktu O(n) saat mencari warga pada proses penimbangan dan penukaran poin. Sifat Map yang tidak menerima key ganda juga secara otomatis menjamin ID warga selalu unik.   

Riwayat Transaksi (ArrayList<Transaksi>): Riwayat transaksi hanya memerlukan operasi penambahan data di akhir (append) secara kronologis, sehingga ArrayList sangat ideal. Pencarian riwayat warga spesifik cukup dilakukan menggunakan fungsi .stream().filter(). ArrayList juga didukung secara native oleh FXCollections.observableArrayList() untuk kebutuhan GUI JavaFX (Tabel) di fase selanjutnya karena memiliki akses indeks O(1).   

Jenis Sampah (List<String>): List statis berisi "Plastik", "Kertas", dan "Logam" digunakan karena kebutuhan datanya yang tetap (tidak berubah) dan akan langsung di-binding (dihubungkan) ke komponen ComboBox pada layar penimbangan di fase GUI.