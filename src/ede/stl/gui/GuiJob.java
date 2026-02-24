package ede.stl.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ede.stl.common.Search;
import ede.stl.common.Search.SearchDirection;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public abstract class GuiJob extends JPanel {
    private JButton ExeButton;
    private JComponent InputSection;
    private HashSet<String> keywords;
    private TextAreaNumbered isNumbered;

    private static class LineNumberGutter extends JComponent {
        private JComponent textComponent;
        private int gutterWidth = 40;

        LineNumberGutter(JComponent textComponent) {
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
    public enum TextAreaType{
        DEFAULT,
        KEYWORD,
        NONE
    }

    public enum TextAreaNumbered{
        IS_NUMBERED,
        IS_NOT_NUMBERED
    }

    protected GuiJob(String ButtonText, TextAreaType type, TextAreaNumbered isNumbered, double Width, double Height, String... keywordArr){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.isNumbered = isNumbered;
        
        ExeButton = new JButton(ButtonText);
        ExeButton.setPreferredSize(new Dimension((int)Width, 30));
        ExeButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        ExeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        ExeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                RunJob();
            }
        });

        keywords = new HashSet<String>();

        for(String keyword : keywordArr){
            this.keywords.add(keyword);
        }

        JScrollPane scrollPane = null;

        if(type == TextAreaType.KEYWORD){
            JTextPane textPane = new JTextPane();
            InputSection = textPane;

            textPane.getStyledDocument().addDocumentListener(new DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e){ highlightKeywords(textPane); }
                @Override
                public void removeUpdate(DocumentEvent e){ highlightKeywords(textPane); }
                @Override
                public void changedUpdate(DocumentEvent e){ }
            });

            scrollPane = new JScrollPane(textPane);
        } else if(type == TextAreaType.DEFAULT) {
            JTextArea textArea = new JTextArea();
            InputSection = textArea;
            scrollPane = new JScrollPane(textArea);
        }

        if(scrollPane != null && isNumbered == TextAreaNumbered.IS_NUMBERED){
            scrollPane.setRowHeaderView(new LineNumberGutter(InputSection));
        }

        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int)Height + 30));
        this.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.add(ExeButton);
        if(type != TextAreaType.NONE && scrollPane != null){
            scrollPane.setPreferredSize(new Dimension((int)Width, (int)Height));
            scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int)Height));
            scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            this.add(scrollPane);
        }
    }

    private void highlightKeywords(JTextPane textPane){
        SwingUtilities.invokeLater(() -> {
            try {
                StyledDocument doc = textPane.getStyledDocument();
                String text = doc.getText(0, doc.getLength());
                if(text.isEmpty()) return;

                StyleContext sc = StyleContext.getDefaultStyleContext();
                AttributeSet blackAttr = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
                AttributeSet blueAttr = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLUE);

                doc.setCharacterAttributes(0, text.length(), blackAttr, false);

                int i = 0;
                while(i < text.length()){
                    if(Character.isWhitespace(text.charAt(i))){
                        i++;
                        continue;
                    }
                    int start = i;
                    while(i < text.length() && !Character.isWhitespace(text.charAt(i))){
                        i++;
                    }
                    String word = text.substring(start, i);
                    if(keywords.contains(word)){
                        doc.setCharacterAttributes(start, word.length(), blueAttr, false);
                    }
                }
            } catch(BadLocationException ex){
            }
        });
    }

    public abstract void RunJob();

    public JComponent getInputSection(){
        return InputSection;
    }

    public String getText(){
        if(InputSection instanceof JTextPane){
            return ((JTextPane)InputSection).getText();
        } else if(InputSection instanceof JTextArea){
            return ((JTextArea)InputSection).getText();
        }
        return "";
    }

    public void setText(String text){
        if(InputSection instanceof JTextPane){
            ((JTextPane)InputSection).setText(text);
        } else if(InputSection instanceof JTextArea){
            ((JTextArea)InputSection).setText(text);
        }
    }
}
