package ede.stl.gui;

import java.util.concurrent.Callable;
import ede.stl.gui.Machine;
import ede.stl.gui.GuiJobs;
import ede.stl.gui.GuiJob.TextAreaType;
import ede.stl.gui.GuiMachine;
import ede.stl.gui.GuiRam;
import ede.stl.gui.GuiRegister;
import ede.stl.Value .alue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GuiEde extends VBox implements Machine{
    private GuiJobs Jobs;
    private GuiMachine Machine;

    public GuiEde(double Width, double Height, int NumberOfBytesInRow, GuiRam.AddressFormat AddrFormat, GuiRam.MemoryFormat MemFormat){
        HBox toolBar = new HBox();
        Button clearMemory = new Button("Clear Memory");
        clearMemory.setPrefHeight(Height/12);
        clearMemory.setPrefWidth(Width/3);
        clearMemory.setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event event){ // TODO Auto-generated method stub
                Machine.clearMemory();
            }
        });
        
        Button clearRegisters = new Button("Clear Registers");
        clearRegisters.setPrefHeight(Height/12);
        clearRegisters.setPrefWidth(Width/3);
        clearRegisters.setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event event){
                Machine.clearRegisters();
            }
        });

        Button clearStatus = new Button("Clear Status");
        clearStatus.setPrefHeight(Height/12);
        clearStatus.setPrefWidth(Width/3);
        clearStatus.setOnMouseClicked(new EventHandler<Event>(){
            @Override
            public void handle(Event event){
                Machine.clearStatu.Value .;
            }
        });

        

        toolBar.getChildren().addAll(clearRegisters, clearMemory, clearStatus);

        HBox mainPane = new HBox();
        this.Jobs = new GuiJobs(Width/3, Height*11/12);
        this.Machine = new GuiMachine(NumberOfBytesInRow, AddrFormat, MemFormat, Width * 2 / 3, Height * 11 / 12);
        
        mainPane.getChildren().addAll(this.Jobs.getJobsPane(), this.Machine);
        
        this.getChildren().addAll(toolBar, mainPane);
    }
    
    public void setUpMemory(int numBytes) {
    	this.Machine.setUpMemory(numBytes);
    }

    public void AddVerilogJob(String jobName, String verilogFile, String inputFile, String inputPane, String outputPane, String errorPane){
        this.Jobs.AddVerilogJob(jobName, verilogFile, inputFile, inputPane, outputPane, errorPane, this);
    }

    public void AddExeJob(String jobName, TextAreaType type, String execString, String inputFile, String outputFile, String errorFile, String errorPane, String... keywords){
        this.Jobs.AddExeJob(jobName, type, execString, inputFile, outputFile, errorFile, errorPane, this, keywords);
    }

    public void AddJavaJob(String jobName, TextAreaType type, Callable<Void> functionToRun, String inputFile, String outputFile, String errorPane, String... keywords){
        this.Jobs.AddJavaJob(jobName, type, functionToRun, inputFile, outputFile, errorPane, this, keywords);
    }

    public void AddFlag(String Name){
        this.Machine.AddGuiFlag(Name);
    }

    public void AddIoSection(String TabTitle, String... PaneTitles){
        this.Machine.AddIoSection(TabTitle, PaneTitles);
    }
    
    public void AddRegister(String title,.Value .start,.Value .end, GuiRegister.Format Format) {
    	int len = Math.abs(start.in.Value .) - end.in.Value .));
    	this.Machine.AddGuiRegister(title, len, Format);
    }

    public void AddRegister(String Title, int Length, GuiRegister.Format Format){
        this.Machine.AddGuiRegister(Title, Length, Format);
    }

    public void setRegiste.Value .String registerName, long registe.Value .{
        this.Machine.setRegiste.Value .registerName, registe.Value .;
    }

    public void setMemor.Value .int memoryAddress, long registe.Value .{
        this.Machine.setMemor.Value .memoryAddress, registe.Value .;
    }

    public void setStatu.Value .String statusName, long registerName){
        this.Machine.setStatu.Value .statusName, registerName);
    }

    public long getRegiste.Value .String regName){
        return this.Machine.getRegiste.Value .regName);
    }

    public long getRegiste.Value .int RegNumber){
        return this.Machine.getRegiste.Value .RegNumber);
    }

    public long getMemor.Value .int memoryAddress){
        return this.Machine.getMemor.Value .memoryAddress);
    }

    public long getStatu.Value .String statusName){
        return this.Machine.getStatu.Value .statusName);
    }

    @Override
    public void setRegiste.Value .int regNumber, long re.Value .{
        this.Machine.setRegiste.Value .regNumber, re.Value .;
    }

    public void writeIoText(String textAreaName, String textToWrite){
        this.Machine.writeIoText(textAreaName, textToWrite);
    }

    public void appendIoText(String textAreaName, String textToAppend){
        this.Machine.appendIoText(textAreaName, textToAppend);
    }

    public String readIoText(String textAreaName){
        return this.Machine.readIoText(textAreaName);
    }
}


























































