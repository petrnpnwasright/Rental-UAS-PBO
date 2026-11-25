package rental.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * Merepresentasikan bukti tagihan (Invoice) resmi atas suatu transaksi sewa.
 * Bertanggung jawab menghitung denda keterlambatan per hari, menghitung grand total,
 * serta mencetak rincian invoice ke dalam file .txt.
 *
 * @author [Nama Anggota 3] - [NIM 3]
 * @version 1.0
 */
public class Invoice {

    /** Tarif denda keterlambatan tetap per hari (Rp 50.000/hari). */
    public static final double DENDA_PER_HARI = 50000.0;

    private String idInvoice;
    private Transaksi transaksi;
    private double total;
    private double denda;

    /**
     * Membuat instance Invoice baru yang terhubung dengan sebuah Transaksi.
     *
     * @param idInvoice kode unik invoice
     * @param transaksi objek {@link Transaksi} terkait
     */
    public Invoice(String idInvoice, Transaksi transaksi) {
        this.idInvoice = idInvoice;
        this.transaksi = transaksi;
        this.denda = 0.0;
        this.total = hitungTotal();
    }

    /**
     * Menghitung denda keterlambatan berdasarkan selisih hari antara tanggal jatuh tempo
     * dan tanggal pengembalian (atau tanggal hari ini apabila belum dikembalikan).
     *
     * @return nominal denda dalam Rupiah (0 jika tidak terlambat)
     */
    public double hitungDenda() {
        LocalDate pembanding = (transaksi.getTanggalKembali() != null)
                ? transaksi.getTanggalKembali()
                : LocalDate.now();
        long hariTerlambat = ChronoUnit.DAYS.between(transaksi.getTanggalJatuhTempo(), pembanding);
        if (hariTerlambat > 0) {
            this.denda = hariTerlambat * DENDA_PER_HARI;
        } else {
            this.denda = 0.0;
        }
        return this.denda;
    }

    /**
     * Menghitung grand total tagihan (subtotal sewa barang + denda keterlambatan).
     *
     * @return total tagihan akhir dalam Rupiah
     */
    public double hitungTotal() {
        hitungDenda();
        this.total = transaksi.hitungTotalSewa() + this.denda;
        return this.total;
    }

    /**
     * Menghasilkan string rincian lengkap Invoice untuk ditampilkan pada UI maupun disimpan ke file.
     *
     * @return teks terformat bukti invoice
     */
    public String generateTeksInvoice() {
        hitungTotal();
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("              RENTAL STUDIO - OFFICIAL INVOICE              \n");
        sb.append("         iPhone & Professional Photography Equipment        \n");
        sb.append("============================================================\n");
        sb.append("No. Invoice      : ").append(idInvoice).append("\n");
        sb.append("ID Transaksi     : ").append(transaksi.getId()).append("\n");
        sb.append("Nama Penyewa     : ").append(transaksi.getPenyewa().getNamaLengkap())
          .append(" (@").append(transaksi.getPenyewa().getUsername()).append(")\n");
        sb.append("Status Transaksi : ").append(transaksi.getStatus()).append("\n");
        sb.append("Tanggal Sewa     : ").append(transaksi.getTanggalSewa()).append("\n");
        sb.append("Jatuh Tempo      : ").append(transaksi.getTanggalJatuhTempo()).append("\n");
        sb.append("Tanggal Kembali  : ")
          .append(transaksi.getTanggalKembali() != null ? transaksi.getTanggalKembali() : "Belum Dikembalikan")
          .append("\n");
        sb.append("Lama Sewa        : ").append(transaksi.getLamaSewa()).append(" Hari\n");
        sb.append("Metode Bayar     : ").append(transaksi.getPembayaran().getNamaMetode()).append("\n");
        sb.append("Info Pembayaran  : ").append(transaksi.getPembayaran().getDetailPembayaran()).append("\n");
        sb.append("------------------------------------------------------------\n");
        sb.append("RINCIAN PERANGKAT DISEWA:\n");
        int no = 1;
        for (Barang b : transaksi.getBarang()) {
            sb.append(String.format(" %d. [%s] %s (%s)\n", no++, b.getId(), b.getNama(), b.getKategori()));
            sb.append(String.format("    Tarif: %s/hari | %s\n",
                    nf.format(b.getTarifHarian()), b.getKeteranganTarif()));
            sb.append(String.format("    Subtotal (%d hari): %s\n",
                    transaksi.getLamaSewa(), nf.format(b.hitungBiaya(transaksi.getLamaSewa()))));
        }
        sb.append("------------------------------------------------------------\n");
        long hariTelat = 0;
        if (transaksi.getTanggalKembali() != null) {
            hariTelat = Math.max(0, ChronoUnit.DAYS.between(
                    transaksi.getTanggalJatuhTempo(), transaksi.getTanggalKembali()));
        }
        sb.append(String.format("Subtotal Sewa    : %s\n", nf.format(transaksi.hitungTotalSewa())));
        sb.append(String.format("Keterlambatan    : %d hari (Rp 50.000/hari)\n", hariTelat));
        sb.append(String.format("Denda            : %s\n", nf.format(denda)));
        sb.append("============================================================\n");
        sb.append(String.format("TOTAL TAGIHAN    : %s\n", nf.format(total)));
        sb.append("============================================================\n");
        return sb.toString();
