package rental.model;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Subclass Barang kategori IPhone.
 * Perhitungan biaya sewa menambahkan biaya asuransi perlindungan perangkat tetap per penyewaan:
 * {@code (tarifHarian * hari) + biayaAsuransi}.
 *
 * @version 1.0
 */
public class IPhone extends Barang {

    private double biayaAsuransi;

    /**
     * Membuat objek barang kategori IPhone.
     *
     * @param id            kode unik iPhone
     * @param nama          model iPhone
     * @param tarifHarian   tarif sewa harian
     * @param tersedia      status ketersediaan
     * @param spesifikasi   detail penyimpanan & warna
     * @param biayaAsuransi biaya asuransi perangkat per transaksi
     */
    public IPhone(String id, String nama, double tarifHarian, boolean tersedia,
                  String spesifikasi, double biayaAsuransi) {
        super(id, nama, tarifHarian, tersedia, spesifikasi);
        this.biayaAsuransi = biayaAsuransi;
    }

    @Override
    public double hitungBiaya(int hari) {
        return (getTarifHarian() * hari) + biayaAsuransi;
    }

    @Override
    public String getKategori() {
        return "IPhone";
    }

    @Override
    public String getKeteranganTarif() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return "+ Asuransi Perangkat " + nf.format(biayaAsuransi);
    }

    @Override
    public String getParameterKhususRaw() {
        return String.valueOf(biayaAsuransi);
    }

    public double getBiayaAsuransi() {
        return biayaAsuransi;
    }

    public void setBiayaAsuransi(double biayaAsuransi) {
        this.biayaAsuransi = biayaAsuransi;
    }
}
