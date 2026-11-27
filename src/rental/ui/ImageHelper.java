package rental.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.GradientPaint;
import java.awt.LayoutManager;

/**
 * Utilitas pemuatan dan rendering gambar berkualitas tinggi (anti-aliased & rounded)
 * untuk logo aplikasi dan foto katalog produk.
 */
public final class ImageHelper {

    private ImageHelper() {
    }

    /**
     * Memuat gambar dari path file. Jika ekstensi tidak cocok, otomatis mencari
     * alternatif .png, .jpg, atau .jpeg dengan nama file yang sama.
     */
    public static BufferedImage loadImage(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        File file = new File(path.trim());
        if (!file.exists()) {
            String base = path.replaceAll("\\.[^.]+$", "");
            String[] exts = {".png", ".jpg", ".jpeg", ".PNG", ".JPG", ".JPEG"};
            for (String ext : exts) {
                File alt = new File(base + ext);
                if (alt.exists()) {
                    file = alt;
                    break;
                }
            }
        }
        if (!file.exists()) {
            return null;
        }
        try {
            return ImageIO.read(file);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Membuat panel logo berbentuk kotak membulat dari file gambar (misal assets/logo.png).
     */
    public static JPanel createLogoPanel(String imagePath, int width, int height, int radius) {
        BufferedImage img = loadImage(imagePath);
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

                int w = getWidth();
                int h = getHeight();
                g2.setClip(new RoundRectangle2D.Float(0, 0, w, h, radius, radius));

                if (img != null) {
                    g2.drawImage(img, 0, 0, w, h, null);
                } else {
                    // Fallback jika file assets/logo.png belum tersedia
                    g2.setColor(Theme.ACCENT_LIME);
                    g2.fillRoundRect(0, 0, w, h, radius, radius);
                    g2.setColor(Theme.DARK_PILL);
                    g2.setStroke(new BasicStroke(2.2f));
                    g2.drawOval(8, 8, w - 16, h - 16);
                    g2.fillOval((w / 2) - 3, (h / 2) - 3, 6, 6);
                }
                g2.dispose();
            }
        };
        panel.setPreferredSize(new Dimension(width, height));
        panel.setOpaque(false);
        return panel;
    }

    /**
     * Membuat panel gambar produk dengan aspect-fit (proporsional, tidak gepeng)
     * di dalam wadah membulat bergaya e-commerce.
     */
    public static JPanel createProductImagePanel(String imagePath, String fallbackTitle, String kategori, int radius) {
        BufferedImage img = loadImage(imagePath);
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

                int w = getWidth();
                int h = getHeight();

                // Latar wadah gambar halus
                g2.setColor(Theme.BG_CARD_ALT);
                g2.fillRoundRect(0, 0, w - 1, h - 1, radius, radius);

                g2.setClip(new RoundRectangle2D.Float(0, 0, w, h, radius, radius));

                if (img != null) {
                    int pad = 10;
                    int availW = Math.max(10, w - (pad * 2));
                    int availH = Math.max(10, h - (pad * 2));
                    double scale = Math.min((double) availW / img.getWidth(), (double) availH / img.getHeight());
                    int drawW = (int) Math.round(img.getWidth() * scale);
                    int drawH = (int) Math.round(img.getHeight() * scale);
                    int drawX = (w - drawW) / 2;
                    int drawY = (h - drawH) / 2;
                    g2.drawImage(img, drawX, drawY, drawW, drawH, null);
                } else {
                    // Placeholder elegan jika foto produk belum ada di assets/products/
                    g2.setColor(new Color(224, 232, 224));
                    g2.fillOval((w / 2) - 24, (h / 2) - 30, 48, 48);

                    g2.setColor(Theme.TEXT_SECONDARY);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                    String label = (kategori != null) ? kategori.toUpperCase() : "PRODUK";
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(label, (w - fm.stringWidth(label)) / 2, (h / 2) + 32);

                    g2.setColor(Theme.TEXT_MUTED);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                    String hint = "Letakkan foto di " + imagePath;
                    FontMetrics fm2 = g2.getFontMetrics();
                    if (fm2.stringWidth(hint) < w - 16) {
                        g2.drawString(hint, (w - fm2.stringWidth(hint)) / 2, (h / 2) + 47);
                    }
                }
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    /**
     * Membuat panel kontainer dengan latar belakang gambar full-bleed (center-crop)
     * dan lapisan gradient gelap transparan agar teks & logo di atasnya tetap jelas.
     */
    public static JPanel createBackgroundImagePanel(LayoutManager layout, String imagePath, int radius) {
        BufferedImage img = loadImage(imagePath);
        JPanel panel = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

                int w = getWidth();
                int h = getHeight();
                g2.setClip(new RoundRectangle2D.Float(0, 0, w, h, radius, radius));

                if (img != null) {
                    // Gambar latar penuh (aspect-fill / center-crop)
                    double scale = Math.max((double) w / img.getWidth(), (double) h / img.getHeight());
                    int drawW = (int) Math.round(img.getWidth() * scale);
                    int drawH = (int) Math.round(img.getHeight() * scale);
                    int drawX = (w - drawW) / 2;
                    int drawY = (h - drawH) / 2;
                    g2.drawImage(img, drawX, drawY, drawW, drawH, null);

                    // Lapisan overlay gelap transparan agar tulisan putih tetap tajam terbaca
                    GradientPaint overlay = new GradientPaint(
                            0, 0, new Color(15, 22, 17, 165),
                            0, h, new Color(12, 16, 13, 215));
                    g2.setPaint(overlay);
                    g2.fillRect(0, 0, w, h);
                } else {
                    // Fallback warna gradien bawaan jika file gambar belum ditemukan
                    GradientPaint fallbackGp = new GradientPaint(
                            0, 0, Theme.GRADIENT_CARD_TOP,
                            0, h, Theme.GRADIENT_CARD_BOT);
                    g2.setPaint(fallbackGp);
                    g2.fillRoundRect(0, 0, w, h, radius, radius);
                }

                // Garis tepi halus pada sisi card
                g2.setClip(null);
                g2.setColor(Theme.BORDER_SUBTLE);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);
                g2.dispose();

                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        return panel;
    }
}