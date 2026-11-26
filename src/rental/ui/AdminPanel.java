package rental.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import rental.exception.BarangTidakTersediaException;
import rental.exception.InputTidakValidException;
import rental.model.Admin;
import rental.model.AksesorisFotografi;
import rental.model.Barang;
import rental.model.IPhone;
import rental.model.Kamera;
import rental.model.Transaksi;
import rental.service.RentalDataStore;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Panel antarmuka Administrator bergaya Monetra Light & Lime Green
 * untuk mengelola katalog barang (CRUD), verifikasi transaksi, dan pengembalian.
 */
public class AdminPanel extends JPanel {

    private final RentalDataStore dataStore;
    private final Admin admin;
    private final Runnable onHeaderStatsRefresh;

    private String activeTab = "OVERVIEW";
    private JPanel mainHolder;
    private JPanel rightSummaryHolder;

    public AdminPanel(RentalDataStore dataStore, Admin admin, Runnable onHeaderStatsRefresh) {
        this.dataStore = dataStore;
        this.admin = admin;
        this.onHeaderStatsRefresh = onHeaderStatsRefresh;
        setOpaque(false);
        setLayout(new BorderLayout(18, 0));

        mainHolder = new JPanel(new BorderLayout());
        mainHolder.setOpaque(false);

        rightSummaryHolder = new JPanel(new BorderLayout());
        rightSummaryHolder.setOpaque(false);
        rightSummaryHolder.setPreferredSize(new Dimension(330, 620));

        add(mainHolder, BorderLayout.CENTER);
        add(rightSummaryHolder, BorderLayout.EAST);

        refreshAll();
    }

    public void switchTab(String tab) {
        this.activeTab = tab;
        refreshAll();
    }

    public void refreshAll() {
        mainHolder.removeAll();
        if ("BARANG".equals(activeTab)) {
            mainHolder.add(buildKelolaBarangView(), BorderLayout.CENTER);
        } else if ("TRANSAKSI".equals(activeTab)) {
            mainHolder.add(buildTerimaTransaksiView(), BorderLayout.CENTER);
        } else if ("PENGEMBALIAN".equals(activeTab)) {
            mainHolder.add(buildPengembalianView(), BorderLayout.CENTER);
        } else {
            mainHolder.add(buildOverviewView(), BorderLayout.CENTER);
        }

        rightSummaryHolder.removeAll();
        rightSummaryHolder.add(buildRightAdminPanel(), BorderLayout.CENTER);

        mainHolder.revalidate();
        mainHolder.repaint();
        rightSummaryHolder.revalidate();
        rightSummaryHolder.repaint();

        if (onHeaderStatsRefresh != null) {
            onHeaderStatsRefresh.run();
        }
    }

    private JPanel buildOverviewView() {
        JPanel view = new JPanel(new BorderLayout(0, 14));
        view.setOpaque(false);

        // Baris sapaan atas (seperti "Good morning, ..." pada Monetra)
        JPanel greetRow = new JPanel(new BorderLayout());
        greetRow.setOpaque(false);
        JPanel greetStack = new JPanel();
        greetStack.setOpaque(false);
        greetStack.setLayout(new BoxLayout(greetStack, BoxLayout.Y_AXIS));
        JLabel lblGreet = new JLabel("Selamat datang, " + admin.getNamaLengkap());
        lblGreet.setFont(Theme.FONT_HERO);
        lblGreet.setForeground(Theme.TEXT_PRIMARY);
        JLabel lblGreetSub = new JLabel("Pantau pendapatan studio, persetujuan sewa, dan ketersediaan perangkat.");
        lblGreetSub.setFont(Theme.FONT_BODY);
        lblGreetSub.setForeground(Theme.TEXT_SECONDARY);
        greetStack.add(lblGreet);
        greetStack.add(Box.createVerticalStrut(3));
        greetStack.add(lblGreetSub);
        greetRow.add(greetStack, BorderLayout.WEST);

        double totalPendapatan = 0.0;
        double totalDenda = 0.0;
        int aktifCount = 0;
        int pendingCount = 0;

        for (Transaksi t : dataStore.getDaftarTransaksi()) {
            if ("DISEWA".equals(t.getStatus())) {
                aktifCount++;
                totalPendapatan += t.hitungTotalSewa();
            } else if ("SELESAI".equals(t.getStatus())) {
                if (t.getInvoice() != null) {
                    totalPendapatan += t.getInvoice().getTotal();
                    totalDenda += t.getInvoice().getDenda();
                } else {
                    totalPendapatan += t.hitungTotalSewa();
                }
            } else if ("MENUNGGU_KONFIRMASI".equals(t.getStatus())) {
                pendingCount++;
            }
        }

        int totalUnit = dataStore.getDaftarBarang().getBarang().size();
        int unitTersedia = dataStore.getDaftarBarang().getBarangTersedia().size();

        // Kartu KPI 4 kolom ala Monetra (Current Balance / Savings / Income / Expenses)
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 12, 0));
        kpiRow.setOpaque(false);
        kpiRow.add(createKpiCard("Total Pendapatan", Theme.formatRupiah(totalPendapatan), "+Aktif", true));
        kpiRow.add(createKpiCard("Total Denda", Theme.formatRupiah(totalDenda), "Rp50rb/hr", true));
        kpiRow.add(createKpiCard("Unit Tersedia", unitTersedia + " / " + totalUnit, "Siap Sewa", true));
        kpiRow.add(createKpiCard("Antrean & Aktif", pendingCount + " / " + aktifCount, "Trx", pendingCount == 0));

        // Kartu Grafik Cash Flow (Monetra Bar Chart dengan Lime Highlight)
        RoundedPanel revenueCard = new RoundedPanel(new BorderLayout(0, 10), 22, Theme.BG_CARD, Theme.BORDER_SUBTLE);
        revenueCard.setBorder(BorderFactory.createEmptyBorder(18, 22, 16, 22));

        JPanel revHeader = new JPanel(new BorderLayout());
        revHeader.setOpaque(false);

        JPanel leftTitle = new JPanel();
        leftTitle.setOpaque(false);
        leftTitle.setLayout(new BoxLayout(leftTitle, BoxLayout.Y_AXIS));

