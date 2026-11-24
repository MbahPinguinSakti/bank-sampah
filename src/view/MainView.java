package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.*;

public class MainView {

    private final StackPane overlayRoot = new StackPane();
    private final BorderPane appLayout = new BorderPane();

    private final WargaTab wargaTab = new WargaTab();
    private final PenimbanganTab penimbanganTab = new PenimbanganTab();
    private final PenukaranTab penukaranTab = new PenukaranTab();
    private final RiwayatTab riwayatTab = new RiwayatTab();

    private final ToggleGroup navGroup = new ToggleGroup();
    private final ToggleButton navWarga = buatNavButton("👥   Data Warga");
    private final ToggleButton navPenimbangan = buatNavButton("⚖   Penimbangan");
    private final ToggleButton navPenukaran = buatNavButton("🎁   Penukaran Poin");
    private final ToggleButton navRiwayat = buatNavButton("🧾   Riwayat & Resi");

    private final VBox navMenuBox = new VBox(6);
    private final StackPane contentArea = new StackPane();
    private final Label lblPageHeading = new Label("Data Warga");
    private final Label lblPageSub = new Label("Kelola data anggota dan saldo poin bank sampah");

    private final Label lblJumlahWarga = new Label("0");
    private final Label lblTotalPoin = new Label("0");
    private final Label lblSetoranHariIni = new Label("0.0 kg");
    private HBox bentoStatsBox;

    private final Label lblRoleBadge = new Label("ADMIN");
    private final Button btnTutorial = new Button("? Panduan");
    private final Button btnLogout = new Button("Keluar");

    public MainView() {
        appLayout.getStyleClass().add("app-shell");

        // 1. SIDEBAR KIRI (Proporsional & Tidak Terpotong)
        VBox sidebar = new VBox(14);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(240);

        Label logo = new Label("♻");
        logo.getStyleClass().add("logo");

        VBox brandText = new VBox(2);
        Label appTitle = new Label("Bank Sampah");
        appTitle.getStyleClass().add("sidebar-title");
        Label appSub = new Label("Digital Eco System");
        appSub.getStyleClass().add("sidebar-subtitle");
        brandText.getChildren().addAll(appTitle, appSub);

        HBox brandBox = new HBox(12, logo, brandText);
        brandBox.setAlignment(Pos.CENTER_LEFT);

        Label menuSection = new Label("MENU UTAMA");
        menuSection.getStyleClass().add("sidebar-section");

        navMenuBox.getChildren().addAll(navWarga, navPenimbangan, navPenukaran, navRiwayat);

        Region sidebarSpacer = new Region();
        VBox.setVgrow(sidebarSpacer, Priority.ALWAYS);

        VBox bottomProfile = new VBox(8);
        bottomProfile.getStyleClass().add("sidebar-footer");
        lblRoleBadge.getStyleClass().addAll("badge", "badge-setor");
        lblRoleBadge.setMaxWidth(Double.MAX_VALUE);
        lblRoleBadge.setAlignment(Pos.CENTER);

        btnTutorial.getStyleClass().add("btn-secondary");
        btnTutorial.setPrefHeight(34);
        btnTutorial.setMaxWidth(Double.MAX_VALUE);

        btnLogout.getStyleClass().add("btn-danger");
        btnLogout.setPrefHeight(34);
        btnLogout.setMaxWidth(Double.MAX_VALUE);

        bottomProfile.getChildren().addAll(lblRoleBadge, btnTutorial, btnLogout);
        sidebar.getChildren().addAll(brandBox, menuSection, navMenuBox, sidebarSpacer, bottomProfile);

        // 2. TOPBAR & BENTO STATS
        HBox topBar = new HBox(14);
        topBar.getStyleClass().add("modern-topbar");
        topBar.setAlignment(Pos.CENTER_LEFT);

        lblPageHeading.getStyleClass().add("page-heading");
        lblPageSub.getStyleClass().add("app-subtitle");
        VBox pageTitleBox = new VBox(2, lblPageHeading, lblPageSub);

        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);

        bentoStatsBox = new HBox(10,
                buatStatCard("Warga Terdaftar", lblJumlahWarga, "stat-accent-green"),
                buatStatCard("Total Saldo Poin", lblTotalPoin, "stat-accent-emerald"),
                buatStatCard("Setoran Sampah", lblSetoranHariIni, "stat-accent-teal")
        );

