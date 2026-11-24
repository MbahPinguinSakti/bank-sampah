package view;

import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.util.List;
import java.util.function.Supplier;

public class ModernPopup {

    public enum Tipe { SUKSES, PERINGATAN, ERROR, INFO }

    public record LangkahSpotlight(
            String badge,
            String judul,
            String deskripsi,
            Supplier<Node> targetSupplier,
            Runnable sebelumLangkah
    ) {}

    /**
     * Menampilkan popup pemberitahuan modern dengan efek membesar (Scale-Up + Fade).
     */
    public static void tampilkanAlert(StackPane rootOverlay, Tipe tipe, String judul, String pesan) {
        StackPane backdrop = new StackPane();
        backdrop.getStyleClass().add("modal-backdrop");

        VBox dialog = new VBox(14);
        dialog.getStyleClass().add("modal-card");
        dialog.setMaxWidth(400);
        dialog.setMaxHeight(Region.USE_PREF_SIZE);
        dialog.setAlignment(Pos.CENTER);

        String ikon = switch (tipe) {
            case SUKSES -> "✓";
            case PERINGATAN -> "!";
            case ERROR -> "✕";
            case INFO -> "i";
        };
        String warnaClass = switch (tipe) {
            case SUKSES -> "modal-icon-sukses";
            case PERINGATAN -> "modal-icon-warn";
            case ERROR -> "modal-icon-error";
            case INFO -> "modal-icon-info";
        };

        Label lblIkon = new Label(ikon);
        lblIkon.getStyleClass().addAll("modal-icon", warnaClass);

        Label lblJudul = new Label(judul);
        lblJudul.getStyleClass().add("modal-title");
        lblJudul.setWrapText(true);

        Label lblPesan = new Label(pesan);
        lblPesan.getStyleClass().add("modal-body");
        lblPesan.setWrapText(true);

        Button btnTutup = new Button("Mengerti");
        btnTutup.getStyleClass().add("btn-primary");
        btnTutup.setMaxWidth(Double.MAX_VALUE);

        dialog.getChildren().addAll(lblIkon, lblJudul, lblPesan, btnTutup);
        backdrop.getChildren().add(dialog);
        rootOverlay.getChildren().add(backdrop);

        animasiBukaModal(backdrop, dialog);

        btnTutup.setOnAction(e -> animasiTutupModal(rootOverlay, backdrop, dialog));
        backdrop.setOnMouseClicked(e -> {
            if (e.getTarget() == backdrop) animasiTutupModal(rootOverlay, backdrop, dialog);
        });
    }

    /**
     * Menampilkan Toast Notification melayang di pojok kanan atas.
     */
    public static void tampilkanToast(StackPane rootOverlay, Tipe tipe, String judul, String pesan) {
        HBox toast = new HBox(12);
        toast.getStyleClass().add("toast-card");
        toast.setMaxWidth(340);
        toast.setMaxHeight(Region.USE_PREF_SIZE);
        toast.setAlignment(Pos.CENTER_LEFT);

        Label badge = new Label(tipe == Tipe.SUKSES ? "✓" : "i");
        badge.getStyleClass().addAll("toast-badge", tipe == Tipe.SUKSES ? "modal-icon-sukses" : "modal-icon-info");

        VBox teks = new VBox(2);
        Label lJudul = new Label(judul);
        lJudul.setStyle("-fx-font-weight: bold; -fx-text-fill: #141E18; -fx-font-size: 13px;");
        Label lPesan = new Label(pesan);
        lPesan.setStyle("-fx-text-fill: #5A6860; -fx-font-size: 11.5px;");
        lPesan.setWrapText(true);
        teks.getChildren().addAll(lJudul, lPesan);

        toast.getChildren().addAll(badge, teks);

        StackPane.setAlignment(toast, Pos.TOP_RIGHT);
        StackPane.setMargin(toast, new Insets(20, 24, 0, 0));
        rootOverlay.getChildren().add(toast);

        toast.setOpacity(0);
        toast.setTranslateX(50);
        toast.setScaleX(0.92);
        toast.setScaleY(0.92);

        new ParallelTransition(
                fade(toast, 0, 1, 260),
                slideX(toast, 50, 0, 260),
                scale(toast, 0.92, 1.0, 260)
        ).play();

        PauseTransition tahan = new PauseTransition(Duration.seconds(3.5));
        tahan.setOnFinished(e -> {
            ParallelTransition keluar = new ParallelTransition(
                    fade(toast, 1, 0, 220),
                    slideX(toast, 0, 40, 220)
            );
            keluar.setOnFinished(ev -> rootOverlay.getChildren().remove(toast));
            keluar.play();
        });
        tahan.play();
    }

