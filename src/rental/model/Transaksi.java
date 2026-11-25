package rental.model;

import java.time.LocalDate;
import java.util.ArrayList;
import rental.payment.Pembayaran;

/**
 * Merepresentasikan satu transaksi penyewaan perangkat iPhone dan alat fotografi.
 * Menyimpan informasi penyewa, daftar barang yang disewa, tanggal sewa, tanggal jatuh tempo,
 * tanggal pengembalian, metode pembayaran, serta status transaksi.
 *
 * @version 1.0
 */
public class Transaksi {

    private String id;
    private Penyewa penyewa;
    private ArrayList<Barang> barang;
    private int lamaSewa;
    private LocalDate tanggalSewa;
    private LocalDate tanggalJatuhTempo;
    private LocalDate tanggalKembali;
    private String status;
    private Pembayaran pembayaran;
    private Invoice invoice;

    /**
     * Membuat objek Transaksi baru ketika penyewa melakukan checkout.
     *
     * @param id          kode unik transaksi
     * @param penyewa     akun {@link Penyewa} yang melakukan transaksi
     * @param barang      daftar {@link Barang} yang disewa
     * @param lamaSewa    durasi sewa dalam hari
     * @param tanggalSewa tanggal mulai penyewaan
     * @param pembayaran  objek metode {@link Pembayaran} yang dipilih
     */
    public Transaksi(String id, Penyewa penyewa, ArrayList<Barang> barang, int lamaSewa,
                     LocalDate tanggalSewa, Pembayaran pembayaran) {
        this.id = id;
        this.penyewa = penyewa;
        this.barang = barang;
        this.lamaSewa = lamaSewa;
        this.tanggalSewa = tanggalSewa;
        this.tanggalJatuhTempo = tanggalSewa.plusDays(lamaSewa);
        this.tanggalKembali = null;
        this.status = "MENUNGGU_KONFIRMASI";
        this.pembayaran = pembayaran;
    }

    /**
     * Menghitung subtotal biaya sewa seluruh barang dalam transaksi ini (sebelum denda).
     *
     * @return total biaya sewa dalam Rupiah
     */
    public double hitungTotalSewa() {
        double total = 0.0;
        for (Barang b : barang) {
            total += b.hitungBiaya(lamaSewa);
        }
        return total;
    }