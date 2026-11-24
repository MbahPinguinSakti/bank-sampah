import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import controller.PenimbanganController;
import controller.PenukaranController;
import controller.RiwayatController;
import controller.WargaController;
import model.Transaksi;
import model.Warga;
import repository.BankSampahRepository;
import view.LoginView;
import view.MainView;
import view.UiAnimator;

public class Main extends Application {

    private MainView view;
    private WargaController wargaController;
    private PenimbanganController penimbanganController;
    private PenukaranController penukaranController;
    private RiwayatController riwayatController;

    private final BankSampahRepository repo = new BankSampahRepository();
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Bank Sampah");
        stage.setMinWidth(1000);
        stage.setMinHeight(680);

        tampilkanLogin();
        stage.show();
    }

    private void tampilkanLogin() {
        LoginView loginView = new LoginView();
        Scene scene = new Scene(loginView.getRoot(), 1280, 820);
        scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());

        loginView.getBtnLogin().setOnAction(e -> {
            if (loginView.isModeAdmin()) {
                if ("admin".equals(loginView.getUsername()) && "admin123".equals(loginView.getPassword())) {
                    bukaDashboard(true, null);
                } else {
                    loginView.setError("Username atau password admin salah!");
                }
            } else {
                String idInput = loginView.getIdWarga();
                Optional<Warga> found = repo.semuaWarga().stream()
                        .filter(w -> w.getIdWarga().equalsIgnoreCase(idInput))
                        .findFirst();

                if (found.isPresent()) {
                    bukaDashboard(false, found.get());
                } else {
                    loginView.setError("ID Warga '" + idInput + "' tidak ditemukan!");
                }
            }
        });

        UiAnimator.inisialisasiInteraktif(loginView.getRoot());
        primaryStage.setScene(scene);
    }

    private void bukaDashboard(boolean isAdmin, Warga wargaLogin) {
        view = new MainView();
        wargaController = new WargaController(view.getWargaTab(), repo, () -> refreshSemua(isAdmin, wargaLogin));
        penimbanganController = new PenimbanganController(view.getPenimbanganTab(), repo, () -> refreshSemua(isAdmin, wargaLogin));
        penukaranController = new PenukaranController(view.getPenukaranTab(), repo, () -> refreshSemua(isAdmin, wargaLogin));
        riwayatController = new RiwayatController(view.getRiwayatTab(), repo);
        riwayatController.setWargaLogin(wargaLogin);

        // Tambahkan tombol Keluar (Logout) & badge peran di header
        Node headerNode = view.getRoot().lookup(".app-header");
        if (headerNode instanceof HBox headerBox) {
            Label badgeRole = new Label(isAdmin ? "ADMIN" : "WARGA: " + wargaLogin.getNama());
            badgeRole.getStyleClass().addAll("badge", isAdmin ? "badge-setor" : "badge-tukar");

            Button btnLogout = new Button("Keluar");
            btnLogout.getStyleClass().add("btn-danger");
            btnLogout.setOnAction(e -> tampilkanLogin());

            HBox userControls = new HBox(10, badgeRole, btnLogout);
            userControls.setAlignment(Pos.CENTER_RIGHT);
            headerBox.getChildren().add(userControls);
        }

        // Batasi tab jika yang login adalah Warga
        if (!isAdmin) {
            Node tabNode = view.getRoot().lookup(".tab-pane");
            if (tabNode instanceof TabPane tabPane && tabPane.getTabs().size() >= 4) {
                // Hapus tab Data Warga (index 0) & Penimbangan (index 1)
                tabPane.getTabs().remove(0, 2);
            }
        }

        Scene scene = new Scene(view.getRoot(), 1280, 820);
        scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());

        refreshSemua(isAdmin, wargaLogin);
        UiAnimator.inisialisasiInteraktif(view.getRoot());
        primaryStage.setScene(scene);
    }

    private void refreshSemua(boolean isAdmin, Warga wargaLogin) {
        List<Warga> warga = repo.semuaWarga();
        List<Transaksi> transaksi = repo.semuaTransaksi();

        wargaController.refresh();
        penimbanganController.refresh();
        penukaranController.refresh();
        riwayatController.refresh();

        if (isAdmin) {
            int jumlahWarga = warga.size();
            int totalPoin = warga.stream().mapToInt(Warga::getSaldoPoin).sum();
            double kgHariIni = transaksi.stream()
                    .filter(t -> t.getTipe() == Transaksi.Tipe.SETOR)
                    .filter(t -> t.getTanggal().toLocalDate().equals(LocalDate.now()))
                    .mapToDouble(Transaksi::getBerat).sum();

            UiAnimator.animasiAngkaInt(view.getLblJumlahWarga(), jumlahWarga);
            UiAnimator.animasiAngkaInt(view.getLblTotalPoin(), totalPoin);
            UiAnimator.animasiAngkaKg(view.getLblSetoranHariIni(), kgHariIni);
        } else if (wargaLogin != null) {
            // Ambil data terbaru warga yang sedang login
            Warga terbaru = repo.semuaWarga().stream()
                    .filter(w -> w.getIdWarga().equals(wargaLogin.getIdWarga()))
                    .findFirst().orElse(wargaLogin);

            double kgWarga = transaksi.stream()
                    .filter(t -> t.getIdWarga().equals(terbaru.getIdWarga()))
                    .filter(t -> t.getTipe() == Transaksi.Tipe.SETOR)
                    .mapToDouble(Transaksi::getBerat).sum();

            UiAnimator.animasiAngkaInt(view.getLblJumlahWarga(), 1);
            UiAnimator.animasiAngkaInt(view.getLblTotalPoin(), terbaru.getSaldoPoin());
            UiAnimator.animasiAngkaKg(view.getLblSetoranHariIni(), kgWarga);

            // Kunci pilihan warga di tab Penukaran Poin agar tetap akun warga yang login
            view.getPenukaranTab().getCbWarga().setItems(FXCollections.observableArrayList(terbaru));
            view.getPenukaranTab().getCbWarga().setValue(terbaru);
            view.getPenukaranTab().getCbWarga().setDisable(true);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}