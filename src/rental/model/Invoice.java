package rental.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * Merepresentasikan bukti tagihan (Invoice) resmi atas suatu transaksi sewa.
 * Bertanggung jawab menghitung denda keterlambatan per hari, menghitung grand total,
 * serta mencetak rincian invoice ke dalam file .txt.
 *
 * @author [Nama Anggota 3] - [NIM 3]
 * @version 1.0
 */
public class Invoice {

    /** Tarif denda keterlambatan tetap per hari (Rp 50.000/hari). */
    public static final double DENDA_PER_HARI = 50000.0;

    private String idInvoice;
    private Transaksi transaksi;
    private double total;
    private double denda;

    /**
     * Membuat instance Invoice baru yang terhubung dengan sebuah Transaksi.
     *
     * @param idInvoice kode unik invoice
     * @param transaksi objek {@link Transaksi} terkait
     */
    public Invoice(String idInvoice, Transaksi transaksi) {
        this.idInvoice = idInvoice;
        this.transaksi = transaksi;
        this.denda = 0.0;
        this.total = hitungTotal();
    }

    /**
     * Menghitung denda keterlambatan berdasarkan selisih hari antara tanggal jatuh tempo
     * dan tanggal pengembalian (atau tanggal hari ini apabila belum dikembalikan).
     *
     * @return nominal denda dalam Rupiah (0 jika tidak terlambat)
     */
    public double hitungDenda() {
        LocalDate pembanding = (transaksi.getTanggalKembali() != null)
                ? transaksi.getTanggalKembali()
                : LocalDate.now();
        long hariTerlambat = ChronoUnit.DAYS.between(transaksi.getTanggalJatuhTempo(), pembanding);
        if (hariTerlambat > 0) {
            this.denda = hariTerlambat * DENDA_PER_HARI;
        } else {
            this.denda = 0.0;
        }
        return this.denda;
    }

    /**
     * Menghitung grand total tagihan (subtotal sewa barang + denda keterlambatan).
     *
     * @return total tagihan akhir dalam Rupiah
     */
    public double hitungTotal() {
        hitungDenda();
        this.total = transaksi.hitungTotalSewa() + this.denda;
        return this.total;
