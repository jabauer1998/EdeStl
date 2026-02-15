package ede.stl.gui;

import javax.swing.*;
import java.awt.*;

public class GuiRegister extends JPanel {
    private JLabel TitleReg;
    private JLabel RegisterValue;
    private int RegisterDecimalLength;
    private double Width;
    private double Height;

    public enum Format{
        HEXIDECIMAL,
        BINARY
    }

    private Format regFormat;

    public GuiRegister(String Title, int Length, Format Format, double Width, double Height){
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        TitleReg = new JLabel(Title);
        TitleReg.setPreferredSize(new Dimension((int)(Width/6), (int)Height));

        this.Width = Width;
        this.Height = Height;

        RegisterDecimalLength = Length;
        this.regFormat = Format;
        
        RegisterValue = new JLabel(GenZeros());
        RegisterValue.setPreferredSize(new Dimension((int)(Width*5/6), (int)Height));

        this.add(TitleReg);
        this.add(RegisterValue);
    }

    public String getTitle(){
        return TitleReg.getText();
    }

    private String GenZeros(){  
        StringBuilder Sb = new StringBuilder();
        for(int i = 0; i < RegisterDecimalLength; i++){
            Sb.append("0");
        }
        return Sb.toString();
    }

    public void SetRegisterValue(long Value){
        if(this.regFormat == Format.BINARY){
            String strVal = Long.toBinaryString(Value);
            if(strVal.length() > this.RegisterDecimalLength){
                strVal = strVal.substring(strVal.length() - RegisterDecimalLength);
            } else if(strVal.length() < this.RegisterDecimalLength){
                StringBuilder padder = new StringBuilder();
                int NumberOfZeros = this.RegisterDecimalLength - strVal.length();
                for(int i = 0; i < NumberOfZeros; i++){
                    padder.append('0');
                }
                padder.append(strVal);
                strVal = padder.toString();
            }
            this.RegisterValue.setText(strVal);
        } else {
            String strVal = Long.toHexString(Value);
            int HexDecimalLength = this.RegisterDecimalLength/4;
            if(strVal.length() > HexDecimalLength){
                strVal = strVal.substring(strVal.length() - HexDecimalLength);
            } else if(strVal.length() < HexDecimalLength){
                StringBuilder padder = new StringBuilder();
                int NumberOfZeros = HexDecimalLength - strVal.length();
                for(int i = 0; i < NumberOfZeros; i++){
                    padder.append('0');
                }
                padder.append(strVal);
                strVal = padder.toString();
            }
        }
    }

    public long GetRegisterValue(){
        String RegisterText = this.RegisterValue.getText();
        if(this.regFormat == Format.BINARY){
            return Long.parseLong(RegisterText, 2);
        } else {
            return Long.parseLong(RegisterText, 16);
        }
    }
}
