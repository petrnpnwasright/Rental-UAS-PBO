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
 * @author [Althaffia Putri] - [250810701100036]
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

    /**
     * Membuat transaksi penyewaan baru dari seluruh barang yang ada di dalam keranjang sewa.
     *
     * @param idTransaksi kode unik transaksi baru
     * @param lamaSewa    lama durasi penyewaan dalam hari (minimal 1 hari)
     * @param pembayaran  metode pembayaran yang dipilih penyewa
     * @return objek {@link Transaksi} yang berhasil dibuat
     * @throws InputTidakValidException     jika keranjang kosong, lama sewa <= 0, atau metode pembayaran null
     * @throws BarangTidakTersediaException jika ada barang di keranjang yang mendadak tidak tersedia
     */
    public Transaksi buatTransaksi(String idTransaksi, int lamaSewa, Pembayaran pembayaran)
            throws InputTidakValidException, BarangTidakTersediaException {
        if (keranjang.getBarang().isEmpty()) {
            throw new InputTidakValidException("Keranjang sewa masih kosong. Silakan pilih barang terlebih dahulu.");
        }
        if (lamaSewa <= 0) {
            throw new InputTidakValidException("Lama sewa minimal adalah 1 hari.");
        }
        if (pembayaran == null) {
            throw new InputTidakValidException("Silakan pilih metode pembayaran terlebih dahulu.");
        }
        for (Barang b : keranjang.getBarang()) {
            if (!b.isTersedia()) {
                throw new BarangTidakTersediaException(
                        "Maaf, barang '" + b.getNama() + "' baru saja disewa penyewa lain.");
            }
        }

        ArrayList<Barang> daftarItem = new ArrayList<>(keranjang.getBarang());
        LocalDate tglSewa = LocalDate.now();
        Transaksi transaksi = new Transaksi(idTransaksi, this, daftarItem, lamaSewa, tglSewa, pembayaran);
        riwayat.add(transaksi);
        keranjang.kosongkan();
        return transaksi;
    }

    /**
     * Method kompatibilitas sesuai spesifikasi Class Diagram untuk pembuatan transaksi default.
     *
     * @return null jika dipanggil tanpa parameter konteks checkout
     */
    public Transaksi buatTransaksi() {
        return null;
    }

    /**
     * Menampilkan riwayat transaksi penyewaan milik penyewa sesuai spesifikasi Class Diagram.
     */
    public void lihatRiwayat() {
        // Riwayat transaksi ditampilkan pada tab Riwayat Sewa & Invoice di GUI Penyewa
    }

    public KeranjangSewa getKeranjang() {
        return keranjang;
    }

    public ArrayList<Transaksi> getRiwayat() {
        return riwayat;
    }

    @Override
    public String getRole() {
        return "PENYEWA";
    }
}
