package model;

/**
 * Parent class untuk semua jenis sampah.
 * - Abstraksi  : class abstract, tidak bisa dibuat objeknya langsung.
 * - Enkapsulasi: field private, hanya bisa dibaca lewat getter.
 * - Polimorfisme: tiap subclass wajib meng-override hitungPoin().
 */
public abstract class Sampah {

    private final String nama;
    private final double berat; // dalam kg

    protected Sampah(String nama, double berat) {
        if (berat <= 0) {
            throw new IllegalArgumentException("Berat harus lebih dari 0 kg");
        }
        this.nama = nama;
        this.berat = berat;
    }

    public String getNama() {
        return nama;
    }

    public double getBerat() {
        return berat;
    }

    /** Tarif dasar poin per kg untuk jenis sampah ini. */
    public abstract int getPoinPerKg();

    /** Konversi berat sampah menjadi poin; aturannya berbeda per jenis. */
    public abstract int hitungPoin();

    @Override
    public String toString() {
        return String.format("%s %.2f kg = %d poin", nama, berat, hitungPoin());
    }
}
