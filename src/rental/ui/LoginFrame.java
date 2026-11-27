package rental.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import rental.Main;
import rental.exception.AuthException;
import rental.model.Akun;
import rental.service.RentalDataStore;

/**
 * Layar autentikasi (Login) aplikasi Rental dengan desain modern Light & Lime Green.
 */
public class LoginFrame extends JFrame {

    private final RentalDataStore dataStore;
    private RoundedTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblError;

    public LoginFrame(RentalDataStore dataStore) {
        this.dataStore = dataStore;
        initUI();
    }

    private void initUI() {
        setTitle("Rental Studio — Sistem Penyewaan iPhone & Alat Fotografi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 580);
        setMinimumSize(new Dimension(880, 540));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG_ROOT);
        setLayout(new BorderLayout());

        JPanel wrapper = new JPanel(new GridLayout(1, 2, 22, 0));
        wrapper.setBackground(Theme.BG_ROOT);
        wrapper.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        wrapper.add(buildLeftShowcaseCard());
        wrapper.add(buildRightLoginCard());

        add(wrapper, BorderLayout.CENTER);
    }
    
    private JPanel buildLeftShowcaseCard() {
        JPanel card = ImageHelper.createBackgroundImagePanel(
                new BorderLayout(0, 16), "assets/login_banner.png", 26);
        card.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JPanel topBrand = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBrand.setOpaque(false);

        JPanel logoBadge = ImageHelper.createLogoPanel("assets/logo.png", 38, 38, 12);

        JLabel lblBrand = new JLabel("Rental Studio");
        lblBrand.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblBrand.setForeground(Color.WHITE);
        topBrand.add(logoBadge);
        topBrand.add(lblBrand);

        JPanel centerBox = new JPanel();
        centerBox.setOpaque(false);
        centerBox.setLayout(new BoxLayout(centerBox, BoxLayout.Y_AXIS));

        JLabel lblTag = new JLabel("  iPHONE & PHOTOGRAPHY GEAR RENTAL  ");
        lblTag.setFont(Theme.FONT_SMALL_BOLD);
        lblTag.setForeground(Theme.DARK_PILL);
        lblTag.setOpaque(true);
        lblTag.setBackground(Theme.ACCENT_LIME);
        lblTag.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

        JLabel lblTitle = new JLabel("<html>Sewa Flagship iPhone<br>&amp; Kamera Profesional<br>Dalam Hitungan Detik.</html>");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 27));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblDesc = new JLabel("<html>Katalog lengkap iPhone Pro Max, Kamera Mirrorless Full-Frame,<br>"
                + "Gimbal, Lensa G-Master, hingga Lighting Studio dengan perhitungan<br>"
                + "tarif transparan dan invoice otomatis.</html>");
        lblDesc.setFont(Theme.FONT_BODY);
        lblDesc.setForeground(new Color(230, 238, 232));

        centerBox.add(Box.createVerticalStrut(12));
        centerBox.add(lblTag);
        centerBox.add(Box.createVerticalStrut(16));
        centerBox.add(lblTitle);
        centerBox.add(Box.createVerticalStrut(14));
        centerBox.add(lblDesc);
        centerBox.add(Box.createVerticalGlue());

        RoundedPanel statsCard = new RoundedPanel(new GridLayout(1, 3, 10, 0), 18,
                new Color(15, 20, 16, 175), new Color(158, 240, 26, 90));
        statsCard.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        statsCard.add(createMiniStat("3 Kategori", "iPhone, Kamera, Gear"));
        statsCard.add(createMiniStat("3 Pembayaran", "Transfer, QRIS, Tunai"));
        statsCard.add(createMiniStat("Auto Invoice", "Real-Time & Denda"));

        card.add(topBrand, BorderLayout.NORTH);
        card.add(centerBox, BorderLayout.CENTER);
        card.add(statsCard, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createMiniStat(String title, String sub) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel l1 = new JLabel(title);
        l1.setFont(Theme.FONT_BODY_BOLD);
        l1.setForeground(Theme.ACCENT_LIME);
        JLabel l2 = new JLabel(sub);
        l2.setFont(Theme.FONT_SMALL);
        l2.setForeground(new Color(215, 225, 217));
        p.add(l1);
        p.add(Box.createVerticalStrut(3));
        p.add(l2);
        return p;
    }
    
    private JPanel buildRightLoginCard() {
        RoundedPanel card = new RoundedPanel(new GridBagLayout(), 26, Theme.BG_CARD, Theme.BORDER_SUBTLE);
        card.setBorder(BorderFactory.createEmptyBorder(30, 34, 30, 34));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblWelcome = new JLabel("Selamat Datang Kembali");
        lblWelcome.setFont(Theme.FONT_HERO);
        lblWelcome.setForeground(Theme.TEXT_PRIMARY);

        JLabel lblSub = new JLabel("Masuk menggunakan akun Penyewa atau Admin untuk melanjutkan.");
        lblSub.setFont(Theme.FONT_BODY);
        lblSub.setForeground(Theme.TEXT_SECONDARY);

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(Theme.FONT_BODY_BOLD);
        lblUser.setForeground(Theme.TEXT_PRIMARY);

        txtUsername = new RoundedTextField(20, 14);
        txtUsername.setPreferredSize(new Dimension(280, 42));

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(Theme.FONT_BODY_BOLD);
        lblPass.setForeground(Theme.TEXT_PRIMARY);

        txtPassword = new JPasswordField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_INPUT);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.setColor(hasFocus() ? Theme.EMERALD : Theme.BORDER_SUBTLE);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        txtPassword.setOpaque(false);
        txtPassword.setForeground(Theme.TEXT_PRIMARY);
        txtPassword.setCaretColor(Theme.DARK_PILL);
        txtPassword.setFont(Theme.FONT_BODY);
        txtPassword.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        txtPassword.setPreferredSize(new Dimension(280, 42));
        txtPassword.addActionListener(e -> prosesLogin());

        lblError = new JLabel(" ");
        lblError.setFont(Theme.FONT_SMALL_BOLD);
        lblError.setForeground(Theme.CORAL_RED);

        RoundedButton btnLogin = new RoundedButton(
                "Masuk ke Sistem",
                Theme.ACCENT_LIME, Theme.ACCENT_LIME_HOVER, Theme.DARK_PILL, 18);
        btnLogin.setPreferredSize(new Dimension(280, 44));
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogin.addActionListener(e -> prosesLogin());

        JPanel quickFillPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        quickFillPanel.setOpaque(false);
