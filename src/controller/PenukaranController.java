package controller;

import javafx.collections.FXCollections;
import model.SaldoTidakCukupException;
import model.Warga;
import repository.BankSampahRepository;
import view.PenukaranTab;
import view.Ui;



public class PenukaranController {

    private final PenukaranTab view;
    private final BankSampahRepository repo;

    public PenukaranController(PenukaranTab view, BankSampahRepository repo) {
        this.view = view;
        this.repo = repo;
        view.getBtnTukar().setOnAction(e -> tukar());

        // ganti warga -> saldo & riwayat ikut berubah; ketik jumlah -> ringkasan ikut berubah
        view.getCbWarga().valueProperty().addListener((obs, lama, baru) -> tampilkanWarga());
        view.getTfJumlah().textProperty().addListener((obs, lama, baru) -> hitungRingkasan());
    }

    private void tukar() {
        Warga warga = view.getCbWarga().getValue();
        if (warga == null) {
            Ui.error("Pilih warga yang akan menukar poin");
            return;
        }

        try {
            int jumlah = Integer.parseInt(view.getTfJumlah().getText().trim());
            if (!Ui.konfirmasi("Tukar " + jumlah + " poin milik " + warga.getNama() + "?")) {
                return; // user pilih batal
            }
            repo.tukar(warga.getIdWarga(), jumlah);
            Ui.info("Penukaran berhasil, sisa saldo " + warga.getSaldoPoin() + " poin");
            view.getTfJumlah().clear();
            tampilkanWarga();
        } catch (NumberFormatException ex) {
            Ui.error("Jumlah poin harus berupa angka bulat");
        } catch (SaldoTidakCukupException | IllegalArgumentException ex) {
            Ui.error(ex.getMessage());
        }
    }

    private void tampilkanWarga() {
        Warga warga = view.getCbWarga().getValue();
        if (warga == null) {
            view.getLblSaldo().setText("0");
            view.getLblNamaWarga().setText("Belum ada warga dipilih");
            view.getTabelRiwayat().getItems().clear();
        } else {
            view.getLblSaldo().setText(String.valueOf(warga.getSaldoPoin()));
            view.getLblNamaWarga().setText(warga.getIdWarga() + " · " + warga.getNama());
            view.getTabelRiwayat().setItems(FXCollections.observableArrayList(
                    repo.transaksiWarga(warga.getIdWarga()).reversed()));
        }
        hitungRingkasan();
    }

    private void hitungRingkasan() {
        Warga warga = view.getCbWarga().getValue();
        int saldo = warga == null ? 0 : warga.getSaldoPoin();

        int jumlah;
        try {
            jumlah = Integer.parseInt(view.getTfJumlah().getText().trim());
        } catch (NumberFormatException ex) {
            jumlah = 0; // masih kosong / bukan angka, anggap 0 dulu
        }

        view.getLblRingkasSaldo().setText(String.valueOf(saldo));
        view.getLblRingkasTukar().setText("−" + jumlah);
        view.getLblSisa().setText((saldo - jumlah) + " poin");
    }
}
