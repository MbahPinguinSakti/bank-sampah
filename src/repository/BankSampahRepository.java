package repository;

import dao.TransaksiDAO;
import dao.WargaDAO;
import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import model.SaldoTidakCukupException;
import model.Sampah;
import model.Transaksi;
import model.Warga;

public class BankSampahRepository {
    
    private Map<String, Warga> dataWarga = new LinkedHashMap<>();
    private List<Transaksi> riwayatTransaksi = new ArrayList<>();
    private List<String> jenisSampah = Arrays.asList("Plastik", "Kertas", "Logam");
    
    private WargaDAO wargaDAO = new WargaDAO();
    private TransaksiDAO transaksiDAO = new TransaksiDAO();

    public BankSampahRepository() {
        loadDataDariDatabase();
    }

    private void loadDataDariDatabase() {
        try {
            List<Warga> wargasList = wargaDAO.findAll();
            for (Warga w : wargasList) {
                dataWarga.put(w.getIdWarga(), w);
            }

            List<Transaksi> transaksiList = transaksiDAO.findAll();
            riwayatTransaksi.addAll(transaksiList);
        } catch (SQLException e) {
            System.err.println("Gagal memuat data awal dari database: " + e.getMessage());
        }
    }

    public List<String> getJenisSampah() {
        return jenisSampah;
    }

    // --- CRUD WARGA ---

    public void tambahWarga(Warga warga) {
        if (dataWarga.containsKey(warga.getIdWarga())) {
            throw new IllegalArgumentException("Error: ID Warga '" + warga.getIdWarga() + "' sudah terdaftar.");
        }
        try {
            wargaDAO.insert(warga);
            dataWarga.put(warga.getIdWarga(), warga);
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal menyimpan warga ke database: " + e.getMessage(), e);
        }
    }

    public Warga cariWarga(String idWarga) {
        return dataWarga.get(idWarga);
    }

    public List<Warga> semuaWarga() {
        return new ArrayList<>(dataWarga.values());
    }

    public void ubahWarga(String idWarga, String namaBaru, String alamatBaru) {
        Warga warga = cariWarga(idWarga);
        if (warga == null) {
            throw new IllegalArgumentException("Error: Warga tidak ditemukan.");
        }
        
        String namaLama = warga.getNama();
        String alamatLama = warga.getAlamat();
        
        try {
            warga.setNama(namaBaru);
            warga.setAlamat(alamatBaru);
            wargaDAO.update(warga);
        } catch (SQLException e) {
            warga.setNama(namaLama);
            warga.setAlamat(alamatLama);
            throw new IllegalStateException("Gagal update warga ke database: " + e.getMessage(), e);
        }
    }

    public void hapusWarga(String idWarga) {
        Warga warga = cariWarga(idWarga);
        if (warga == null) {
            throw new IllegalArgumentException("Error: Warga tidak ditemukan.");
        }
        
        boolean punyaTransaksi = riwayatTransaksi.stream().anyMatch(t -> t.getIdWarga().equals(idWarga));
        if (punyaTransaksi) {
            throw new IllegalStateException("Error: Tidak bisa menghapus warga karena memiliki riwayat transaksi.");
        }

        try {
            wargaDAO.delete(idWarga);
            dataWarga.remove(idWarga);
        } catch (SQLException e) {
            throw new IllegalStateException("Gagal hapus warga di database: " + e.getMessage(), e);
        }
    }

    // --- TRANSAKSI (SETOR & TUKAR) PAKE DATABASE TRANSACTION ---

    public void setor(String idWarga, Sampah sampah) {
        Warga warga = cariWarga(idWarga);
        if (warga == null) {
            throw new IllegalArgumentException("Error: Warga tidak ditemukan.");
        }
        
        Transaksi transaksi = Transaksi.setor(idWarga, sampah); 

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int saldoBaru = warga.getSaldoPoin() + transaksi.getPoin();
                wargaDAO.updateSaldo(conn, idWarga, saldoBaru);
                transaksiDAO.insert(conn, transaksi);
                
                conn.commit();
                
                warga.tambahPoin(transaksi.getPoin());
                riwayatTransaksi.add(transaksi);
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Transaksi setor gagal: " + e.getMessage(), e);
        }
    }

    public void tukar(String idWarga, int poinTukar) throws SaldoTidakCukupException {
        Warga warga = cariWarga(idWarga);
        if (warga == null) {
            throw new IllegalArgumentException("Error: Warga tidak ditemukan.");
        }

        if (warga.getSaldoPoin() < poinTukar) {
            throw new SaldoTidakCukupException(warga.getSaldoPoin(), poinTukar);
        }

        Transaksi transaksi = Transaksi.tukar(idWarga, poinTukar);

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int saldoBaru = warga.getSaldoPoin() - poinTukar;
                wargaDAO.updateSaldo(conn, idWarga, saldoBaru);
                transaksiDAO.insert(conn, transaksi);
                
                conn.commit();
                
                warga.kurangiPoin(poinTukar);
                riwayatTransaksi.add(transaksi);
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Transaksi tukar gagal: " + e.getMessage(), e);
        }
    }

    // --- RIWAYAT TRANSAKSI ---

    public List<Transaksi> semuaTransaksi() {
        return new ArrayList<>(riwayatTransaksi);
    }

    public List<Transaksi> transaksiWarga(String idWarga) {
        return riwayatTransaksi.stream()
            .filter(t -> t.getIdWarga().equals(idWarga))
            .collect(Collectors.toList());
    }
}