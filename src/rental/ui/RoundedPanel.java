package rental.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Komponen panel kustom dengan sudut membulat (rounded corners) dan garis tepi halus.
 *
 * @author [Nama Anggota 1] - [NIM 1]
 * @version 1.0
 */
public class RoundedPanel extends JPanel {

    private int radius;
    private Color fillColor;
    private Color borderColor;

    /**
     * Membuat panel membulat dengan radius sudut, warna isi, dan warna border kustom.
     *
     * @param radius      jari-jari kelengkungan sudut dalam piksel
     * @param fillColor   warna latar dalam kartu
     * @param borderColor warna garis tepi kartu
     */
    public RoundedPanel(int radius, Color fillColor, Color borderColor) {
        this.radius = radius;
        this.fillColor = fillColor;
        this.borderColor = borderColor;
        setOpaque(false);
    }

    /**
     * Membuat panel membulat dengan layout manager tertentu.
     *
     * @param layout      layout manager panel
     * @param radius      jari-jari kelengkungan sudut
     * @param fillColor   warna latar dalam kartu
     * @param borderColor warna garis tepi kartu
     */
    public RoundedPanel(LayoutManager layout, int radius, Color fillColor, Color borderColor) {
        super(layout);
        this.radius = radius;
        this.fillColor = fillColor;
        this.borderColor = borderColor;
        setOpaque(false);
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
        repaint();
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();

        g2.setColor(fillColor);
        g2.fillRoundRect(0, 0, w - 1, h - 1, radius, radius);

        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
