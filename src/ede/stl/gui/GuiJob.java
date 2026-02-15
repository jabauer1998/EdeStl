package ede.stl.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ede.stl.common.Search;
import ede.stl.common.Search.SearchDirection;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public abstract class GuiJob extends JPanel {
    private JButton ExeButton;
    private JComponent InputSection;
    private HashSet<String> keywords;

    public enum TextAreaType{
        DEFAULT,
        KEYWORD
    }

    protected GuiJob(String ButtonText, TextAreaType type, double Width, double Height, String... keywordArr){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        ExeButton = new JButton(ButtonText);
        ExeButton.setPreferredSize(new Dimension((int)Width, 30));
        ExeButton.setMaximumSize(new Dimension((int)Width, 30));
        ExeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                RunJob();
            }
        });

        keywords = new HashSet<String>();

        for(String keyword : keywordArr){
            this.keywords.add(keyword);
        }

        if(type == TextAreaType.KEYWORD){
            JTextPane textPane = new JTextPane();
            textPane.setPreferredSize(new Dimension((int)Width, (int)Height));
            InputSection = textPane;

            textPane.addKeyListener(new KeyAdapter(){
                @Override
                public void keyTyped(KeyEvent arg0){
                    StyledDocument doc = textPane.getStyledDocument();
                    int cursorPosition = textPane.getCaretPosition();
                    String text = textPane.getText();

                    int findEndPositionLeft = Search.findNextNonWhitespace(cursorPosition, text, SearchDirection.LEFT);
                    int findBeginPositionLeft = Search.findNextWhiteSpace(findEndPositionLeft, text, SearchDirection.LEFT);

                    int findBeginPositionRight = Search.findNextNonWhitespace(cursorPosition, text, SearchDirection.RIGHT);
                    int findEndPositionRight = Search.findNextWhiteSpace(findBeginPositionRight, text, SearchDirection.RIGHT);

                    StyleContext sc = StyleContext.getDefaultStyleContext();

                    String leftSubString = text.substring(findBeginPositionLeft, findEndPositionLeft);
                    if(keywords.contains(leftSubString)){
                        AttributeSet blueAttr = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLUE);
                        doc.setCharacterAttributes(findBeginPositionLeft, findEndPositionLeft - findBeginPositionLeft, blueAttr, false);
                    } else {
                        AttributeSet blackAttr = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
                        doc.setCharacterAttributes(findBeginPositionLeft, findEndPositionLeft - findBeginPositionLeft, blackAttr, false);
                    }

                    String rightSubString = text.substring(findBeginPositionRight, findEndPositionRight + 1);
                    if(keywords.contains(rightSubString)){
                        AttributeSet blueAttr = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLUE);
                        doc.setCharacterAttributes(findBeginPositionRight, findEndPositionRight + 1 - findBeginPositionRight, blueAttr, false);
                    } else {
                        AttributeSet blackAttr = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
                        doc.setCharacterAttributes(findBeginPositionRight, findEndPositionRight + 1 - findBeginPositionRight, blackAttr, false);
                    }
                }
            });
        } else {
            JTextArea textArea = new JTextArea();
            textArea.setPreferredSize(new Dimension((int)Width, (int)Height));
            InputSection = textArea;
        }

        this.add(ExeButton);
        this.add(InputSection);
    }

    public abstract void RunJob();

    public JComponent getInputSection(){
        return InputSection;
    }
}
