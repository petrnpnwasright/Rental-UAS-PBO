package rental.model;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Subclass Barang kategori Kamera.
 * Perhitungan biaya sewa menambahkan biaya kalibrasi dan perawatan sensor:
 * {@code (tarifHarian * hari) + biayaPerawatanSensor}.
 *
 * @version 1.0
 */
public class Kamera extends Barang {

    private double biayaPerawatanSensor;

    /**
     * Membuat objek barang kategori Kamera.
     *
     * @param id                   kode unik kamera
     * @param nama                 tipe kamera
     * @param tarifHarian          tarif sewa harian
     * @param tersedia             status ketersediaan
     * @param spesifikasi          detail sensor & mount lensa
     * @param biayaPerawatanSensor biaya perawatan sensor per transaksi
     */
    public Kamera(String id, String nama, double tarifHarian, boolean tersedia,
                  String spesifikasi, double biayaPerawatanSensor) {
        super(id, nama, tarifHarian, tersedia, spesifikasi);
        this.biayaPerawatanSensor = biayaPerawatanSensor;
    }

    @Override
    public double hitungBiaya(int hari) {
        return (getTarifHarian() * hari) + biayaPerawatanSensor;
    }

    @Override
    public String getKategori() {
        return "Kamera";
    }

    @Override
    public String getKeteranganTarif() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return "+ Perawatan Sensor " + nf.format(biayaPerawatanSensor);
    }

    @Override
    public String getParameterKhususRaw() {
        return String.valueOf(biayaPerawatanSensor);
    }

    public double getBiayaPerawatanSensor() {
        return biayaPerawatanSensor;
    }

    public void setBiayaPerawatanSensor(double biayaPerawatanSensor) {
        this.biayaPerawatanSensor = biayaPerawatanSensor;
    }
}
