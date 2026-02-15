package ede.stl.gui;

import java.util.Collection;
import java.util.HashMap;
import ede.stl.gui.Flags;
import javax.swing.*;
import java.awt.*;

public class GuiFlags extends JPanel implements Flags {
    private JScrollPane FlagPane;
    private HashMap<String, GuiFlag> flagMap;

    private double actualWidth;
    private double actualHeight;
    
    public GuiFlags(double Width, double Height){
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        FlagPane = new JScrollPane(this);
        FlagPane.setMaximumSize(new Dimension((int)Width, (int)Height));
        FlagPane.setPreferredSize(new Dimension((int)Width, (int)Height));

        this.actualHeight = Height;
        this.actualWidth = Width;

        FlagPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        flagMap = new HashMap<>();
    }

    public void AddGuiFlag(String Name){
        GuiFlag Flag = new GuiFlag(Name, actualWidth / 6, actualHeight);

        this.add(Flag);

        flagMap.put(Flag.getName(), Flag);
    }

    public JScrollPane getScrollPane(){
        return FlagPane;
    }

    @Override
    public long getStatusValue(String statusName){
        GuiFlag Flag = flagMap.get(statusName);
        return Flag.isSet() ? 1 : 0;
    }

    @Override
    public void setStatusValue(String statusName, long statusValue){
        GuiFlag Flag = flagMap.get(statusName);
        Flag.Set(statusValue != 0);
    }

    public void clearStatusValues(){
        Collection<GuiFlag> values = flagMap.values();
        for(GuiFlag value : values){
            value.Set(false);
        }
    }
}
