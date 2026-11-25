package rental.ui;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JTextField;

/**
 * Komponen input teks dengan sudut membulat dan gaya light mode bersih.
 */
public class RoundedTextField extends JTextField {

    private final int radius;

    public RoundedTextField(int columns, int radius) {
        super(columns);
        this.radius = radius;
        setOpaque(false);
        setBackground(Theme.BG_INPUT);
        setForeground(Theme.TEXT_PRIMARY);
        setCaretColor(Theme.DARK_PILL);
        setFont(Theme.FONT_BODY);
        setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Theme.BG_INPUT);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.setColor(hasFocus() ? Theme.EMERALD : Theme.BORDER_SUBTLE);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}