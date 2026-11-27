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