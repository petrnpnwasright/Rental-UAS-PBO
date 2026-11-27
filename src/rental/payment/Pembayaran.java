package rental.payment;

/**
 * Interface kontrak metode pembayaran pada sistem Rental.
 * Diimplementasikan oleh berbagai kanal pembayaran seperti TransferBank, QRIS, dan Tunai.
 *
 * @version 1.0
 */
public interface Pembayaran {

    /**
     * Memproses pembayaran sebesar jumlah tagihan yang diberikan.
     *
     * @param jumlah total nominal pembayaran dalam Rupiah
     */
    void prosesPembayaran(double jumlah);

    /**
     * Mengembalikan nama metode pembayaran untuk ditampilkan pada UI dan Invoice.
     *
     * @return nama metode pembayaran
     */
    String getNamaMetode();

    /**
     * Mengembalikan informasi detail instruksi atau status pembayaran terakhir.
     *
     * @return detail ringkasan pembayaran
     */
    String getDetailPembayaran();
}
