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
    }

    /**
     * Memuat daftar akun pengguna dari {@code data/akun.txt}.
     */
    public void loadAkun() {
        daftarAkun.clear();
        File file = new File(FILE_AKUN);
        if (!file.exists()) {
            seedAkunDefault();
            saveAkun();
            return;
        }
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] p = line.split("\\|");
                if (p.length >= 5) {
                    String role = p[0].trim();
                    String id = p[1].trim();
                    String user = p[2].trim();
                    String pass = p[3].trim();
                    String nama = p[4].trim();
                    if ("ADMIN".equalsIgnoreCase(role)) {
                        daftarAkun.add(new Admin(id, user, pass, nama));
                    } else {
                        daftarAkun.add(new Penyewa(id, user, pass, nama));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Gagal membaca akun.txt: " + e.getMessage());
        }
        if (daftarAkun.isEmpty()) {
            seedAkunDefault();
            saveAkun();
        }
    }

    private void seedAkunDefault() {
        daftarAkun.add(new Admin("ADM-01", "admin", "admin123", "Ethan Reynolds"));
        daftarAkun.add(new Penyewa("PNY-01", "penyewa", "penyewa123", "Raka Pratama"));
        daftarAkun.add(new Penyewa("PNY-02", "nadia", "nadia123", "Nadia Putri"));
    }

    /**
     * Menyimpan seluruh daftar akun ke dalam {@code data/akun.txt}.
     */
    public void saveAkun() {
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(FILE_AKUN), StandardCharsets.UTF_8))) {
            pw.println("# Format: ROLE|ID|USERNAME|PASSWORD|NAMA_LENGKAP");
            for (Akun a : daftarAkun) {
                pw.println(a.getRole() + "|" + a.getId() + "|" + a.getUsername() + "|"
                        + a.getPassword() + "|" + a.getNamaLengkap());
            }
        } catch (Exception e) {
            System.err.println("Gagal menyimpan akun.txt: " + e.getMessage());
        }
    }

    /**
     * Memuat katalog barang dari {@code data/barang.txt}.
     */
    public void loadBarang() {
        daftarBarang.getBarang().clear();
        File file = new File(FILE_BARANG);
        if (!file.exists()) {
            seedBarangDefault();
            saveBarang();
            return;
        }
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] p = line.split("\\|");
                if (p.length >= 7) {
                    String kategori = p[0].trim();
                    String id = p[1].trim();
                    String nama = p[2].trim();
                    double tarif = Double.parseDouble(p[3].trim());
                    boolean tersedia = Boolean.parseBoolean(p[4].trim());
                    String spek = p[5].trim();
                    String paramKhusus = p[6].trim();

                    Barang itemBaru;
                    if ("IPhone".equalsIgnoreCase(kategori)) {
                        itemBaru = new IPhone(
                                id, nama, tarif, tersedia, spek, Double.parseDouble(paramKhusus));
                    } else if ("Kamera".equalsIgnoreCase(kategori)) {
                        itemBaru = new Kamera(
                                id, nama, tarif, tersedia, spek, Double.parseDouble(paramKhusus));
                    } else {
                        itemBaru = new AksesorisFotografi(
                                id, nama, tarif, tersedia, spek, paramKhusus);
                    }
                    if (p.length >= 8 && !p[7].trim().isEmpty()) {
                        itemBaru.setGambarPath(p[7].trim());
                    }
                    daftarBarang.getBarang().add(itemBaru);
                }
            }
        } catch (Exception e) {
            System.err.println("Gagal membaca barang.txt: " + e.getMessage());
        }
        if (daftarBarang.getBarang().isEmpty()) {
            seedBarangDefault();
            saveBarang();
        }
