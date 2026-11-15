package model;

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

    /** Tarif dasar poin per kg untuk jenis sampah ini */
    public abstract int getPoinPerKg();

    /** Konversi berat sampah menjadi poin;  */
    public abstract int hitungPoin();

    @Override
    public String toString() {
        return String.format("%s %.2f kg = %d poin", nama, berat, hitungPoin());
    }

    /** Polimorfisme: yang dikembalikan selalu bertipe Sampah, isinya Plastik/Kertas/Logam. */
    public static Sampah buatSampah(String jenis, double berat) {
        return switch (jenis) {
            case "Plastik" -> new Plastik(berat);
            case "Kertas" -> new Kertas(berat);
            case "Logam" -> new Logam(berat);
            default -> throw new IllegalArgumentException("Jenis sampah tidak dikenal: " + jenis);
        };
    }
}
