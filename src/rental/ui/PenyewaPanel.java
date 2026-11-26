package rental.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import rental.exception.BarangTidakTersediaException;
import rental.exception.InputTidakValidException;
import rental.model.Barang;
import rental.model.Invoice;
import rental.model.Penyewa;
import rental.model.Transaksi;
import rental.payment.Pembayaran;
import rental.payment.QRIS;
import rental.payment.TransferBank;
import rental.payment.Tunai;
import rental.service.RentalDataStore;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Panel antarmuka utama untuk akun Penyewa dengan desain Monetra Light & Lime Green.
 */
public class PenyewaPanel extends JPanel {

    private final RentalDataStore dataStore;
    private final Penyewa penyewa;
    private final Runnable onHeaderStatsRefresh;

    private String activeTab = "KATALOG";
    private String selectedCategory = "SEMUA";
    private String searchQuery = "";

    private JPanel centerContentHolder;
    private JPanel catalogGridPanel;
    private JPanel cartItemsContainer;
    private JLabel lblCartTotal;
    private JLabel lblCartCount;
    private JSpinner spnLamaSewa;
    private JComboBox<Pembayaran> cmbPembayaran;
    private JPanel recentActivityList;

    public PenyewaPanel(RentalDataStore dataStore, Penyewa penyewa, Runnable onHeaderStatsRefresh) {
        this.dataStore = dataStore;
        this.penyewa = penyewa;
        this.onHeaderStatsRefresh = onHeaderStatsRefresh;
        setOpaque(false);
        setLayout(new BorderLayout(18, 0));
        buildLayout();
    }

    public void switchTab(String tab) {
        this.activeTab = tab;
        refreshAll();
    }

    public void setSearchQuery(String query) {
        this.searchQuery = (query != null) ? query.trim().toLowerCase() : "";
        if ("KATALOG".equals(activeTab)) {
            refreshCatalogGrid();
        }
    }

    private void buildLayout() {
        centerContentHolder = new JPanel(new BorderLayout());
        centerContentHolder.setOpaque(false);

        JPanel rightColumn = buildRightWalletAndActivityPanel();
        rightColumn.setPreferredSize(new Dimension(335, 620));

        add(centerContentHolder, BorderLayout.CENTER);
        add(rightColumn, BorderLayout.EAST);

        refreshAll();
    }

    public void refreshAll() {
        centerContentHolder.removeAll();
        if ("RIWAYAT".equals(activeTab)) {
            centerContentHolder.add(buildRiwayatView(), BorderLayout.CENTER);
        } else {
            centerContentHolder.add(buildKatalogView(), BorderLayout.CENTER);
        }
        refreshCartPanel();
        refreshRecentActivity();
        centerContentHolder.revalidate();
        centerContentHolder.repaint();
        if (onHeaderStatsRefresh != null) {
            onHeaderStatsRefresh.run();
        }
    }

    private JPanel buildGreetingBar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setOpaque(false);

        JPanel greetLeft = new JPanel();
        greetLeft.setOpaque(false);
        greetLeft.setLayout(new BoxLayout(greetLeft, BoxLayout.Y_AXIS));

        JLabel lblHello = new JLabel("Selamat datang, " + penyewa.getNamaLengkap());
        lblHello.setFont(Theme.FONT_HERO);
        lblHello.setForeground(Theme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Pilih perangkat iPhone & peralatan fotografi profesional untuk disewa hari ini.");
        lblSub.setFont(Theme.FONT_BODY);
        lblSub.setForeground(Theme.TEXT_SECONDARY);

        greetLeft.add(lblHello);
        greetLeft.add(Box.createVerticalStrut(3));
        greetLeft.add(lblSub);

        RoundedTextField txtSearch = new RoundedTextField(18, 20);
        txtSearch.setPreferredSize(new Dimension(235, 38));
        txtSearch.setText(searchQuery);
        txtSearch.setToolTipText("Cari produk iPhone / Kamera / Aksesoris...");
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                setSearchQuery(txtSearch.getText());
            }
        });

        JPanel searchWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
        searchWrap.setOpaque(false);
        searchWrap.add(txtSearch);

        bar.add(greetLeft, BorderLayout.WEST);
        bar.add(searchWrap, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildKatalogView() {
        JPanel main = new JPanel(new BorderLayout(0, 14));
        main.setOpaque(false);

        JPanel northContainer = new JPanel(new BorderLayout(0, 14));
        northContainer.setOpaque(false);
        northContainer.add(buildGreetingBar(), BorderLayout.NORTH);

        JPanel topRow = new JPanel(new GridLayout(1, 2, 16, 0));
        topRow.setOpaque(false);
        topRow.setPreferredSize(new Dimension(500, 175));

        // Kartu Kiri: Smart Catalog Summary + Filter Kategori (seperti Smart Wallet di gambar)
        RoundedPanel smartCard = new RoundedPanel(new BorderLayout(0, 10), 22, Theme.BG_CARD, Theme.BORDER_SUBTLE);
        smartCard.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JPanel smartTop = new JPanel(new BorderLayout());
        smartTop.setOpaque(false);
        JPanel stLeft = new JPanel();
        stLeft.setOpaque(false);
        stLeft.setLayout(new BoxLayout(stLeft, BoxLayout.Y_AXIS));
        JLabel lblSmartTitle = new JLabel("Katalog Studio");
        lblSmartTitle.setFont(Theme.FONT_H2);
        lblSmartTitle.setForeground(Theme.TEXT_PRIMARY);
        JLabel lblSmartSub = new JLabel("Diskon 10% Aksesoris sewa >= 3 hari");
        lblSmartSub.setFont(Theme.FONT_SMALL);
        lblSmartSub.setForeground(Theme.TEXT_SECONDARY);
        stLeft.add(lblSmartTitle);
        stLeft.add(lblSmartSub);

        RoundedButton btnSemua = new RoundedButton(
                "Semua Unit", Theme.ACCENT_LIME, Theme.ACCENT_LIME_HOVER, Theme.DARK_PILL, 16);
        btnSemua.setFont(Theme.FONT_SMALL_BOLD);
        btnSemua.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btnSemua.addActionListener(e -> {
            selectedCategory = "SEMUA";
            refreshAll();
        });

        smartTop.add(stLeft, BorderLayout.WEST);
        smartTop.add(btnSemua, BorderLayout.EAST);

        int tersediaCount = dataStore.getDaftarBarang().getBarangTersedia().size();
        int totalCount = dataStore.getDaftarBarang().getBarang().size();

        JLabel lblBigUnit = new JLabel(tersediaCount + " / " + totalCount + " Unit Siap Sewa");
        lblBigUnit.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblBigUnit.setForeground(Theme.TEXT_PRIMARY);

