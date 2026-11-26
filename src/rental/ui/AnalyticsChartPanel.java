package rental.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

/**
 * Komponen visualisasi grafik batang kustom bergaya Monetra dengan sorotan Lime Green
 * dan tooltip pill gelap pada nilai tertinggi.
 */
public class AnalyticsChartPanel extends JPanel {

    public enum Mode {
        AREA_REVENUE,
        BAR_CATEGORY
    }

    private Mode mode;
    private double[] values;
    private String[] labels;

    public AnalyticsChartPanel(Mode mode, double[] values, String[] labels) {
        this.mode = mode;
        this.values = values;
        this.labels = labels;
        setOpaque(false);
    }

    public void updateData(double[] values, String[] labels) {
        this.values = values;
        this.labels = labels;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (values == null || values.length == 0) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int padLeft = 18;
        int padRight = 18;
        int padTop = 30;
        int padBottom = 26;
        int plotW = Math.max(20, w - padLeft - padRight);
        int plotH = Math.max(20, h - padTop - padBottom);

        double maxVal = 1.0;
        int peakIndex = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i] >= maxVal) {
                maxVal = values[i];
                peakIndex = i;
            }
        }

        // Garis grid horizontal tipis
        g2.setColor(new Color(236, 240, 236));
        g2.setStroke(new BasicStroke(1f));
        for (int i = 0; i <= 3; i++) {
            int y = padTop + (plotH * i / 3);
            g2.drawLine(padLeft, y, w - padRight, y);
        }

        int n = values.length;
        int slotW = plotW / n;
        int barW = Math.min(36, Math.max(16, slotW - 14));

        for (int i = 0; i < n; i++) {
            int barH = (int) Math.max(12, (values[i] / maxVal) * (plotH - 6));
            int x = padLeft + i * slotW + (slotW - barW) / 2;
            int y = padTop + plotH - barH;
            boolean isPeak = (i == peakIndex);

            if (isPeak) {
                GradientPaint limeGp = new GradientPaint(
                        x, y, Theme.ACCENT_LIME,
                        x, y + barH, new Color(195, 249, 108));
                g2.setPaint(limeGp);
            } else {
                g2.setColor(new Color(241, 244, 241));
            }
            g2.fill(new RoundRectangle2D.Double(x, y, barW, barH, 12, 12));

            // Tooltip pill hitam di atas batang tertinggi (seperti $8,689.20 di gambar)
            if (isPeak) {
                String badgeText = (mode == Mode.AREA_REVENUE)
                        ? Theme.formatRupiah(values[i] * 1000.0)
                        : ((int) values[i]) + " Unit";
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                int tw = g2.getFontMetrics().stringWidth(badgeText) + 14;
                int th = 20;
                int bx = Math.max(4, Math.min(w - tw - 4, x + (barW - tw) / 2));
                int by = Math.max(2, y - th - 5);

                g2.setColor(Theme.DARK_PILL);
                g2.fillRoundRect(bx, by, tw, th, 10, 10);
                g2.setColor(Color.WHITE);
                g2.drawString(badgeText, bx + 7, by + 14);
            }

            if (labels != null && i < labels.length) {
                g2.setFont(new Font("SansSerif", isPeak ? Font.BOLD : Font.PLAIN, 10));
                g2.setColor(isPeak ? Theme.TEXT_PRIMARY : Theme.TEXT_MUTED);
                int lw = g2.getFontMetrics().stringWidth(labels[i]);
                g2.drawString(labels[i], x + (barW - lw) / 2, h - 6);
            }
        }
        g2.dispose();
    }
}