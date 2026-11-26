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
