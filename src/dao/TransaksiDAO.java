package dao;

import db.DatabaseConnection;
import model.Sampah;
import model.Transaksi;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransaksiDAO {

    public void insert(Connection conn, Transaksi t) throws SQLException {
        String query = "INSERT INTO transaksi (jenis, warga_id, jenis_sampah, berat, poin_atau_nominal, tanggal) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, t.getJenis());
            stmt.setString(2, t.getIdWarga());
            
            if (t.getSampah() != null) {
                stmt.setString(3, t.getSampah().getNama()); // Asumsi pakai getNama() atau getJenis() tergantung class Sampah lo
                stmt.setDouble(4, t.getSampah().getBerat());
            } else {
                stmt.setNull(3, Types.VARCHAR);
                stmt.setNull(4, Types.DOUBLE);
            }
            
            stmt.setInt(5, t.getPoinAtauNominal());
            stmt.setTimestamp(6, Timestamp.valueOf(t.getTanggal()));
            
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    t.setId(rs.getInt(1)); 
                }
            }
        }
    }

    public List<Transaksi> findAll() throws SQLException {
        List<Transaksi> list = new ArrayList<>();
        String query = "SELECT id, jenis, warga_id, jenis_sampah, berat, poin_atau_nominal, tanggal FROM transaksi";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Sampah sampah = null;
                if (rs.getString("jenis_sampah") != null) {
                    // Pastikan constructor (String jenis/nama, double berat) ada di class Sampah
                    // Kalau class Sampah berbentuk abstract, lo mungkin perlu menyesuaikan ini dengan class turunannya.
                    // Untuk sementara, kita asumsikan bisa di-instantiate atau sesuaikan dengan struktur Sampah lo.
                    // sampah = new Sampah(rs.getString("jenis_sampah"), rs.getDouble("berat"));
                }
                
                Transaksi t = Transaksi.dariDatabase(
                    rs.getInt("id"),
                    rs.getString("jenis"),
                    rs.getString("warga_id"),
                    sampah,
                    rs.getInt("poin_atau_nominal"),
                    rs.getTimestamp("tanggal").toLocalDateTime()
                );
                list.add(t);
            }
        }
        return list;
    }
}