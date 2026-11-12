import model.Kertas;
import model.Logam;
import model.Plastik;
import model.Warga;
import model.Transaksi;
import repository.BankSampahRepository;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== DEMO KONSOL BANK SAMPAH (FASE 1) ===");
        BankSampahRepository repo = new BankSampahRepository();

        // 1. Test Tambah Warga
        System.out.println("\n[1] Menambahkan Warga...");
        repo.tambahWarga(new Warga("W001", "Budi Santoso", "Jl. Mawar No 1"));
        repo.tambahWarga(new Warga("W002", "Siti Aminah", "Jl. Melati No 5"));
        
        for (Warga w : repo.semuaWarga()) {
            System.out.println("- " + w.getIdWarga() + " | " + w.getNama() + " | Saldo: " + w.getSaldoPoin());
        }

        // 2. Test Setor Sampah
        System.out.println("\n[2] Budi dan Siti menyetor sampah...");
        repo.setor("W001", new Plastik("Botol Plastik", 2.0)); // Plastik: misal 10 poin/kg -> 20 poin
        repo.setor("W002", new Logam("Pipa Besi", 5.0));       // Logam: 20 poin/kg + bonus 50 (krn >= 5kg) -> 150 poin
        repo.setor("W001", new Kertas("Koran Bekas", 4.0));    // Kertas: 5 poin/kg -> 20 poin

        System.out.println("Saldo Budi sekarang: " + repo.cariWarga("W001").getSaldoPoin());
        System.out.println("Saldo Siti sekarang: " + repo.cariWarga("W002").getSaldoPoin());

        // 3. Test Tukar Poin
        System.out.println("\n[3] Budi menukar 15 poin...");
        repo.tukar("W001", 15);
        System.out.println("Sisa Saldo Budi: " + repo.cariWarga("W001").getSaldoPoin());

        // 4. Test Riwayat Transaksi
        System.out.println("\n[4] Riwayat Transaksi Seluruhnya:");
        for (Transaksi t : repo.semuaTransaksi()) {
            System.out.println(t.getId() + " | " + t.getTipe() + " | Warga: " + t.getIdWarga() + " | Poin: " + t.getPoin());
        }

        System.out.println("\n[5] Riwayat Transaksi khusus Budi (W001):");
        for (Transaksi t : repo.transaksiWarga("W001")) {
            System.out.println(t.getId() + " | " + t.getTipe() + " | Poin: " + t.getPoin());
        }
    }
}