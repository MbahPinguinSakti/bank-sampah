import java.util.List;
import model.Kertas;
import model.Logam;
import model.Plastik;
import model.SaldoTidakCukupException;
import model.Sampah;
import model.Transaksi;
import model.Warga;

/**
 * Demo konsol Langkah 0 untuk menguji class model.
 * Nanti (P11) isi main() diganti untuk membuka MainFrame.
 */
public class Main {

    public static void main(String[] args) {
        Warga budi = new Warga("W001", "Budi", "Jl. Melati 1");

        // Polimorfisme: semua disimpan sebagai Sampah, tapi hitungPoin() yang
        // terpanggil adalah milik masing-masing subclass.
        List<Sampah> setoran = List.of(new Plastik(2.0), new Kertas(3.5), new Logam(5.0));

        System.out.println("== Setor sampah ==");
        for (Sampah s : setoran) {
            Transaksi t = Transaksi.setor(budi.getIdWarga(), s);
            budi.tambahPoin(t.getPoin());
            System.out.println(s + "  -> saldo " + budi.getSaldoPoin());
        }

        System.out.println("\n== Tukar poin ==");
        try {
            budi.kurangiPoin(100);
            System.out.println("Tukar 100 poin berhasil, saldo " + budi.getSaldoPoin());
            budi.kurangiPoin(1000);
        } catch (SaldoTidakCukupException e) {
            System.out.println("Gagal: " + e.getMessage());
        }

        System.out.println("\n== Validasi ==");
        try {
            new Plastik(-1);
        } catch (IllegalArgumentException e) {
            System.out.println("Gagal: " + e.getMessage());
        }
    }
}
