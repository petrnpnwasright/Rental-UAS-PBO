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

        String[] specs = b.getSpesifikasi().split(",");
        StringBuilder bulletHtml = new StringBuilder("<html><body style='width:305px'>");
        for (String s : specs) {
            bulletHtml.append("• ").append(s.trim()).append("<br>");
        }
        bulletHtml.append("• Denda keterlambatan: Rp 50.000 / hari</body></html>");

        JLabel lblSpecDetail = new JLabel(bulletHtml.toString());
        lblSpecDetail.setFont(Theme.FONT_SMALL);
        lblSpecDetail.setForeground(Theme.TEXT_SECONDARY);
        lblSpecDetail.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Simulasi Kalkulator Biaya Sewa di dalam Modal Detail
        RoundedPanel calcBox = new RoundedPanel(new BorderLayout(8, 6), 14, Theme.BG_CARD_ALT, Theme.BORDER_SUBTLE);
        calcBox.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        calcBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        calcBox.setMaximumSize(new Dimension(360, 74));

        JPanel simTop = new JPanel(new BorderLayout());
        simTop.setOpaque(false);
        JLabel lblSimDur = new JLabel("Simulasi Durasi Sewa (Hari):");
        lblSimDur.setFont(Theme.FONT_SMALL_BOLD);
        lblSimDur.setForeground(Theme.TEXT_PRIMARY);

        JSpinner spnSimHari = new JSpinner(new SpinnerNumberModel(1, 1, 60, 1));
        spnSimHari.setPreferredSize(new Dimension(65, 26));
        simTop.add(lblSimDur, BorderLayout.WEST);
        simTop.add(spnSimHari, BorderLayout.EAST);

        JLabel lblSimTotal = new JLabel("Estimasi Biaya: " + Theme.formatRupiah(b.hitungBiaya(1)));
        lblSimTotal.setFont(Theme.FONT_BODY_BOLD);
        lblSimTotal.setForeground(Theme.TEXT_PRIMARY);

        spnSimHari.addChangeListener(e -> {
            int h = (Integer) spnSimHari.getValue();
            lblSimTotal.setText("Estimasi Biaya (" + h + " hari): " + Theme.formatRupiah(b.hitungBiaya(h)));
        });

        calcBox.add(simTop, BorderLayout.NORTH);
        calcBox.add(lblSimTotal, BorderLayout.SOUTH);

        infoStack.add(badgeRow);
        infoStack.add(Box.createVerticalStrut(10));
        infoStack.add(lblTitle);
        infoStack.add(Box.createVerticalStrut(6));
        infoStack.add(lblTarif);
        infoStack.add(Box.createVerticalStrut(4));
        infoStack.add(lblSkema);
        infoStack.add(Box.createVerticalStrut(12));
        infoStack.add(lblSpecHeader);
        infoStack.add(Box.createVerticalStrut(4));
        infoStack.add(lblSpecDetail);
        infoStack.add(Box.createVerticalStrut(12));
        infoStack.add(calcBox);

        // Tombol Bawah Modal (Tambah ke Keranjang & Tutup)
        JPanel actionRow = new JPanel(new GridLayout(1, 2, 10, 0));
        actionRow.setOpaque(false);

        RoundedButton btnClose = new RoundedButton(
                "Tutup", Theme.BG_CARD, Theme.BG_CARD_ALT, Theme.TEXT_PRIMARY, 16);
        btnClose.setBorderColor(Theme.BORDER_SUBTLE);
        btnClose.setPreferredSize(new Dimension(120, 40));
        btnClose.addActionListener(e -> dialog.dispose());

        RoundedButton btnAddModal = new RoundedButton(
                b.isTersedia() ? "+ Tambah ke Keranjang" : "Unit Sedang Disewa",
                b.isTersedia() ? Theme.ACCENT_LIME : Theme.BG_CARD_ALT,
                Theme.ACCENT_LIME_HOVER,
                b.isTersedia() ? Theme.DARK_PILL : Theme.TEXT_MUTED,
                16);
        btnAddModal.setEnabled(b.isTersedia());
        btnAddModal.setPreferredSize(new Dimension(170, 40));
        btnAddModal.addActionListener(e -> {
            try {
                penyewa.getKeranjang().tambahBarang(b);
                spnLamaSewa.setValue(spnSimHari.getValue());
                refreshCartPanel();
                dialog.dispose();
            } catch (BarangTidakTersediaException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Barang Tidak Tersedia", JOptionPane.WARNING_MESSAGE);
            }
        });

        actionRow.add(btnClose);
        actionRow.add(btnAddModal);

        rightCol.add(infoStack, BorderLayout.CENTER);
        rightCol.add(actionRow, BorderLayout.SOUTH);

        container.add(leftCol);
        container.add(rightCol);

        dialog.add(outerPad, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JPanel buildRightWalletAndActivityPanel() {
        JPanel right = new JPanel(new BorderLayout(0, 14));
        right.setOpaque(false);

        RoundedPanel walletWrapper = new RoundedPanel(new BorderLayout(0, 12), 22, Theme.BG_CARD, Theme.BORDER_SUBTLE);
        walletWrapper.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel walletHeader = new JPanel(new BorderLayout());
        walletHeader.setOpaque(false);
        JLabel lblWalletTitle = new JLabel("Keranjang Sewa");
        lblWalletTitle.setFont(Theme.FONT_H2);
        lblWalletTitle.setForeground(Theme.TEXT_PRIMARY);

        lblCartCount = new JLabel("0 Item");
        lblCartCount.setFont(Theme.FONT_SMALL_BOLD);
        lblCartCount.setForeground(Theme.EMERALD);

        walletHeader.add(lblWalletTitle, BorderLayout.WEST);
        walletHeader.add(lblCartCount, BorderLayout.EAST);

        // Kartu Gelap ala Visa Card di Monetra
        GradientCardPanel passCard = new GradientCardPanel(
                new BorderLayout(0, 10), 20,
                Theme.GRADIENT_CARD_TOP, Theme.GRADIENT_CARD_BOT);
        passCard.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JPanel cardTop = new JPanel(new BorderLayout());
        cardTop.setOpaque(false);
        JLabel lblPass = new JLabel("STUDIO PASS");
        lblPass.setFont(Theme.FONT_SMALL_BOLD);
        lblPass.setForeground(Theme.ACCENT_LIME);
        JLabel lblBrandCard = new JLabel("RENTAL");
        lblBrandCard.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblBrandCard.setForeground(Color.WHITE);
        cardTop.add(lblPass, BorderLayout.WEST);
        cardTop.add(lblBrandCard, BorderLayout.EAST);

        JPanel totalBox = new JPanel();
        totalBox.setOpaque(false);
        totalBox.setLayout(new BoxLayout(totalBox, BoxLayout.Y_AXIS));
        JLabel lblEstTitle = new JLabel("Estimasi Total Tagihan");
        lblEstTitle.setFont(Theme.FONT_SMALL);
        lblEstTitle.setForeground(new Color(195, 208, 198));

        lblCartTotal = new JLabel("Rp0");
        lblCartTotal.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblCartTotal.setForeground(Color.WHITE);

        JLabel lblHolder = new JLabel(penyewa.getNamaLengkap() + "  •  " + penyewa.getId());
        lblHolder.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblHolder.setForeground(new Color(170, 185, 173));

        totalBox.add(lblEstTitle);
        totalBox.add(Box.createVerticalStrut(2));
        totalBox.add(lblCartTotal);
        totalBox.add(Box.createVerticalStrut(6));
        totalBox.add(lblHolder);

        passCard.add(cardTop, BorderLayout.NORTH);
        passCard.add(totalBox, BorderLayout.SOUTH);

        cartItemsContainer = new JPanel();
        cartItemsContainer.setOpaque(false);
        cartItemsContainer.setLayout(new BoxLayout(cartItemsContainer, BoxLayout.Y_AXIS));

        JScrollPane cartScroll = new JScrollPane(cartItemsContainer);
        cartScroll.setOpaque(false);
        cartScroll.getViewport().setOpaque(false);
        cartScroll.setBorder(null);
        cartScroll.setPreferredSize(new Dimension(290, 115));

        JPanel controls = new JPanel();
        controls.setOpaque(false);
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        JPanel durRow = new JPanel(new BorderLayout(8, 0));
        durRow.setOpaque(false);
        JLabel lblDur = new JLabel("Lama Sewa (Hari):");
        lblDur.setFont(Theme.FONT_SMALL_BOLD);
        lblDur.setForeground(Theme.TEXT_SECONDARY);
        spnLamaSewa = new JSpinner(new SpinnerNumberModel(1, 1, 60, 1));
        spnLamaSewa.setPreferredSize(new Dimension(80, 30));
        spnLamaSewa.addChangeListener(e -> updateEstimatedTotal());
        durRow.add(lblDur, BorderLayout.WEST);
        durRow.add(spnLamaSewa, BorderLayout.EAST);

        JPanel payRow = new JPanel(new BorderLayout(8, 0));
        payRow.setOpaque(false);
        JLabel lblPay = new JLabel("Pembayaran:");
        lblPay.setFont(Theme.FONT_SMALL_BOLD);
        lblPay.setForeground(Theme.TEXT_SECONDARY);
        cmbPembayaran = new JComboBox<>(new Pembayaran[]{
                new TransferBank(), new QRIS(), new Tunai()
        });
        cmbPembayaran.setBackground(Theme.BG_INPUT);
        cmbPembayaran.setForeground(Theme.TEXT_PRIMARY);
        cmbPembayaran.setFont(Theme.FONT_SMALL_BOLD);
        cmbPembayaran.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? Theme.ACCENT_LIME : Theme.BG_INPUT);
                setForeground(Theme.TEXT_PRIMARY);
                return this;
            }
        });
        payRow.add(lblPay, BorderLayout.WEST);
        payRow.add(cmbPembayaran, BorderLayout.EAST);

        // Dua tombol sejajar seperti Deposit (Lime) & Transfer (White) pada gambar
        JPanel actionBtns = new JPanel(new GridLayout(1, 2, 10, 0));
        actionBtns.setOpaque(false);

        RoundedButton btnCheckout = new RoundedButton(
                "Sewa Sekarang", Theme.ACCENT_LIME, Theme.ACCENT_LIME_HOVER, Theme.DARK_PILL, 18);
        btnCheckout.setPreferredSize(new Dimension(135, 38));
        btnCheckout.addActionListener(e -> prosesCheckout());

        RoundedButton btnClear = new RoundedButton(
                "Kosongkan", Theme.BG_CARD, Theme.BG_CARD_ALT, Theme.TEXT_PRIMARY, 18);
        btnClear.setBorderColor(Theme.BORDER_SUBTLE);
        btnClear.setPreferredSize(new Dimension(120, 38));
        btnClear.addActionListener(e -> {
            penyewa.getKeranjang().kosongkan();
            refreshCartPanel();
        });

        actionBtns.add(btnCheckout);
        actionBtns.add(btnClear);

        controls.add(durRow);
        controls.add(Box.createVerticalStrut(8));
        controls.add(payRow);
        controls.add(Box.createVerticalStrut(12));
        controls.add(actionBtns);

        JPanel topWalletStack = new JPanel(new BorderLayout(0, 10));
        topWalletStack.setOpaque(false);
        topWalletStack.add(walletHeader, BorderLayout.NORTH);
        topWalletStack.add(passCard, BorderLayout.CENTER);

        walletWrapper.add(topWalletStack, BorderLayout.NORTH);
        walletWrapper.add(cartScroll, BorderLayout.CENTER);
        walletWrapper.add(controls, BorderLayout.SOUTH);

        RoundedPanel recentCard = new RoundedPanel(new BorderLayout(0, 10), 22, Theme.BG_CARD, Theme.BORDER_SUBTLE);
        recentCard.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JPanel recHeader = new JPanel(new BorderLayout());
        recHeader.setOpaque(false);
        JLabel lblRecTitle = new JLabel("Transaksi Terakhir");
        lblRecTitle.setFont(Theme.FONT_H2);
        lblRecTitle.setForeground(Theme.TEXT_PRIMARY);

        RoundedButton btnViewAll = new RoundedButton(
                "Semua Riwayat", Theme.ACCENT_LIME, Theme.ACCENT_LIME_HOVER, Theme.DARK_PILL, 14);
        btnViewAll.setFont(Theme.FONT_SMALL_BOLD);
        btnViewAll.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        btnViewAll.addActionListener(e -> switchTab("RIWAYAT"));

        recHeader.add(lblRecTitle, BorderLayout.WEST);
        recHeader.add(btnViewAll, BorderLayout.EAST);

        recentActivityList = new JPanel();
        recentActivityList.setOpaque(false);
        recentActivityList.setLayout(new BoxLayout(recentActivityList, BoxLayout.Y_AXIS));

        recentCard.add(recHeader, BorderLayout.NORTH);
        recentCard.add(recentActivityList, BorderLayout.CENTER);

        right.add(walletWrapper, BorderLayout.CENTER);
        right.add(recentCard, BorderLayout.SOUTH);
        return right;
    }

    private void refreshCartPanel() {
        if (cartItemsContainer == null) return;
        cartItemsContainer.removeAll();
        ArrayList<Barang> items = penyewa.getKeranjang().getBarang();
        lblCartCount.setText(items.size() + " Item");

        if (items.isEmpty()) {
            JLabel empty = new JLabel("Keranjang kosong. Pilih barang di katalog.");
            empty.setFont(Theme.FONT_SMALL);
            empty.setForeground(Theme.TEXT_MUTED);
            empty.setBorder(BorderFactory.createEmptyBorder(16, 4, 16, 4));
            cartItemsContainer.add(empty);
        } else {
            for (Barang b : items) {
                RoundedPanel row = new RoundedPanel(new BorderLayout(8, 0), 14, Theme.BG_CARD_ALT, null);
                row.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                row.setMaximumSize(new Dimension(320, 46));

                JPanel info = new JPanel();
                info.setOpaque(false);
                info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
                JLabel lName = new JLabel(b.getNama());
                lName.setFont(Theme.FONT_SMALL_BOLD);
                lName.setForeground(Theme.TEXT_PRIMARY);
                JLabel lItemSub = new JLabel(Theme.formatRupiah(b.getTarifHarian()) + "/hari • " + b.getKategori());
                lItemSub.setFont(new Font("SansSerif", Font.PLAIN, 10));
                lItemSub.setForeground(Theme.TEXT_SECONDARY);
                info.add(lName);
                info.add(lItemSub);

                RoundedButton btnDel = new RoundedButton(
                        "x", Theme.CORAL_BG, Theme.CORAL_RED, Theme.CORAL_RED, 12);
                btnDel.setFont(Theme.FONT_SMALL_BOLD);
                btnDel.setPreferredSize(new Dimension(26, 26));
                btnDel.addActionListener(e -> {
                    penyewa.getKeranjang().hapusBarang(b.getId());
                    refreshCartPanel();
                });

                row.add(info, BorderLayout.CENTER);
                row.add(btnDel, BorderLayout.EAST);
                cartItemsContainer.add(row);
                cartItemsContainer.add(Box.createVerticalStrut(6));
            }
        }
        updateEstimatedTotal();
        cartItemsContainer.revalidate();
        cartItemsContainer.repaint();
    }

    private void updateEstimatedTotal() {
        if (spnLamaSewa == null || lblCartTotal == null) return;
        int hari = (Integer) spnLamaSewa.getValue();
        double est = penyewa.getKeranjang().hitungEstimasiTotal(hari);
        lblCartTotal.setText(Theme.formatRupiah(est));
    }

    private void prosesCheckout() {
        try {
            int hari = (Integer) spnLamaSewa.getValue();
            Pembayaran metode = (Pembayaran) cmbPembayaran.getSelectedItem();
            String idTrx = dataStore.generateNextTransaksiId();

            Transaksi trx = penyewa.buatTransaksi(idTrx, hari, metode);
            dataStore.getDaftarTransaksi().add(trx);
            dataStore.saveTransaksi();

            refreshAll();
            JOptionPane.showMessageDialog(this,
                    "Transaksi " + trx.getId() + " berhasil dibuat!\n"
                    + "Total Estimasi: " + Theme.formatRupiah(trx.hitungTotalSewa()) + "\n"
                    + "Metode Pembayaran: " + metode.getNamaMetode() + "\n"
                    + "Status: MENUNGGU KONFIRMASI ADMIN.",
                    "Checkout Berhasil", JOptionPane.INFORMATION_MESSAGE);
        } catch (InputTidakValidException | BarangTidakTersediaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Gagal Membuat Transaksi", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void refreshRecentActivity() {
        if (recentActivityList == null) return;
        recentActivityList.removeAll();
        ArrayList<Transaksi> list = penyewa.getRiwayat();
        if (list.isEmpty()) {
            JLabel empty = new JLabel("Belum ada transaksi.");
            empty.setFont(Theme.FONT_SMALL);
            empty.setForeground(Theme.TEXT_MUTED);
            recentActivityList.add(empty);
        } else {
            int start = Math.max(0, list.size() - 3);
            for (int i = list.size() - 1; i >= start; i--) {
                Transaksi t = list.get(i);
                RoundedPanel item = new RoundedPanel(new BorderLayout(8, 0), 14, Theme.BG_CARD_ALT, null);
                item.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                item.setMaximumSize(new Dimension(320, 48));

                JPanel left = new JPanel();
                left.setOpaque(false);
                left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
                JLabel lId = new JLabel(t.getId() + " (" + t.getBarang().size() + " unit • " + t.getLamaSewa() + " hari)");
                lId.setFont(Theme.FONT_SMALL_BOLD);
                lId.setForeground(Theme.TEXT_PRIMARY);
                JLabel lSt = new JLabel("Status: " + t.getStatus());
                lSt.setFont(new Font("SansSerif", Font.PLAIN, 10));
                lSt.setForeground(getStatusColor(t.getStatus()));
                left.add(lId);
                left.add(lSt);

                double amt = (t.getInvoice() != null) ? t.getInvoice().getTotal() : t.hitungTotalSewa();
                JLabel lAmt = new JLabel(Theme.formatRupiah(amt));
                lAmt.setFont(Theme.FONT_SMALL_BOLD);
                lAmt.setForeground(Theme.EMERALD);

                item.add(left, BorderLayout.CENTER);
                item.add(lAmt, BorderLayout.EAST);
                recentActivityList.add(item);
                recentActivityList.add(Box.createVerticalStrut(6));
            }
        }
        recentActivityList.revalidate();
        recentActivityList.repaint();
    }

    private JPanel buildRiwayatView() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 14));
        wrapper.setOpaque(false);

        RoundedPanel header = new RoundedPanel(new BorderLayout(), 22, Theme.BG_CARD, Theme.BORDER_SUBTLE);
        header.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        JLabel lblTitle = new JLabel("Riwayat Penyewaan & Invoice Resmi");
        lblTitle.setFont(Theme.FONT_H1);
        lblTitle.setForeground(Theme.TEXT_PRIMARY);
        JLabel lblSub = new JLabel("Setelah transaksi diterima Admin, Anda dapat melihat dan mencetak Invoice (.txt).");
        lblSub.setFont(Theme.FONT_BODY);
        lblSub.setForeground(Theme.TEXT_SECONDARY);
        titleBox.add(lblTitle);
        titleBox.add(Box.createVerticalStrut(4));
        titleBox.add(lblSub);

        RoundedButton btnBack = new RoundedButton(
                "Kembali ke Katalog", Theme.ACCENT_LIME, Theme.ACCENT_LIME_HOVER, Theme.DARK_PILL, 16);
        btnBack.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnBack.addActionListener(e -> switchTab("KATALOG"));

        header.add(titleBox, BorderLayout.WEST);
        header.add(btnBack, BorderLayout.EAST);

        JPanel listPanel = new JPanel();
        listPanel.setOpaque(false);
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        ArrayList<Transaksi> riwayat = penyewa.getRiwayat();
        if (riwayat.isEmpty()) {
            RoundedPanel emptyCard = new RoundedPanel(new BorderLayout(), 22, Theme.BG_CARD, Theme.BORDER_SUBTLE);
            emptyCard.setBorder(BorderFactory.createEmptyBorder(40, 24, 40, 24));
            JLabel lblEmpty = new JLabel("Belum ada riwayat penyewaan pada akun Anda.");
            lblEmpty.setFont(Theme.FONT_H2);
            lblEmpty.setForeground(Theme.TEXT_SECONDARY);
            emptyCard.add(lblEmpty, BorderLayout.CENTER);
            listPanel.add(emptyCard);
        } else {
            for (int i = riwayat.size() - 1; i >= 0; i--) {
                Transaksi t = riwayat.get(i);
                listPanel.add(buildTransaksiHistoryCard(t));
                listPanel.add(Box.createVerticalStrut(12));
            }
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        wrapper.add(header, BorderLayout.NORTH);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildTransaksiHistoryCard(Transaksi t) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(12, 8), 22, Theme.BG_CARD, Theme.BORDER_SUBTLE);
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        card.setMaximumSize(new Dimension(1200, 155));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JPanel topLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topLine.setOpaque(false);
        JLabel lblId = new JLabel(t.getId());
        lblId.setFont(Theme.FONT_H2);
        lblId.setForeground(Theme.TEXT_PRIMARY);

        JLabel lblStatus = new JLabel(" " + t.getStatus() + " ");
        lblStatus.setFont(Theme.FONT_SMALL_BOLD);
        lblStatus.setForeground(getStatusColor(t.getStatus()));
        lblStatus.setOpaque(true);
        lblStatus.setBackground(Theme.BG_CARD_ALT);

        topLine.add(lblId);
        topLine.add(lblStatus);

        StringBuilder itemNames = new StringBuilder();
        for (int i = 0; i < t.getBarang().size(); i++) {
            if (i > 0) itemNames.append(", ");
            itemNames.append(t.getBarang().get(i).getNama());
        }
        JLabel lblItems = new JLabel("Perangkat: " + itemNames);
        lblItems.setFont(Theme.FONT_BODY);
        lblItems.setForeground(Theme.TEXT_SECONDARY);

        JLabel lblDates = new JLabel("Sewa: " + t.getTanggalSewa()
                + "  |  Jatuh Tempo: " + t.getTanggalJatuhTempo()
                + "  |  Durasi: " + t.getLamaSewa() + " hari (" + t.getPembayaran().getNamaMetode() + ")");
        lblDates.setFont(Theme.FONT_SMALL);
        lblDates.setForeground(Theme.TEXT_MUTED);

        left.add(topLine);
        left.add(Box.createVerticalStrut(6));
        left.add(lblItems);
        left.add(Box.createVerticalStrut(4));
        left.add(lblDates);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        double total = (t.getInvoice() != null) ? t.getInvoice().getTotal() : t.hitungTotalSewa();
        JLabel lblTotal = new JLabel(Theme.formatRupiah(total));
        lblTotal.setFont(Theme.FONT_H1);
        lblTotal.setForeground(Theme.EMERALD);
        lblTotal.setAlignmentX(Component.RIGHT_ALIGNMENT);

        boolean approved = ("DISEWA".equals(t.getStatus()) || "SELESAI".equals(t.getStatus()))
                && t.getInvoice() != null;

        RoundedButton btnInvoice = new RoundedButton(
                approved ? "Cetak Invoice" : "Menunggu Admin",
                approved ? Theme.ACCENT_LIME : Theme.BG_CARD_ALT,
                Theme.ACCENT_LIME_HOVER,
                approved ? Theme.DARK_PILL : Theme.TEXT_MUTED,
                16);
        btnInvoice.setEnabled(approved);
        btnInvoice.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnInvoice.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnInvoice.addActionListener(e -> tampilkanModalInvoice(t.getInvoice()));

        right.add(lblTotal);
        right.add(Box.createVerticalStrut(8));
        right.add(btnInvoice);

        card.add(left, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);
        return card;
    }
