package rental.model;

import java.time.LocalDate;
import java.util.ArrayList;
import rental.exception.BarangTidakTersediaException;
import rental.exception.InputTidakValidException;
import rental.payment.Pembayaran;

/**
 * Merepresentasikan akun Penyewa (pelanggan rental).
 * Memiliki keranjang sewa aktif dan daftar riwayat transaksi penyewaan.
 *
 * @version 1.0
 */
public class Penyewa extends Akun {

    private KeranjangSewa keranjang;
    private ArrayList<Transaksi> riwayat;

    /**
     * Membuat instance akun Penyewa baru dengan keranjang dan riwayat kosong.
     *
     * @param id          ID unik penyewa
     * @param username    username login penyewa
     * @param password    password login penyewa
     * @param namaLengkap nama lengkap penyewa
     */
    public Penyewa(String id, String username, String password, String namaLengkap) {
        super(id, username, password, namaLengkap);
        this.keranjang = new KeranjangSewa();
        this.riwayat = new ArrayList<>();
    }

    /**
     * Menampilkan daftar barang tersedia sesuai spesifikasi Class Diagram.
     */
    public void lihatBarang() {
        // Katalog barang ditampilkan pada panel Dashboard & Katalog Penyewa
    }