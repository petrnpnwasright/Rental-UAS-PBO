package rental.ui;

import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Palet warna, tipografi, dan utilitas pemformatan antarmuka GUI Rental.
 * Mengadaptasi desain modern light-mode dengan aksen Lime Green & Dark Card.
 */
public final class Theme {

    private Theme() {
    }

    // Latar belakang utama & permukaan kartu (Light Mode)
    public static final Color BG_ROOT = new Color(245, 247, 245);
    public static final Color BG_SIDEBAR = new Color(255, 255, 255);
    public static final Color BG_CARD = new Color(255, 255, 255);
    public static final Color BG_CARD_ALT = new Color(244, 246, 244);
    public static final Color BG_INPUT = new Color(255, 255, 255);
    public static final Color BORDER_SUBTLE = new Color(228, 234, 228);

    // Aksen utama Lime Green & Dark Pill
    public static final Color ACCENT_LIME = new Color(158, 240, 26);
    public static final Color ACCENT_LIME_HOVER = new Color(142, 224, 14);
    public static final Color ACCENT_LIME_SOFT = new Color(236, 252, 203);
    public static final Color DARK_PILL = new Color(24, 28, 25);
    public static final Color DARK_PILL_HOVER = new Color(42, 48, 44);

    // Alias kompatibilitas komponen
    public static final Color ACCENT_ORANGE = ACCENT_LIME;
    public static final Color ACCENT_ORANGE_HOVER = ACCENT_LIME_HOVER;

    // Gradien Kartu Gelap (Studio Pass / Control Card)
    public static final Color GRADIENT_CARD_TOP = new Color(32, 48, 34);
    public static final Color GRADIENT_CARD_BOT = new Color(15, 19, 16);
    public static final Color GRADIENT_AMBER_TOP = GRADIENT_CARD_TOP;
    public static final Color GRADIENT_AMBER_BOT = GRADIENT_CARD_BOT;

    // Badge Status
    public static final Color EMERALD = new Color(22, 163, 74);
    public static final Color EMERALD_BG = new Color(220, 252, 231);
    public static final Color CORAL_RED = new Color(220, 38, 38);
    public static final Color CORAL_BG = new Color(254, 226, 226);
    public static final Color AMBER_BADGE = new Color(217, 119, 6);
    public static final Color AMBER_BG = new Color(254, 243, 199);

    // Tipografi Gelap di atas Latar Terang
    public static final Color TEXT_PRIMARY = new Color(20, 24, 21);
    public static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    public static final Color TEXT_MUTED = new Color(156, 163, 175);

    public static final Font FONT_HERO = new Font("SansSerif", Font.BOLD, 25);
    public static final Font FONT_H1 = new Font("SansSerif", Font.BOLD, 19);
    public static final Font FONT_H2 = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font FONT_SMALL_BOLD = new Font("SansSerif", Font.BOLD, 11);

    public static String formatRupiah(double amount) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        nf.setMaximumFractionDigits(0);
        return nf.format(amount);
    }
}