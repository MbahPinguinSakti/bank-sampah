package view;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.util.List;

public class UiAnimator {

    /**
     * Animasi layar utama membesar (Zoom-In / Expand) saat aplikasi dibuka atau ganti halaman.
     */
    public static void animasiLayarMembesar(Node root) {
        root.setOpacity(0);
        root.setScaleX(0.92);
        root.setScaleY(0.92);

        FadeTransition fade = new FadeTransition(Duration.millis(340), root);
        fade.setFromValue(0);
        fade.setToValue(1);

        ScaleTransition expand = new ScaleTransition(Duration.millis(380), root);
        expand.setFromX(0.92);
        expand.setFromY(0.92);
        expand.setToX(1.0);
        expand.setToY(1.0);
        expand.setInterpolator(Interpolator.SPLINE(0.16, 1, 0.3, 1));

        new ParallelTransition(fade, expand).play();
    }

    /**
     * Memasang semua animasi interaktif secara otomatis ke seluruh elemen UI.
     */
    public static void inisialisasiInteraktif(Parent root) {
        root.lookupAll(".stat").forEach(node -> pasangEfekHoverAngkat(node, -4.0, 1.03));
        root.lookupAll(".tarif").forEach(node -> pasangEfekHoverAngkat(node, -3.0, 1.02));
        root.lookupAll(".button").forEach(UiAnimator::pasangEfekTombol);
        root.lookupAll(".chip").forEach(UiAnimator::pasangEfekTombol);
        root.lookupAll(".nav-item").forEach(UiAnimator::pasangEfekTombol);

        animasiKartuBerurutan(root);
    }

    /**
     * Memunculkan halaman konten dengan kombinasi Scale-Up + Slide-Up halus.
     */
    public static void animasiGantiHalaman(Node node) {
        node.setOpacity(0);
        node.setTranslateY(16);
        node.setScaleX(0.98);
        node.setScaleY(0.98);

        FadeTransition fade = new FadeTransition(Duration.millis(260), node);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(260), node);
        slide.setFromY(16);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.SPLINE(0.22, 1, 0.36, 1));

        ScaleTransition scale = new ScaleTransition(Duration.millis(260), node);
        scale.setFromX(0.98);
        scale.setFromY(0.98);
        scale.setToX(1.0);
        scale.setToY(1.0);

        new ParallelTransition(fade, slide, scale).play();

        if (node instanceof Parent parent) {
            animasiKartuBerurutan(parent);
        }
    }

    public static void animasiMasuk(Node node) {
        node.setOpacity(0);
        node.setTranslateY(14);

        FadeTransition fade = new FadeTransition(Duration.millis(280), node);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(280), node);
        slide.setFromY(14);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.SPLINE(0.22, 1, 0.36, 1));

        new ParallelTransition(fade, slide).play();
    }

    public static void animasiKartuBerurutan(Parent root) {
        List<Node> cards = root.lookupAll(".card").stream().toList();
        for (int i = 0; i < cards.size(); i++) {
            Node card = cards.get(i);
            card.setOpacity(0);
            card.setTranslateY(18);
            card.setScaleX(0.97);
            card.setScaleY(0.97);

            PauseTransition delay = new PauseTransition(Duration.millis(i * 70L));
            delay.setOnFinished(e -> {
                FadeTransition f = new FadeTransition(Duration.millis(260), card);
                f.setToValue(1);
                TranslateTransition t = new TranslateTransition(Duration.millis(260), card);
                t.setToY(0);
                ScaleTransition s = new ScaleTransition(Duration.millis(260), card);
                s.setToX(1.0);
                s.setToY(1.0);
                new ParallelTransition(f, t, s).play();
            });
            delay.play();
        }
    }

    public static void pasangEfekHoverAngkat(Node node, double offsetY, double scale) {
        node.setOnMouseEntered(e -> {
            TranslateTransition up = new TranslateTransition(Duration.millis(180), node);
            up.setToY(offsetY);
            ScaleTransition sc = new ScaleTransition(Duration.millis(180), node);
            sc.setToX(scale);
            sc.setToY(scale);
            new ParallelTransition(up, sc).play();
        });

        node.setOnMouseExited(e -> {
            TranslateTransition down = new TranslateTransition(Duration.millis(180), node);
            down.setToY(0);
            ScaleTransition sc = new ScaleTransition(Duration.millis(180), node);
            sc.setToX(1.0);
            sc.setToY(1.0);
            new ParallelTransition(down, sc).play();
        });
    }

    public static void pasangEfekTombol(Node btn) {
        btn.setOnMouseEntered(e -> {
            ScaleTransition sc = new ScaleTransition(Duration.millis(130), btn);
            sc.setToX(1.03);
            sc.setToY(1.03);
            sc.play();
        });
        btn.setOnMouseExited(e -> {
            ScaleTransition sc = new ScaleTransition(Duration.millis(130), btn);
            sc.setToX(1.0);
            sc.setToY(1.0);
            sc.play();
        });
        btn.setOnMousePressed(e -> {
            ScaleTransition sc = new ScaleTransition(Duration.millis(85), btn);
            sc.setToX(0.95);
            sc.setToY(0.95);
            sc.play();
        });
        btn.setOnMouseReleased(e -> {
            ScaleTransition sc = new ScaleTransition(Duration.millis(120), btn);
            sc.setToX(btn.isHover() ? 1.03 : 1.0);
            sc.setToY(btn.isHover() ? 1.03 : 1.0);
            sc.play();
        });
    }

    public static void animasiAngkaInt(Label label, int target) {
        int awal = 0;
        try {
            awal = Integer.parseInt(label.getText().replaceAll("[^0-9-]", ""));
        } catch (NumberFormatException ignored) {}

        final int startVal = awal;
        if (startVal == target) {
            label.setText(String.valueOf(target));
            return;
        }

        Transition counter = new Transition() {
            { setCycleDuration(Duration.millis(600)); setInterpolator(Interpolator.EASE_OUT); }
            @Override
            protected void interpolate(double frac) {
                int current = (int) Math.round(startVal + (target - startVal) * frac);
                label.setText(String.valueOf(current));
            }
        };
        counter.play();
    }

    public static void animasiAngkaKg(Label label, double targetKg) {
        double awal = 0.0;
        try {
            awal = Double.parseDouble(label.getText().replace("kg", "").replace(",", ".").trim());
        } catch (NumberFormatException ignored) {}

        final double startVal = awal;
        Transition counter = new Transition() {
            { setCycleDuration(Duration.millis(600)); setInterpolator(Interpolator.EASE_OUT); }
            @Override
            protected void interpolate(double frac) {
                double current = startVal + (targetKg - startVal) * frac;
                label.setText(String.format("%.1f kg", current));
            }
        };
        counter.play();
    }
}