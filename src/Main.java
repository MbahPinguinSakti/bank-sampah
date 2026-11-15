import java.time.LocalDate;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.stage.Stage;
import controller.PenimbanganController;
import controller.PenukaranController;
import controller.RiwayatController;
import controller.WargaController;
import model.Transaksi;
import model.Warga;
import repository.BankSampahRepository;
import view.MainView;

public class Main extends Application {

    private final BankSampahRepository repo = new BankSampahRepository();

    @Override
    public void start(Stage stage) {
        // isiDataContoh() SUDAH DIHAPUS karena data ditarik otomatis dari Database

        MainView view = new MainView();
        tampilkanData(view);

        // satu objek repo yang sama dioper ke semua controller
        new WargaController(view.getWargaTab(), repo);
        new PenimbanganController(view.getPenimbanganTab(), repo);
        new PenukaranController(view.getPenukaranTab(), repo);
        new RiwayatController(view.getRiwayatTab(), repo);

        Scene scene = new Scene(view.getRoot(), 1280, 820);
        scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());

        stage.setTitle("Bank Sampah");
        stage.setMinWidth(1000);
        stage.setMinHeight(680);
        stage.setScene(scene);
        stage.show();
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