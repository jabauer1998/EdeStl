package ede.stl.gui;

import javax.swing.*;
import java.awt.*;

import ede.stl.common.Utils;
import ede.stl.values.RegVal;
import ede.stl.values.Value;
import ede.stl.values.VectorVal;

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
        this.setLayout(new BorderLayout());

        TitleReg = new JLabel(Title);
        TitleReg.setHorizontalAlignment(SwingConstants.LEFT);

        this.Width = Width;
        this.Height = Height;

        RegisterDecimalLength = Length;
        this.regFormat = Format;
        
        RegisterValue = new JLabel(GenZeros());
        RegisterValue.setHorizontalAlignment(SwingConstants.CENTER);

        this.setPreferredSize(new Dimension((int)Width, (int)Height));
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int)Height));
        this.setMinimumSize(new Dimension((int)Width, (int)Height));

        this.add(TitleReg, BorderLayout.WEST);
        this.add(RegisterValue, BorderLayout.CENTER);
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

    public int getRegisterLength(){
        return RegisterDecimalLength;
    }

    public VectorVal GetRegisterVector(){
        String RegisterText = this.RegisterValue.getText();
        int width = (RegisterDecimalLength <= 0) ? 1 : RegisterDecimalLength;
        VectorVal vec = new VectorVal(width - 1, 0);
        if(this.regFormat == Format.BINARY){
            int len = RegisterText.length();
            for(int j = 0; j < len; j++){
                int vecIdx = width - 1 - j;
                if(vecIdx < 0) continue;
                boolean bit = (RegisterText.charAt(j) == '1');
                vec.setValue(vecIdx, new RegVal(bit));
            }
        } else {
            int len = RegisterText.length();
            for(int j = 0; j < len; j++){
                int nibble = Character.digit(RegisterText.charAt(j), 16);
                if(nibble < 0) nibble = 0;
                int baseIdx = width - 1 - 4 * j;
                for(int b = 0; b < 4; b++){
                    int vecIdx = baseIdx - b;
                    if(vecIdx < 0) continue;
                    boolean bit = ((nibble >> (3 - b)) & 1) != 0;
                    vec.setValue(vecIdx, new RegVal(bit));
                }
            }
        }
        return vec;
    }

    public Value GetRegisterValue(){
        return Utils.getOptimalUnsignedForm(GetRegisterVector());
    }

    public void SetRegisterValue(Value value){
        int width = (RegisterDecimalLength <= 0) ? 1 : RegisterDecimalLength;
        boolean[] bits = new boolean[width];
        VectorVal inVec = null;
        if(value != null){
            try {
                inVec = value.asVector();
            } catch(UnsupportedOperationException ignored){
                inVec = null;
            }
        }
        if(inVec != null){
            int inSize = inVec.getSize();
            int idx1 = inVec.getIndex1();
            int idx2 = inVec.getIndex2();
            int step = (idx1 > idx2) ? 1 : (idx1 < idx2) ? -1 : 0;
            for(int k = 0; k < width && k < inSize; k++){
                int vecIdx = (step == 0) ? idx2 : idx2 + step * k;
                bits[k] = inVec.getValue(vecIdx).getStateSignal();
            }
        } else if(value != null){
            long raw = value.longValue();
            int copyBits = Math.min(width, 64);
            for(int k = 0; k < copyBits; k++){
                bits[k] = ((raw >> k) & 1L) != 0;
            }
        }
        StringBuilder sb = new StringBuilder();
        if(this.regFormat == Format.BINARY){
            for(int k = width - 1; k >= 0; k--){
                sb.append(bits[k] ? '1' : '0');
            }
        } else {
            int hexLen = width / 4;
            for(int j = 0; j < hexLen; j++){
                int nibble = 0;
                int baseLsbPos = width - 1 - 4 * j;
                for(int b = 0; b < 4; b++){
                    int kPos = baseLsbPos - b;
                    if(kPos < 0) continue;
                    if(bits[kPos]){
                        nibble |= (1 << (3 - b));
                    }
                }
                sb.append(Character.forDigit(nibble, 16));
            }
        }
        this.RegisterValue.setText(sb.toString());
    }
}
