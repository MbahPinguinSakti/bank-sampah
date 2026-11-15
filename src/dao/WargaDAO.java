package dao;

import db.DatabaseConnection;
import model.Warga;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WargaDAO {
    
    public List<Warga> findAll() throws SQLException {
        List<Warga> list = new ArrayList<>();
        String query = "SELECT id, nama, alamat, saldo FROM warga";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Warga w = new Warga(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4));
                list.add(w);
            }
        }
        return list;
    }

    public void insert(Warga w) throws SQLException {
        String query = "INSERT INTO warga (id, nama, alamat, saldo) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, w.getIdWarga()); 
            stmt.setString(2, w.getNama());
            stmt.setString(3, w.getAlamat());
            stmt.setInt(4, w.getSaldoPoin());
            stmt.executeUpdate();
        }
    }

    public void updateSaldo(Connection conn, String id, int saldoBaru) throws SQLException {
        String query = "UPDATE warga SET saldo = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, saldoBaru);
            stmt.setString(2, id);
            stmt.executeUpdate();
        }
    }
    
    public void update(Warga w) throws SQLException {
        String query = "UPDATE warga SET nama = ?, alamat = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, w.getNama());
            stmt.setString(2, w.getAlamat());
            stmt.setString(3, w.getIdWarga()); 
            stmt.executeUpdate();
        }
    }

    public void delete(String id) throws SQLException {
        String query = "DELETE FROM warga WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }
}