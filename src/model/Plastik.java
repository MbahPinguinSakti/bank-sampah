package model;

public class Plastik extends Sampah {

    public static final int POIN_PER_KG = 10;

    public Plastik(double berat) {
        super("Plastik", berat);
    }

    @Override
    public int getPoinPerKg() {
        return POIN_PER_KG;
    }

    @Override
    public int hitungPoin() {
        return (int) Math.round(getBerat() * POIN_PER_KG);
    }
}
