package util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import model.Transaksi;
import model.Warga;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

public class ResiPdfGenerator {

    private static final Font FONT_TITLE = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, new BaseColor(34, 96, 60));
    private static final Font FONT_SUB   = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.DARK_GRAY);
    private static final Font FONT_BOLD  = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
    private static final Font FONT_REG   = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    private static final Font FONT_POIN  = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, new BaseColor(46, 125, 79));

    /**
     * Mencetak resi transaksi ke PDF (ukuran struk A6) dan langsung membukanya.
     */
    public static File cetakResi(Transaksi t, Warga warga) throws Exception {
        // Simpan di folder C:\temp\bank-sampah-resi agar bebas dari blokir izin tulis OneDrive
        Path dirPath = Paths.get("C:", "temp", "bank-sampah-resi");
        Files.createDirectories(dirPath);

        String fileName = String.format("Resi_%s_%d.pdf", warga.getIdWarga(), System.currentTimeMillis() / 1000);
        File filePdf = dirPath.resolve(fileName).toFile();

        // Ukuran struk A6 (105mm x 148mm)
        Document doc = new Document(PageSize.A6, 18, 18, 20, 20);
        PdfWriter.getInstance(doc, new FileOutputStream(filePdf));
        doc.open();

        // Header Resi
        Paragraph title = new Paragraph("BANK SAMPAH DIGITAL", FONT_TITLE);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);

        Paragraph sub = new Paragraph("Bukti Transaksi Setoran & Penukaran Poin", FONT_SUB);
        sub.setAlignment(Element.ALIGN_CENTER);
        sub.setSpacingAfter(8);
        doc.add(sub);

        doc.add(new LineSeparator(1f, 100f, new BaseColor(200, 210, 204), Element.ALIGN_CENTER, -2));

        // Tabel Detail Transaksi
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);
        table.setWidths(new float[]{1.2f, 1.8f});

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        tambahBaris(table, "Tanggal", t.getTanggal().format(fmt));
        tambahBaris(table, "ID Warga", warga.getIdWarga());
        tambahBaris(table, "Nama Warga", warga.getNama());
        tambahBaris(table, "Jenis Transaksi", t.getTipe().name());

        if (t.getTipe() == Transaksi.Tipe.SETOR) {
            tambahBaris(table, "Jenis Sampah", t.getJenisSampah() != null ? t.getJenisSampah() : "-");
            tambahBaris(table, "Berat Sampah", String.format("%.2f kg", t.getBerat()));
        } else {
            String ket = (t.getJenisSampah() != null && !t.getJenisSampah().isBlank())
                    ? t.getJenisSampah()
                    : "Penukaran Saldo Poin";
            tambahBaris(table, "Keterangan", ket);
        }

        doc.add(table);
        doc.add(new LineSeparator(1f, 100f, new BaseColor(200, 210, 204), Element.ALIGN_CENTER, -2));

        // Ringkasan Poin
        String tanda = (t.getTipe() == Transaksi.Tipe.SETOR) ? "+" : "-";
        Paragraph pPoin = new Paragraph(
                String.format("Perubahan Poin : %s%d Poin", tanda, Math.abs(t.getPoin())),
                FONT_POIN
        );
        pPoin.setSpacingBefore(8);
        doc.add(pPoin);

        Paragraph pSaldo = new Paragraph(
                String.format("Sisa Saldo Poin : %d Poin", warga.getSaldoPoin()),
                FONT_BOLD
        );
        pSaldo.setSpacingAfter(14);
        doc.add(pSaldo);

        Paragraph footer = new Paragraph("Terima kasih telah menjaga lingkungan!\nSimpan resi ini sebagai bukti sah.", FONT_SUB);
        footer.setAlignment(Element.ALIGN_CENTER);
        doc.add(footer);

        doc.close();

        // Otomatis buka file PDF di aplikasi pembaca PDF bawaan Windows
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(filePdf);
        }
        return filePdf;
    }

    private static void tambahBaris(PdfPTable table, String label, String nilai) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, FONT_SUB));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setPaddingBottom(5);

        PdfPCell c2 = new PdfPCell(new Phrase(": " + nilai, FONT_REG));
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setPaddingBottom(5);

        table.addCell(c1);
        table.addCell(c2);
    }
}