package rental.model;

import java.time.LocalDate;
import rental.exception.BarangTidakTersediaException;
import rental.exception.InputTidakValidException;

/**
 * Merepresentasikan akun Administrator studio rental.
 * Memiliki wewenang mengelola katalog barang, menerima transaksi penyewa,
 * serta mengonfirmasi pengembalian barang beserta perhitungan denda keterlambatan.
 *
 * @author [Nama Anggota 1] - [NIM 1]
 * @version 1.0
 */
public class Admin extends Akun {

    /**
     * Membuat instance akun Admin baru.
     *
     * @param id          ID unik admin
     * @param username    username login admin
     * @param password    password login admin
     * @param namaLengkap nama lengkap admin
     */
    public Admin(String id, String username, String password, String namaLengkap) {
        super(id, username, password, namaLengkap);
    }

    /**
     * Method pengelolaan barang pada sistem sesuai spesifikasi Class Diagram.
     */
    public void kelolaBarang() {
        // Operasi CRUD dijalankan melalui modul Kelola Barang di antarmuka Admin
    }

    /**
     * Menyetujui transaksi penyewaan yang diajukan oleh penyewa.
     * Mengubah status setiap barang menjadi sedang disewa (tersedia = false)
     * dan menerbitkan Invoice resmi untuk transaksi tersebut.
     *
     * @param t transaksi yang akan diterima
     * @throws BarangTidakTersediaException jika salah satu barang sudah lebih dulu disewa orang lain
     * @throws InputTidakValidException     jika transaksi tidak valid atau bukan berstatus menunggu
     */
    public void terimaTransaksi(Transaksi t)
            throws BarangTidakTersediaException, InputTidakValidException {
        if (t == null) {
            throw new InputTidakValidException("Transaksi tidak ditemukan.");
        }
        if (!"MENUNGGU_KONFIRMASI".equals(t.getStatus())) {
            throw new InputTidakValidException(
                    "Transaksi " + t.getId() + " sudah diproses sebelumnya (Status: " + t.getStatus() + ").");
        }
        for (Barang b : t.getBarang()) {
            if (!b.isTersedia()) {
                throw new BarangTidakTersediaException(
                        "Barang '" + b.getNama() + "' sudah tidak tersedia karena disewa pada transaksi lain.");
            }
        }
        for (Barang b : t.getBarang()) {
            b.setTersedia(false);
        }
        t.ubahStatus("DISEWA");

        double totalBiaya = t.hitungTotalSewa();
        if (t.getPembayaran() != null) {
            t.getPembayaran().prosesPembayaran(totalBiaya);
        }
        Invoice invoice = new Invoice("INV-" + t.getId(), t);
        invoice.hitungTotal();
        t.setInvoice(invoice);
