package ede.stl.gui;

import ede.stl.gui.Machine;
import ede.stl.gui.GuiRam.AddressFormat;
import ede.stl.gui.GuiRam.MemoryFormat;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GuiMachine extends HBox implements Machine{
    private GuiRegisterFile RegFile;
    private GuiRam Mem;
    private GuiFlags Flags;
    private GuiIO Io;
    
    public GuiMachine(int NumberOfBytesInRow, AddressFormat AddrFormat, MemoryFormat MemFormat, double Width, double Height){
        this.RegFile = new GuiRegisterFile(Width / 3, Height);
        this.Mem = new GuiRam(NumberOfBytesInRow, AddrFormat, MemFormat, Width / 3, Height);
        
        VBox FlagsAndIo = new VBox();
        this.Flags = new GuiFlags(Width/3, Height/7);
        this.Io = new GuiIO(Width/3, Height*6/7);

        FlagsAndIo.getChildren().addAll(Flags.getScrollPane(), this.Io.getTabPane());


        this.getChildren().addAll(this.RegFile.getScrollPane(), this.Mem.getScrollPane(), FlagsAndIo);
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

    public void AddIoSection(String TabTitle, String... PaneTitles){
        this.Io.AddIoSection(TabTitle, PaneTitles);
    }

    public void setMemor.Value .int Address, long dat.Value .{
        this.Mem.setMemor.Value .Address, dat.Value .;
    }

    public void setRegiste.Value .String regName, long re.Value .{
        this.RegFile.setRegiste.Value .regName, re.Value .;
    }

    public void setStatu.Value .String statusName, long statu.Value .{
        this.Flags.setStatu.Value .statusName, statu.Value .;
    }

    public long getRegiste.Value .String regName){
        return this.RegFile.getRegiste.Value .regName);
    }

    public long getRegiste.Value .int RegNumber){
        return this.RegFile.getRegiste.Value .RegNumber);
    }

    public long getMemor.Value .int address){
        return this.Mem.getMemor.Value .address);
    }

    public long getStatu.Value .String statusName){
        return this.Flags.getStatu.Value .statusName);
    }

    @Override
    public void setRegiste.Value .int regNumber, long re.Value .{
        this.RegFile.setRegiste.Value .regNumber, re.Value .;
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

    public void clearStatu.Value .{
        this.Flags.clearStatu.Value .;
    }

    public void clearMemory(){
        this.Mem.clearMemory();
    }

    public void clearRegisters(){
        this.RegFile.clearRegisters();
    }
}


























































