package rental.payment;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Implementasi pembayaran Tunai (Cash) secara langsung di meja kasir studio rental.
 *
 * @version 1.0
 */
public class Tunai implements Pembayaran {

    private String detailTerakhir;

    /**
     * Konstruktor default Tunai.
     */
    public Tunai() {
        this.detailTerakhir = "Pembayaran Tunai di Kasir Studio Rental";
    }

    @Override
    public void prosesPembayaran(double jumlah) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        this.detailTerakhir = "Lunas Tunai di Studio Rental | Nominal Diterima: " + nf.format(jumlah);
    }

    @Override
    public String getNamaMetode() {
        return "Tunai";
    }

    @Override
    public String getDetailPembayaran() {
        return detailTerakhir;
    }

    @Override
    public String toString() {
        return getNamaMetode();
    }
}
