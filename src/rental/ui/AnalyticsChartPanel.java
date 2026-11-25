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

