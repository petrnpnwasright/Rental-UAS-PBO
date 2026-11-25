package rental.exception;

/**
 * Exception yang dilempar ketika input dari pengguna tidak memenuhi aturan validasi,
 * misalnya lama sewa kurang dari 1 hari, tarif negatif, atau keranjang kosong.
 *
 * @version 1.0
 */
public class InputTidakValidException extends RentalException {

    /**
     * Membuat instance InputTidakValidException baru.
     *
     * @param message pesan detail kesalahan validasi input
     */
    public InputTidakValidException(String message) {
        super(message);
    }
}
