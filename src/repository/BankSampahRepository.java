package repository;

import model.Warga;
import model.SaldoTidakCukupException;
import model.Sampah;
import model.Transaksi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BankSampahRepository {
    
    // 1. In-memory storage menggunakan Collection Framework
    private Map<String, Warga> dataWarga = new LinkedHashMap<>();
    private List<Transaksi> riwayatTransaksi = new ArrayList<>();
    private List<String> jenisSampah = Arrays.asList("Plastik", "Kertas", "Logam");
    
    private int autoIncrementIdTransaksi = 1;

    public List<String> getJenisSampah() {
        return jenisSampah;
    }

    // --- CRUD WARGA ---

    public void tambahWarga(Warga warga) {
        if (dataWarga.containsKey(warga.getIdWarga())) {
            throw new IllegalArgumentException("Error: ID Warga '" + warga.getIdWarga() + "' sudah terdaftar.");
        }
        dataWarga.put(warga.getIdWarga(), warga);
    }

    public Warga cariWarga(String idWarga) {
        return dataWarga.get(idWarga);
    }

    public List<Warga> semuaWarga() {
        return new ArrayList<>(dataWarga.values());
    }

    public void ubahWarga(String idWarga, String namaBaru, String alamatBaru) {
        Warga warga = cariWarga(idWarga);
        if (warga != null) {
            warga.setNama(namaBaru);
            warga.setAlamat(alamatBaru);
        } else {
            throw new IllegalArgumentException("Error: Warga tidak ditemukan.");
        }
    }

    public void hapusWarga(String idWarga) {
        // Cek apakah warga memiliki transaksi. Jika ada, tolak penghapusan untuk menjaga riwayat
        try {
            Warga warga = cariWarga(idWarga);
            if (warga == null) {
                throw new IllegalArgumentException("Error: Warga tidak ditemukan.");
            }
        } catch (IllegalArgumentException e) {
            throw e; // lempar lagi jika warga tidak ditemukan
        }
        
        boolean punyaTransaksi = riwayatTransaksi.stream().anyMatch(t -> t.getIdWarga().equals(idWarga));
        
        if (punyaTransaksi) {
            throw new IllegalStateException("Error: Tidak bisa menghapus warga karena memiliki riwayat transaksi.");
        }

        dataWarga.remove(idWarga);
    }

    // --- TRANSAKSI (SETOR & TUKAR) ---

    public void setor(String idWarga, Sampah sampah) {
        Warga warga = cariWarga(idWarga);
        if (warga == null) {
            throw new IllegalArgumentException("Error: Warga tidak ditemukan.");
        }
        Transaksi transaksi = Transaksi.setor(idWarga, sampah); 

        // Polimorfisme: hitungPoin() dipanggil tanpa perlu tahu jenis spesifik sampahnya
        warga.tambahPoin(transaksi.getPoin());
        

        transaksi.setId(autoIncrementIdTransaksi++);  
        riwayatTransaksi.add(transaksi);
    }

    public void tukar(String idWarga, int poinTukar) throws SaldoTidakCukupException {
        Warga warga = cariWarga(idWarga);
        if (warga == null) {
            throw new IllegalArgumentException("Error: Warga tidak ditemukan.");
        }

        // kurangiPoin() di class Warga harus melempar SaldoTidakCukupException jika saldo kurang
        warga.kurangiPoin(poinTukar);

        // Catat ke riwayat
        Transaksi transaksi = Transaksi.tukar(idWarga, poinTukar);
        transaksi.setId(autoIncrementIdTransaksi++);
        riwayatTransaksi.add(transaksi);
    }

    // --- RIWAYAT TRANSAKSI ---

    public List<Transaksi> semuaTransaksi() {
        return new ArrayList<>(riwayatTransaksi);
    }

    public List<Transaksi> transaksiWarga(String idWarga) {
        // Filter riwayat khusus untuk satu warga
        return riwayatTransaksi.stream()
                .filter(t -> t.getIdWarga().equals(idWarga))
                .collect(Collectors.toList());
    }
}