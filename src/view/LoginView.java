package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class LoginView {

    private final StackPane root = new StackPane();
    private final ToggleButton btnRoleAdmin = new ToggleButton("Masuk sebagai Admin");
    private final ToggleButton btnRoleWarga = new ToggleButton("Masuk sebagai Warga");

    private final VBox boxAdmin = new VBox(12);
    private final TextField txtUsername = new TextField();
    private final PasswordField txtPassword = new PasswordField();

    private final VBox boxWarga = new VBox(12);
    private final TextField txtIdWarga = new TextField();

    private final Label lblError = new Label();
    private final Button btnLogin = new Button("Masuk ke Aplikasi");

    public LoginView() {
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #E8F3EC, #F4F7F4);");

        VBox card = new VBox(18);
        card.getStyleClass().add("card");
        card.setMaxWidth(410);
        card.setMaxHeight(Region.USE_PREF_SIZE);
        card.setPadding(new Insets(32));
        card.setAlignment(Pos.TOP_CENTER);

        // Logo tetap sama seperti header utama
        Label logo = new Label("♻");
        logo.getStyleClass().add("logo");
        logo.setMinSize(52, 52);
        logo.setStyle("-fx-font-size: 26px;");

        Label title = new Label("Bank Sampah");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #141E18;");

        Label subtitle = new Label("Silakan pilih peran untuk melanjutkan");
        subtitle.getStyleClass().add("card-subtitle");

        // Switcher Role Admin vs Warga
        ToggleGroup roleGroup = new ToggleGroup();
        btnRoleAdmin.setToggleGroup(roleGroup);
        btnRoleWarga.setToggleGroup(roleGroup);
        btnRoleAdmin.setSelected(true);

        btnRoleAdmin.getStyleClass().add("chip");
        btnRoleWarga.getStyleClass().add("chip");
        btnRoleAdmin.setMaxWidth(Double.MAX_VALUE);
        btnRoleWarga.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnRoleAdmin, Priority.ALWAYS);
        HBox.setHgrow(btnRoleWarga, Priority.ALWAYS);

        HBox roleBar = new HBox(8, btnRoleAdmin, btnRoleWarga);
        roleBar.setAlignment(Pos.CENTER);

        // Form Admin
        txtUsername.setPromptText("Username (default: admin)");
        txtPassword.setPromptText("Password (default: admin123)");
        boxAdmin.getChildren().addAll(
                buatField("Username Admin", txtUsername),
                buatField("Password", txtPassword)
        );

        // Form Warga
        txtIdWarga.setPromptText("Masukkan ID Warga (contoh: W001)");
        Label hintWarga = new Label("Warga dapat melihat saldo poin, riwayat transaksi, dan mencetak resi PDF.");
        hintWarga.getStyleClass().add("hint");
        hintWarga.setWrapText(true);
        boxWarga.getChildren().addAll(
                buatField("ID Warga Terdaftar", txtIdWarga),
                hintWarga
        );
        boxWarga.setVisible(false);
        boxWarga.setManaged(false);

        // Toggle tampilan form sesuai peran
        roleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                oldVal.setSelected(true);
                return;
            }
            boolean isAdmin = (newVal == btnRoleAdmin);
            boxAdmin.setVisible(isAdmin);
            boxAdmin.setManaged(isAdmin);
            boxWarga.setVisible(!isAdmin);
            boxWarga.setManaged(!isAdmin);
            lblError.setText("");
            UiAnimator.animasiMasuk(isAdmin ? boxAdmin : boxWarga);
        });

        lblError.setStyle("-fx-text-fill: #B42318; -fx-font-size: 12px; -fx-font-weight: bold;");

        btnLogin.getStyleClass().add("btn-primary");
        btnLogin.setMaxWidth(Double.MAX_VALUE);

        card.getChildren().addAll(logo, title, subtitle, roleBar, boxAdmin, boxWarga, lblError, btnLogin);
        root.getChildren().add(card);
        UiAnimator.animasiMasuk(card);
    }

    private VBox buatField(String labelText, Control input) {
        Label lbl = new Label(labelText);
        lbl.getStyleClass().add("field-label");
        VBox box = new VBox(5, lbl, input);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    public Parent getRoot() { return root; }
    public boolean isModeAdmin() { return btnRoleAdmin.isSelected(); }
    public String getUsername() { return txtUsername.getText().trim(); }
    public String getPassword() { return txtPassword.getText(); }
    public String getIdWarga() { return txtIdWarga.getText().trim(); }
    public Button getBtnLogin() { return btnLogin; }
    public void setError(String msg) { lblError.setText(msg); }
}