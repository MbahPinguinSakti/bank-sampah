package view;

import java.time.format.DateTimeFormatter;

import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.Transaksi;
import model.Warga;

/** Tab Riwayat: baris filter di atas, tabel semua transaksi memenuhi sisa ruang. */
public class RiwayatTab {

    private static final DateTimeFormatter TGL = DateTimeFormatter.ofPattern("dd MMM yyyy, HH.mm");

    private final BorderPane root = new BorderPane();

    private final ComboBox<Warga> cbFilterWarga = new ComboBox<>();
    private final ToggleGroup grupTipe = new ToggleGroup();
    private final ToggleButton tgSemua = chip("Semua");
    private final ToggleButton tgSetor = chip("Setor");
    private final ToggleButton tgTukar = chip("Tukar");
    private final Label lblJumlah = new Label("0");
    private final Label lblPoinMasuk = new Label("+0");
    private final Label lblPoinKeluar = new Label("−0");
    private final TableView<Transaksi> tabel = new TableView<>();

    public RiwayatTab() {
        root.getStyleClass().add("tab-content");

        VBox kartu = Ui.card("Riwayat Transaksi", "Semua setoran dan penukaran poin, terbaru di atas");
        kartu.getChildren().addAll(buatFilter(), buatTabel());
        VBox.setVgrow(tabel, Priority.ALWAYS);
        root.setCenter(kartu);
    }

    private ToggleButton chip(String teks) {
        ToggleButton tg = new ToggleButton(teks);
        tg.getStyleClass().add("chip");
        tg.setToggleGroup(grupTipe); // dalam satu grup, hanya satu yang bisa aktif
        return tg;
    }

    private HBox buatFilter() {
        cbFilterWarga.setPromptText("Semua warga");
        cbFilterWarga.setPrefWidth(260);
        tgSemua.setSelected(true);

        HBox tipe = new HBox(6, tgSemua, tgSetor, tgTukar);
        tipe.setAlignment(Pos.CENTER_LEFT);

        lblPoinMasuk.getStyleClass().add("poin-plus");
        lblPoinKeluar.getStyleClass().add("poin-minus");

        HBox filter = new HBox(12,
                Ui.field("Filter warga", cbFilterWarga),
                tipe,
                Ui.spacer(),
                ringkas("Transaksi", lblJumlah),
                ringkas("Poin masuk", lblPoinMasuk),
                ringkas("Poin keluar", lblPoinKeluar));
        filter.setAlignment(Pos.BOTTOM_LEFT);
        return filter;
    }

    private VBox ringkas(String judul, Label nilai) {
        Label lbl = new Label(judul);
        lbl.getStyleClass().add("stat-label");
        nilai.getStyleClass().add("stat-value");
        VBox box = new VBox(2, lbl, nilai);
        box.getStyleClass().add("ringkas");
        return box;
    }

    private TableView<Transaksi> buatTabel() {
        tabel.getColumns().add(Ui.kolom("ID", t -> "#" + t.getId(), 50));
        tabel.getColumns().add(Ui.kolom("Tanggal", t -> t.getTanggal().format(TGL), 170));
        tabel.getColumns().add(Ui.kolom("Warga", Transaksi::getIdWarga, 200));
        tabel.getColumns().add(Ui.kolomTipe());
        tabel.getColumns().add(Ui.kolom("Jenis", Transaksi::getJenisSampah, 100));
        tabel.getColumns().add(Ui.kolom("Berat",
                t -> t.getSampah() == null ? "—" : String.format("%.1f kg", t.getBerat()), 80));
        tabel.getColumns().add(Ui.kolomPoin());
        tabel.getColumns().get(5).getStyleClass().add("col-angka");
        tabel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tabel.setPlaceholder(new Label("Belum ada transaksi"));
        return tabel;
    }

    public BorderPane getRoot() { return root; }
    public ComboBox<Warga> getCbFilterWarga() { return cbFilterWarga; }
    public ToggleGroup getGrupTipe() { return grupTipe; }
    public ToggleButton getTgSemua() { return tgSemua; }
    public ToggleButton getTgSetor() { return tgSetor; }
    public ToggleButton getTgTukar() { return tgTukar; }
    public Label getLblJumlah() { return lblJumlah; }
    public Label getLblPoinMasuk() { return lblPoinMasuk; }
    public Label getLblPoinKeluar() { return lblPoinKeluar; }
    public TableView<Transaksi> getTabel() { return tabel; }
}
