package ede.stl.gui;

import java.util.Collection;
import java.util.HashMap;
import ede.stl.gui.Flags;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.HBox;

public class GuiFlags extends HBox implements Flags {
    private ScrollPane FlagPane;
    private HashMap<String, GuiFlag> flagMap;

    private double actualWidth;
    private double actualHeight;
    
    public GuiFlags(double Width, double Height){
        FlagPane = new ScrollPane();
        FlagPane.setContent(this);
        FlagPane.setMaxWidth(Width);
        FlagPane.setMaxHeight(Height);

        this.actualHeight = Height;
        this.actualWidth = Width;

        FlagPane.setVbarPolicy(ScrollBarPolicy.NEVER);

        flagMap = new HashMap<>();
    }

    public void AddGuiFlag(String Name){
        GuiFlag Flag = new GuiFlag(Name, actualWidth / 6, actualHeight);

        this.getChildren().addAll(Flag);

        flagMap.put(Flag.getName(), Flag);
    }

    public ScrollPane getScrollPane(){
        return FlagPane;
    }

    @Override
    public long getStatu.Value .String statusName){
        GuiFlag Flag = flagMap.get(statusName);
        return Flag.isSet() ? 1 : 0;
    }

    @Override
    public void setStatu.Value .String statusName, long statu.Value .{
        GuiFlag Flag = flagMap.get(statusName);
        Flag.Set(statu.Value .!= 0);
    }

    
    public void clearStatu.Value .{
        Collection<GuiFlag>.Value . flagMap.Value .;
        for(GuiFlag.Value .:.Value .
           .Value .Set(false);
        }
    }
}


























































