package model;

/** Dilempar saat warga menukar poin melebihi saldonya. */
public class SaldoTidakCukupException extends Exception {

    public SaldoTidakCukupException(int saldo, int diminta) {
        super("Saldo tidak cukup. Saldo: " + saldo + " poin, diminta: " + diminta + " poin");
    }
}
