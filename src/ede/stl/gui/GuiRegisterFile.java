package ede.stl.gui;

import java.util.Collection;
import java.util.HashMap;
import ede.stl.gui.RegFile;
import javax.swing.*;
import java.awt.*;

public class GuiRegisterFile extends JPanel implements RegFile {
    private JScrollPane Pane;

    private double RegisterWidth;
    private double RegisterHeight;

    private double actualWidth;
    private double actualHeight;

    private HashMap<String, GuiRegister> regFile;
    private HashMap<Integer, GuiRegister> intRegFile;
    
    public GuiRegisterFile(double Width, double Height){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        actualWidth = Width;
        actualHeight = Height;
        
        Pane = new JScrollPane(this);
        Pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        this.RegisterWidth = Width;
        this.RegisterHeight = Height / 6;

        regFile = new HashMap<>();
        intRegFile = new HashMap<>();
    }

    public JScrollPane getScrollPane(){
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

        this.add(Register);
        resizeRegisterFile();
    }

    private void resizeRegisterFile(){
        this.setPreferredSize(new Dimension((int)actualWidth, (int)actualHeight));

        Pane.setPreferredSize(new Dimension((int)actualWidth, (int)actualHeight));
    }

    @Override
    public long getRegisterValue(String regName){
        GuiRegister Reg = regFile.get(regName);
        return Reg.GetRegisterValue();
    }

    public long getRegisterValue(int RegNumber){
        GuiRegister Reg = intRegFile.get(RegNumber);
        return Reg.GetRegisterValue();
    }

    @Override
    public void setRegisterValue(String regName, long regValue){ 
       GuiRegister Reg = regFile.get(regName);
       Reg.SetRegisterValue(regValue);
    }

    public void setRegisterValue(int regNumber, long regValue){
        GuiRegister Reg = intRegFile.get(regNumber);
        Reg.SetRegisterValue(regValue);
    }

    public void clearRegisters(){
        Collection<GuiRegister> registers = regFile.values();
        for(GuiRegister register : registers){
            register.SetRegisterValue(0);
        }
    }
}
