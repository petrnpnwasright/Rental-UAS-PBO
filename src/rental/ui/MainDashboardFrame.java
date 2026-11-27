package rental.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import rental.Main;
import rental.model.Admin;
import rental.model.Akun;
import rental.model.Penyewa;
import rental.service.RentalDataStore;

/**
 * Jendela utama aplikasi Rental dengan tata letak modern bergaya Monetra:
 * bilah navigasi pill di atas, floating icon sidebar di kiri, dan grid konten bento.
 */
public class MainDashboardFrame extends JFrame {

    private final RentalDataStore dataStore;
    private final Akun akunAktif;

    private PenyewaPanel penyewaPanel;
    private AdminPanel adminPanel;
    private JLabel lblHeaderStatus;
    private int activeNavIndex = 0;
    private JButton[] navButtons;
    private RoundedButton[] topNavPills;

    public MainDashboardFrame(RentalDataStore dataStore, Akun akunAktif) {
        this.dataStore = dataStore;
        this.akunAktif = akunAktif;
        initUI();
    }

    private void initUI() {
        setTitle("Rental Studio — " + akunAktif.getRole() + " Dashboard (" + akunAktif.getNamaLengkap() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 780);
        setMinimumSize(new Dimension(1140, 700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG_ROOT);
        setLayout(new BorderLayout());

        JPanel rootPadding = new JPanel(new BorderLayout(16, 14));
        rootPadding.setBackground(Theme.BG_ROOT);
        rootPadding.setBorder(BorderFactory.createEmptyBorder(14, 18, 16, 18));

        rootPadding.add(buildTopHeaderBar(), BorderLayout.NORTH);
        rootPadding.add(buildLeftSidebar(), BorderLayout.WEST);

        if (akunAktif instanceof Admin) {
            adminPanel = new AdminPanel(dataStore, (Admin) akunAktif, this::updateHeaderBadge);
            rootPadding.add(adminPanel, BorderLayout.CENTER);
        } else if (akunAktif instanceof Penyewa) {
            penyewaPanel = new PenyewaPanel(dataStore, (Penyewa) akunAktif, this::updateHeaderBadge);
            rootPadding.add(penyewaPanel, BorderLayout.CENTER);
        }

        add(rootPadding, BorderLayout.CENTER);
        updateHeaderBadge();
    }