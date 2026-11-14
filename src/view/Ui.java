package view;

import java.util.function.Function;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.Transaksi;


public final class Ui {
    private Ui() {
        // Private constructor to prevent instantiation
    }

    public static VBox card(String judul, String subjudul, Node kanan){
        Label lbljudul = new Label(judul);
        lbljudul.getStyleClass().add("card-title");

        Label lblsub = new Label(subjudul);
        lblsub.getStyleClass().add("card-subtitle");

        HBox header = new HBox(12, new VBox(2, lbljudul, lblsub));

        header.setAlignment(Pos.CENTER_LEFT);

        if (kanan != null) {
            header.getChildren().addAll(spacer(), kanan);
        }

        VBox card = new VBox(16, header);
        card.getStyleClass().add("card");   
        return card;
    }

    public static VBox card(String judul, String subjudul){
        return card(judul, subjudul, null);
    }

    public static VBox field(String label, Region input){
        Label lbl = new Label(label);
        lbl.getStyleClass().add("field-label");
        input.setMaxWidth(Double.MAX_VALUE);

        return new VBox (6, lbl, input);
    }


    public static Label hint(String text){
        Label lbl = new Label(text);
        lbl.getStyleClass().add("hint");
        lbl.setWrapText(true);
        return lbl;
    }

    public static Button tombol(String text, String StyleClass){
        Button btn = new Button(text);
        btn.getStyleClass().add(StyleClass);
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    public static Region spacer(){
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    public static <T> TableColumn<T, String> kolom(String judul, Function<T, String> isi, double lebar){
        javafx.scene.control.TableColumn<T, String> col = new TableColumn<>(judul);
        col.setCellValueFactory(data -> new SimpleStringProperty(isi.apply(data.getValue())));
        col.setPrefWidth(lebar);
        return col;
    }

    public static TableColumn<Transaksi, String> kolomPoin() {
        TableColumn<Transaksi, String> col = kolom("Poin",
                t -> (t.getTipe() == Transaksi.Tipe.SETOR ? "+" : "−") + t.getPoin(), 80);
        col.getStyleClass().add("col-angka");
        col.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("poin-plus", "poin-minus");
                setText(empty ? null : item);
                if (!empty) {
                    getStyleClass().add(item.startsWith("+") ? "poin-plus" : "poin-minus");
                }
            }
        });
        return col;
    }

    public static TableColumn<Transaksi,String> kolomTipe(){
        
        TableColumn<Transaksi, String> col = kolom("Tipe", t -> t.getTipe().name(), 90);
        col.setCellFactory(c -> new TableCell<>(){
            @Override 
            protected void updateItem(String item, boolean empty){
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().addAll("badge", "badge-" + item.toLowerCase());
                    setGraphic(badge);
                }
            }
        });
        return col;
    }
}