package rental.exception;

/**
 * Exception yang dilempar ketika penyewa mencoba menyewa atau memasukkan barang
 * yang sedang disewa orang lain atau sudah berada di dalam keranjang sewa.
 *
 * @version 1.0
 */
public class BarangTidakTersediaException extends RentalException {

    /**
     * Membuat instance BarangTidakTersediaException baru.
     *
     * @param message pesan detail barang yang tidak tersedia
     */
    public BarangTidakTersediaException(String message) {
        super(message);
    }
}
