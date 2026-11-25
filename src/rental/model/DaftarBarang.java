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
