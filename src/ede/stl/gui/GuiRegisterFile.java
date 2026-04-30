package ede.stl.gui;

import java.util.Collection;
import java.util.HashMap;
import javax.swing.*;
import java.awt.*;

import ede.stl.values.Value;
import ede.stl.values.VectorVal;

public class GuiRegisterFile extends JPanel {
    private JScrollPane Pane;
    private JPanel contentPanel;

    private double RegisterWidth;
    private double RegisterHeight;

    private double actualWidth;
    private double actualHeight;

    private HashMap<String, GuiRegister> regFile;
    private HashMap<Integer, GuiRegister> intRegFile;
    
    public GuiRegisterFile(double Width, double Height){
        actualWidth = Width;
        actualHeight = Height;

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        Pane = new JScrollPane(contentPanel);
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

        contentPanel.add(Register);
        resizeRegisterFile();
    }

    private void resizeRegisterFile(){
        Pane.setPreferredSize(new Dimension((int)actualWidth, (int)actualHeight));
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public Value getRegisterValue(String regName){
        GuiRegister Reg = regFile.get(regName);
        return Reg.GetRegisterValue();
    }

    public Value getRegisterValue(int RegNumber){
        GuiRegister Reg = intRegFile.get(RegNumber);
        return Reg.GetRegisterValue();
    }

    public VectorVal getRegisterVector(String regName){
        GuiRegister Reg = regFile.get(regName);
        return Reg.GetRegisterVector();
    }

    public VectorVal getRegisterVector(int RegNumber){
        GuiRegister Reg = intRegFile.get(RegNumber);
        return Reg.GetRegisterVector();
    }

    public void setRegisterValue(String regName, Value regValue){
        GuiRegister Reg = regFile.get(regName);
        Reg.SetRegisterValue(regValue);
    }

    public void setRegisterValue(int regNumber, Value regValue){
        GuiRegister Reg = intRegFile.get(regNumber);
        Reg.SetRegisterValue(regValue);
    }

    public void clearRegisters(){
        Collection<GuiRegister> registers = regFile.values();
        for(GuiRegister register : registers){
            int width = register.getRegisterLength();
            if(width <= 0) width = 1;
            VectorVal zeroVec = new VectorVal(width - 1, 0);
            register.SetRegisterValue(zeroVec);
        }
    }
}
