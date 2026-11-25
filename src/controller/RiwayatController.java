package controller;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import model.Transaksi;
import model.Warga;
import repository.BankSampahRepository;
import util.ResiPdfGenerator;
import view.RiwayatTab;
import view.Ui;

public class RiwayatController {

    private final RiwayatTab view;
    private final BankSampahRepository repo;
    private Warga wargaTerkunci = null;

    public RiwayatController(RiwayatTab view, BankSampahRepository repo) {
        this.view = view;
        this.repo = repo;

        // Buat tombol Cetak Resi PDF yang jelas & langsung menempel di bawah tabel riwayat
        Button btnCetakPdf = new Button("Cetak Resi PDF");
        btnCetakPdf.getStyleClass().add("btn-primary");
        btnCetakPdf.setOnAction(e -> cetakResiTerpilih());

        HBox actionBar = new HBox(btnCetakPdf);
        actionBar.setAlignment(Pos.CENTER_RIGHT);

        // Sisipkan actionBar tepat setelah tabel di dalam parent container-nya
        Node parentTabel = view.getTabel().getParent();
        if (parentTabel instanceof Pane pane) {
            int idx = pane.getChildren().indexOf(view.getTabel());
            if (idx >= 0) {
                pane.getChildren().add(idx + 1, actionBar);
            } else {
                pane.getChildren().add(actionBar);
            }
        }

        // Listener filter
        view.getCbFilterWarga().valueProperty().addListener((obs, lama, baru) -> tampilkan());
        view.getGrupTipe().selectedToggleProperty().addListener((obs, lama, baru) -> tampilkan());
    }

    public void setWargaLogin(Warga warga) {
        this.wargaTerkunci = warga;
        if (warga != null) {
            view.getCbFilterWarga().setItems(FXCollections.observableArrayList(warga));
            view.getCbFilterWarga().setValue(warga);
            view.getCbFilterWarga().setDisable(true);
        } else {
            view.getCbFilterWarga().setDisable(false);
        }
        tampilkan();
    }

    public void refresh() {
        if (wargaTerkunci != null) {
            setWargaLogin(wargaTerkunci);
            return;
        }

        Warga dipilih = view.getCbFilterWarga().getValue();
        List<Warga> semua = repo.semuaWarga();
        view.getCbFilterWarga().setItems(FXCollections.observableArrayList(semua));
        view.getCbFilterWarga().setValue(semua.contains(dipilih) ? dipilih : null);
        tampilkan();
    }

    private void tampilkan() {
        Warga warga = (wargaTerkunci != null) ? wargaTerkunci : view.getCbFilterWarga().getValue();
        List<Transaksi> data = warga == null
                ? repo.semuaTransaksi()
                : repo.transaksiWarga(warga.getIdWarga());

        Toggle dipilih = view.getGrupTipe().getSelectedToggle();
        if (dipilih == view.getTgSetor()) {
            data = data.stream().filter(t -> t.getTipe() == Transaksi.Tipe.SETOR).toList();
        } else if (dipilih == view.getTgTukar()) {
            data = data.stream().filter(t -> t.getTipe() == Transaksi.Tipe.TUKAR).toList();
        }

        view.getTabel().setItems(FXCollections.observableArrayList(data.reversed()));

        int masuk = data.stream().filter(t -> t.getTipe() == Transaksi.Tipe.SETOR).mapToInt(Transaksi::getPoin).sum();
        int keluar = data.stream().filter(t -> t.getTipe() == Transaksi.Tipe.TUKAR).mapToInt(Transaksi::getPoin).sum();
        view.getLblJumlah().setText(String.valueOf(data.size()));
        view.getLblPoinMasuk().setText("+" + masuk);
        view.getLblPoinKeluar().setText("−" + keluar);
    }

    private void cetakResiTerpilih() {
        Transaksi terpilih = view.getTabel().getSelectionModel().getSelectedItem();
        if (terpilih == null) {
            // Jika user belum klik baris, otomatis ambil transaksi paling atas (terbaru)
            if (!view.getTabel().getItems().isEmpty()) {
                terpilih = view.getTabel().getItems().get(0);
            } else {
                Ui.error("Belum ada transaksi yang bisa dicetak.");
                return;
            }
        }

        final Transaksi target = terpilih;
        Warga warga = repo.semuaWarga().stream()
                .filter(w -> w.getIdWarga().equals(target.getIdWarga()))
                .findFirst()
                .orElse(new Warga(target.getIdWarga(), "Warga", "-", 0));

        try {
            ResiPdfGenerator.cetakResi(target, warga);
            Ui.info("Resi PDF untuk transaksi #" + target.getId() + " (" + warga.getNama() + ") berhasil dicetak!");
        } catch (Exception ex) {
            Ui.error("Terjadi kesalahan saat membuat PDF: " + ex.getMessage());
        }
    }
}