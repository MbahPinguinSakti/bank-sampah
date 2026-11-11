package model;

import java.time.LocalDateTime;

/**
 * Satu catatan riwayat: setoran sampah (SETOR) atau penukaran poin (TUKAR).
 * Objek dibuat lewat factory method setor() / tukar() supaya datanya selalu konsisten.
 */
public class Transaksi {

    public enum Tipe { SETOR, TUKAR }

    private int id; // diisi repository (P10) atau AUTO_INCREMENT database (P13)
    private final String idWarga;
    private final Tipe tipe;
    private final Sampah sampah; // null untuk TUKAR
    private final int poin;
    private final LocalDateTime tanggal;

    private Transaksi(String idWarga, Tipe tipe, Sampah sampah, int poin, LocalDateTime tanggal) {
        this.idWarga = idWarga;
        this.tipe = tipe;
        this.sampah = sampah;
        this.poin = poin;
        this.tanggal = tanggal;
    }

    /** Poin dihitung lewat polimorfisme: hitungPoin() milik Plastik/Kertas/Logam. */
    public static Transaksi setor(String idWarga, Sampah sampah) {
        return new Transaksi(idWarga, Tipe.SETOR, sampah, sampah.hitungPoin(), LocalDateTime.now());
    }

    public static Transaksi tukar(String idWarga, int poin) {
        return new Transaksi(idWarga, Tipe.TUKAR, null, poin, LocalDateTime.now());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdWarga() {
        return idWarga;
    }

    public Tipe getTipe() {
        return tipe;
    }

    public Sampah getSampah() {
        return sampah;
    }

    public String getJenisSampah() {
        return sampah == null ? "-" : sampah.getNama();
    }

    public double getBerat() {
        return sampah == null ? 0 : sampah.getBerat();
    }

    public int getPoin() {
        return poin;
    }

    public LocalDateTime getTanggal() {
        return tanggal;
    }

    @Override
    public String toString() {
        return String.format("#%d [%s] %s %s %.2f kg %s%d poin",
                id, tipe, idWarga, getJenisSampah(), getBerat(),
                tipe == Tipe.SETOR ? "+" : "-", poin);
    }
}
