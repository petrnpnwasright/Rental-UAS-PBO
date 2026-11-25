package rental.exception;

/**
 * Kelas dasar exception untuk seluruh kesalahan logika bisnis pada sistem Rental.
 *
 * @version 1.0
 */
public class RentalException extends Exception {

    /**
     * Membuat instance RentalException baru dengan pesan kesalahan tertentu.
     *
     * @param message pesan penjelasan kesalahan
     */
    public RentalException(String message) {
        super(message);
    }
}
