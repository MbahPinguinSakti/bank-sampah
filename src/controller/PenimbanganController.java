package controller;

import java.time.LocalDate;
import java.util.List;

import javafx.collections.FXCollections;
import model.Kertas;
import model.Logam;
import model.Plastik;
import model.Sampah;
import model.Transaksi;
import model.Warga;
import repository.BankSampahRepository;
import view.PenimbanganTab;
import view.Ui;



public class PenimbanganController {

    private final PenimbanganTab view;
    private final BankSampahRepository repo;

    public PenimbanganController(PenimbanganTab view, BankSampahRepository repo) {
        this.view = view;
        this.repo = repo;
        view.getBtnSimpan().setOnAction(e -> simpan());

        // perkiraan poin dihitung ulang tiap kali jenis atau berat berubah
        view.getCbJenis().valueProperty().addListener((obs, lama, baru) -> hitungPerkiraan());
        view.getTfBerat().textProperty().addListener((obs, lama, baru) -> hitungPerkiraan());
    }

    private void simpan() {
        Warga warga = view.getCbWarga().getValue();
        if (warga == null) {
            Ui.error("Pilih warga yang menyetor");
            return;
        }
        String jenis = view.getCbJenis().getValue();
        if (jenis == null) {
            Ui.error("Pilih jenis sampah");
            return;
        }

        try {
            double berat = bacaBerat();
            Sampah sampah = buatSampah(jenis, berat);
            repo.setor(warga.getIdWarga(), sampah);
            Ui.info("Setoran berhasil, " + warga.getNama() + " mendapat " + sampah.hitungPoin() + " poin");
            view.getCbWarga().setValue(null);
            view.getCbJenis().setValue(null);
            view.getTfBerat().clear();
            tampilkanSetoranHariIni();
        } catch (NumberFormatException ex) {
            Ui.error("Berat harus berupa angka, contoh: 2,5");
        } catch (IllegalArgumentException ex) {
            Ui.error(ex.getMessage());
        }
    }

    private void hitungPerkiraan() {
        String jenis = view.getCbJenis().getValue();
        if (jenis == null || view.getTfBerat().getText().isBlank()) {
            view.getLblRumus().setText("Isi berat untuk melihat perkiraan");
            view.getLblPerkiraan().setText("0");
            return;
        }

        try {
            Sampah sampah = buatSampah(jenis, bacaBerat());
            view.getLblRumus().setText(sampah.getBerat() + " kg × " + sampah.getPoinPerKg() + " poin/kg");
            view.getLblPerkiraan().setText(String.valueOf(sampah.hitungPoin()));
        } catch (IllegalArgumentException ex) {
            // NumberFormatException juga turunan IllegalArgumentException, jadi ikut tertangkap di sini
            view.getLblRumus().setText("Berat tidak valid");
            view.getLblPerkiraan().setText("0");
        }
    }

    /** Terima koma maupun titik sebagai pemisah desimal. */
    private double bacaBerat() {
        return Double.parseDouble(view.getTfBerat().getText().trim().replace(",", "."));
    }

    /** Polimorfisme: yang dikembalikan selalu bertipe Sampah, isinya Plastik/Kertas/Logam. */
    private Sampah buatSampah(String jenis, double berat) {
        return switch (jenis) {
            case "Plastik" -> new Plastik(berat);
            case "Kertas" -> new Kertas(berat);
            case "Logam" -> new Logam(berat);
            default -> throw new IllegalArgumentException("Jenis sampah tidak dikenal: " + jenis);
        };
    }

    private void tampilkanSetoranHariIni() {
        List<Transaksi> hariIni = repo.semuaTransaksi().reversed().stream()
                .filter(t -> t.getTipe() == Transaksi.Tipe.SETOR)
                .filter(t -> t.getTanggal().toLocalDate().equals(LocalDate.now()))
                .toList();
        view.getTabelHariIni().setItems(FXCollections.observableArrayList(hariIni));
    }
}
