package view;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class MascotView extends StackPane {

    public enum Karakter {
        ADMIN_PRIA,
        WARGA_WANITA
    }

    private final Group karakterGroup = new Group();
    private Ellipse mataKiri;
    private Ellipse mataKanan;
    private Group lenganKanan;
    private Ellipse bayanganBawah;
    private Timeline blinkTimeline;

    public MascotView(Karakter karakter, double skala) {
        setAlignment(Pos.CENTER);
        setPickOnBounds(false);

        bangunKarakter(karakter);

        karakterGroup.setScaleX(skala);
        karakterGroup.setScaleY(skala);

        // Bayangan lembut di bawah maskot
        bayanganBawah = new Ellipse(0, 88 * skala, 44 * skala, 9 * skala);
        bayanganBawah.setFill(Color.rgb(6, 78, 59, 0.22));

        getChildren().addAll(bayanganBawah, karakterGroup);
        mulaiAnimasiHidup();
    }

    private void bangunKarakter(Karakter karakter) {
        boolean isAdmin = (karakter == Karakter.ADMIN_PRIA);

        // 1. Aura / Lingkaran Latar Belakang Lembut
        Circle auraLuar = new Circle(0, 5, 74);
        auraLuar.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(16, 185, 129, 0.16)),
                new Stop(1, Color.rgb(5, 150, 105, 0.05))));
        auraLuar.setStroke(Color.rgb(16, 185, 129, 0.35));
        auraLuar.setStrokeWidth(1.5);

        // 2. Rambut Belakang (khusus Wanita)
        Group rambutBelakang = new Group();
        if (!isAdmin) {
            Ellipse rambutPanjang = new Ellipse(0, 2, 46, 54);
            rambutPanjang.setFill(Color.web("#1E293B"));
            Circle ikalKiri = new Circle(-34, 34, 18, Color.web("#1E293B"));
            Circle ikalKanan = new Circle(34, 34, 18, Color.web("#1E293B"));
            rambutBelakang.getChildren().addAll(rambutPanjang, ikalKiri, ikalKanan);
        }

        // 3. Lengan Kiri (diam santai)
        Rectangle lenganKiri = new Rectangle(-46, 28, 16, 40);
        lenganKiri.setArcWidth(16);
        lenganKiri.setArcHeight(16);
        lenganKiri.setFill(isAdmin ? Color.web("#047857") : Color.web("#10B981"));
        lenganKiri.setRotate(14);
        Circle tanganKiri = new Circle(-43, 66, 8, Color.web("#FDBA74"));

        // 4. Lengan Kanan (akan dianimasikan melambai)
        lenganKanan = new Group();
        Rectangle batangLengan = new Rectangle(30, 14, 16, 38);
        batangLengan.setArcWidth(16);
        batangLengan.setArcHeight(16);
        batangLengan.setFill(isAdmin ? Color.web("#047857") : Color.web("#10B981"));
        Circle tanganKanan = new Circle(38, 12, 8.5, Color.web("#FDBA74"));
        lenganKanan.getChildren().addAll(batangLengan, tanganKanan);
        lenganKanan.getTransforms().add(new Rotate(-25, 34, 46));

        // 5. Badan / Baju
        Rectangle badan = new Rectangle(-32, 24, 64, 54);
        badan.setArcWidth(28);
        badan.setArcHeight(28);
        badan.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                isAdmin
                        ? new Stop[]{new Stop(0, Color.web("#059669")), new Stop(1, Color.web("#064E3B"))}
                        : new Stop[]{new Stop(0, Color.web("#34D399")), new Stop(1, Color.web("#059669"))}
        ));

        // Kerah & Detail Baju
        Polygon kerah = new Polygon(-14, 24, 14, 24, 0, 42);
        kerah.setFill(Color.WHITE);

        // Lencana Daun Daur Ulang di Dada
        Circle badge = new Circle(15, 44, 7, Color.web("#ECFDF5"));
        Ellipse daunBadge = new Ellipse(15, 44, 4, 2.5);
        daunBadge.setFill(Color.web("#10B981"));
        daunBadge.setRotate(-30);

        // 6. Leher & Kepala
        Rectangle leher = new Rectangle(-9, 14, 18, 16);
        leher.setArcWidth(8);
        leher.setArcHeight(8);
        leher.setFill(Color.web("#F59E0B").interpolate(Color.web("#FDE68A"), 0.65));

        Circle telingaKiri = new Circle(-34, -8, 7, Color.web("#FDBA74"));
        Circle telingaKanan = new Circle(34, -8, 7, Color.web("#FDBA74"));

        Ellipse kepala = new Ellipse(0, -12, 34, 36);
        kepala.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#FED7AA")),
                new Stop(1, Color.web("#FDBA74"))));

        // 7. Wajah (Mata, Pipi, Senyum)
        mataKiri = new Ellipse(-12, -12, 4.2, 5.2);
        mataKiri.setFill(Color.web("#0F172A"));
        Circle kilauKiri = new Circle(-13.5, -14, 1.5, Color.WHITE);

        mataKanan = new Ellipse(12, -12, 4.2, 5.2);
        mataKanan.setFill(Color.web("#0F172A"));
        Circle kilauKanan = new Circle(10.5, -14, 1.5, Color.WHITE);

        Ellipse pipiKiri = new Ellipse(-20, -3, 5.5, 3.2);
        pipiKiri.setFill(Color.rgb(244, 63, 94, 0.28));
        Ellipse pipiKanan = new Ellipse(20, -3, 5.5, 3.2);
        pipiKanan.setFill(Color.rgb(244, 63, 94, 0.28));

        Arc senyum = new Arc(0, -3, 9, 6.5, 195, 150);
        senyum.setType(ArcType.OPEN);
        senyum.setFill(Color.TRANSPARENT);
        senyum.setStroke(Color.web("#7C2D12"));
        senyum.setStrokeWidth(2.6);
        senyum.setStrokeLineCap(StrokeLineCap.ROUND);

        // 8. Rambut Depan & Aksesori Khas Gender
        Group rambutDepan = new Group();
        if (isAdmin) {
            // Rambut Pria Rapi + Kacamata Profesional
            Arc rambutPria = new Arc(0, -22, 36, 28, 0, 180);
            rambutPria.setFill(Color.web("#0F172A"));
            Polygon jambul = new Polygon(-12, -48, 8, -54, 18, -42, -4, -36);
            jambul.setFill(Color.web("#0F172A"));

            Rectangle kacaKiri = new Rectangle(-23, -19, 19, 13);
            kacaKiri.setArcWidth(6);
            kacaKiri.setArcHeight(6);
            kacaKiri.setFill(Color.rgb(255, 255, 255, 0.18));
            kacaKiri.setStroke(Color.web("#064E3B"));
            kacaKiri.setStrokeWidth(2);

            Rectangle kacaKanan = new Rectangle(4, -19, 19, 13);
            kacaKanan.setArcWidth(6);
            kacaKanan.setArcHeight(6);
            kacaKanan.setFill(Color.rgb(255, 255, 255, 0.18));
            kacaKanan.setStroke(Color.web("#064E3B"));
            kacaKanan.setStrokeWidth(2);

            Rectangle jembatanKaca = new Rectangle(-4, -14, 8, 2);
            jembatanKaca.setFill(Color.web("#064E3B"));

            rambutDepan.getChildren().addAll(rambutPria, jambul, kacaKiri, kacaKanan, jembatanKaca);
        } else {
            // Poni Wanita + Jepit Rambut Daun Hijau
            Arc poniKiri = new Arc(-10, -24, 28, 24, 25, 155);
            poniKiri.setFill(Color.web("#1E293B"));
            Arc poniKanan = new Arc(14, -24, 24, 22, 0, 145);
            poniKanan.setFill(Color.web("#1E293B"));

            // Jepit Daun
            Ellipse daun1 = new Ellipse(24, -34, 7, 3.8);
            daun1.setFill(Color.web("#10B981"));
            daun1.setRotate(-35);
            Ellipse daun2 = new Ellipse(29, -30, 6, 3.2);
            daun2.setFill(Color.web("#34D399"));
            daun2.setRotate(15);

            rambutDepan.getChildren().addAll(poniKiri, poniKanan, daun1, daun2);
        }

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(6, 78, 59, 0.25));
        shadow.setRadius(14);
        shadow.setOffsetY(6);
        karakterGroup.setEffect(shadow);

        karakterGroup.getChildren().addAll(
                auraLuar,
                rambutBelakang,
                lenganKiri, tanganKiri,
                lenganKanan,
                badan, kerah, badge, daunBadge,
                leher,
                telingaKiri, telingaKanan,
                kepala,
                mataKiri, kilauKiri, mataKanan, kilauKanan,
                pipiKiri, pipiKanan, senyum,
                rambutDepan
        );
    }

    private void mulaiAnimasiHidup() {
        // 1. Animasi Melayang Naik-Turun (Floating)
        TranslateTransition floatAnim = new TranslateTransition(Duration.millis(1800), karakterGroup);
        floatAnim.setFromY(0);
        floatAnim.setToY(-9);
        floatAnim.setAutoReverse(true);
        floatAnim.setCycleCount(Animation.INDEFINITE);
        floatAnim.setInterpolator(Interpolator.EASE_BOTH);
        floatAnim.play();

        // Bayangan mengecil saat karakter melayang naik
        ScaleTransition shadowScale = new ScaleTransition(Duration.millis(1800), bayanganBawah);
        shadowScale.setFromX(1.0);
        shadowScale.setToX(0.84);
        shadowScale.setAutoReverse(true);
        shadowScale.setCycleCount(Animation.INDEFINITE);
        shadowScale.setInterpolator(Interpolator.EASE_BOTH);
        shadowScale.play();

        // 2. Animasi Tangan Melambai Ramah
        RotateTransition waveAnim = new RotateTransition(Duration.millis(650), lenganKanan);
        waveAnim.setFromAngle(-14);
        waveAnim.setToAngle(18);
        waveAnim.setAutoReverse(true);
        waveAnim.setCycleCount(Animation.INDEFINITE);
        waveAnim.setInterpolator(Interpolator.EASE_BOTH);
        waveAnim.play();

        // 3. Animasi Mata Berkedip Natural setiap 3.4 detik
        blinkTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(mataKiri.scaleYProperty(), 1.0),
                        new KeyValue(mataKanan.scaleYProperty(), 1.0)),
                new KeyFrame(Duration.millis(3100),
                        new KeyValue(mataKiri.scaleYProperty(), 1.0),
                        new KeyValue(mataKanan.scaleYProperty(), 1.0)),
                new KeyFrame(Duration.millis(3200),
                        new KeyValue(mataKiri.scaleYProperty(), 0.08),
                        new KeyValue(mataKanan.scaleYProperty(), 0.08)),
                new KeyFrame(Duration.millis(3320),
                        new KeyValue(mataKiri.scaleYProperty(), 1.0),
                        new KeyValue(mataKanan.scaleYProperty(), 1.0))
        );
        blinkTimeline.setCycleCount(Animation.INDEFINITE);
        blinkTimeline.play();
    }

    /**
     * Animasi memantul keren saat maskot muncul atau berganti karakter (Admin <-> Warga)
     */
    public void animasiMasukMemantul() {
        karakterGroup.setOpacity(0);
        double targetScale = karakterGroup.getScaleX();
        karakterGroup.setScaleX(targetScale * 0.55);
        karakterGroup.setScaleY(targetScale * 0.55);

        javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(Duration.millis(320), karakterGroup);
        fade.setFromValue(0);
        fade.setToValue(1);

        ScaleTransition bounce = new ScaleTransition(Duration.millis(480), karakterGroup);
        bounce.setToX(targetScale);
        bounce.setToY(targetScale);
        bounce.setInterpolator(Interpolator.SPLINE(0.18, 0.89, 0.32, 1.28));

        new ParallelTransition(fade, bounce).play();
    }
}