import java.time.LocalDate;
import java.util.List;

import javafx.application.Application;

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


    private MainView view = new MainView();
    private WargaController wargaController;
    private PenimbanganController penimbanganController;
    private PenukaranController penukaranController;
    private RiwayatController riwayatController;

    private final BankSampahRepository repo = new BankSampahRepository();

    @Override
    public void start(Stage stage) {
        wargaController = new WargaController(view.getWargaTab(), repo, this::refreshSemua);
        penimbanganController = new PenimbanganController(view.getPenimbanganTab(), repo, this::refreshSemua);
        penukaranController = new PenukaranController(view.getPenukaranTab(), repo, this::refreshSemua);
        riwayatController = new RiwayatController(view.getRiwayatTab(), repo);
        
        refreshSemua();

        Scene scene = new Scene(view.getRoot(), 1280, 820);
        scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());

        stage.setTitle("Bank Sampah");
        stage.setMinWidth(1000);
        stage.setMinHeight(680);
        stage.setScene(scene);
        stage.show();
    }

    private void refreshSemua(){
        List<Warga> warga = repo.semuaWarga();
        List<Transaksi> transaksi = repo.semuaTransaksi();

        // header
        view.getLblJumlahWarga().setText(String.valueOf(warga.size()));
        view.getLblTotalPoin().setText(String.valueOf(warga.stream().mapToInt(Warga::getSaldoPoin).sum()));
        double kgHariIni = transaksi.stream()
                .filter(t -> t.getTipe() == Transaksi.Tipe.SETOR)
                .filter(t -> t.getTanggal().toLocalDate().equals(LocalDate.now()))
                .mapToDouble(Transaksi::getBerat).sum();
        view.getLblSetoranHariIni().setText(String.format("%.1f kg", kgHariIni));

        // isi ulang semua tab
        wargaController.refresh();
        penimbanganController.refresh();
        penukaranController.refresh();
        riwayatController.refresh();
    }

    public static void main(String[] args) {
        launch(args);
    }
}