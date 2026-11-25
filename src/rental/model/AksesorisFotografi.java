package rental.model;

/**
 * Subclass Barang kategori Aksesoris Fotografi (Gimbal, Lensa, Lighting).
 * Perhitungan biaya menggunakan tarif harian normal, serta memberikan potongan diskon 10%
 * apabila lama sewa minimal 3 hari.
 *
 * @version 1.0
 */
public class AksesorisFotografi extends Barang {

    private String jenisAksesoris;

    /**
     * Membuat objek barang kategori AksesorisFotografi.
     *
     * @param id             kode unik aksesoris
     * @param nama           nama perangkat aksesoris
     * @param tarifHarian    tarif sewa harian
     * @param tersedia       status ketersediaan
     * @param spesifikasi    detail kompatibilitas & fitur
     * @param jenisAksesoris jenis aksesoris (misal: Gimbal, Lensa, Lighting)
     */
    public AksesorisFotografi(String id, String nama, double tarifHarian, boolean tersedia,
                              String spesifikasi, String jenisAksesoris) {
        super(id, nama, tarifHarian, tersedia, spesifikasi);
        this.jenisAksesoris = jenisAksesoris;
    }

    @Override
    public double hitungBiaya(int hari) {
        double dasar = getTarifHarian() * hari;
        if (hari >= 3) {
            return dasar * 0.90;
        }
        return dasar;
    }

    @Override
    public String getKategori() {
        return "Aksesoris Fotografi";
    }

    @Override
    public String getKeteranganTarif() {
        return jenisAksesoris + " | Diskon 10% sewa >= 3 hari";
    }

    @Override
    public String getParameterKhususRaw() {
        return jenisAksesoris;
    }

    public String getJenisAksesoris() {
        return jenisAksesoris;
    }

    public void setJenisAksesoris(String jenisAksesoris) {
        this.jenisAksesoris = jenisAksesoris;
    }
}
