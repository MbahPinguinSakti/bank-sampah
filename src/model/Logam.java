package model;

public class Logam extends Sampah {

    public static final int POIN_PER_KG = 20;
    public static final double BATAS_BONUS_KG = 5.0;
    public static final int BONUS_POIN = 50;

    public Logam(double berat) {
        super("Logam", berat);
    }

    @Override
    public int getPoinPerKg() {
        return POIN_PER_KG;
    }

    /** Logam punya aturan berbeda: dapat bonus kalau setoran minimal 5 kg. */
    @Override
    public int hitungPoin() {
        int poin = (int) Math.round(getBerat() * POIN_PER_KG);
        if (getBerat() >= BATAS_BONUS_KG) {
            poin += BONUS_POIN;
        }
        return poin;
    }
}