    /**
     * Tutorial Spotlight Interaktif:
     * Layar lain menjadi gelap, hanya fitur yang ditutorialkan yang terang benderang,
     * disertai panah memantul & kartu penjelasan di samping fitur tersebut.
     */
    public static void tampilkanSpotlightTutorial(StackPane rootOverlay, List<LangkahSpotlight> steps, Runnable onSelesai) {
        if (steps == null || steps.isEmpty()) return;

        Pane spotlightLayer = new Pane();
        spotlightLayer.setPickOnBounds(true);

        // Layer gelap dengan lubang transparan (Even-Odd FillRule)
        Path darkMask = new Path();
        darkMask.setFill(Color.rgb(10, 22, 15, 0.72));
        darkMask.setStroke(null);
        darkMask.setFillRule(FillRule.EVEN_ODD);

        // Bingkai cahaya hijau di sekeliling fitur yang sedang aktif
        Rectangle highlightRing = new Rectangle();
        highlightRing.setArcWidth(20);
        highlightRing.setArcHeight(20);
        highlightRing.setFill(Color.TRANSPARENT);
        highlightRing.setStroke(Color.web("#4ADE80"));
        highlightRing.setStrokeWidth(3);

        // Animasi denyut cahaya pada bingkai
        ScaleTransition ringPulse = new ScaleTransition(Duration.millis(900), highlightRing);
        ringPulse.setFromX(1.0);
        ringPulse.setFromY(1.0);
        ringPulse.setToX(1.02);
        ringPulse.setToY(1.02);
        ringPulse.setAutoReverse(true);
        ringPulse.setCycleCount(Animation.INDEFINITE);
        ringPulse.play();

        // Panah animasi memantul yang menunjuk ke target
        Label lblPanah = new Label("⬅");
        lblPanah.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: #4ADE80; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 10, 0, 0, 2);");

        TranslateTransition bounceArrow = new TranslateTransition(Duration.millis(550), lblPanah);
        bounceArrow.setFromX(0);
        bounceArrow.setToX(12);
        bounceArrow.setAutoReverse(true);
        bounceArrow.setCycleCount(Animation.INDEFINITE);
        bounceArrow.play();

        // Kartu penjelasan di samping fitur
        VBox card = new VBox(12);
        card.getStyleClass().add("modal-card");
        card.setPrefWidth(360);
        card.setMaxWidth(360);
        card.setPadding(new Insets(20));

        Label lblStep = new Label();
        lblStep.getStyleClass().add("tutorial-pill");

        Label lblJudul = new Label();
        lblJudul.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #102117;");
        lblJudul.setWrapText(true);

        Label lblDesc = new Label();
        lblDesc.setStyle("-fx-font-size: 12.5px; -fx-text-fill: #4A5A51; -fx-line-spacing: 2.5;");
        lblDesc.setWrapText(true);

        HBox dotsBox = new HBox(5);
        dotsBox.setAlignment(Pos.CENTER_LEFT);

        Button btnSkip = new Button("Lewati");
        btnSkip.getStyleClass().add("btn-ghost");
        btnSkip.setPrefHeight(34);

        Button btnNext = new Button("Lanjut →");
        btnNext.getStyleClass().add("btn-primary");
        btnNext.setPrefHeight(34);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox footer = new HBox(8, dotsBox, spacer, btnSkip, btnNext);
        footer.setAlignment(Pos.CENTER);

        card.getChildren().addAll(lblStep, lblJudul, lblDesc, footer);
        spotlightLayer.getChildren().addAll(darkMask, highlightRing, lblPanah, card);
        rootOverlay.getChildren().add(spotlightLayer);

        final int[] idx = {0};

        Runnable posisiSpotlight = () -> {
            LangkahSpotlight step = steps.get(idx[0]);
            if (step.sebelumLangkah() != null) {
                step.sebelumLangkah().run();
            }
            rootOverlay.applyCss();
            rootOverlay.layout();

            lblStep.setText(step.badge());
            lblJudul.setText(step.judul());
            lblDesc.setText(step.deskripsi());
            btnNext.setText(idx[0] == steps.size() - 1 ? "Selesai 🚀" : "Lanjut →");

            dotsBox.getChildren().clear();
            for (int i = 0; i < steps.size(); i++) {
                Region dot = new Region();
                dot.setMinSize(i == idx[0] ? 20 : 7, 7);
                dot.setMaxSize(i == idx[0] ? 20 : 7, 7);
                dot.setStyle(i == idx[0]
                        ? "-fx-background-color: #2E7D4F; -fx-background-radius: 99;"
                        : "-fx-background-color: #D0DBD4; -fx-background-radius: 99;");
                dotsBox.getChildren().add(dot);
            }

            double W = rootOverlay.getWidth() > 0 ? rootOverlay.getWidth() : 1280;
            double H = rootOverlay.getHeight() > 0 ? rootOverlay.getHeight() : 720;

            Node target = step.targetSupplier() != null ? step.targetSupplier().get() : null;
            if (target != null && target.getScene() != null) {
                Bounds b = rootOverlay.sceneToLocal(target.localToScene(target.getBoundsInLocal()));
                double pad = 8;
                double tx = Math.max(4, b.getMinX() - pad);
                double ty = Math.max(4, b.getMinY() - pad);
                double tw = Math.min(W - tx - 4, b.getWidth() + pad * 2);
                double th = Math.min(H - ty - 4, b.getHeight() + pad * 2);

                // Buat lubang terang tepat di area target
                darkMask.getElements().setAll(
                        new MoveTo(0, 0), new LineTo(W, 0), new LineTo(W, H), new LineTo(0, H), new ClosePath(),
                        new MoveTo(tx, ty), new LineTo(tx + tw, ty), new LineTo(tx + tw, ty + th), new LineTo(tx, ty + th), new ClosePath()
                );

                highlightRing.setVisible(true);
                highlightRing.setX(tx);
                highlightRing.setY(ty);
                highlightRing.setWidth(tw);
                highlightRing.setHeight(th);

                lblPanah.setVisible(true);

                // Tentukan apakah kartu ditaruh di kanan, kiri, atau bawah target agar tidak keluar layar
                if (tx + tw + 430 < W) {
                    // Taruh di SEBELAH KANAN fitur
                    lblPanah.setText("⬅");
                    lblPanah.setLayoutX(tx + tw + 12);
                    lblPanah.setLayoutY(ty + Math.min(th / 2 - 20, 40));

                    card.setLayoutX(tx + tw + 56);
                    card.setLayoutY(Math.max(20, Math.min(ty, H - 250)));
                } else if (tx - 430 > 0) {
                    // Taruh di SEBELAH KIRI fitur
                    lblPanah.setText("➡");
                    lblPanah.setLayoutX(tx - 46);
                    lblPanah.setLayoutY(ty + Math.min(th / 2 - 20, 40));

                    card.setLayoutX(tx - 416);
                    card.setLayoutY(Math.max(20, Math.min(ty, H - 250)));
                } else {
                    // Taruh di BAWAH fitur
                    lblPanah.setText("⬆");
                    lblPanah.setLayoutX(tx + tw / 2 - 16);
                    lblPanah.setLayoutY(ty + th + 8);

                    card.setLayoutX(Math.max(20, Math.min(tx, W - 380)));
                    card.setLayoutY(Math.min(ty + th + 48, H - 240));
                }
            } else {
                // Fallback jika target tidak ditemukan
                darkMask.getElements().setAll(
                        new MoveTo(0, 0), new LineTo(W, 0), new LineTo(W, H), new LineTo(0, H), new ClosePath()
                );
                highlightRing.setVisible(false);
                lblPanah.setVisible(false);
                card.setLayoutX((W - 360) / 2);
                card.setLayoutY((H - 220) / 2);
            }

            // Animasi membesar pada kartu setiap pindah langkah
            card.setScaleX(0.9);
            card.setScaleY(0.9);
            ScaleTransition pop = new ScaleTransition(Duration.millis(240), card);
            pop.setToX(1.0);
            pop.setToY(1.0);
            pop.setInterpolator(Interpolator.SPLINE(0.18, 0.89, 0.32, 1.15));
            pop.play();
        };

        // Jalankan sedikit tertunda agar layout sudah selesai dihitung JavaFX
        PauseTransition initDelay = new PauseTransition(Duration.millis(120));
        initDelay.setOnFinished(e -> posisiSpotlight.run());
        initDelay.play();

        btnNext.setOnAction(e -> {
            if (idx[0] < steps.size() - 1) {
                idx[0]++;
                posisiSpotlight.run();
            } else {
                ringPulse.stop();
                bounceArrow.stop();
                rootOverlay.getChildren().remove(spotlightLayer);
                if (onSelesai != null) onSelesai.run();
            }
        });

        btnSkip.setOnAction(e -> {
            ringPulse.stop();
            bounceArrow.stop();
            rootOverlay.getChildren().remove(spotlightLayer);
        });
    }

