import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.Scene;
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
import view.ModernPopup;
import view.ModernPopup.LangkahSpotlight;
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
        stage.setMinHeight(640);
        stage.setMaximized(true);

        tampilkanLogin();
        stage.show();
    }

    private void tampilkanLogin() {
        LoginView loginView = new LoginView();
        double w = primaryStage.getWidth() > 0 ? primaryStage.getWidth() : 1280;
        double h = primaryStage.getHeight() > 0 ? primaryStage.getHeight() : 720;
        Scene scene = new Scene(loginView.getRoot(), w, h);
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
                        .filter(warga -> warga.getIdWarga().equalsIgnoreCase(idInput))
                        .findFirst();

                if (found.isPresent()) {
                    bukaDashboard(false, found.get());
                } else {
                    loginView.setError("ID Warga '" + idInput + "' tidak ditemukan!");
                }
            }
        });

        UiAnimator.inisialisasiInteraktif(loginView.getRoot());
        UiAnimator.animasiLayarMembesar(loginView.getRoot());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
    }

    private void bukaDashboard(boolean isAdmin, Warga wargaLogin) {
        view = new MainView();
        wargaController = new WargaController(view.getWargaTab(), repo, () -> refreshSemua(isAdmin, wargaLogin));
        penimbanganController = new PenimbanganController(view.getPenimbanganTab(), repo, () -> refreshSemua(isAdmin, wargaLogin));
        penukaranController = new PenukaranController(view.getPenukaranTab(), repo, () -> refreshSemua(isAdmin, wargaLogin));
        riwayatController = new RiwayatController(view.getRiwayatTab(), repo);
        riwayatController.setWargaLogin(wargaLogin);

        view.getBtnLogout().setOnAction(e -> tampilkanLogin());

        view.getBtnTutorial().setOnAction(e -> {
            if (!isAdmin && wargaLogin != null) {
                jalankanSpotlightWarga(wargaLogin);
            } else {
                ModernPopup.tampilkanAlert(
                        view.getOverlayRoot(),
                        ModernPopup.Tipe.INFO,
                        "Panduan Administrator",
                        "Gunakan menu Data Warga untuk mengelola anggota, Penimbangan untuk mencatat setoran sampah, dan Riwayat & Resi untuk mencetak struk bukti PDF."
                );
            }
        });

        if (!isAdmin && wargaLogin != null) {
            view.aturModeWarga(wargaLogin.getNama());
        }

        double w = primaryStage.getWidth() > 0 ? primaryStage.getWidth() : 1280;
        double h = primaryStage.getHeight() > 0 ? primaryStage.getHeight() : 720;
        Scene scene = new Scene(view.getRoot(), w, h);
        scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());

        refreshSemua(isAdmin, wargaLogin);
        UiAnimator.inisialisasiInteraktif(view.getRoot());
        UiAnimator.animasiLayarMembesar(view.getOverlayRoot());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);

        if (!isAdmin && wargaLogin != null) {
            jalankanSpotlightWarga(wargaLogin);
        } else {
            ModernPopup.tampilkanToast(view.getOverlayRoot(), ModernPopup.Tipe.SUKSES, "Login Berhasil", "Selamat datang kembali, Administrator!");
        }
    }

    /**
     * Menjalankan Spotlight Tutorial dengan area gelap di luar fitur yang disorot + panah animasi.
     */
    private void jalankanSpotlightWarga(Warga wargaLogin) {
        List<LangkahSpotlight> steps = List.of(
                new LangkahSpotlight(
                        "TUTORIAL 1 / 4 • RINGKASAN SALDO",
                        "Pantau Saldo & Setoran Anda",
                        "Kotak statistik di atas menampilkan total saldo poin aktif Anda serta akumulasi berat sampah yang sudah disetorkan.",
                        () -> view.getBentoStatsBox(),
                        view::bukaTabPenukaranWarga
                ),
                new LangkahSpotlight(
                        "TUTORIAL 2 / 4 • MENU PENUKARAN",
                        "Menu Penukaran Poin",
                        "Klik menu ini kapan saja jika Anda ingin menukarkan poin tabungan sampah Anda.",
                        () -> view.getNavPenukaran(),
                        view::bukaTabPenukaranWarga
                ),
                new LangkahSpotlight(
                        "TUTORIAL 3 / 4 • FORM TUKAR POIN",
                        "Masukkan Jumlah Poin",
                        "Akun Anda sudah terpilih otomatis. Cukup ketik jumlah poin yang ingin ditukar pada kolom di kartu ini, lalu klik tombol Tukar Poin.",
                        () -> {
                            Node kartuForm = view.getPenukaranTab().getRoot().lookup(".card");
                            return kartuForm != null ? kartuForm : view.getPenukaranTab().getRoot();
                        },
                        view::bukaTabPenukaranWarga
                ),
                new LangkahSpotlight(
                        "TUTORIAL 4 / 4 • RIWAYAT & CETAK PDF",
                        "Lihat Riwayat & Cetak Resi PDF",
                        "Buka menu Riwayat & Resi untuk melihat semua transaksi Anda, lalu tekan tombol 'Cetak Resi PDF' di bawah tabel untuk mengunduh struk resmi.",
                        () -> view.getNavRiwayat(),
                        view::bukaTabRiwayatWarga
                )
        );

        ModernPopup.tampilkanSpotlightTutorial(view.getOverlayRoot(), steps, () -> {
            view.bukaTabPenukaranWarga();
            ModernPopup.tampilkanToast(view.getOverlayRoot(), ModernPopup.Tipe.SUKSES, "Siap Digunakan!", "Halo " + wargaLogin.getNama() + ", selamat bertransaksi.");
        });
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

            view.getPenukaranTab().getCbWarga().setItems(FXCollections.observableArrayList(terbaru));
            view.getPenukaranTab().getCbWarga().setValue(terbaru);
            view.getPenukaranTab().getCbWarga().setDisable(true);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}