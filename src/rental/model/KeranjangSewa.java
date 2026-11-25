package rental.model;

import java.util.ArrayList;
import rental.exception.BarangTidakTersediaException;

/**
 * Merepresentasikan keranjang sewa sementara milik penyewa sebelum checkout transaksi.
 * Mendukung penambahan lebih dari satu barang sewa sekaligus.
 *
 * @version 1.0
 */
public class KeranjangSewa {

    private ArrayList<Barang> barang;

    /**
     * Membuat instance KeranjangSewa kosong.
     */
    public KeranjangSewa() {
        this.barang = new ArrayList<>();
    }

    /**
     * Menambahkan barang ke dalam keranjang sewa setelah memvalidasi ketersediaan.
     *
     * @param b barang yang ingin disewa
     * @throws BarangTidakTersediaException jika barang sedang disewa atau sudah ada di keranjang
     */
    public void tambahBarang(Barang b) throws BarangTidakTersediaException {
        if (b == null) {
            throw new BarangTidakTersediaException("Barang tidak valid.");
        }
        if (!b.isTersedia()) {
            throw new BarangTidakTersediaException(
                    "Barang '" + b.getNama() + "' sedang disewa oleh penyewa lain.");
        }
        for (Barang item : barang) {
            if (item.getId().equalsIgnoreCase(b.getId())) {
                throw new BarangTidakTersediaException(
                        "Barang '" + b.getNama() + "' sudah ada di dalam keranjang sewa Anda.");
            }
        }
        barang.add(b);
    }

    /**
     * Menghapus barang dari keranjang sewa berdasarkan ID barang.
     *
     * @param id kode ID barang yang akan dihapus dari keranjang
     */
    public void hapusBarang(String id) {
        barang.removeIf(item -> item.getId().equalsIgnoreCase(id));
    }

    /**
     * Menghitung estimasi total biaya seluruh barang di dalam keranjang untuk lama hari tertentu.
     *
     * @param hari lama sewa dalam hari
     * @return total biaya sewa seluruh item di keranjang
     */
    public double hitungEstimasiTotal(int hari) {
        double total = 0.0;
        for (Barang b : barang) {
            total += b.hitungBiaya(hari);
        }
        return total;
    }

    /**
     * Mengosongkan seluruh isi keranjang sewa setelah transaksi berhasil dibuat.
     */
    public void kosongkan() {
        barang.clear();
    }

    /**
     * Mengembalikan daftar barang yang ada di dalam keranjang sewa.
     *
     * @return ArrayList berisi objek {@link Barang}
     */
    public ArrayList<Barang> getBarang() {
        return barang;
    }
}
