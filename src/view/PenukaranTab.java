package view;

import java.time.format.DateTimeFormatter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.Transaksi;
import model.Warga;

/** Tab Penukaran Poin: form tukar di LEFT, riwayat warga terpilih di CENTER. */
public class PenukaranTab {

    private static final DateTimeFormatter TGL = DateTimeFormatter.ofPattern("dd MMM yyyy, HH.mm");

    private final BorderPane root = new BorderPane();

    private final ComboBox<Warga> cbWarga = new ComboBox<>();
    private final Label lblSaldo = new Label("0");
    private final Label lblNamaWarga = new Label("Belum ada warga dipilih");
    private final TextField tfJumlah = new TextField();
    private final Label lblRingkasSaldo = new Label("0");
    private final Label lblRingkasTukar = new Label("0");
    private final Label lblSisa = new Label("0 poin");
    private final Button btnTukar = Ui.tombol("Tukar Poin", "btn-primary");
    private final TableView<Transaksi> tabelRiwayat = new TableView<>();

    public PenukaranTab() {
        root.getStyleClass().add("tab-content");
        root.setLeft(buatForm());
        root.setCenter(buatKartuRiwayat());
    }

    private VBox buatForm() {
        cbWarga.setPromptText("Pilih warga");
        tfJumlah.setPromptText("Jumlah poin yang ditukar");

        // kartu hijau saldo
        Label lblJudulSaldo = new Label("Saldo saat ini");
        Label lblSatuan = new Label("poin");
        lblSaldo.getStyleClass().add("saldo-value");
        HBox angka = new HBox(6, lblSaldo, lblSatuan);
        angka.setAlignment(Pos.BASELINE_LEFT);
        VBox saldo = new VBox(4, lblJudulSaldo, angka, lblNamaWarga);
        saldo.getStyleClass().add("saldo-box");

        // ringkasan: GridPane 2 kolom (keterangan kiri, angka kanan)
        GridPane ringkasan = new GridPane();
        ringkasan.setVgap(10);
        ringkasan.addRow(0, new Label("Saldo"), lblRingkasSaldo);
        ringkasan.addRow(1, new Label("Ditukar"), lblRingkasTukar);
        ringkasan.add(new Separator(), 0, 2, 2, 1); // colspan 2
        Label lblSisaJudul = new Label("Sisa saldo");
        lblSisaJudul.getStyleClass().add("teks-tebal");
        ringkasan.addRow(3, lblSisaJudul, lblSisa);
        for (Label l : new Label[] { lblRingkasSaldo, lblRingkasTukar, lblSisa }) {
            GridPane.setHalignment(l, javafx.geometry.HPos.RIGHT);
            GridPane.setHgrow(l, Priority.ALWAYS);
        }
        lblRingkasTukar.getStyleClass().add("poin-minus");
        lblSisa.getStyleClass().addAll("teks-tebal", "poin-plus");
        ringkasan.getStyleClass().add("ringkasan");

        VBox form = Ui.card("Tukar Poin", "Poin dikurangi dari saldo warga");
        form.getChildren().addAll(
                Ui.field("Warga", cbWarga),
                saldo,
                new VBox(6, Ui.field("Jumlah poin", tfJumlah), Ui.hint("Hanya angka. Tidak boleh melebihi saldo.")),
                ringkasan,
                btnTukar);
        form.setPrefWidth(400);
        form.setMaxHeight(Region.USE_PREF_SIZE);
        BorderPane.setMargin(form, new Insets(0, 20, 0, 0));
        return form;
    }

    private VBox buatKartuRiwayat() {
        tabelRiwayat.getColumns().add(Ui.kolom("Tanggal", t -> t.getTanggal().format(TGL), 200));
        tabelRiwayat.getColumns().add(Ui.kolomTipe());
        tabelRiwayat.getColumns().add(Ui.kolom("Jenis", Transaksi::getJenisSampah, 90));
        tabelRiwayat.getColumns().add(Ui.kolomPoin());
        tabelRiwayat.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tabelRiwayat.setPlaceholder(new Label("Pilih warga untuk melihat riwayatnya"));
        tabelRiwayat.setPrefHeight(220);

        VBox kartu = Ui.card("Riwayat warga", "Transaksi warga yang sedang dipilih");
        kartu.getChildren().add(tabelRiwayat);
        kartu.setMaxHeight(Region.USE_PREF_SIZE);
        BorderPane.setAlignment(kartu, Pos.TOP_LEFT); // default CENTER, kita mau nempel ke atas
        return kartu;
    }

    public BorderPane getRoot() { return root; }
    public ComboBox<Warga> getCbWarga() { return cbWarga; }
    public Label getLblSaldo() { return lblSaldo; }
    public Label getLblNamaWarga() { return lblNamaWarga; }
    public TextField getTfJumlah() { return tfJumlah; }
    public Label getLblRingkasSaldo() { return lblRingkasSaldo; }
    public Label getLblRingkasTukar() { return lblRingkasTukar; }
    public Label getLblSisa() { return lblSisa; }
    public Button getBtnTukar() { return btnTukar; }
    public TableView<Transaksi> getTabelRiwayat() { return tabelRiwayat; }
}