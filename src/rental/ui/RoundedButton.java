package rental.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

/**
 * Tombol pill membulat dengan efek hover halus sesuai gaya antarmuka modern.
 */
public class RoundedButton extends JButton {

    private Color bgColor;
    private Color hoverColor;
    private Color borderColor;
    private int radius;
    private boolean hovered = false;

    public RoundedButton(String text, Color bgColor, Color hoverColor, Color textColor, int radius) {
        super(text);
        this.bgColor = bgColor;
        this.hoverColor = hoverColor;
        this.radius = radius;
        setForeground(textColor);
        setFont(new Font("SansSerif", Font.BOLD, 12));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    hovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
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

        Color fill = !isEnabled()
                ? new Color(235, 238, 235)
                : (hovered ? hoverColor : bgColor);

        g2.setColor(fill);
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