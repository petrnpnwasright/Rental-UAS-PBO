package rental.model;

/**
 * Kelas abstrak yang merepresentasikan item barang yang disewakan pada sistem Rental.
 * Mengimplementasikan konsep Abstraction dan Encapsulation serta menyediakan method polimorfik
 * {@link #hitungBiaya(int)} yang di-override oleh setiap kategori barang.
 *
 * @version 1.0
 */
public abstract class Barang {

    private String id;
    private String nama;
    private double tarifHarian;
    private boolean tersedia;
    private String spesifikasi;
    private String gambarPath;

    /**
     * Konstruktor untuk menginisialisasi atribut dasar barang sewa.
     *
     * @param id          kode unik barang
     * @param nama        nama perangkat atau barang
     * @param tarifHarian harga sewa per hari dalam Rupiah
     * @param tersedia    status ketersediaan barang (true jika tersedia)
     * @param spesifikasi ringkasan spesifikasi teknis perangkat
     */
    public Barang(String id, String nama, double tarifHarian, boolean tersedia, String spesifikasi) {
        this.id = id;
        this.nama = nama;
        this.tarifHarian = tarifHarian;
        this.tersedia = tersedia;
        this.spesifikasi = spesifikasi;
        this.gambarPath = "assets/products/" + id + ".png";
    }

    /**
     * Menghitung total biaya sewa barang selama jumlah hari tertentu.
     * Setiap subclass kategori memiliki rumus perhitungan biaya yang berbeda.
     *
     * @param hari lama penyewaan dalam satuan hari
     * @return total biaya sewa dalam Rupiah
     */
    public abstract double hitungBiaya(int hari);

    /**
     * Mengembalikan nama kategori barang (misalnya: IPhone, Kamera, Aksesoris Fotografi).
     *
     * @return label kategori barang
     */
    public abstract String getKategori();

    /**
     * Mengembalikan keterangan komponen biaya tambahan atau diskon pada kategori ini.
     *
     * @return deskripsi skema tarif kategori
     */
    public abstract String getKeteranganTarif();

    /**
     * Mengembalikan nilai parameter khusus kategori untuk penyimpanan ke file .txt.
     *
     * @return nilai parameter khusus dalam bentuk String
     */
    public abstract String getParameterKhususRaw();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public double getTarifHarian() {
        return tarifHarian;
    }

    public void setTarifHarian(double tarifHarian) {
        this.tarifHarian = tarifHarian;
    }

    public boolean isTersedia() {
        return tersedia;
    }

    public void setTersedia(boolean tersedia) {
        this.tersedia = tersedia;
    }

    public String getSpesifikasi() {
        return spesifikasi;
    }

    public void setSpesifikasi(String spesifikasi) {
        this.spesifikasi = spesifikasi;
    }

    public String getGambarPath() {
        return (gambarPath != null && !gambarPath.isEmpty())
                ? gambarPath
                : "assets/products/" + id + ".png";
    }

    public void setGambarPath(String gambarPath) {
        this.gambarPath = gambarPath;
    }

    @Override
    public String toString() {
        return id + " - " + nama + " (" + getKategori() + ")";
    }
}
