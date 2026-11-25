package rental.payment;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Implementasi pembayaran menggunakan QRIS instan tanpa biaya tambahan.
 *
 * @version 1.0
 */
public class QRIS implements Pembayaran {

    private String merchantId;
    private String detailTerakhir;

    /**
     * Konstruktor default QRIS dengan ID Merchant resmi studio.
     */
    public QRIS() {
        this.merchantId = "ID1020268891204 (RENTAL STUDIO)";
        this.detailTerakhir = "Scan QRIS Merchant " + merchantId + " (Bebas Biaya Admin)";
    }

    @Override
    public void prosesPembayaran(double jumlah) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        this.detailTerakhir = "Lunas via QRIS Instan (" + merchantId + ") | Nominal: " + nf.format(jumlah);
    }

    @Override
    public String getNamaMetode() {
        return "QRIS";
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
