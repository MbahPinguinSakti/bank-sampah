-- 1. Buat database
CREATE DATABASE IF NOT EXISTS bank_sampah;
USE bank_sampah;

-- 2. Buat user & berikan hak akses (sesuai DatabaseConnection)
CREATE USER IF NOT EXISTS 'bank_sampah'@'localhost' IDENTIFIED BY 'rahasia123';
GRANT ALL PRIVILEGES ON bank_sampah.* TO 'bank_sampah'@'localhost';
FLUSH PRIVILEGES;

-- 3. Buat tabel warga
CREATE TABLE IF NOT EXISTS warga (
    id VARCHAR(20) PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    alamat VARCHAR(255) NOT NULL,
    saldo INT NOT NULL DEFAULT 0
);

-- 4. Buat tabel transaksi
CREATE TABLE IF NOT EXISTS transaksi (
    id VARCHAR(50) PRIMARY KEY,
    jenis ENUM('SETOR', 'TUKAR') NOT NULL,
    warga_id VARCHAR(20) NOT NULL,
    jenis_sampah VARCHAR(50) NULL,
    berat DOUBLE NULL,
    poin_atau_nominal INT NOT NULL,
    tanggal DATETIME NOT NULL,
    CONSTRAINT fk_transaksi_warga FOREIGN KEY (warga_id) REFERENCES warga(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 5. Data contoh awal agar tabel aplikasi langsung terisi saat dibuka
INSERT IGNORE INTO warga (id, nama, alamat, saldo) VALUES
('W001', 'Budi Santoso', 'Jl. Melati No. 12', 200),
('W002', 'Siti Aminah', 'Jl. Kenanga No. 4', 320),
('W003', 'Agus Prasetyo', 'Jl. Mawar No. 7', 900),
('W004', 'Dewi Lestari', 'Jl. Anggrek No. 21', 0);

INSERT IGNORE INTO transaksi (id, jenis, warga_id, jenis_sampah, berat, poin_atau_nominal, tanggal) VALUES
('TRX-001', 'SETOR', 'W001', 'Plastik', 2.0, 200, NOW()),
('TRX-002', 'SETOR', 'W003', 'Logam', 5.0, 1000, NOW()),
('TRX-003', 'SETOR', 'W002', 'Kertas', 4.0, 320, NOW()),
('TRX-004', 'TUKAR', 'W003', NULL, NULL, 100, NOW());