package rental.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import javax.swing.JPanel;

/**
 * Kartu bento bergradien gelap dengan pendar cahaya hijau lembut.
 */
public class GradientCardPanel extends JPanel {

    private final int radius;
    private final Color topColor;
    private final Color bottomColor;

    public GradientCardPanel(LayoutManager layout, int radius, Color topColor, Color bottomColor) {
        super(layout);
        this.radius = radius;
        this.topColor = topColor;
        this.bottomColor = bottomColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();

        GradientPaint gp = new GradientPaint(0, 0, topColor, w * 0.55f, h, bottomColor);
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, w - 1, h - 1, radius, radius);

        // Pendar cahaya hijau lime lembut di sudut kanan atas
        g2.setColor(new Color(158, 240, 26, 42));
        g2.fill(new Ellipse2D.Double(w * 0.38, -h * 0.30, w * 0.85, h * 0.90));

        g2.setColor(new Color(158, 240, 26, 65));
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}