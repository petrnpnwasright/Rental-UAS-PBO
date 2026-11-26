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
    }

    private void seedBarangDefault() {
        daftarBarang.getBarang().add(new IPhone(
                "IPH-01", "iPhone 16 Pro Max 256GB", 275000, true,
                "Desert Titanium, ProRes 4K120, Battery Health 100%", 35000));
        daftarBarang.getBarang().add(new IPhone(
                "IPH-02", "iPhone 15 Pro 128GB", 220000, true,
                "Natural Titanium, Apple Log, USB-C 10Gbps", 30000));
        daftarBarang.getBarang().add(new IPhone(
                "IPH-03", "iPhone 14 Pro Max 256GB", 185000, true,
                "Deep Purple, 48MP Main ProRAW, Dynamic Island", 25000));

        daftarBarang.getBarang().add(new Kamera(
                "KMR-01", "Sony Alpha A7 IV Body Only", 350000, true,
                "33MP Full-Frame Exmor R, 4K 60p 10-bit 4:2:2", 40000));
        daftarBarang.getBarang().add(new Kamera(
                "KMR-02", "Canon EOS R6 Mark II", 330000, true,
                "24.2MP Full-Frame CMOS, Dual Pixel AF II, 40fps", 40000));
        daftarBarang.getBarang().add(new Kamera(
                "KMR-03", "Fujifilm X100VI Silver", 260000, true,
                "40.2MP X-Trans 5 HR, IBIS 6-Stop, Film Simulation", 30000));

        daftarBarang.getBarang().add(new AksesorisFotografi(
                "AKS-01", "DJI Ronin RS 3 Pro Gimbal", 140000, true,
                "Carbon Fiber Arms, Payload 4.5kg, LiDAR Support", "Stabilizer Gimbal"));
        daftarBarang.getBarang().add(new AksesorisFotografi(
                "AKS-02", "Sony FE 24-70mm f/2.8 GM II", 175000, true,
                "G-Master Constant f/2.8, E-Mount Full Frame", "Lensa Zoom"));
        daftarBarang.getBarang().add(new AksesorisFotografi(
                "AKS-03", "Godox SL-60W LED Studio Kit", 95000, true,
                "60W 5600K Daylight + Softbox Octagon 80cm + Stand", "Studio Lighting"));
    }

    /**
     * Menyimpan seluruh katalog barang ke dalam {@code data/barang.txt}.
     */
    public void saveBarang() {
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(FILE_BARANG), StandardCharsets.UTF_8))) {
            pw.println("# Format: KATEGORI|ID|NAMA|TARIF_HARIAN|TERSEDIA|SPESIFIKASI|PARAM_KHUSUS|GAMBAR_PATH");
            for (Barang b : daftarBarang.getBarang()) {
                pw.println(b.getKategori() + "|" + b.getId() + "|" + b.getNama() + "|"
                        + b.getTarifHarian() + "|" + b.isTersedia() + "|"
                        + b.getSpesifikasi().replace("|", ",") + "|" + b.getParameterKhususRaw() + "|"
                        + b.getGambarPath());
            }
        } catch (Exception e) {
            System.err.println("Gagal menyimpan barang.txt: " + e.getMessage());
        }
    }

    /**
     * Memuat seluruh riwayat transaksi dari {@code data/transaksi.txt}.
     */
    public void loadTransaksi() {
        daftarTransaksi.clear();
        for (Akun a : daftarAkun) {
            if (a instanceof Penyewa) {
                ((Penyewa) a).getRiwayat().clear();
            }
        }
        File file = new File(FILE_TRANSAKSI);
        if (!file.exists()) {
            saveTransaksi();
            return;
        }
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] p = line.split("\\|");
                if (p.length >= 11) {
                    String idTrx = p[0].trim();
                    String usernamePenyewa = p[1].trim();
                    String[] itemIds = p[2].trim().split(",");
                    int lamaSewa = Integer.parseInt(p[3].trim());
                    LocalDate tglSewa = LocalDate.parse(p[4].trim());
                    LocalDate tglJatuhTempo = LocalDate.parse(p[5].trim());
                    String tglKembaliStr = p[6].trim();
                    String status = p[7].trim();
                    String metodeBayar = p[8].trim();
                    double denda = Double.parseDouble(p[9].trim());
                    double total = Double.parseDouble(p[10].trim());

                    Penyewa penyewa = cariPenyewa(usernamePenyewa);
                    if (penyewa == null) continue;

                    ArrayList<Barang> items = new ArrayList<>();
                    for (String itemId : itemIds) {
                        Barang b = daftarBarang.cariBarangById(itemId.trim());
                        if (b != null) {
                            items.add(b);
                        }
                    }
                    if (items.isEmpty()) continue;

                    Pembayaran pemb = buatPembayaranDariNama(metodeBayar);
                    Transaksi trx = new Transaksi(idTrx, penyewa, items, lamaSewa, tglSewa, pemb);
                    trx.setTanggalJatuhTempo(tglJatuhTempo);
                    if (!"NULL".equalsIgnoreCase(tglKembaliStr) && !tglKembaliStr.isEmpty()) {
                        trx.setTanggalKembali(LocalDate.parse(tglKembaliStr));
                    }
                    trx.ubahStatus(status);

                    if ("DISEWA".equals(status) || "SELESAI".equals(status)) {
                        pemb.prosesPembayaran(trx.hitungTotalSewa());
                        Invoice inv = new Invoice("INV-" + idTrx, trx);
                        inv.setDenda(denda);
                        inv.setTotal(total);
                        trx.setInvoice(inv);
                    }

                    daftarTransaksi.add(trx);
                    penyewa.getRiwayat().add(trx);
                }
            }
        } catch (Exception e) {
            System.err.println("Gagal membaca transaksi.txt: " + e.getMessage());
        }
    }

    /**
     * Menyimpan seluruh daftar transaksi ke dalam {@code data/transaksi.txt}.
     */
    public void saveTransaksi() {
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(FILE_TRANSAKSI), StandardCharsets.UTF_8))) {
            pw.println("# Format: ID|USERNAME|ITEM_IDS|LAMA_SEWA|TGL_SEWA|JATUH_TEMPO|TGL_KEMBALI|STATUS|METODE_BAYAR|DENDA|TOTAL");
            for (Transaksi t : daftarTransaksi) {
                StringBuilder ids = new StringBuilder();
                for (int i = 0; i < t.getBarang().size(); i++) {
                    if (i > 0) ids.append(",");
                    ids.append(t.getBarang().get(i).getId());
                }
                String tglKembali = (t.getTanggalKembali() != null)
                        ? t.getTanggalKembali().toString()
                        : "NULL";
                double denda = (t.getInvoice() != null) ? t.getInvoice().getDenda() : 0.0;
                double total = (t.getInvoice() != null) ? t.getInvoice().getTotal() : t.hitungTotalSewa();

                pw.println(t.getId() + "|" + t.getPenyewa().getUsername() + "|" + ids + "|"
                        + t.getLamaSewa() + "|" + t.getTanggalSewa() + "|" + t.getTanggalJatuhTempo() + "|"
                        + tglKembali + "|" + t.getStatus() + "|" + t.getPembayaran().getNamaMetode() + "|"
                        + denda + "|" + total);
            }
        } catch (Exception e) {
            System.err.println("Gagal menyimpan transaksi.txt: " + e.getMessage());
        }
    }

    /**
     * Menyimpan perubahan katalog barang dan daftar transaksi sekaligus.
     */
    public void saveAll() {
        saveBarang();
        saveTransaksi();
    }

    /**
     * Menghasilkan kode transaksi berikutnya secara berurutan (misal: TRX-001).
     *
     * @return ID transaksi unik
     */
    public String generateNextTransaksiId() {
        int max = 0;
        for (Transaksi t : daftarTransaksi) {
            try {
                String num = t.getId().replaceAll("[^0-9]", "");
                if (!num.isEmpty()) {
                    max = Math.max(max, Integer.parseInt(num));
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return String.format("TRX-%03d", max + 1);
