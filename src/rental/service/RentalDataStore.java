package rental.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import rental.model.Admin;
import rental.model.Akun;
import rental.model.AksesorisFotografi;
import rental.model.Barang;
import rental.model.DaftarBarang;
import rental.model.IPhone;
import rental.model.Invoice;
import rental.model.Kamera;
import rental.model.Penyewa;
import rental.model.Transaksi;
import rental.payment.Pembayaran;
import rental.payment.QRIS;
import rental.payment.TransferBank;
import rental.payment.Tunai;

/**
 * Layanan penyimpanan data persisten berbasis file teks (.txt) untuk sistem Rental.
 * Mengelola pembacaan dan penulisan data akun, katalog barang, dan riwayat transaksi
 * ke dalam direktori {@code data/}.
 *
 * @author [Nama Anggota 3] - [NIM 3]
 * @version 1.0
 */
public class RentalDataStore {

    private static final String DATA_DIR = "data";
    private static final String FILE_AKUN = DATA_DIR + "/akun.txt";
    private static final String FILE_BARANG = DATA_DIR + "/barang.txt";
    private static final String FILE_TRANSAKSI = DATA_DIR + "/transaksi.txt";

    private final List<Akun> daftarAkun;
    private final DaftarBarang daftarBarang;
    private final List<Transaksi> daftarTransaksi;

    /**
     * Menginisialisasi data store dan memuat seluruh data dari file .txt.
     * Jika file belum tersedia, data awal (seed) akan otomatis dibuat.
     */
    public RentalDataStore() {
        this.daftarAkun = new ArrayList<>();
        this.daftarBarang = new DaftarBarang();
        this.daftarTransaksi = new ArrayList<>();
        ensureDirectory();
        loadAkun();
        loadBarang();
        loadTransaksi();
    }

    private void ensureDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File invDir = new File(DATA_DIR + "/invoices");
        if (!invDir.exists()) {
            invDir.mkdirs();
        }
