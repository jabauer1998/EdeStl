package ede.stl.gui;

import java.io.FileReader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import ede.stl.ast.ModuleDeclaration;
import ede.stl.ast.VerilogFile;
import ede.stl.common.Destination;
import ede.stl.common.ErrorLog;
import ede.stl.common.Source;
import ede.stl.gui.Machine;
import ede.stl.gui.GuiJobs;
import ede.stl.gui.GuiJob.TextAreaType;
import ede.stl.gui.GuiMachine;
import ede.stl.gui.GuiRam;
import ede.stl.gui.GuiRegister;
import ede.stl.parser.Lexer;
import ede.stl.parser.Parser;
import ede.stl.parser.Preprocessor;
import ede.stl.parser.Token;
import ede.stl.passes.MetaDataGatherer;
import ede.stl.values.Value;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GuiEde extends JPanel implements Machine {
    private GuiJobs Jobs;
    private GuiMachine Machine;

    public GuiEde(double Width, double Height, int NumberOfBytesInRow, GuiRam.AddressFormat AddrFormat, GuiRam.MemoryFormat MemFormat){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        int toolBarHeight = (int)(Height/12);

        JPanel toolBar = new JPanel();
        toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));
        toolBar.setPreferredSize(new Dimension((int)Width, toolBarHeight));
        toolBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, toolBarHeight));
        toolBar.setMinimumSize(new Dimension((int)Width, toolBarHeight));

        JButton clearMemory = new JButton("Clear Memory");
        clearMemory.setPreferredSize(new Dimension((int)(Width/3), toolBarHeight));
        clearMemory.setMaximumSize(new Dimension((int)(Width/3), toolBarHeight));
        clearMemory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event){
                Machine.clearMemory();
            }
        });
        
        JButton clearRegisters = new JButton("Clear Registers");
        clearRegisters.setPreferredSize(new Dimension((int)(Width/3), toolBarHeight));
        clearRegisters.setMaximumSize(new Dimension((int)(Width/3), toolBarHeight));
        clearRegisters.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event){
                Machine.clearRegisters();
            }
        });

        JButton clearStatus = new JButton("Clear Status");
        clearStatus.setPreferredSize(new Dimension((int)(Width/3), toolBarHeight));
        clearStatus.setMaximumSize(new Dimension((int)(Width/3), toolBarHeight));
        clearStatus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event){
                Machine.clearStatusValues();
            }
        });

        toolBar.add(clearRegisters);
        toolBar.add(clearMemory);
        toolBar.add(clearStatus);

        double jobsWidth = Width / 3;
        double mainHeight = Height - toolBarHeight;

        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.X_AXIS));
        this.Jobs = new GuiJobs(jobsWidth, mainHeight);
        this.Machine = new GuiMachine(NumberOfBytesInRow, AddrFormat, MemFormat, Width - jobsWidth, mainHeight);
        
        JScrollPane jobsPane = this.Jobs.getJobsPane();
        jobsPane.setPreferredSize(new Dimension((int)jobsWidth, (int)mainHeight));
        jobsPane.setMaximumSize(new Dimension((int)jobsWidth, Integer.MAX_VALUE));
        jobsPane.setMinimumSize(new Dimension((int)jobsWidth, (int)mainHeight));

        mainPane.add(jobsPane);
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
        try {
            java.io.PrintWriter errWriter = new java.io.PrintWriter(System.err, true);
            ErrorLog errLog = new ErrorLog(new Destination(errWriter));
            Lexer lexer = new Lexer(new Source(new FileReader(verilogFile)), errLog);
            LinkedList<Token> tokens = lexer.tokenize();
            Preprocessor preProc = new Preprocessor(errLog, tokens);
            List<Token> processed = preProc.executePass();
            List<Token> filtered = Lexer.filterWhiteSpace(processed);
            Parser parser = new Parser(filtered, errLog);
            VerilogFile file = parser.parseVerilogFile();
            System.err.println("[GuiEde] Parsed " + file.modules.size() + " modules");
            for(ModuleDeclaration decl : file.modules){
                System.err.println("[GuiEde] Processing module with " + decl.moduleItemList.size() + " items");
                MetaDataGatherer gatherer = new MetaDataGatherer(this, new StringWriter(), format);
                gatherer.visit(decl);
            }
            System.err.println("[GuiEde] MetaData gathering complete");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
