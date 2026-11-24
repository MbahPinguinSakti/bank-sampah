package view;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.util.Duration;

import java.util.List;

public class UiAnimator {

    /**
     * Memasang semua animasi interaktif (hover, press, tab transition, entrance)
     * secara otomatis ke seluruh elemen UI di dalam root.
     */
    public static void inisialisasiInteraktif(Parent root) {
        // 1. Animasi transisi saat berpindah tab
        Node tabNode = root.lookup(".tab-pane");
        if (tabNode instanceof TabPane tabPane) {
            pasangAnimasiTab(tabPane);
        }

        // 2. Efek melayang halus pada kotak statistik header (.stat) & kartu tarif (.tarif)
        root.lookupAll(".stat").forEach(node -> pasangEfekHoverAngkat(node, -3.0, 1.02));
        root.lookupAll(".tarif").forEach(node -> pasangEfekHoverAngkat(node, -2.5, 1.015));

        // 3. Efek pegas (spring press & hover) pada semua tombol (.button) dan chip (.chip)
        root.lookupAll(".button").forEach(UiAnimator::pasangEfekTombol);
        root.lookupAll(".chip").forEach(UiAnimator::pasangEfekTombol);

        // 4. Animasi kartu masuk berurutan saat aplikasi pertama kali terbuka
        animasiKartuBerurutan(root);
    }

    /**
     * Animasi perpindahan tab: Fade-In + Slide-Up halus (280ms).
     */
    public static void pasangAnimasiTab(TabPane tabPane) {
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null && newTab.getContent() != null) {
                Node content = newTab.getContent();
                animasiMasuk(content);
                if (content instanceof Parent parent) {
                    animasiKartuBerurutan(parent);
                }
            }
        });
    }

    /**
     * Memunculkan elemen dengan transisi Fade + Slide dari bawah ke atas.
     */
    public static void animasiMasuk(Node node) {
        node.setOpacity(0);
        node.setTranslateY(14);

        FadeTransition fade = new FadeTransition(Duration.millis(280), node);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(280), node);
        slide.setFromY(14);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.SPLINE(0.22, 1, 0.36, 1)); // Smooth cubic-bezier

        new ParallelTransition(fade, slide).play();
    }

    /**
     * Memunculkan setiap .card secara bergelombang (staggered cascade).
     */
    public static void animasiKartuBerurutan(Parent root) {
        List<Node> cards = root.lookupAll(".card").stream().toList();
        for (int i = 0; i < cards.size(); i++) {
            Node card = cards.get(i);
            card.setOpacity(0);
            card.setTranslateY(18);

            PauseTransition delay = new PauseTransition(Duration.millis(i * 75L));
            delay.setOnFinished(e -> animasiMasuk(card));
            delay.play();
        }
    }

    /**
     * Efek kartu terangkat halus & membesar sedikit saat disorot mouse.
     */
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

    /**
     * Efek tombol taktil: sedikit membesar saat hover, membal mengecil saat diklik.
     */
    public static void pasangEfekTombol(Node btn) {
        btn.setOnMouseEntered(e -> {
            ScaleTransition sc = new ScaleTransition(Duration.millis(130), btn);
            sc.setToX(1.02);
            sc.setToY(1.02);
            sc.play();
        });
        btn.setOnMouseExited(e -> {
            ScaleTransition sc = new ScaleTransition(Duration.millis(130), btn);
            sc.setToX(1.0);
            sc.setToY(1.0);
            sc.play();
        });
        btn.setOnMousePressed(e -> {
            ScaleTransition sc = new ScaleTransition(Duration.millis(90), btn);
            sc.setToX(0.96);
            sc.setToY(0.96);
            sc.play();
        });
        btn.setOnMouseReleased(e -> {
            ScaleTransition sc = new ScaleTransition(Duration.millis(120), btn);
            sc.setToX(btn.isHover() ? 1.02 : 1.0);
            sc.setToY(btn.isHover() ? 1.02 : 1.0);
            sc.play();
        });
    }

    /**
     * Animasi angka berjalan (rolling counter) untuk statistik integer.
     */
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

    /**
     * Animasi angka berjalan untuk berat sampah (%.1f kg).
     */
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