package rental;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import rental.model.Akun;
import rental.service.RentalDataStore;
import rental.ui.LoginFrame;

/**
 * Titik masuk utama (Entry Point) aplikasi Rental â€” Sistem Penyewaan iPhone &amp; Alat Fotografi.
 * Menginisialisasi penyimpanan data persisten (.txt) dan meluncurkan antarmuka GUI berbasis Java Swing.
 *
 * @version 1.0
 */
public class Main {

    private static Akun akunAktif;

    /**
     * Memulai proses login dengan menampilkan jendela LoginFrame.
     */
    public static void login() {
        RentalDataStore dataStore = new RentalDataStore();
        LoginFrame loginFrame = new LoginFrame(dataStore);
        loginFrame.setVisible(true);
    }

    /**
     * Method utama (main) untuk menjalankan aplikasi Rental.
     *
     * @param args argumen baris perintah
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(Main::login);
    }

    public static Akun getAkunAktif() {
        return akunAktif;
    }

    public static void setAkunAktif(Akun akun) {
        akunAktif = akun;
    }
}
