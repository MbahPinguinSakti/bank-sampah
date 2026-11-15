package controller;

import java.util.List;
import javafx.collections.FXCollections;
import model.Warga;
import view.WargaTab;
import view.Ui;
import repository.BankSampahRepository;



public class WargaController {

    private final WargaTab view;
    private final BankSampahRepository repo;
    private final Runnable onDataBerubah; // dipanggil setelah data berubah supaya semua tab ikut di-refresh

    public WargaController(WargaTab view, BankSampahRepository repo, Runnable onDataBerubah) {
        this.view = view;
        this.repo = repo;
        this.onDataBerubah = onDataBerubah;
        view.getBtnTambah().setOnAction(e -> tambah());
        view.getBtnHapus().setOnAction(e -> hapus());
        view.getBtnUbah().setOnAction(e -> ubah());

    }

    private void tambah() {
        String id = view.getTfId().getText().trim();
        String nama = view.getTfNama().getText().trim();
        String alamat = view.getTaAlamat().getText().trim();

        try {
            Warga warga = new Warga(id, nama, alamat);
            repo.tambahWarga(warga);
            Ui.info("warga berhasil ditambahkan");
            view.getTfId().clear();
            view.getTfNama().clear();
            view.getTaAlamat().clear();
            onDataBerubah.run();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Ui.error(ex.getMessage());
        }
    }

    private void hapus(){
        Warga warga = view.getTabel().getSelectionModel().getSelectedItem();
        if (warga == null) {
            Ui.error("Pilih warga yang akan dihapus");
            return;
        }
        try{
            if (!Ui.konfirmasi("yakin hapus warga " + warga.getNama())) {
                return; // user pilih batal
            }
            repo.hapusWarga(warga.getIdWarga());
            onDataBerubah.run();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Ui.error(ex.getMessage());
        }

    }

    private void ubah() {
        Warga warga = view.getTabel().getSelectionModel().getSelectedItem();
        if (warga == null) {
            Ui.error("Pilih warga yang akan diubah");
            return;
        }
        String namaBaru = view.getTfNama().getText().trim();
        String alamatBaru = view.getTaAlamat().getText().trim();
        try {
            repo.ubahWarga(warga.getIdWarga(), namaBaru, alamatBaru);
            Ui.info("Data warga berhasil diubah");
            onDataBerubah.run();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Ui.error(ex.getMessage());
        }
    }

    /** Isi ulang tabel warga dari repository. Dipanggil lewat Main.refreshSemua(). */
    public void refresh() {
        List<Warga> semua = repo.semuaWarga();
        view.getTabel().setItems(FXCollections.observableArrayList(semua));
    }
}
