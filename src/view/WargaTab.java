package view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.Warga;


public class WargaTab {

    private final BorderPane root = new BorderPane();

    private final TextField tfId = new TextField();
    private final TextField tfNama = new TextField();
    private final TextArea taAlamat = new TextArea();
    private final Button btnTambah = Ui.tombol("Tambah", "btn-primary");
    private final Button btnHapus = Ui.tombol("Hapus", "btn-secondary");
    private final Button btnUbah = Ui.tombol("Ubah", "btn-danger");
    private final Button btnBersihkan = Ui.tombol("Bersihkan", "btn-ghost");


    private final TextField tfCari = new TextField();
    private final TableView<Warga> tabel = new TableView<>();

    public WargaTab(){
        root.getStyleClass().add("tab-content");
        root.setLeft(buatForm());
        root.setCenter(buatDaftar());
    }

    private VBox buatForm(){
        
        tfId.setPromptText("contoh: W001");
        tfNama.setPromptText("contoh: John Doe");
        taAlamat.setPromptText("contoh: Jl. Merdeka No. 123");
        taAlamat.setPrefRowCount(3);
        taAlamat.setWrapText(true);

        GridPane tombol = new GridPane();
        tombol.setHgap(8);
        tombol.setVgap(8);
        ColumnConstraints setengah = new ColumnConstraints();
        setengah.setPercentWidth(50);
        tombol.getColumnConstraints().addAll(setengah, setengah);
        tombol.add(btnTambah, 0, 0);
        tombol.add(btnHapus, 1, 0);
        tombol.add(btnUbah, 0, 1);
        tombol.add(btnBersihkan, 1, 1);

        VBox form = Ui.card("Form Warga", "klik baris di tabel untuk mengubah data");
        form.getChildren().addAll(
            new VBox(6, Ui.field("ID Warga", tfId), Ui.field("Nama", tfNama), Ui.field("Alamat", taAlamat)),

            new Separator(),
            tombol
        );

        form.setPrefWidth(400);
        form.setMaxHeight(Region.USE_PREF_SIZE);
        BorderPane.setMargin(form, new Insets(0, 20, 0, 0));
        return form;
    }

    private VBox buatDaftar() {
        tfCari.setPromptText("Cari nama atau ID…");
        tfCari.setPrefWidth(220);

        // PropertyValueFactory("idWarga") otomatis memanggil getIdWarga() pada tiap objek Warga
        TableColumn<Warga, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idWarga"));
        colId.setPrefWidth(70);

        TableColumn<Warga, String> colNama = new TableColumn<>("Nama");
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colNama.setPrefWidth(180);

        TableColumn<Warga, String> colAlamat = new TableColumn<>("Alamat");
        colAlamat.setCellValueFactory(new PropertyValueFactory<>("alamat"));
        colAlamat.setPrefWidth(260);

        TableColumn<Warga, Integer> colSaldo = new TableColumn<>("Saldo Poin");
        colSaldo.setCellValueFactory(new PropertyValueFactory<>("saldoPoin"));
        colSaldo.setPrefWidth(110);
        colSaldo.getStyleClass().add("col-angka");

        tabel.getColumns().add(colId);
        tabel.getColumns().add(colNama);
        tabel.getColumns().add(colAlamat);
        tabel.getColumns().add(colSaldo);
        tabel.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tabel.setPlaceholder(new Label("Belum ada data warga"));

        VBox daftar = Ui.card("Daftar Warga", "Semua warga yang terdaftar di bank sampah", tfCari);
        daftar.getChildren().add(tabel);
        VBox.setVgrow(tabel, Priority.ALWAYS); // tabel mengisi sisa tinggi kartu
        return daftar;
    }

    public BorderPane getRoot() { return root; }
    public TextField getTfId() { return tfId; }
    public TextField getTfNama() { return tfNama; }
    public TextArea getTaAlamat() { return taAlamat; }
    public Button getBtnTambah() { return btnTambah; }
    public Button getBtnUbah() { return btnUbah; }
    public Button getBtnHapus() { return btnHapus; }
    public Button getBtnBersihkan() { return btnBersihkan; }
    public TextField getTfCari() { return tfCari; }
    public TableView<Warga> getTabel() { return tabel; }

}
