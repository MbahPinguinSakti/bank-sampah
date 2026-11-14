import java.time.LocalDate;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Kertas;
import model.Logam;
import model.Plastik;
import model.Transaksi;
import model.Warga;
import repository.BankSampahRepository;
import view.MainView;

public class Main extends Application {

    private final BankSampahRepository repo = new BankSampahRepository();

    @Override
    public void start(Stage stage) {
        isiDataContoh();

        MainView view = new MainView();
        tampilkanData(view);

        Scene scene = new Scene(view.getRoot(), 1280, 820);
        scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());

        stage.setTitle("Bank Sampah");
        stage.setMinWidth(1000);
        stage.setMinHeight(680);
        stage.setScene(scene);
        stage.show();
    }

    /** Data awal supaya tabel tidak kosong saat diambil screenshot (P11). */
    private void isiDataContoh() {
        repo.tambahWarga(new Warga("W001", "Budi Santoso", "Jl. Melati No. 12"));
        repo.tambahWarga(new Warga("W002", "Siti Aminah", "Jl. Kenanga No. 4"));
        repo.tambahWarga(new Warga("W003", "Agus Prasetyo", "Jl. Mawar No. 7"));
        repo.tambahWarga(new Warga("W004", "Dewi Lestari", "Jl. Anggrek No. 21"));

        repo.setor("W001", new Plastik(2.0));
        repo.setor("W003", new Logam(5.0));
        repo.setor("W002", new Kertas(4.0));
        try {
            repo.tukar("W003", 100);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Sementara (P11): isi komponen view dengan data dari repository.
     * Di P12 bagian ini dipindah ke controller dan dipanggil ulang setiap ada perubahan.
     */
    private void tampilkanData(MainView view) {
        List<Warga> warga = repo.semuaWarga();
        List<Transaksi> transaksi = repo.semuaTransaksi();
        List<Transaksi> terbaruDulu = transaksi.reversed();

        // header
        view.getLblJumlahWarga().setText(String.valueOf(warga.size()));
        view.getLblTotalPoin().setText(String.valueOf(warga.stream().mapToInt(Warga::getSaldoPoin).sum()));
        double kgHariIni = transaksi.stream()
                .filter(t -> t.getTipe() == Transaksi.Tipe.SETOR)
                .filter(t -> t.getTanggal().toLocalDate().equals(LocalDate.now()))
                .mapToDouble(Transaksi::getBerat).sum();
        view.getLblSetoranHariIni().setText(String.format("%.1f kg", kgHariIni));

        // tab Data Warga
        view.getWargaTab().getTabel().setItems(FXCollections.observableArrayList(warga));

        // tab Penimbangan
        view.getPenimbanganTab().getCbWarga().setItems(FXCollections.observableArrayList(warga));
        view.getPenimbanganTab().getCbJenis().setItems(FXCollections.observableArrayList(repo.getJenisSampah()));
        view.getPenimbanganTab().getTabelHariIni().setItems(FXCollections.observableArrayList(
                terbaruDulu.stream().filter(t -> t.getTipe() == Transaksi.Tipe.SETOR).toList()));

        // tab Penukaran
        view.getPenukaranTab().getCbWarga().setItems(FXCollections.observableArrayList(warga));

        // tab Riwayat
        view.getRiwayatTab().getCbFilterWarga().setItems(FXCollections.observableArrayList(warga));
        view.getRiwayatTab().getTabel().setItems(FXCollections.observableArrayList(terbaruDulu));
        view.getRiwayatTab().getLblJumlah().setText(String.valueOf(transaksi.size()));
        int masuk = transaksi.stream().filter(t -> t.getTipe() == Transaksi.Tipe.SETOR).mapToInt(Transaksi::getPoin).sum();
        int keluar = transaksi.stream().filter(t -> t.getTipe() == Transaksi.Tipe.TUKAR).mapToInt(Transaksi::getPoin).sum();
        view.getRiwayatTab().getLblPoinMasuk().setText("+" + masuk);
        view.getRiwayatTab().getLblPoinKeluar().setText("−" + keluar);
    }

    public static void main(String[] args) {
        launch(args);
    }
}