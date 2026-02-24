package ede.stl.gui;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class GuiLineNumberGutter extends JComponent {
    private JComponent textComponent;
    private int gutterWidth = 40;

    public LineNumberGutter(JComponent textComponent) {
        this.textComponent = textComponent;
        setFont(textComponent.getFont());

        if (textComponent instanceof JTextArea) {
            ((JTextArea)textComponent).getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { updateAndRepaint(); }
                public void removeUpdate(DocumentEvent e) { updateAndRepaint(); }
                public void changedUpdate(DocumentEvent e) { updateAndRepaint(); }
            });
        } else if (textComponent instanceof JTextPane) {
            ((JTextPane)textComponent).getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { SwingUtilities.invokeLater(() -> updateAndRepaint()); }
                public void removeUpdate(DocumentEvent e) { SwingUtilities.invokeLater(() -> updateAndRepaint()); }
                public void changedUpdate(DocumentEvent e) {}
            });
        }

        textComponent.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) { updateAndRepaint(); }
        });
    }

    private void updateAndRepaint() {
        Dimension newSize = new Dimension(gutterWidth, textComponent.getPreferredSize().height);
        if (!newSize.equals(getPreferredSize())) {
            setPreferredSize(newSize);
            revalidate();
        }
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(gutterWidth, textComponent.getPreferredSize().height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(230, 230, 230));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.GRAY);
        g.setFont(textComponent.getFont());

        FontMetrics fm = g.getFontMetrics();
        int totalLines;

        if (textComponent instanceof JTextArea) {
            totalLines = ((JTextArea)textComponent).getLineCount();
        } else if (textComponent instanceof JTextPane) {
            String text = ((JTextPane)textComponent).getText();
            totalLines = 1;
            for (int i = 0; i < text.length(); i++) {
                if (text.charAt(i) == '\n') totalLines++;
            }
        } else {
            return;
        }

        int digits = String.valueOf(totalLines).length();
        int newWidth = fm.stringWidth("0".repeat(Math.max(digits, 3))) + 16;
        if (newWidth != gutterWidth) {
            gutterWidth = newWidth;
            revalidate();
        }

        Insets insets = textComponent.getInsets();
        int y = insets.top + fm.getAscent();
        int lineHeight = fm.getHeight();
        for (int line = 1; line <= totalLines; line++) {
            String num = String.valueOf(line);
            int x = getWidth() - fm.stringWidth(num) - 8;
            g.drawString(num, x, y);
            y += lineHeight;
        }
    }
}
