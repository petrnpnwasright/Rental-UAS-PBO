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