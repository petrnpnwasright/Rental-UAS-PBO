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

        JLabel lblSub = new JLabel("Cash Flow & Pendapatan Studio");
        lblSub.setFont(Theme.FONT_H2);
        lblSub.setForeground(Theme.TEXT_PRIMARY);

        JLabel lblAmount = new JLabel(Theme.formatRupiah(totalPendapatan));
        lblAmount.setFont(Theme.FONT_HERO);
        lblAmount.setForeground(Theme.TEXT_PRIMARY);

        leftTitle.add(lblSub);
        leftTitle.add(Box.createVerticalStrut(4));
        leftTitle.add(lblAmount);

        JPanel quickNavPills = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        quickNavPills.setOpaque(false);
        RoundedButton btnToTrx = new RoundedButton(
                "Antrean (" + pendingCount + ")", Theme.ACCENT_LIME, Theme.ACCENT_LIME_HOVER, Theme.DARK_PILL, 16);
        btnToTrx.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        btnToTrx.addActionListener(e -> switchTab("TRANSAKSI"));

        RoundedButton btnToRet = new RoundedButton(
                "Pengembalian (" + aktifCount + ")", Theme.BG_CARD_ALT, new Color(232, 237, 232), Theme.TEXT_PRIMARY, 16);
        btnToRet.setBorderColor(Theme.BORDER_SUBTLE);
        btnToRet.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        btnToRet.addActionListener(e -> switchTab("PENGEMBALIAN"));

        quickNavPills.add(btnToTrx);
        quickNavPills.add(btnToRet);

        revHeader.add(leftTitle, BorderLayout.WEST);
        revHeader.add(quickNavPills, BorderLayout.EAST);

        AnalyticsChartPanel cashFlowChart = new AnalyticsChartPanel(
                AnalyticsChartPanel.Mode.AREA_REVENUE,
                new double[]{450, 720, 610, 980, 890, Math.max(1250, totalPendapatan / 1000.0), 760, 840},
                new String[]{"Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des"});

        revenueCard.add(revHeader, BorderLayout.NORTH);
        revenueCard.add(cashFlowChart, BorderLayout.CENTER);

        JPanel topStack = new JPanel(new BorderLayout(0, 14));
        topStack.setOpaque(false);
        topStack.add(greetRow, BorderLayout.NORTH);
        topStack.add(kpiRow, BorderLayout.CENTER);

        view.add(topStack, BorderLayout.NORTH);
        view.add(revenueCard, BorderLayout.CENTER);
        return view;
    }

    private JPanel createKpiCard(String title, String value, String badge, boolean positive) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(0, 8), 20, Theme.BG_CARD, Theme.BORDER_SUBTLE);
        card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(Theme.FONT_SMALL_BOLD);
        lblTitle.setForeground(Theme.TEXT_SECONDARY);

        JPanel bot = new JPanel(new BorderLayout(6, 0));
        bot.setOpaque(false);
        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblVal.setForeground(Theme.TEXT_PRIMARY);

        JLabel lblBadge = new JLabel(" " + badge + " ");
        lblBadge.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblBadge.setForeground(positive ? Theme.EMERALD : Theme.AMBER_BADGE);
        lblBadge.setOpaque(true);
        lblBadge.setBackground(positive ? Theme.EMERALD_BG : Theme.AMBER_BG);

        bot.add(lblVal, BorderLayout.WEST);
        bot.add(lblBadge, BorderLayout.EAST);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(bot, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildKelolaBarangView() {
        JPanel view = new JPanel(new BorderLayout(0, 14));
        view.setOpaque(false);

        RoundedPanel formCard = new RoundedPanel(new BorderLayout(0, 12), 22, Theme.BG_CARD, Theme.BORDER_SUBTLE);
        formCard.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JLabel lblTitle = new JLabel("Kelola Katalog Barang (Tambah / Edit / Hapus)");
        lblTitle.setFont(Theme.FONT_H2);
        lblTitle.setForeground(Theme.TEXT_PRIMARY);

        JPanel fieldsGrid = new JPanel(new GridLayout(2, 3, 12, 10));
        fieldsGrid.setOpaque(false);

        JComboBox<String> cmbKategori = new JComboBox<>(new String[]{"IPhone", "Kamera", "Aksesoris Fotografi"});
        cmbKategori.setBackground(Theme.BG_INPUT);
        cmbKategori.setForeground(Theme.TEXT_PRIMARY);

        RoundedTextField txtId = new RoundedTextField(10, 12);
        txtId.setText(dataStore.generateNextBarangId("IPH"));

        RoundedTextField txtNama = new RoundedTextField(16, 12);
        RoundedTextField txtTarif = new RoundedTextField(12, 12);
        RoundedTextField txtSpek = new RoundedTextField(18, 12);
        RoundedTextField txtKhusus = new RoundedTextField(14, 12);
        txtKhusus.setText("35000");

        cmbKategori.addActionListener(e -> {
            String sel = (String) cmbKategori.getSelectedItem();
            if ("IPhone".equals(sel)) {
                txtId.setText(dataStore.generateNextBarangId("IPH"));
                txtKhusus.setText("35000");
            } else if ("Kamera".equals(sel)) {
                txtId.setText(dataStore.generateNextBarangId("KMR"));
                txtKhusus.setText("40000");
            } else {
                txtId.setText(dataStore.generateNextBarangId("AKS"));
                txtKhusus.setText("Stabilizer Gimbal");
            }
        });

        fieldsGrid.add(wrapLabeledField("Kategori Barang", cmbKategori));
        fieldsGrid.add(wrapLabeledField("ID Barang (Unik)", txtId));
        fieldsGrid.add(wrapLabeledField("Nama Perangkat", txtNama));
        fieldsGrid.add(wrapLabeledField("Tarif Harian (Rp)", txtTarif));
        fieldsGrid.add(wrapLabeledField("Spesifikasi Singkat", txtSpek));
        fieldsGrid.add(wrapLabeledField("Asuransi / Sensor / Jenis", txtKhusus));

        String[] cols = {"ID", "Kategori", "Nama Barang", "Tarif/Hari", "Skema Tarif", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (Barang b : dataStore.getDaftarBarang().getBarang()) {
            model.addRow(new Object[]{
                    b.getId(),
                    b.getKategori(),
                    b.getNama(),
                    Theme.formatRupiah(b.getTarifHarian()),
                    b.getKeteranganTarif(),
                    b.isTersedia() ? "TERSEDIA" : "SEDANG DISEWA"
            });
        }

        JTable table = createStyledTable(model);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String id = (String) model.getValueAt(row, 0);
                Barang b = dataStore.getDaftarBarang().cariBarangById(id);
                if (b != null) {
                    cmbKategori.setSelectedItem(b.getKategori());
                    txtId.setText(b.getId());
                    txtNama.setText(b.getNama());
                    txtTarif.setText(String.valueOf((long) b.getTarifHarian()));
                    txtSpek.setText(b.getSpesifikasi());
                    txtKhusus.setText(b.getParameterKhususRaw());
                }
            }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        RoundedButton btnTambah = new RoundedButton(
                "+ Tambah Barang", Theme.ACCENT_LIME, Theme.ACCENT_LIME_HOVER, Theme.DARK_PILL, 14);
        btnTambah.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnTambah.addActionListener(e -> {
            try {
                String kat = (String) cmbKategori.getSelectedItem();
                String id = txtId.getText().trim();
                String nama = txtNama.getText().trim();
                if (nama.isEmpty()) throw new InputTidakValidException("Nama perangkat wajib diisi.");
                double tarif = parsePositiveDouble(txtTarif.getText(), "Tarif harian");
                String spek = txtSpek.getText().trim();
                String khusus = txtKhusus.getText().trim();

                Barang baru;
                if ("IPhone".equals(kat)) {
                    double asuransi = parsePositiveDouble(khusus, "Biaya asuransi");
                    baru = new IPhone(id, nama, tarif, true, spek, asuransi);
                } else if ("Kamera".equals(kat)) {
                    double sensor = parsePositiveDouble(khusus, "Biaya perawatan sensor");
                    baru = new Kamera(id, nama, tarif, true, spek, sensor);
                } else {
                    if (khusus.isEmpty()) khusus = "Aksesoris Studio";
                    baru = new AksesorisFotografi(id, nama, tarif, true, spek, khusus);
                }

                dataStore.getDaftarBarang().tambahBarang(baru);
                dataStore.saveBarang();
                refreshAll();
                JOptionPane.showMessageDialog(this, "Barang '" + nama + "' berhasil ditambahkan.");
            } catch (InputTidakValidException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validasi Input", JOptionPane.WARNING_MESSAGE);
            }
        });

        RoundedButton btnEdit = new RoundedButton(
                "Simpan Edit", Theme.EMERALD_BG, new Color(187, 247, 208), Theme.EMERALD, 14);
        btnEdit.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnEdit.addActionListener(e -> {
            try {
                String id = txtId.getText().trim();
                String nama = txtNama.getText().trim();
                double tarif = parsePositiveDouble(txtTarif.getText(), "Tarif harian");
                String spek = txtSpek.getText().trim();
                Barang existing = dataStore.getDaftarBarang().cariBarangById(id);
                if (existing == null) {
                    throw new InputTidakValidException("Pilih barang di tabel terlebih dahulu untuk diedit.");
                }
                dataStore.getDaftarBarang().editBarang(id, nama, tarif, spek, existing.isTersedia());
                if (existing instanceof IPhone) {
                    ((IPhone) existing).setBiayaAsuransi(parsePositiveDouble(txtKhusus.getText(), "Biaya asuransi"));
                } else if (existing instanceof Kamera) {
                    ((Kamera) existing).setBiayaPerawatanSensor(parsePositiveDouble(txtKhusus.getText(), "Biaya sensor"));
                } else if (existing instanceof AksesorisFotografi) {
                    ((AksesorisFotografi) existing).setJenisAksesoris(txtKhusus.getText().trim());
                }
                dataStore.saveBarang();
                refreshAll();
                JOptionPane.showMessageDialog(this, "Perubahan barang " + id + " berhasil disimpan.");
            } catch (InputTidakValidException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Validasi Edit", JOptionPane.WARNING_MESSAGE);
            }
        });

        RoundedButton btnHapus = new RoundedButton(
                "Hapus Barang", Theme.CORAL_BG, new Color(254, 202, 202), Theme.CORAL_RED, 14);
        btnHapus.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnHapus.addActionListener(e -> {
            try {
                int row = table.getSelectedRow();
                if (row < 0) {
                    throw new InputTidakValidException("Pilih baris barang pada tabel yang ingin dihapus.");
                }
                String id = (String) model.getValueAt(row, 0);
                dataStore.getDaftarBarang().hapusBarang(id);
                dataStore.saveBarang();
                refreshAll();
                JOptionPane.showMessageDialog(this, "Barang " + id + " berhasil dihapus.");
