-- Skema database Bank Sampah (MariaDB / MySQL)
-- Jalankan sebagai admin:  sudo mariadb < sql/bank_sampah.sql
-- Nama kolom harus sama dengan query di src/dao/WargaDAO.java dan TransaksiDAO.java.

CREATE DATABASE IF NOT EXISTS bank_sampah;
CREATE USER IF NOT EXISTS 'bank_sampah'@'localhost' IDENTIFIED BY 'rahasia123';
GRANT ALL PRIVILEGES ON bank_sampah.* TO 'bank_sampah'@'localhost';
USE bank_sampah;

CREATE TABLE IF NOT EXISTS warga (
    id      VARCHAR(20) PRIMARY KEY,
    nama    VARCHAR(100) NOT NULL,
    alamat  VARCHAR(255),
    saldo   INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS transaksi (
    id                 INT PRIMARY KEY AUTO_INCREMENT,
    jenis              ENUM('SETOR','TUKAR') NOT NULL,
    warga_id           VARCHAR(20) NOT NULL,
    jenis_sampah       VARCHAR(20) NULL,
    berat              DECIMAL(6,2) NULL,
    poin_atau_nominal  INT NOT NULL,
    tanggal            DATETIME NOT NULL,
    FOREIGN KEY (warga_id) REFERENCES warga(id)
);
