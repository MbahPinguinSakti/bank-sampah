package view;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


/** Jendela utama: header di TOP, TabPane berisi 4 tab di CENTER. */
public class MainView {

    private final BorderPane root = new BorderPane();

    private final WargaTab wargaTab = new WargaTab();
    private final PenimbanganTab penimbanganTab = new PenimbanganTab();
    private final PenukaranTab penukaranTab = new PenukaranTab();
    private final RiwayatTab riwayatTab = new RiwayatTab();

    private final Label lblJumlahWarga = new Label("0");
    private final Label lblTotalPoin = new Label("0");
    private final Label lblSetoranHariIni = new Label("0 kg");

    public MainView() {
        root.setTop(buatHeader());
        root.setCenter(buatTabPane());
    }

    private HBox buatHeader() {
        Label logo = new Label("♻");
        logo.getStyleClass().add("logo");

        Label judul = new Label("Bank Sampah");
        judul.getStyleClass().add("app-title");
        Label sub = new Label("Pencatatan setoran sampah & poin warga");
        sub.getStyleClass().add("app-subtitle");

        HBox header = new HBox(14,
                logo,
                new VBox(2, judul, sub),
                Ui.spacer(),
                stat("Warga terdaftar", lblJumlahWarga),
                stat("Total saldo poin", lblTotalPoin),
                stat("Setoran hari ini", lblSetoranHariIni));
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("app-header");
        return header;
    }

    /** Kotak kecil ringkasan di header (judul kecil + angka besar). */
    private VBox stat(String judul, Label nilai) {
        Label lblJudul = new Label(judul);
        lblJudul.getStyleClass().add("stat-label");
        nilai.getStyleClass().add("stat-value");
        VBox box = new VBox(1, lblJudul, nilai);
        box.getStyleClass().add("stat");
        return box;
    }

    private TabPane buatTabPane() {
        TabPane tabPane = new TabPane(
                new Tab("Data Warga", wargaTab.getRoot()),
                new Tab("Penimbangan", penimbanganTab.getRoot()),
                new Tab("Penukaran Poin", penukaranTab.getRoot()),
                new Tab("Riwayat", riwayatTab.getRoot()));
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE); // tab tidak bisa ditutup
        return tabPane;
    }

    public Parent getRoot() { return root; }
    public WargaTab getWargaTab() { return wargaTab; }
    public PenimbanganTab getPenimbanganTab() { return penimbanganTab; }
    public PenukaranTab getPenukaranTab() { return penukaranTab; }
    public RiwayatTab getRiwayatTab() { return riwayatTab; }
    public Label getLblJumlahWarga() { return lblJumlahWarga; }
    public Label getLblTotalPoin() { return lblTotalPoin; }
    public Label getLblSetoranHariIni() { return lblSetoranHariIni; }
}