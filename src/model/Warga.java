package model;

/**
 * Nasabah bank sampah.
 * Enkapsulasi: saldoPoin tidak punya setter, hanya berubah lewat
 * tambahPoin() dan kurangiPoin() yang memvalidasi nilainya.
 */
public class Warga {

    private final String idWarga;
    private String nama;
    private String alamat;
    private int saldoPoin;

    /** Untuk warga baru, saldo mulai dari 0. */
    public Warga(String idWarga, String nama, String alamat) {
        this(idWarga, nama, alamat, 0);
    }

    /** Untuk memuat warga yang sudah ada (misal dari database di P13). */
    public Warga(String idWarga, String nama, String alamat, int saldoPoin) {
        if (idWarga == null || idWarga.isBlank()) {
            throw new IllegalArgumentException("ID warga tidak boleh kosong");
        }
        if (saldoPoin < 0) {
            throw new IllegalArgumentException("Saldo tidak boleh negatif");
        }
        this.idWarga = idWarga;
        setNama(nama);
        this.alamat = alamat;
        this.saldoPoin = saldoPoin;
    }

    public String getIdWarga() {
        return idWarga;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        if (nama == null || nama.isBlank()) {
            throw new IllegalArgumentException("Nama warga tidak boleh kosong");
        }
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public int getSaldoPoin() {
        return saldoPoin;
    }

    public void tambahPoin(int poin) {
        if (poin <= 0) {
            throw new IllegalArgumentException("Poin yang ditambahkan harus lebih dari 0");
        }
        saldoPoin += poin;
    }

    public void kurangiPoin(int poin) throws SaldoTidakCukupException {
        if (poin <= 0) {
            throw new IllegalArgumentException("Poin yang ditukar harus lebih dari 0");
        }
        if (poin > saldoPoin) {
            throw new SaldoTidakCukupException(saldoPoin, poin);
        }
        saldoPoin -= poin;
    }

    /** Dipakai JComboBox di GUI supaya yang tampil "ID - Nama". */
    @Override
    public String toString() {
        return idWarga + " - " + nama;
    }
}
