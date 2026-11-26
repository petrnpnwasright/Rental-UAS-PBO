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