    private static void animasiBukaModal(Node backdrop, Node dialog) {
        backdrop.setOpacity(0);
        dialog.setOpacity(0);
        dialog.setScaleX(0.76);
        dialog.setScaleY(0.76);

        FadeTransition bgFade = fade(backdrop, 0, 1, 220);
        FadeTransition dlgFade = fade(dialog, 0, 1, 260);
        ScaleTransition dlgScale = new ScaleTransition(Duration.millis(300), dialog);
        dlgScale.setFromX(0.76);
        dlgScale.setFromY(0.76);
        dlgScale.setToX(1.0);
        dlgScale.setToY(1.0);
        dlgScale.setInterpolator(Interpolator.SPLINE(0.18, 0.89, 0.32, 1.15));

        new ParallelTransition(bgFade, dlgFade, dlgScale).play();
    }

    private static void animasiTutupModal(StackPane parent, Node backdrop, Node dialog) {
        FadeTransition bgFade = fade(backdrop, 1, 0, 180);
        FadeTransition dlgFade = fade(dialog, 1, 0, 160);
        ScaleTransition dlgScale = scale(dialog, 1.0, 0.85, 180);

        ParallelTransition out = new ParallelTransition(bgFade, dlgFade, dlgScale);
        out.setOnFinished(e -> parent.getChildren().remove(backdrop));
        out.play();
    }

    private static FadeTransition fade(Node n, double from, double to, double ms) {
        FadeTransition ft = new FadeTransition(Duration.millis(ms), n);
        ft.setFromValue(from);
        ft.setToValue(to);
        return ft;
    }

    private static TranslateTransition slideX(Node n, double from, double to, double ms) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(ms), n);
        tt.setFromX(from);
        tt.setToX(to);
        tt.setInterpolator(Interpolator.SPLINE(0.22, 1, 0.36, 1));
        return tt;
    }

    private static ScaleTransition scale(Node n, double from, double to, double ms) {
        ScaleTransition st = new ScaleTransition(Duration.millis(ms), n);
        st.setFromX(from);
        st.setFromY(from);
        st.setToX(to);
        st.setToY(to);
        return st;
    }
}