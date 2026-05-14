package ede.stl.gui;

import ede.stl.gui.GuiRams;
import ede.stl.gui.GuiRams.AddressFormat;
import ede.stl.gui.GuiRams.MemoryFormat;
import javax.swing.*;
import java.awt.*;

public class GuiMachine extends JPanel {
    private GuiRegisterFile RegFile;
    private GuiRams Mem;
    private GuiFlags Flags;
    private GuiIO Io;
    private JCheckBox debuggerCheckBox;
    
    public GuiMachine(int NumberOfBytesInRow, GuiRams.AddressFormat AddrFormat, GuiRams.MemoryFormat MemFormat, double Width, double Height){
        this.setLayout(new BorderLayout());

        double thirdWidth = Width / 3;

        this.RegFile = new GuiRegisterFile(thirdWidth, Height);
        this.Mem = new GuiRams(NumberOfBytesInRow, AddrFormat, MemFormat, thirdWidth, Height);
        
        JPanel FlagsAndIo = new JPanel();
        FlagsAndIo.setLayout(new BoxLayout(FlagsAndIo, BoxLayout.Y_AXIS));
        this.Flags = new GuiFlags(thirdWidth, Height/7);
        this.Io = new GuiIO(thirdWidth, Height*6/7);

        FlagsAndIo.add(Flags.getScrollPane());

        this.debuggerCheckBox = new JCheckBox("Enable Debugger");
        this.debuggerCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        FlagsAndIo.add(this.debuggerCheckBox);

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

    public void addMemory(String name, int length){
	this.Mem.addMemory(name, length);
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

    public void setMemoryValue(String name, int Address, long dataValue){
        this.Mem.setMemoryValue(name, Address, dataValue);
    }

    public void setStatusValue(String statusName, long statusValue){
        this.Flags.setStatusValue(statusName, statusValue);
    }

    public ede.stl.values.Value getRegisterValue(String regName){
        return this.RegFile.getRegisterValue(regName);
    }

    public ede.stl.values.Value getRegisterValue(int RegNumber){
        return this.RegFile.getRegisterValue(RegNumber);
    }

    public ede.stl.values.VectorVal getRegisterVector(String regName){
        return this.RegFile.getRegisterVector(regName);
    }

    public ede.stl.values.VectorVal getRegisterVector(int RegNumber){
        return this.RegFile.getRegisterVector(RegNumber);
    }

    public void setRegisterValue(String regName, ede.stl.values.Value regValue){
        this.RegFile.setRegisterValue(regName, regValue);
    }

    public void setRegisterValue(int regNumber, ede.stl.values.Value regValue){
        this.RegFile.setRegisterValue(regNumber, regValue);
    }

    public long getMemoryValue(String name, int address){
        return this.Mem.getMemoryValue(name, address);
    }

    public long getStatusValue(String statusName){
        return this.Flags.getStatusValue(statusName);
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

    public void clearMemory(String name){
        this.Mem.clearMemory(name);
    }

    public void clearMemory(){
	this.Mem.clearMemory();
    }

    public void clearRegisters(){
        this.RegFile.clearRegisters();
    }

    public boolean isDebuggerEnabled(){
        return this.debuggerCheckBox.isSelected();
    }
}
