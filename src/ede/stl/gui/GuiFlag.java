package ede.stl.gui;

import javax.swing.*;
import java.awt.*;

public class GuiFlag extends JPanel {
    private JLabel Name;
    private JLabel BitSet;
    
    public GuiFlag(String Name, double Width, double Height){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.Name = new JLabel(Name);
        this.Name.setMaximumSize(new Dimension((int)Width, (int)(Height/2)));
        this.Name.setPreferredSize(new Dimension((int)Width, (int)(Height/2)));
        this.Name.setHorizontalAlignment(SwingConstants.CENTER);
        this.Name.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.BitSet = new JLabel("0");
        this.BitSet.setMaximumSize(new Dimension((int)Width, (int)(Height/2)));
        this.BitSet.setMinimumSize(new Dimension((int)Width, (int)(Height/2)));
        this.BitSet.setPreferredSize(new Dimension((int)Width, (int)(Height/2)));
        this.BitSet.setHorizontalAlignment(SwingConstants.CENTER);
        this.BitSet.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.add(this.Name);
        this.add(this.BitSet);

        this.setMaximumSize(new Dimension((int)Width, (int)Height));
        this.setPreferredSize(new Dimension((int)Width, (int)Height));
    }

    public void Set(boolean IsSet){
        if(IsSet){
            this.BitSet.setText("1");
        } else {
            this.BitSet.setText("0");
        }
    }

    public String getName(){
        return Name.getText();
    }

    public boolean isSet(){
        String Text = this.BitSet.getText();
        if(Text.equals("1")){
            return true;
        } else {
            return false;
        }
    }
}
