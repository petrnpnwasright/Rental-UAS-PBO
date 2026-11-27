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
    
    private void selectNavigation(int index, Runnable action) {
        activeNavIndex = index;
        if (navButtons != null) {
            for (JButton nb : navButtons) {
                if (nb != null) nb.repaint();
            }
        }
        if (topNavPills != null) {
            for (int i = 0; i < topNavPills.length; i++) {
                if (topNavPills[i] != null) {
                    boolean active = (i == index);
                    topNavPills[i].setForeground(active ? Theme.TEXT_PRIMARY : Theme.TEXT_SECONDARY);
                    topNavPills[i].setFont(active ? Theme.FONT_SMALL_BOLD : Theme.FONT_SMALL);
                }
            }
        }
        action.run();
    }

    private JPanel buildLeftSidebar() {
        JPanel sidebarHolder = new JPanel(new BorderLayout(0, 14));
        sidebarHolder.setOpaque(false);
        sidebarHolder.setPreferredSize(new Dimension(60, 680));

        RoundedPanel topRail = new RoundedPanel(new BorderLayout(), 28, Theme.BG_SIDEBAR, Theme.BORDER_SUBTLE);
        topRail.setBorder(BorderFactory.createEmptyBorder(12, 7, 12, 7));

        JPanel navStack = new JPanel();
        navStack.setOpaque(false);
        navStack.setLayout(new BoxLayout(navStack, BoxLayout.Y_AXIS));

        if (akunAktif instanceof Admin) {
            navButtons = new JButton[4];
            navButtons[0] = createNavIconButton(0, "Dashboard Ringkasan", "GRID", () -> adminPanel.switchTab("OVERVIEW"));
            navButtons[1] = createNavIconButton(1, "Kelola Katalog Barang", "BOX", () -> adminPanel.switchTab("BARANG"));
            navButtons[2] = createNavIconButton(2, "Persetujuan Transaksi", "CHECK", () -> adminPanel.switchTab("TRANSAKSI"));
            navButtons[3] = createNavIconButton(3, "Pengembalian & Denda", "CLOCK", () -> adminPanel.switchTab("PENGEMBALIAN"));
        } else {
            navButtons = new JButton[2];
            navButtons[0] = createNavIconButton(0, "Dashboard & Katalog Sewa", "GRID", () -> penyewaPanel.switchTab("KATALOG"));
            navButtons[1] = createNavIconButton(1, "Riwayat Sewa & Invoice", "RECEIPT", () -> penyewaPanel.switchTab("RIWAYAT"));
        }

        for (int i = 0; i < navButtons.length; i++) {
            navStack.add(navButtons[i]);
            if (i < navButtons.length - 1) {
                navStack.add(Box.createVerticalStrut(10));
            }
        }
        topRail.add(navStack, BorderLayout.NORTH);

        RoundedPanel bottomRail = new RoundedPanel(new BorderLayout(), 28, Theme.BG_SIDEBAR, Theme.BORDER_SUBTLE);
        bottomRail.setBorder(BorderFactory.createEmptyBorder(10, 7, 10, 7));

        JButton btnLogout = createNavIconButton(-1, "Keluar / Ganti Akun", "LOGOUT", () -> {
            Main.setAkunAktif(null);
            new LoginFrame(dataStore).setVisible(true);
            dispose();
        });
        bottomRail.add(btnLogout, BorderLayout.CENTER);

        sidebarHolder.add(topRail, BorderLayout.CENTER);
        sidebarHolder.add(bottomRail, BorderLayout.SOUTH);
        return sidebarHolder;
    }