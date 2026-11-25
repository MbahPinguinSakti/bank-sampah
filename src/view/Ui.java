package view;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import model.Transaksi;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public final class Ui {

    private Ui() {}

    // ==================== HELPER KOMPONEN UI ====================

    public static VBox card(String judul, String subjudul) {
        return card(judul, subjudul, null);
    }

    public static VBox card(String judul, String subjudul, Node aksiKanan) {
        VBox box = new VBox(14);
        box.getStyleClass().add("card");

        Label lblJudul = new Label(judul);
        lblJudul.getStyleClass().add("card-title");

        Label lblSub = new Label(subjudul);
        lblSub.getStyleClass().add("card-subtitle");

        VBox titleBox = new VBox(2, lblJudul, lblSub);
        if (aksiKanan != null) {
            HBox header = new HBox(12, titleBox, spacer(), aksiKanan);
            header.setAlignment(Pos.CENTER_LEFT);
            box.getChildren().add(header);
        } else {
            box.getChildren().add(titleBox);
        }
        return box;
    }

    public static VBox field(String label, Control input) {
        Label lbl = new Label(label);
        lbl.getStyleClass().add("field-label");
        return new VBox(6, lbl, input);
    }

    public static Button tombol(String teks, String styleClass) {
        Button btn = new Button(teks);
        btn.getStyleClass().add(styleClass);
        return btn;
    }

    public static Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    public static Label hint(String teks) {
        Label lbl = new Label(teks);
        lbl.getStyleClass().add("hint-text");
        lbl.setWrapText(true);
        return lbl;
    }

    public static TableColumn<Transaksi, String> kolom(String judul, Function<Transaksi, String> getter, int lebar) {
        TableColumn<Transaksi, String> col = new TableColumn<>(judul);
        col.setCellValueFactory(cell -> new SimpleStringProperty(getter.apply(cell.getValue())));
        col.setPrefWidth(lebar);
        return col;
    }

    public static TableColumn<Transaksi, String> kolomTipe() {
        TableColumn<Transaksi, String> col = new TableColumn<>("Tipe");
        col.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTipe().name()));
        col.setPrefWidth(95);
        col.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label(item);
                    boolean isSetor = "SETOR".equals(item);
                    badge.setStyle(
                            "-fx-background-color: " + (isSetor ? "#DCFCE7;" : "#FEF3C7;") +
                            "-fx-text-fill: " + (isSetor ? "#15803D;" : "#B45309;") +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 11px;" +
                            "-fx-padding: 3 10;" +
                            "-fx-background-radius: 999;"
                    );
                    setGraphic(badge);
                    setText(null);
                }
            }
        });
        return col;
    }

    public static TableColumn<Transaksi, String> kolomPoin() {
        TableColumn<Transaksi, String> col = new TableColumn<>("Poin");
        col.setCellValueFactory(cell -> {
            Transaksi t = cell.getValue();
            boolean isSetor = t.getTipe() == Transaksi.Tipe.SETOR;
            return new SimpleStringProperty((isSetor ? "+" : "-") + t.getPoin());
        });
        col.setPrefWidth(90);
        col.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    boolean plus = item.startsWith("+");
                    setStyle("-fx-font-weight: bold; -fx-text-fill: " + (plus ? "#15803D;" : "#DC2626;"));
                }
            }
        });
        return col;
    }

    // ==================== POPUP MODAL MODERN EMERALD ====================

    public static void info(String pesan) {
        tampilkanModal(
                "✓",
                "#DCFCE7",
                "#15803D",
                "Berhasil!",
                pesan,
                "Mengerti",
                null
        );
    }

    public static void error(String pesan) {
        tampilkanModal(
                "✕",
                "#FEE2E2",
                "#DC2626",
                "Terjadi Kesalahan",
                pesan,
                "Tutup",
                null
        );
    }

    public static boolean konfirmasi(String pesan) {
        return konfirmasi("Konfirmasi Tindakan", pesan);
    }

    public static boolean konfirmasi(String judul, String pesan) {
        return tampilkanModal(
                "!",
                "#FEF3C7",
                "#D97706",
                judul,
                pesan,
                "Ya, Lanjutkan",
                "Batal"
        );
    }

    private static boolean tampilkanModal(
            String ikon,
            String bgIkon,
            String warnaIkon,
            String judul,
            String pesan,
            String teksTombolUtama,
            String teksTombolBatal
    ) {
        AtomicBoolean hasil = new AtomicBoolean(false);

        Stage dialogStage = new Stage(StageStyle.TRANSPARENT);
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        Window owner = Window.getWindows().stream()
                .filter(Window::isShowing)
                .findFirst()
                .orElse(null);
        if (owner != null) {
            dialogStage.initOwner(owner);
        }

        StackPane backdrop = new StackPane();
        backdrop.setStyle("-fx-background-color: rgba(6, 24, 16, 0.55);");
        backdrop.setPadding(new Insets(24));
        if (owner != null) {
            backdrop.setPrefSize(owner.getWidth(), owner.getHeight());
        } else {
            backdrop.setPrefSize(600, 400);
        }

        VBox card = new VBox(16);
        card.setMaxWidth(410);
        card.setMaxHeight(Region.USE_PREF_SIZE);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(28, 28, 24, 28));
        card.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 22;" +
                "-fx-border-color: #D1FAE5;" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 22;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(6, 78, 59, 0.30));
        shadow.setRadius(28);
        shadow.setOffsetY(10);
        card.setEffect(shadow);

        Label lblIkon = new Label(ikon);
        lblIkon.setMinSize(56, 56);
        lblIkon.setMaxSize(56, 56);
        lblIkon.setAlignment(Pos.CENTER);
        lblIkon.setStyle(
                "-fx-background-color: " + bgIkon + ";" +
                "-fx-text-fill: " + warnaIkon + ";" +
                "-fx-font-size: 24px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 999;"
        );

        Label lblJudul = new Label(judul);
        lblJudul.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        lblJudul.setWrapText(true);

        String pesanRapi = (pesan != null && !pesan.isEmpty())
                ? Character.toUpperCase(pesan.charAt(0)) + pesan.substring(1)
                : "";
        Label lblPesan = new Label(pesanRapi);
        lblPesan.setStyle("-fx-font-size: 13.5px; -fx-text-fill: #475569; -fx-text-alignment: center; -fx-line-spacing: 3px;");
        lblPesan.setWrapText(true);
        lblPesan.setAlignment(Pos.CENTER);

        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPadding(new Insets(6, 0, 0, 0));

        Button btnUtama = new Button(teksTombolUtama);
        btnUtama.setMaxWidth(Double.MAX_VALUE);
        btnUtama.setPrefHeight(42);
        HBox.setHgrow(btnUtama, Priority.ALWAYS);

        String warnaTombol = "#DC2626".equals(warnaIkon)
                ? "linear-gradient(to right, #EF4444, #DC2626)"
                : "linear-gradient(to right, #10B981, #047857)";

        btnUtama.setStyle(
                "-fx-background-color: " + warnaTombol + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13.5px;" +
                "-fx-background-radius: 12;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(5, 150, 105, 0.28), 10, 0, 0, 4);"
        );

        if (teksTombolBatal != null) {
            Button btnBatal = new Button(teksTombolBatal);
            btnBatal.setMaxWidth(Double.MAX_VALUE);
            btnBatal.setPrefHeight(42);
            HBox.setHgrow(btnBatal, Priority.ALWAYS);
            btnBatal.setStyle(
                    "-fx-background-color: #F1F5F9;" +
                    "-fx-text-fill: #475569;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 13.5px;" +
                    "-fx-background-radius: 12;" +
                    "-fx-cursor: hand;"
            );
            btnBatal.setOnAction(e -> {
                hasil.set(false);
                tutupDenganAnimasi(dialogStage, backdrop, card);
            });
            actionBox.getChildren().addAll(btnBatal, btnUtama);
        } else {
            actionBox.getChildren().add(btnUtama);
        }

        btnUtama.setOnAction(e -> {
            hasil.set(true);
            tutupDenganAnimasi(dialogStage, backdrop, card);
        });

        card.getChildren().addAll(lblIkon, lblJudul, lblPesan, actionBox);
        backdrop.getChildren().add(card);

        Scene scene = new Scene(backdrop);
        scene.setFill(Color.TRANSPARENT);
        dialogStage.setScene(scene);

        if (owner != null) {
            dialogStage.setX(owner.getX());
            dialogStage.setY(owner.getY());
        }

        backdrop.setOpacity(0);
        card.setOpacity(0);
        card.setScaleX(0.78);
        card.setScaleY(0.78);

        FadeTransition bgFade = new FadeTransition(Duration.millis(180), backdrop);
        bgFade.setFromValue(0);
        bgFade.setToValue(1);

        FadeTransition cardFade = new FadeTransition(Duration.millis(220), card);
        cardFade.setFromValue(0);
        cardFade.setToValue(1);

        ScaleTransition cardScale = new ScaleTransition(Duration.millis(280), card);
        cardScale.setFromX(0.78);
        cardScale.setFromY(0.78);
        cardScale.setToX(1.0);
        cardScale.setToY(1.0);
        cardScale.setInterpolator(Interpolator.SPLINE(0.18, 0.89, 0.32, 1.18));

        dialogStage.setOnShown(e -> new ParallelTransition(bgFade, cardFade, cardScale).play());
        dialogStage.showAndWait();

        return hasil.get();
    }

    private static void tutupDenganAnimasi(Stage stage, Node backdrop, Node card) {
        FadeTransition bgFade = new FadeTransition(Duration.millis(140), backdrop);
        bgFade.setToValue(0);

        FadeTransition cardFade = new FadeTransition(Duration.millis(140), card);
        cardFade.setToValue(0);

        ScaleTransition cardScale = new ScaleTransition(Duration.millis(140), card);
        cardScale.setToX(0.86);
        cardScale.setToY(0.86);

        ParallelTransition out = new ParallelTransition(bgFade, cardFade, cardScale);
        out.setOnFinished(e -> stage.close());
        out.play();
    }
}