package rental.model;

import java.util.ArrayList;
import java.util.List;
import rental.exception.InputTidakValidException;

/**
 * Mengelola koleksi seluruh barang sewa yang terdaftar di dalam sistem Rental.
 * Menyediakan operasi penambahan, pengeditan, penghapusan, dan pencarian barang.
 *
 * @author [Nama Anggota 1] - [NIM 1]
 * @version 1.0
 */
public class DaftarBarang {

    private ArrayList<Barang> barang;

    /**
     * Membuat instance DaftarBarang kosong.
     */
    public DaftarBarang() {
        this.barang = new ArrayList<>();
    }

    /**
     * Menambahkan barang baru ke dalam katalog sistem.
     *
     * @param b objek {@link Barang} yang akan ditambahkan
     * @throws InputTidakValidException jika ID barang sudah terdaftar atau tarif tidak valid
     */
    public void tambahBarang(Barang b) throws InputTidakValidException {
        if (b == null || b.getId() == null || b.getId().trim().isEmpty()) {
            throw new InputTidakValidException("ID barang tidak boleh kosong.");
        }
        if (b.getTarifHarian() <= 0) {
            throw new InputTidakValidException("Tarif harian harus lebih dari Rp 0.");
        }
        if (cariBarangById(b.getId()) != null) {
            throw new InputTidakValidException("ID barang '" + b.getId() + "' sudah terdaftar di katalog.");
        }
        barang.add(b);
    }

    /**
     * Memperbarui data barang yang sudah ada berdasarkan ID barang.
     *
     * @param id          kode ID barang yang akan diubah
     * @param namaBaru    nama baru barang
     * @param tarifBaru   tarif harian baru
     * @param spekBaru    spesifikasi baru
     * @param tersedia    status ketersediaan baru
     * @throws InputTidakValidException jika barang tidak ditemukan atau tarif tidak valid
     */
    public void editBarang(String id, String namaBaru, double tarifBaru, String spekBaru, boolean tersedia)
            throws InputTidakValidException {
        Barang target = cariBarangById(id);
        if (target == null) {
            throw new InputTidakValidException("Barang dengan ID '" + id + "' tidak ditemukan.");
        }
        if (namaBaru == null || namaBaru.trim().isEmpty()) {
            throw new InputTidakValidException("Nama barang tidak boleh kosong.");
        }
        if (tarifBaru <= 0) {
            throw new InputTidakValidException("Tarif harian harus lebih dari Rp 0.");
        }
        target.setNama(namaBaru.trim());
        target.setTarifHarian(tarifBaru);
        target.setSpesifikasi(spekBaru != null ? spekBaru.trim() : "");
        target.setTersedia(tersedia);
    }

    /**
     * Method kompatibilitas sesuai spesifikasi dasar Class Diagram untuk menandai proses edit barang.
     *
     * @param id kode ID barang
     */
    public void editBarang(String id) {
        Barang target = cariBarangById(id);
        if (target != null) {
            target.setNama(target.getNama());
        }
    }

    /**
     * Menghapus barang dari katalog berdasarkan ID barang.
     *
     * @param id kode ID barang yang akan dihapus
     * @throws InputTidakValidException jika barang tidak ditemukan atau sedang disewa
     */
    public void hapusBarang(String id) throws InputTidakValidException {
        Barang target = cariBarangById(id);
        if (target == null) {
            throw new InputTidakValidException("Barang dengan ID '" + id + "' tidak ditemukan.");
        }
        if (!target.isTersedia()) {
            throw new InputTidakValidException("Barang sedang disewa dan tidak dapat dihapus sebelum dikembalikan.");
        }
        barang.remove(target);
    }

    /**
     * Mencari barang berdasarkan ID secara case-insensitive.
     *
     * @param id kode ID barang
     * @return objek {@link Barang} jika ditemukan, atau null jika tidak ada
     */
    public Barang cariBarangById(String id) {
        if (id == null) return null;
        for (Barang b : barang) {
            if (b.getId().equalsIgnoreCase(id.trim())) {
                return b;
            }
        }
        return null;
    }

    /**
     * Mengembalikan seluruh daftar barang yang tersimpan.
     *
     * @return ArrayList berisi seluruh objek {@link Barang}
     */
    public ArrayList<Barang> getBarang() {
        return barang;
    }

    /**
     * Mengembalikan daftar barang yang sedang tersedia untuk disewa.
     *
     * @return List barang dengan status tersedia == true
     */
    public List<Barang> getBarangTersedia() {
        List<Barang> hasil = new ArrayList<>();
        for (Barang b : barang) {
            if (b.isTersedia()) {
                hasil.add(b);
            }
        }
        return hasil;
    }
}
