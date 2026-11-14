package view;

import java.time.format.DateTimeFormatter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.Kertas;
import model.Logam;
import model.Plastik;
import model.Transaksi;
import model.Warga;

/** Tab Penimbangan: form setoran di LEFT, tarif + setoran hari ini di CENTER. */
public class PenimbanganTab {

    private static final DateTimeFormatter JAM = DateTimeFormatter.ofPattern("HH.mm");

    private final BorderPane root = new BorderPane();

    private final ComboBox<Warga> cbWarga = new ComboBox<>();
    private final ComboBox<String> cbJenis = new ComboBox<>();
    private final TextField tfBerat = new TextField();
    private final Label lblRumus = new Label("Isi berat untuk melihat perkiraan");
    private final Label lblPerkiraan = new Label("0");
    private final Button btnSimpan = Ui.tombol("Simpan Setoran", "btn-primary");
    private final TableView<Transaksi> tabelHariIni = new TableView<>();

    public PenimbanganTab() {
        root.getStyleClass().add("tab-content");
        root.setLeft(buatForm());
        root.setCenter(new VBox(20, buatKartuTarif(), buatKartuHariIni()));
    }

    private VBox buatForm() {
        cbWarga.setPromptText("Pilih warga");
        cbJenis.setPromptText("Pilih jenis");
        tfBerat.setPromptText("0,0");

        // GridPane: jenis sampah dan berat berdampingan, masing-masing 50%
        GridPane baris = new GridPane();
        baris.setHgap(12);
        ColumnConstraints setengah = new ColumnConstraints();
        setengah.setPercentWidth(50);
        baris.getColumnConstraints().addAll(setengah, setengah);
        baris.add(Ui.field("Jenis sampah", cbJenis), 0, 0);
        baris.add(Ui.field("Berat (kg)", tfBerat), 1, 0);

        // kotak hijau perkiraan poin
        Label lblJudul = new Label("Perkiraan poin");
        lblJudul.getStyleClass().add("estimate-title");
        lblRumus.getStyleClass().add("hint");
        lblPerkiraan.getStyleClass().add("estimate-value");
        Label lblSatuan = new Label("poin");
        lblSatuan.getStyleClass().add("estimate-title");
        HBox nilai = new HBox(6, lblPerkiraan, lblSatuan);
        nilai.setAlignment(Pos.BASELINE_RIGHT);
        HBox perkiraan = new HBox(12, new VBox(2, lblJudul, lblRumus), Ui.spacer(), nilai);
        perkiraan.setAlignment(Pos.CENTER_LEFT);
        perkiraan.getStyleClass().add("estimate");

        VBox form = Ui.card("Catat Penimbangan", "Setoran sampah warga hari ini");
        form.getChildren().addAll(Ui.field("Warga", cbWarga), baris, perkiraan, btnSimpan);
        form.setPrefWidth(400);
        form.setMaxHeight(Region.USE_PREF_SIZE);
        BorderPane.setMargin(form, new Insets(0, 20, 0, 0));
        return form;
    }

    private VBox buatKartuTarif() {
        VBox kartu = Ui.card("Tarif poin per jenis", "Dihitung otomatis oleh hitungPoin() tiap kategori");
        kartu.setSpacing(10);
        // angka tarif diambil dari konstanta di class model, jadi selalu sama dengan perhitungan
        kartu.getChildren().addAll(
                tarif("Plastik", "Botol, gelas, kantong plastik", Plastik.POIN_PER_KG, "plastik"),
                tarif("Kertas", "Kardus, koran, HVS bekas", Kertas.POIN_PER_KG, "kertas"),
                tarif("Logam", "Bonus +" + Logam.BONUS_POIN + " poin bila berat ≥ "
                        + (int) Logam.BATAS_BONUS_KG + " kg", Logam.POIN_PER_KG, "logam"));
        return kartu;
    }

    private HBox tarif(String nama, String keterangan, int poinPerKg, String jenis) {
        Region garis = new Region();
        garis.getStyleClass().addAll("tarif-garis", "bg-" + jenis);
        Label lblNama = new Label(nama);
        lblNama.getStyleClass().add("tarif-nama");
        Label lblKet = new Label(keterangan);
        lblKet.getStyleClass().add("hint");
        Label lblAngka = new Label(String.valueOf(poinPerKg));
        lblAngka.getStyleClass().addAll("tarif-angka", "text-" + jenis);
        Label lblSatuan = new Label("poin/kg");
        lblSatuan.getStyleClass().add("hint");
        HBox angka = new HBox(4, lblAngka, lblSatuan);
        angka.setAlignment(Pos.BASELINE_RIGHT);

        HBox baris = new HBox(12, garis, new VBox(2, lblNama, lblKet), Ui.spacer(), angka);
        baris.setAlignment(Pos.CENTER_LEFT);
        baris.getStyleClass().addAll("tarif", "tarif-" + jenis);
        return baris;
    }

    private VBox buatKartuHariIni() {
        tabelHariIni.getColumns().add(Ui.kolom("Waktu", t -> t.getTanggal().format(JAM), 70));
        tabelHariIni.getColumns().add(Ui.kolom("Warga", Transaksi::getIdWarga, 160));
        tabelHariIni.getColumns().add(Ui.kolom("Jenis", Transaksi::getJenisSampah, 90));
        tabelHariIni.getColumns().add(Ui.kolom("Berat", t -> String.format("%.1f kg", t.getBerat()), 80));
        tabelHariIni.getColumns().add(Ui.kolomPoin());
        tabelHariIni.getColumns().get(3).getStyleClass().add("col-angka");
        tabelHariIni.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tabelHariIni.setPlaceholder(new Label("Belum ada setoran hari ini"));
        tabelHariIni.setPrefHeight(230);

        VBox kartu = Ui.card("Setoran hari ini", "Transaksi SETOR yang tercatat hari ini");
        kartu.getChildren().add(tabelHariIni);
        return kartu;
    }

    public BorderPane getRoot() { return root; }
    public ComboBox<Warga> getCbWarga() { return cbWarga; }
    public ComboBox<String> getCbJenis() { return cbJenis; }
    public TextField getTfBerat() { return tfBerat; }
    public Label getLblRumus() { return lblRumus; }
    public Label getLblPerkiraan() { return lblPerkiraan; }
    public Button getBtnSimpan() { return btnSimpan; }
    public TableView<Transaksi> getTabelHariIni() { return tabelHariIni; }
}