        topBar.getChildren().addAll(pageTitleBox, topSpacer, bentoStatsBox);

        // 3. AREA KONTEN DINAMIS
        contentArea.setPadding(new Insets(0, 24, 16, 24));

        VBox rightWorkspace = new VBox(14, topBar, contentArea);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        appLayout.setLeft(sidebar);
        appLayout.setCenter(rightWorkspace);

        navWarga.setOnAction(e -> pindahHalaman(navWarga, wargaTab.getRoot(), "Data Warga", "Tambah, ubah, dan pantau daftar warga terdaftar"));
        navPenimbangan.setOnAction(e -> pindahHalaman(navPenimbangan, penimbanganTab.getRoot(), "Penimbangan Sampah", "Catat setoran sampah warga dan konversi otomatis ke poin"));
        navPenukaran.setOnAction(e -> pindahHalaman(navPenukaran, penukaranTab.getRoot(), "Penukaran Poin", "Tukarkan saldo poin warga menjadi uang tunai atau sembako"));
        navRiwayat.setOnAction(e -> pindahHalaman(navRiwayat, riwayatTab.getRoot(), "Riwayat & Cetak Resi", "Lihat seluruh log transaksi dan cetak bukti resi PDF"));

        navGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT == null && oldT != null) oldT.setSelected(true);
        });

        pindahHalaman(navWarga, wargaTab.getRoot(), "Data Warga", "Tambah, ubah, dan pantau daftar warga terdaftar");

        overlayRoot.getChildren().add(appLayout);
    }

    private ToggleButton buatNavButton(String teks) {
        ToggleButton btn = new ToggleButton(teks);
        btn.setToggleGroup(navGroup);
        btn.getStyleClass().add("nav-item");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        return btn;
    }

    private VBox buatStatCard(String judul, Label nilai, String accentClass) {
        Label lJudul = new Label(judul);
        lJudul.getStyleClass().add("stat-label");
        nilai.getStyleClass().add("stat-value");
        VBox card = new VBox(2, lJudul, nilai);
        card.getStyleClass().addAll("stat", accentClass);
        card.setMinWidth(145);
        return card;
    }

    public void pindahHalaman(ToggleButton navBtn, Node pageRoot, String judul, String subjudul) {
        navBtn.setSelected(true);
        lblPageHeading.setText(judul);
        lblPageSub.setText(subjudul);
        contentArea.getChildren().setAll(pageRoot);
        UiAnimator.animasiGantiHalaman(pageRoot);
    }

    public void aturModeWarga(String namaWarga) {
        lblRoleBadge.setText("WARGA • " + namaWarga);
        lblRoleBadge.getStyleClass().setAll("badge", "badge-tukar");
        navMenuBox.getChildren().setAll(navPenukaran, navRiwayat);
        bukaTabPenukaranWarga();
    }

    public void bukaTabPenukaranWarga() {
        pindahHalaman(navPenukaran, penukaranTab.getRoot(), "Penukaran Poin", "Tukarkan saldo poin Anda dengan mudah");
    }

    public void bukaTabRiwayatWarga() {
        pindahHalaman(navRiwayat, riwayatTab.getRoot(), "Riwayat & Cetak Resi", "Lihat transaksi Anda dan unduh struk bukti PDF");
    }

    public StackPane getOverlayRoot() { return overlayRoot; }
    public Parent getRoot() { return overlayRoot; }
    public WargaTab getWargaTab() { return wargaTab; }
    public PenimbanganTab getPenimbanganTab() { return penimbanganTab; }
    public PenukaranTab getPenukaranTab() { return penukaranTab; }
    public RiwayatTab getRiwayatTab() { return riwayatTab; }
    public Label getLblJumlahWarga() { return lblJumlahWarga; }
    public Label getLblTotalPoin() { return lblTotalPoin; }
    public Label getLblSetoranHariIni() { return lblSetoranHariIni; }
    public Button getBtnLogout() { return btnLogout; }
    public Button getBtnTutorial() { return btnTutorial; }
    public HBox getBentoStatsBox() { return bentoStatsBox; }
    public ToggleButton getNavPenukaran() { return navPenukaran; }
    public ToggleButton getNavRiwayat() { return navRiwayat; }
}