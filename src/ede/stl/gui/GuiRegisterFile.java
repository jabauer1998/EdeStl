package ede.stl.gui;

import java.util.Collection;
import java.util.HashMap;
import ede.stl.gui.RegFile;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.VBox;

public class GuiRegisterFile extends VBox implements RegFile {
    private ScrollPane Pane;

    private double RegisterWidth;
    private double RegisterHeight;

    private double actualWidth;
    private double actualHeight;

    private HashMap<String, GuiRegister> regFile;
    private HashMap<Integer, GuiRegister> intRegFile;
    
    public GuiRegisterFile(double Width, double Height){
        actualWidth = Width;
        actualHeight = Height;
        
        Pane = new ScrollPane();
        Pane.setHbarPolicy(ScrollBarPolicy.NEVER);
        Pane.setContent(this);

        this.RegisterWidth = Width;
        this.RegisterHeight = Height / 6;

        regFile = new HashMap<>();
        intRegFile = new HashMap<>();
    }

    public ScrollPane getScrollPane(){
        return Pane;
    }

    public void AddGuiRegister(String Title, int Length, GuiRegister.Format Format){
        GuiRegister Register = new GuiRegister(Title, Length, Format, RegisterWidth, RegisterHeight);
        
        regFile.put(Title, Register);

        StringBuilder numberBuilder = new StringBuilder();
        char[] TitleChars = Title.toCharArray();

        for(char TitleChar : TitleChars){
            if(Character.isDigit(TitleChar)){
                numberBuilder.append(TitleChar);
            }
        }

        if(numberBuilder.toString().length() > 0){
            int lookupInt = Integer.parseInt(numberBuilder.toString());
            intRegFile.put(lookupInt, Register);
        }

        this.getChildren().add(Register);
        resizeRegisterFile();
    }

    private void resizeRegisterFile(){
        this.setPrefWidth(actualWidth);
        this.setPrefHeight(actualHeight);

        Pane.setPrefHeight(actualHeight);
        Pane.setPrefWidth(actualWidth);
    }

    @Override
    public long getRegiste.Value .String regName){
        GuiRegister Reg = regFile.get(regName);
        return Reg.GetRegiste.Value .);
    }

    public long getRegiste.Value .int RegNumber){
        GuiRegister Reg = intRegFile.get(RegNumber);
        return Reg.GetRegiste.Value .);
    }

    @Override
    public void setRegiste.Value .String regName, long re.Value .{ 
       GuiRegister Reg = regFile.get(regName);
       Reg.SetRegiste.Value .re.Value .;
    }

     
    public void setRegiste.Value .int regNumber, long re.Value .{
        GuiRegister Reg = intRegFile.get(regNumber);
        Reg.SetRegiste.Value .re.Value .;
    }

    public void clearRegisters(){
        Collection<GuiRegister> registers = regFile.Value .;
        for(GuiRegister register : registers){
            register.SetRegiste.Value .0);
        }
    }
}


























































