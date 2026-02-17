package ede.stl.gui;

import java.util.concurrent.Callable;
import ede.stl.gui.Machine;
import ede.stl.gui.GuiJobs;
import ede.stl.gui.GuiJob.TextAreaType;
import ede.stl.gui.GuiMachine;
import ede.stl.gui.GuiRam;
import ede.stl.gui.GuiRegister;
import ede.stl.values.Value;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GuiEde extends JPanel implements Machine {
    private GuiJobs Jobs;
    private GuiMachine Machine;

    public GuiEde(double Width, double Height, int NumberOfBytesInRow, GuiRam.AddressFormat AddrFormat, GuiRam.MemoryFormat MemFormat){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel toolBar = new JPanel();
        toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));

        JButton clearMemory = new JButton("Clear Memory");
        clearMemory.setPreferredSize(new Dimension((int)(Width/3), (int)(Height/12)));
        clearMemory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event){
                Machine.clearMemory();
            }
        });
        
        JButton clearRegisters = new JButton("Clear Registers");
        clearRegisters.setPreferredSize(new Dimension((int)(Width/3), (int)(Height/12)));
        clearRegisters.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event){
                Machine.clearRegisters();
            }
        });

        JButton clearStatus = new JButton("Clear Status");
        clearStatus.setPreferredSize(new Dimension((int)(Width/3), (int)(Height/12)));
        clearStatus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event){
                Machine.clearStatusValues();
            }
        });

        toolBar.add(clearRegisters);
        toolBar.add(clearMemory);
        toolBar.add(clearStatus);

        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.X_AXIS));
        this.Jobs = new GuiJobs(Width/3, Height*11/12);
        this.Machine = new GuiMachine(NumberOfBytesInRow, AddrFormat, MemFormat, Width * 2 / 3, Height * 11 / 12);
        
        mainPane.add(this.Jobs.getJobsPane());
        mainPane.add(this.Machine);
        
        this.add(toolBar);
        this.add(mainPane);
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
    
    public void AddRegister(String title, Value start, Value end, GuiRegister.Format Format) {
        int len = Math.abs(start.intValue() - end.intValue());
        this.Machine.AddGuiRegister(title, len, Format);
    }

    public void AddRegister(String Title, int Length, GuiRegister.Format Format){
        this.Machine.AddGuiRegister(Title, Length, Format);
    }

    public void setRegisterValue(String registerName, long registerValue){
        this.Machine.setRegisterValue(registerName, registerValue);
    }

    public void setMemoryValue(int memoryAddress, long registerValue){
        this.Machine.setMemoryValue(memoryAddress, registerValue);
    }

    public void setStatusValue(String statusName, long registerName){
        this.Machine.setStatusValue(statusName, registerName);
    }

    public long getRegisterValue(String regName){
        return this.Machine.getRegisterValue(regName);
    }

    public long getRegisterValue(int RegNumber){
        return this.Machine.getRegisterValue(RegNumber);
    }

    public long getMemoryValue(int memoryAddress){
        return this.Machine.getMemoryValue(memoryAddress);
    }

    public long getStatusValue(String statusName){
        return this.Machine.getStatusValue(statusName);
    }

    @Override
    public void setRegisterValue(int regNumber, long regValue){
        this.Machine.setRegisterValue(regNumber, regValue);
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

    public void gatherMetaDataFromVerilogFile(String verilogFile, GuiRegister.Format format){
        ErrorLog errLog = new ErrorLog(new Destination(new StringWriter()));
        Lexer lexer = new Lexer(new Source(new FileStream(verilogFile)), errLog);
        Parser parser = new Parser(lexer, errLog);
        VerilogFile file = parser.parse();
        for(ModuleDeclaration decl : file.moduleDeclarationList){
            MetaDataGatherer gatherer = new MetaDataGatherer(this, new StringWriter(), format);
            gatherer.visit(decl);
        }
    }
}
