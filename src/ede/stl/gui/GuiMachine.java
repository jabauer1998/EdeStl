package ede.stl.gui;

import ede.stl.gui.Machine;
import ede.stl.gui.GuiRam.AddressFormat;
import ede.stl.gui.GuiRam.MemoryFormat;
import javax.swing.*;
import java.awt.*;

public class GuiMachine extends JPanel implements Machine {
    private GuiRegisterFile RegFile;
    private GuiRam Mem;
    private GuiFlags Flags;
    private GuiIO Io;
    
    public GuiMachine(int NumberOfBytesInRow, AddressFormat AddrFormat, MemoryFormat MemFormat, double Width, double Height){
        this.setLayout(new BorderLayout());

        double thirdWidth = Width / 3;

        this.RegFile = new GuiRegisterFile(thirdWidth, Height);
        this.Mem = new GuiRam(NumberOfBytesInRow, AddrFormat, MemFormat, thirdWidth, Height);
        
        JPanel FlagsAndIo = new JPanel();
        FlagsAndIo.setLayout(new BoxLayout(FlagsAndIo, BoxLayout.Y_AXIS));
        this.Flags = new GuiFlags(thirdWidth, Height/7);
        this.Io = new GuiIO(thirdWidth, Height*6/7);

        FlagsAndIo.add(Flags.getScrollPane());
        FlagsAndIo.add(this.Io.getTabPane());

        JScrollPane regScroll = this.RegFile.getScrollPane();
        JScrollPane memScroll = this.Mem.getScrollPane();

        regScroll.setMinimumSize(new Dimension(80, 0));
        memScroll.setMinimumSize(new Dimension(80, 0));
        FlagsAndIo.setMinimumSize(new Dimension(80, 0));

        JSplitPane rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, memScroll, FlagsAndIo);
        rightSplit.setDividerLocation((int)thirdWidth);
        rightSplit.setResizeWeight(0.5);
        rightSplit.setContinuousLayout(true);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, regScroll, rightSplit);
        mainSplit.setDividerLocation((int)thirdWidth);
        mainSplit.setResizeWeight(0.33);
        mainSplit.setContinuousLayout(true);

        this.add(mainSplit, BorderLayout.CENTER);
    }
    
    public void setUpMemory(int numBytes){
        this.Mem.setMemory(numBytes);
    }

    public void AddGuiRegister(String Title, int Length, GuiRegister.Format Format){
        this.RegFile.AddGuiRegister(Title, Length, Format);
    }

    public void AddGuiFlag(String Name){
        this.Flags.AddGuiFlag(Name);
    }

    public void AddIoSection(String TabTitle, String PaneTitle, GuiIO.Editable editable){
        this.Io.AddIoSection(TabTitle, PaneTitle, editable);
    }

    public void setMemoryValue(int Address, long dataValue){
        this.Mem.setMemoryValue(Address, dataValue);
    }

    public void setRegisterValue(String regName, long regValue){
        this.RegFile.setRegisterValue(regName, regValue);
    }

    public void setStatusValue(String statusName, long statusValue){
        this.Flags.setStatusValue(statusName, statusValue);
    }

    public long getRegisterValue(String regName){
        return this.RegFile.getRegisterValue(regName);
    }

    public long getRegisterValue(int RegNumber){
        return this.RegFile.getRegisterValue(RegNumber);
    }

    public long getMemoryValue(int address){
        return this.Mem.getMemoryValue(address);
    }

    public long getStatusValue(String statusName){
        return this.Flags.getStatusValue(statusName);
    }

    @Override
    public void setRegisterValue(int regNumber, long regValue){
        this.RegFile.setRegisterValue(regNumber, regValue);
    }

    public void appendIoText(String Name, String textToAppend){
        this.Io.appendIoText(Name, textToAppend);
    }

    public void writeIoText(String Name, String textToWrite){
        this.Io.writeIoText(Name, textToWrite);
    }

    public String readIoText(String Name){
        return this.Io.readIoText(Name);
    }

    public void clearStatusValues(){
        this.Flags.clearStatusValues();
    }

    public void clearMemory(){
        this.Mem.clearMemory();
    }

    public void clearRegisters(){
        this.RegFile.clearRegisters();
    }
}
