package view;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class LoginView {

    private final StackPane root = new StackPane();
    private final ToggleButton btnModeAdmin = new ToggleButton("🛡  Masuk Admin");
    private final ToggleButton btnModeWarga = new ToggleButton("🌿  Masuk Warga");

    private final VBox boxAdmin = new VBox(14);
    private final TextField txtUsername = new TextField();
    private final PasswordField txtPassword = new PasswordField();

    private final VBox boxWarga = new VBox(14);
    private final TextField txtIdWarga = new TextField();

    private final Label lblError = new Label();
    private final Button btnLogin = new Button("Masuk ke Dashboard  →");

    // Komponen visual yang berubah warna sesuai peran
    private final StackPane leftHeroPane = new StackPane();
    private final StackPane mascotStage = new StackPane();
    private final Label lblNamaMaskot = new Label("MAS ECO • PETUGAS ADMIN");
    private final Label lblSapaanMaskot = new Label("Halo, Administrator! Siap kelola data setoran & warga hari ini?");
    private final MascotView maskotAdmin = new MascotView(MascotView.Karakter.ADMIN_PRIA, 1.32);
    private final MascotView maskotWarga = new MascotView(MascotView.Karakter.WARGA_WANITA, 1.32);

    public LoginView() {
        // Latar belakang utama bergradien lembut hijau-biru-emas (bukan abu-abu/silver)
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #064E3B, #047857, #0D9488);");
        root.setPadding(new Insets(32));

        // Ornamen lingkaran cahaya melayang di background luar
        Pane bgOrnaments = new Pane();
        bgOrnaments.setMouseTransparent(true);
        Circle orb1 = new Circle(220, Color.rgb(52, 211, 153, 0.16));
        orb1.setLayoutX(160);
        orb1.setLayoutY(140);
        Circle orb2 = new Circle(280, Color.rgb(250, 204, 21, 0.10));
        orb2.setLayoutX(1120);
        orb2.setLayoutY(620);
        bgOrnaments.getChildren().addAll(orb1, orb2);
        animasiOrnamen(orb1, 16);
        animasiOrnamen(orb2, -20);

        // Kartu Utama Split-Screen (Kiri: Panggung Maskot Berwarna, Kanan: Form Login)
        HBox mainContainer = new HBox();
        mainContainer.setMaxWidth(940);
        mainContainer.setMaxHeight(560);
        mainContainer.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 28;" +
                "-fx-border-color: rgba(255,255,255,0.45);" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 28;"
        );
        DropShadow cardShadow = new DropShadow();
        cardShadow.setColor(Color.rgb(2, 44, 34, 0.45));
        cardShadow.setRadius(36);
        cardShadow.setOffsetY(14);
        mainContainer.setEffect(cardShadow);

        // ==================== PANEL KIRI: PANGGUNG MASKOT ====================
        leftHeroPane.setPrefWidth(420);
        leftHeroPane.setMinWidth(400);
        leftHeroPane.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #059669, #065F46, #0F766E);" +
                "-fx-background-radius: 26 0 0 26;"
        );

        // Dekorasi lingkaran kaca di dalam panel kiri
        Pane heroDecor = new Pane();
        heroDecor.setMouseTransparent(true);
        Circle c1 = new Circle(130, Color.rgb(255, 255, 255, 0.08));
        c1.setLayoutX(70);
        c1.setLayoutY(90);
        Circle c2 = new Circle(95, Color.rgb(167, 243, 208, 0.12));
        c2.setLayoutX(340);
        c2.setLayoutY(470);
        heroDecor.getChildren().addAll(c1, c2);

        VBox heroContent = new VBox(16);
        heroContent.setAlignment(Pos.CENTER);
        heroContent.setPadding(new Insets(32));

        Label brandTag = new Label("♻  ECO-DIGITAL BANK SAMPAH");
        brandTag.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.18);" +
                "-fx-text-fill: #ECFDF5;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 11.5px;" +
                "-fx-padding: 6 14;" +
                "-fx-background-radius: 999;"
        );

        mascotStage.setPrefHeight(225);
        mascotStage.setAlignment(Pos.CENTER);
        mascotStage.getChildren().add(maskotAdmin);

        lblNamaMaskot.setStyle(
                "-fx-background-color: #FDE047;" +
                "-fx-text-fill: #064E3B;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 11px;" +
                "-fx-padding: 4 12;" +
                "-fx-background-radius: 999;"
        );

        lblSapaanMaskot.setStyle(
                "-fx-text-fill: #FFFFFF;" +
                "-fx-font-size: 14.5px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-alignment: center;" +
                "-fx-line-spacing: 3px;"
        );
        lblSapaanMaskot.setWrapText(true);
        lblSapaanMaskot.setMaxWidth(320);
        lblSapaanMaskot.setAlignment(Pos.CENTER);

        Label subHero = new Label("Ubah sampah terpilah menjadi saldo poin bermanfaat & resi digital otomatis.");
        subHero.setStyle("-fx-text-fill: #A7F3D0; -fx-font-size: 12px; -fx-text-alignment: center;");
        subHero.setWrapText(true);
        subHero.setMaxWidth(300);
        subHero.setAlignment(Pos.CENTER);

        heroContent.getChildren().addAll(brandTag, mascotStage, lblNamaMaskot, lblSapaanMaskot, subHero);
        leftHeroPane.getChildren().addAll(heroDecor, heroContent);

        // ==================== PANEL KANAN: FORM LOGIN ====================
        VBox rightFormPane = new VBox(18);
        HBox.setHgrow(rightFormPane, Priority.ALWAYS);
        rightFormPane.setAlignment(Pos.CENTER_LEFT);
        rightFormPane.setPadding(new Insets(40, 44, 40, 44));
        rightFormPane.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 0 26 26 0;");

        Label title = new Label("Selamat Datang!");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Label subtitle = new Label("Pilih jenis akun Anda untuk masuk ke sistem Bank Sampah");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");
        subtitle.setWrapText(true);

        VBox headerBox = new VBox(4, title, subtitle);

        // Toggle Role Pill Bar (Bebas Silver!)
        ToggleGroup group = new ToggleGroup();
        btnModeAdmin.setToggleGroup(group);
        btnModeWarga.setToggleGroup(group);
        btnModeAdmin.setSelected(true);

        btnModeAdmin.setMaxWidth(Double.MAX_VALUE);
        btnModeWarga.setMaxWidth(Double.MAX_VALUE);
        btnModeAdmin.setPrefHeight(40);
        btnModeWarga.setPrefHeight(40);
        HBox.setHgrow(btnModeAdmin, Priority.ALWAYS);
        HBox.setHgrow(btnModeWarga, Priority.ALWAYS);

        HBox roleBar = new HBox(6, btnModeAdmin, btnModeWarga);
        roleBar.setPadding(new Insets(5));
        roleBar.setStyle("-fx-background-color: #F1F5F9; -fx-background-radius: 14;");

        // Form Admin
        txtUsername.setPromptText("Masukkan username (default: admin)");
        txtPassword.setPromptText("Masukkan password (default: admin123)");
        styleInputModern(txtUsername, "#10B981");
        styleInputModern(txtPassword, "#10B981");
        boxAdmin.getChildren().addAll(
                buatField("Username Administrator", txtUsername),
                buatField("Kata Sandi", txtPassword)
        );

        // Form Warga
        txtIdWarga.setPromptText("Contoh: W001 atau W006");
        styleInputModern(txtIdWarga, "#0EA5E9");
        Label hintWarga = new Label("💡 Masuk menggunakan ID Warga untuk mengecek tabungan poin & mencetak struk PDF.");
        hintWarga.setWrapText(true);
        hintWarga.setStyle(
                "-fx-background-color: #F0F9FF;" +
                "-fx-text-fill: #0369A1;" +
                "-fx-font-size: 12px;" +
                "-fx-padding: 10 12;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: #BAE6FD;" +
                "-fx-border-radius: 10;"
        );
        boxWarga.getChildren().addAll(
                buatField("ID Anggota Warga", txtIdWarga),
                hintWarga
        );
        boxWarga.setVisible(false);
        boxWarga.setManaged(false);

        lblError.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 12.5px; -fx-font-weight: bold;");
        lblError.setWrapText(true);

        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setPrefHeight(46);

        // Terapkan tema warna awal (Admin)
        terapkanTemaPeran(true);

        // Event ganti tab Admin <-> Warga
        btnModeAdmin.setOnAction(e -> {
            if (!btnModeAdmin.isSelected()) btnModeAdmin.setSelected(true);
            setMode(true);
        });
        btnModeWarga.setOnAction(e -> {
            if (!btnModeWarga.isSelected()) btnModeWarga.setSelected(true);
            setMode(false);
        });

        Region spacer = new Region();
        spacer.setPrefHeight(4);

        rightFormPane.getChildren().addAll(
                headerBox,
                roleBar,
                boxAdmin,
                boxWarga,
                lblError,
                spacer,
                btnLogin
        );

        mainContainer.getChildren().addAll(leftHeroPane, rightFormPane);
        root.getChildren().addAll(bgOrnaments, mainContainer);
        maskotAdmin.animasiMasukMemantul();
    }

    private void setMode(boolean admin) {
        lblError.setText("");
        boxAdmin.setVisible(admin);
        boxAdmin.setManaged(admin);
        boxWarga.setVisible(!admin);
        boxWarga.setManaged(!admin);

        terapkanTemaPeran(admin);

        mascotStage.getChildren().clear();
        if (admin) {
            mascotStage.getChildren().add(maskotAdmin);
            lblNamaMaskot.setText("MAS ECO • PETUGAS ADMIN");
            lblSapaanMaskot.setText("Halo, Administrator! Siap kelola data setoran & warga hari ini?");
            maskotAdmin.animasiMasukMemantul();
        } else {
            mascotStage.getChildren().add(maskotWarga);
            lblNamaMaskot.setText("MBAK HIJAU • SAHABAT WARGA");
            lblSapaanMaskot.setText("Hai Warga Hebat! Yuk cek saldo poin & tukarkan hadiahmu!");
            maskotWarga.animasiMasukMemantul();
        }
    }

    private void terapkanTemaPeran(boolean admin) {
        String activeTabStyle =
                "-fx-background-color: " + (admin
                        ? "linear-gradient(to right, #10B981, #059669);"
                        : "linear-gradient(to right, #0EA5E9, #0284C7);") +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.14), 6, 0, 0, 2);";

        String inactiveTabStyle =
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #475569;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;";

        if (admin) {
            btnModeAdmin.setStyle(activeTabStyle);
            btnModeWarga.setStyle(inactiveTabStyle);
            leftHeroPane.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, #059669, #065F46, #0F766E);" +
                    "-fx-background-radius: 26 0 0 26;"
            );
            btnLogin.setStyle(
                    "-fx-background-color: linear-gradient(to right, #10B981, #047857);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 14px;" +
                    "-fx-background-radius: 12;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, rgba(5, 150, 105, 0.38), 14, 0, 0, 5);"
            );
        } else {
            btnModeWarga.setStyle(activeTabStyle);
            btnModeAdmin.setStyle(inactiveTabStyle);
            leftHeroPane.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, #0284C7, #0D9488, #059669);" +
                    "-fx-background-radius: 26 0 0 26;"
            );
            btnLogin.setStyle(
                    "-fx-background-color: linear-gradient(to right, #0EA5E9, #0D9488);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 14px;" +
                    "-fx-background-radius: 12;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, rgba(14, 165, 233, 0.38), 14, 0, 0, 5);"
            );
        }
    }

    private void styleInputModern(TextField field, String focusColor) {
        String base =
                "-fx-background-color: #F8FAFC;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 11;" +
                "-fx-background-radius: 11;" +
                "-fx-padding: 11 14;" +
                "-fx-font-size: 13.5px;" +
                "-fx-text-fill: #0F172A;";
        String focused =
                "-fx-background-color: #FFFFFF;" +
                "-fx-border-color: " + focusColor + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 11;" +
                "-fx-background-radius: 11;" +
                "-fx-padding: 11 14;" +
                "-fx-font-size: 13.5px;" +
                "-fx-text-fill: #0F172A;" +
                "-fx-effect: dropshadow(gaussian, rgba(16,185,129,0.18), 8, 0, 0, 2);";
        field.setStyle(base);
        field.focusedProperty().addListener((obs, oldVal, isFocused) -> field.setStyle(isFocused ? focused : base));
    }

    private VBox buatField(String labelText, TextField field) {
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-font-size: 12.5px; -fx-font-weight: bold; -fx-text-fill: #334155;");
        return new VBox(6, lbl, field);
    }

    private void animasiOrnamen(Circle orb, double deltaY) {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(4.5), orb);
        tt.setFromY(0);
        tt.setToY(deltaY);
        tt.setAutoReverse(true);
        tt.setCycleCount(Animation.INDEFINITE);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();
    }

    public Parent getRoot() { return root; }
    public boolean isModeAdmin() { return btnModeAdmin.isSelected(); }
    public String getUsername() { return txtUsername.getText().trim(); }
    public String getPassword() { return txtPassword.getText(); }
    public String getIdWarga() { return txtIdWarga.getText().trim(); }
    public Button getBtnLogin() { return btnLogin; }
    public void setError(String msg) { lblError.setText(msg); }
}