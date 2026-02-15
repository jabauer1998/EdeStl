package ede.stl.gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import ede.stl.gui.Memory;
import javax.swing.*;
import java.awt.*;

public class GuiRam extends JPanel implements Memory {
    private ArrayList<JLabel> Bytes;
    private ArrayList<JLabel> Addresses;
    
    private int NumberOfBytes;
    private int NumberRowsRounded;
    private int BytesPerRow;

    private JScrollPane Pane;
    private AddressFormat AddrFormat;
    private MemoryFormat MemFormat;
    
    private double screenWidth;
    private double screenHeight;

    public enum AddressFormat{
        BINARY,
        HEXIDECIMAL,
        OCTAL,
        DECIMAL
    }

    public enum MemoryFormat{
        BINARY,
        HEXADECIMAL,
    }

    public GuiRam(int BytesPerRow, AddressFormat AddrFormat, MemoryFormat MemFormat, double Width, double Height){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.BytesPerRow = BytesPerRow;

        this.MemFormat = MemFormat;
        this.AddrFormat = AddrFormat;

        this.setMaximumSize(new Dimension((int)Width, (int)Height));
        this.setPreferredSize(new Dimension((int)Width, (int)Height));
        
        this.Pane = new JScrollPane(this);
        this.Pane.setMaximumSize(new Dimension((int)Width, (int)Height));
        this.Pane.setPreferredSize(new Dimension((int)Width, (int)Height));
        this.Pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        Bytes = new ArrayList<>();
        Addresses = new ArrayList<>();
        
        this.screenWidth = Width;
        this.screenHeight = Height;
    }
    
    public void setMemory(int numBytes) {
        this.NumberOfBytes = numBytes;
        this.NumberRowsRounded = (int)Math.ceil((NumberOfBytes / BytesPerRow));
        
        int Byte = 0;
        for(int Row = 0; Row < this.NumberRowsRounded; Row++){
            JPanel AddressToMemory = new JPanel();
            AddressToMemory.setLayout(new BoxLayout(AddressToMemory, BoxLayout.X_AXIS));
            JPanel RowOfMemory = new JPanel();
            RowOfMemory.setLayout(new BoxLayout(RowOfMemory, BoxLayout.X_AXIS));

            if(this.AddrFormat == AddressFormat.HEXIDECIMAL){
                Addresses.add(new JLabel(Integer.toHexString(Byte)));
            } else if(this.AddrFormat == AddressFormat.BINARY){
                Addresses.add(new JLabel(Integer.toBinaryString(Byte)));
            } else if(this.AddrFormat == AddressFormat.OCTAL){
                Addresses.add(new JLabel(Integer.toOctalString(Byte)));
            } else {
                Addresses.add(new JLabel(Integer.toString(Byte)));
            }
            
            for(int i = 0; i < this.BytesPerRow; i++, Byte++){
                if(this.MemFormat == MemoryFormat.HEXADECIMAL){
                    Bytes.add(new JLabel("00"));
                    Bytes.get(Bytes.size()-1).setPreferredSize(new Dimension((int)(this.screenWidth/(this.BytesPerRow * 2)), 20));
                    Bytes.get(Bytes.size()-1).setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    Bytes.add(new JLabel("00000000"));
                    Bytes.get(Bytes.size()-1).setPreferredSize(new Dimension((int)(screenWidth/(this.BytesPerRow * 3)), 20));
                    Bytes.get(Bytes.size()-1).setHorizontalAlignment(SwingConstants.RIGHT);
                }
                RowOfMemory.add(Bytes.get(Bytes.size()-1));
            }
            Addresses.get(Row).setHorizontalAlignment(SwingConstants.LEFT);
            Addresses.get(Row).setPreferredSize(new Dimension((int)(screenWidth/3), 20));
            AddressToMemory.add(Addresses.get(Row));
            AddressToMemory.add(RowOfMemory);
            AddressToMemory.setPreferredSize(new Dimension((int)screenWidth, 20));
            this.add(AddressToMemory);
        }
    }

    public void LoadProgram(String Program){
        Program = Program.trim();
        int Length = Program.length();
        List<String> Results = new LinkedList<String>(); 
        for (int i = 0; i < Length; i += 8) {
            Results.add(Program.substring(i, Math.min(Length, i + 8)));
        }

        for(int i = 0; i < Results.size() && i < NumberOfBytes; i++){
            String First = Results.remove(0);
            Bytes.get(i).setText(First);
        }
    }

    public JScrollPane getScrollPane(){
        return Pane;
    }

    @Override
    public void setMemoryValue(int address, long dataValue){
        JLabel Byte = Bytes.get(address);
        if(MemFormat == MemoryFormat.BINARY){
            String asString = Long.toBinaryString(dataValue);
            if(asString.length() > 8){
                asString = asString.substring(asString.length() - 8);
            } else if(asString.length() < 8){
                StringBuilder Builder = new StringBuilder();
                int NumberOfZeros = 8 - asString.length();
                for(int i = 0; i < NumberOfZeros; i++){
                    Builder.append('0');
                }
                Builder.append(asString);
                asString = Builder.toString();
            }
            Byte.setText(asString);
        } else {
            String asString = Long.toHexString(dataValue);
            if(asString.length() > 2){
                asString = asString.substring(asString.length() - 2);
            } else if(asString.length() < 2){
                StringBuilder Builder = new StringBuilder();
                int NumberOfZeros = 2 - asString.length();
                for(int i = 0; i < NumberOfZeros; i++){
                    Builder.append('0');
                }
                Builder.append(asString);
                asString = Builder.toString();
            }
            Byte.setText(asString);
        }
    }

    @Override
    public long getMemoryValue(int address){
        JLabel Byte = Bytes.get(address);
        String text = Byte.getText();
        
        if(MemFormat == MemoryFormat.BINARY){
            return Long.parseLong(text, 2);
        } else {
            return Long.parseLong(text, 16);
        }
    }

    public void clearMemory(){
        for(int i = 0; i < NumberOfBytes; i++){
            this.setMemoryValue(i, 0);
        }
    }
}
