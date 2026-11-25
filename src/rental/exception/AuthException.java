package rental.exception;

/**
 * Exception yang dilempar ketika proses autentikasi login gagal,
 * seperti username tidak ditemukan, password salah, atau kolom kosong.
 *
 * @version 1.0
 */
public class AuthException extends RentalException {

    /**
     * Membuat instance AuthException baru.
     *
     * @param message pesan kesalahan autentikasi
     */
    public AuthException(String message) {
        super(message);
    }
}
