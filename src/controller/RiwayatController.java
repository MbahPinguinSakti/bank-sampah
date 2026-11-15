package controller;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.scene.control.Toggle;
import model.Transaksi;
import model.Warga;
import repository.BankSampahRepository;
import view.RiwayatTab;



public class RiwayatController {

    private final RiwayatTab view;
    private final BankSampahRepository repo;

    public RiwayatController(RiwayatTab view, BankSampahRepository repo) {
        this.view = view;
        this.repo = repo;

        // tab ini tidak punya tombol: tabel langsung difilter ulang tiap filter berubah
        view.getCbFilterWarga().valueProperty().addListener((obs, lama, baru) -> tampilkan());
        view.getGrupTipe().selectedToggleProperty().addListener((obs, lama, baru) -> tampilkan());
    }

    /** Isi ulang ComboBox filter, lalu tabel sesuai filter yang aktif. Dipanggil lewat Main.refreshSemua(). */
    public void refresh() {
        Warga dipilih = view.getCbFilterWarga().getValue();
        List<Warga> semua = repo.semuaWarga();
        view.getCbFilterWarga().setItems(FXCollections.observableArrayList(semua));
        view.getCbFilterWarga().setValue(semua.contains(dipilih) ? dipilih : null); // filter user jangan hilang
        tampilkan();
    }

    private void tampilkan() {
        Warga warga = view.getCbFilterWarga().getValue();
        List<Transaksi> data = warga == null
                ? repo.semuaTransaksi()
                : repo.transaksiWarga(warga.getIdWarga());

        // tidak ada chip aktif (chip aktif diklik lagi) dianggap sama dengan "Semua"
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
}
