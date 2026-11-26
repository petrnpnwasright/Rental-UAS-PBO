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

        JPanel catTiles = new JPanel(new GridLayout(1, 3, 8, 0));
        catTiles.setOpaque(false);
        catTiles.add(createCategoryTile("iPhone", "IPhone"));
        catTiles.add(createCategoryTile("Kamera", "Kamera"));
        catTiles.add(createCategoryTile("Aksesoris", "Aksesoris Fotografi"));

        smartCard.add(smartTop, BorderLayout.NORTH);
        smartCard.add(lblBigUnit, BorderLayout.CENTER);
        smartCard.add(catTiles, BorderLayout.SOUTH);

        // Kartu Kanan: Distribusi Stok per Kategori (Bar Chart bergaya Monetra)
        RoundedPanel chartCard = new RoundedPanel(new BorderLayout(0, 6), 22, Theme.BG_CARD, Theme.BORDER_SUBTLE);
        chartCard.setBorder(BorderFactory.createEmptyBorder(16, 18, 12, 18));

        JPanel chTop = new JPanel(new BorderLayout());
        chTop.setOpaque(false);
        JLabel lblChTitle = new JLabel("Ketersediaan Kategori");
        lblChTitle.setFont(Theme.FONT_H2);
        lblChTitle.setForeground(Theme.TEXT_PRIMARY);
        JLabel lblTrend = new JLabel("  Real-Time  ");
        lblTrend.setFont(Theme.FONT_SMALL_BOLD);
        lblTrend.setForeground(Theme.EMERALD);
        lblTrend.setOpaque(true);
        lblTrend.setBackground(Theme.EMERALD_BG);
        chTop.add(lblChTitle, BorderLayout.WEST);
        chTop.add(lblTrend, BorderLayout.EAST);

        double[] catCounts = countAvailableByCategory();
        AnalyticsChartPanel barChart = new AnalyticsChartPanel(
                AnalyticsChartPanel.Mode.BAR_CATEGORY,
                catCounts,
                new String[]{"iPhone", "Kamera", "Aksesoris"});

        chartCard.add(chTop, BorderLayout.NORTH);
        chartCard.add(barChart, BorderLayout.CENTER);

        topRow.add(smartCard);
        topRow.add(chartCard);
        northContainer.add(topRow, BorderLayout.CENTER);

        catalogGridPanel = new JPanel(new GridLayout(0, 2, 14, 14));
        catalogGridPanel.setOpaque(false);
        refreshCatalogGrid();

        JScrollPane scroll = new JScrollPane(catalogGridPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        main.add(northContainer, BorderLayout.NORTH);
        main.add(scroll, BorderLayout.CENTER);
        return main;
    }

    private RoundedButton createCategoryTile(String label, String catValue) {
        boolean active = selectedCategory.equalsIgnoreCase(catValue);
        RoundedButton btn = new RoundedButton(
                label,
                active ? Theme.DARK_PILL : Theme.BG_CARD_ALT,
                active ? Theme.DARK_PILL_HOVER : new Color(233, 238, 233),
                active ? Color.WHITE : Theme.TEXT_PRIMARY,
                14);
        btn.setFont(Theme.FONT_SMALL_BOLD);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 6, 8, 6));
        btn.addActionListener(e -> {
            selectedCategory = catValue;
            refreshAll();
        });
        return btn;
    }

    private double[] countAvailableByCategory() {
        double iph = 0, kmr = 0, aks = 0;
        for (Barang b : dataStore.getDaftarBarang().getBarang()) {
            if (b.isTersedia()) {
                if ("IPhone".equalsIgnoreCase(b.getKategori())) iph++;
                else if ("Kamera".equalsIgnoreCase(b.getKategori())) kmr++;
                else aks++;
            }
        }
        return new double[]{iph, kmr, aks};
    }

    private void refreshCatalogGrid() {
        if (catalogGridPanel == null) return;
        catalogGridPanel.removeAll();

        List<Barang> list = dataStore.getDaftarBarang().getBarang();
        for (Barang b : list) {
            if (!"SEMUA".equalsIgnoreCase(selectedCategory)
                    && !b.getKategori().equalsIgnoreCase(selectedCategory)) {
                continue;
            }
            if (!searchQuery.isEmpty()
                    && !b.getNama().toLowerCase().contains(searchQuery)
                    && !b.getId().toLowerCase().contains(searchQuery)
                    && !b.getKategori().toLowerCase().contains(searchQuery)) {
                continue;
            }
            catalogGridPanel.add(buildProductCard(b));
        }
        catalogGridPanel.revalidate();
        catalogGridPanel.repaint();
    }

    private JPanel buildProductCard(Barang b) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(0, 10), 22, Theme.BG_CARD, Theme.BORDER_SUBTLE);
        card.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setToolTipText("Klik card untuk melihat detail lengkap produk");

        // Header Badge Kategori & Status
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblCat = new JLabel(" " + b.getId() + " • " + b.getKategori().toUpperCase() + " ");
        lblCat.setFont(Theme.FONT_SMALL_BOLD);
        lblCat.setForeground(Theme.TEXT_PRIMARY);
        lblCat.setOpaque(true);
        lblCat.setBackground(Theme.BG_CARD_ALT);

        JLabel lblStatus = new JLabel(b.isTersedia() ? " TERSEDIA " : " SEDANG DISEWA ");
        lblStatus.setFont(Theme.FONT_SMALL_BOLD);
        lblStatus.setForeground(b.isTersedia() ? Theme.EMERALD : Theme.CORAL_RED);
        lblStatus.setOpaque(true);
        lblStatus.setBackground(b.isTersedia() ? Theme.EMERALD_BG : Theme.CORAL_BG);

        top.add(lblCat, BorderLayout.WEST);
        top.add(lblStatus, BorderLayout.EAST);

        // Bagian Tengah: Gambar Produk + Info Singkat
        JPanel centerWrap = new JPanel(new BorderLayout(0, 8));
        centerWrap.setOpaque(false);

        JPanel imgBox = ImageHelper.createProductImagePanel(
                b.getGambarPath(), b.getNama(), b.getKategori(), 16);
        imgBox.setPreferredSize(new Dimension(220, 130));

        JPanel mid = new JPanel();
        mid.setOpaque(false);
        mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));

        JLabel lblName = new JLabel(b.getNama());
        lblName.setFont(Theme.FONT_H2);
        lblName.setForeground(Theme.TEXT_PRIMARY);

        JLabel lblSpec = new JLabel("<html><body style='width:220px'>" + b.getSpesifikasi() + "</body></html>");
        lblSpec.setFont(Theme.FONT_SMALL);
        lblSpec.setForeground(Theme.TEXT_SECONDARY);

        JLabel lblHintDetail = new JLabel("Lihat detail & simulasi sewa ->");
        lblHintDetail.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblHintDetail.setForeground(Theme.EMERALD);

        mid.add(lblName);
        mid.add(Box.createVerticalStrut(3));
        mid.add(lblSpec);
        mid.add(Box.createVerticalStrut(5));
        mid.add(lblHintDetail);

        centerWrap.add(imgBox, BorderLayout.NORTH);
        centerWrap.add(mid, BorderLayout.CENTER);

        // Bagian Bawah: Harga Harian + Tombol + Keranjang
        JPanel bottom = new JPanel(new BorderLayout(8, 0));
        bottom.setOpaque(false);

        JPanel priceBox = new JPanel();
        priceBox.setOpaque(false);
        priceBox.setLayout(new BoxLayout(priceBox, BoxLayout.Y_AXIS));
        JLabel lblPrice = new JLabel(Theme.formatRupiah(b.getTarifHarian()));
        lblPrice.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblPrice.setForeground(Theme.TEXT_PRIMARY);
        JLabel lblPerDay = new JLabel("/ hari");
        lblPerDay.setFont(Theme.FONT_SMALL);
        lblPerDay.setForeground(Theme.TEXT_MUTED);
        priceBox.add(lblPrice);
        priceBox.add(lblPerDay);

        RoundedButton btnAdd = new RoundedButton(
                b.isTersedia() ? "+ Keranjang" : "Disewa",
                b.isTersedia() ? Theme.ACCENT_LIME : Theme.BG_CARD_ALT,
                Theme.ACCENT_LIME_HOVER,
                b.isTersedia() ? Theme.DARK_PILL : Theme.TEXT_MUTED,
                16);
        btnAdd.setEnabled(b.isTersedia());
        btnAdd.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnAdd.addActionListener(e -> {
            try {
                penyewa.getKeranjang().tambahBarang(b);
                refreshCartPanel();
            } catch (BarangTidakTersediaException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Barang Tidak Tersedia", JOptionPane.WARNING_MESSAGE);
            }
        });

        bottom.add(priceBox, BorderLayout.WEST);
        bottom.add(btnAdd, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        card.add(centerWrap, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        // Klik pada card membuka modal detail produk ala e-commerce
        MouseAdapter cardClickListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tampilkanDetailProdukECommerce(b);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorderColor(Theme.EMERALD);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorderColor(Theme.BORDER_SUBTLE);
            }
        };
        card.addMouseListener(cardClickListener);
        imgBox.addMouseListener(cardClickListener);

        return card;
    }

    /**
     * Menampilkan jendela modal detail produk lengkap bergaya e-commerce
     * beserta kalkulator estimasi biaya sewa dan tombol tambah ke keranjang.
     */
    private void tampilkanDetailProdukECommerce(Barang b) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                "Detail Produk — " + b.getNama(), JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(760, 480);
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Theme.BG_ROOT);
        dialog.setLayout(new BorderLayout());

        RoundedPanel container = new RoundedPanel(new GridLayout(1, 2, 22, 0), 24, Theme.BG_CARD, Theme.BORDER_SUBTLE);
        container.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        JPanel outerPad = new JPanel(new BorderLayout());
        outerPad.setBackground(Theme.BG_ROOT);
        outerPad.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        outerPad.add(container, BorderLayout.CENTER);

        // KOLOM KIRI: Preview Gambar Besar + Info Jaminan Studio
        JPanel leftCol = new JPanel(new BorderLayout(0, 12));
        leftCol.setOpaque(false);

        JPanel largeImage = ImageHelper.createProductImagePanel(
                b.getGambarPath(), b.getNama(), b.getKategori(), 20);
        largeImage.setPreferredSize(new Dimension(310, 290));

        RoundedPanel guaranteeBox = new RoundedPanel(new BorderLayout(), 14, Theme.BG_CARD_ALT, Theme.BORDER_SUBTLE);
        guaranteeBox.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JLabel lblGuarantee = new JLabel("<html><b>Unit Terkalibrasi &amp; Siap Pakai</b><br>"
                + "Termasuk pengecekan fungsi, baterai penuh, dan tas pelindung.</html>");
        lblGuarantee.setFont(Theme.FONT_SMALL);
        lblGuarantee.setForeground(Theme.TEXT_SECONDARY);
        guaranteeBox.add(lblGuarantee, BorderLayout.CENTER);

        leftCol.add(largeImage, BorderLayout.CENTER);
        leftCol.add(guaranteeBox, BorderLayout.SOUTH);

        // KOLOM KANAN: Detail Lengkap E-Commerce & Aksi Sewa
        JPanel rightCol = new JPanel(new BorderLayout(0, 12));
        rightCol.setOpaque(false);

        JPanel infoStack = new JPanel();
        infoStack.setOpaque(false);
        infoStack.setLayout(new BoxLayout(infoStack, BoxLayout.Y_AXIS));

        // Baris Badge Kategori & Status
        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        badgeRow.setOpaque(false);
        badgeRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblKatBadge = new JLabel("  " + b.getKategori().toUpperCase() + " • " + b.getId() + "  ");
        lblKatBadge.setFont(Theme.FONT_SMALL_BOLD);
        lblKatBadge.setForeground(Theme.DARK_PILL);
        lblKatBadge.setOpaque(true);
        lblKatBadge.setBackground(Theme.ACCENT_LIME_SOFT);

        JLabel lblStBadge = new JLabel(b.isTersedia() ? "  TERSEDIA  " : "  SEDANG DISEWA  ");
        lblStBadge.setFont(Theme.FONT_SMALL_BOLD);
        lblStBadge.setForeground(b.isTersedia() ? Theme.EMERALD : Theme.CORAL_RED);
        lblStBadge.setOpaque(true);
        lblStBadge.setBackground(b.isTersedia() ? Theme.EMERALD_BG : Theme.CORAL_BG);

        badgeRow.add(lblKatBadge);
        badgeRow.add(lblStBadge);

        // Nama Produk
        JLabel lblTitle = new JLabel("<html><body style='width:310px'>" + b.getNama() + "</body></html>");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitle.setForeground(Theme.TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Harga Harian
        JLabel lblTarif = new JLabel(Theme.formatRupiah(b.getTarifHarian()) + " / hari");
        lblTarif.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTarif.setForeground(Theme.EMERALD);
        lblTarif.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Skema Biaya / Promo Kategori
        JLabel lblSkema = new JLabel("Skema Tarif: " + b.getKeteranganTarif());
        lblSkema.setFont(Theme.FONT_SMALL_BOLD);
        lblSkema.setForeground(Theme.TEXT_SECONDARY);
        lblSkema.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Spesifikasi Lengkap dalam bentuk poin
        JLabel lblSpecHeader = new JLabel("Spesifikasi & Kelengkapan:");
        lblSpecHeader.setFont(Theme.FONT_BODY_BOLD);
        lblSpecHeader.setForeground(Theme.TEXT_PRIMARY);
        lblSpecHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

