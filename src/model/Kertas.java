package model;

public class Kertas extends Sampah {

    public static final int POIN_PER_KG = 5;

    public Kertas(double berat) {
        super("Kertas", berat);
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
