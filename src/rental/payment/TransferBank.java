package rental.payment;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Implementasi pembayaran menggunakan metode Transfer Bank (Virtual Account).
 * Dikenakan biaya administrasi bank tetap sebesar Rp 2.500.
 *
 * @version 1.0
 */
public class TransferBank implements Pembayaran {

    private static final double BIAYA_ADMIN = 2500.0;
    private String nomorVA;
    private String detailTerakhir;

    /**
     * Konstruktor default TransferBank dengan nomor Virtual Account studio.
     */
    public TransferBank() {
        this.nomorVA = "8801-9920-4412 (BCA Virtual Account)";
        this.detailTerakhir = "Transfer ke " + nomorVA + " (Admin Rp 2.500)";
    }

    @Override
    public void prosesPembayaran(double jumlah) {
        double totalBayar = jumlah + BIAYA_ADMIN;
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        this.detailTerakhir = "Lunas via Transfer Bank (" + nomorVA + ") | Total Dibayar: "
                + nf.format(totalBayar) + " (termasuk admin Rp 2.500)";
    }

    @Override
    public String getNamaMetode() {
        return "Transfer Bank";
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
