package ede.stl.gui;

import java.io.FileReader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import ede.stl.ast.ModuleDeclaration;
import ede.stl.ast.VerilogFile;
import ede.stl.common.Destination;
import ede.stl.common.ErrorLog;
import ede.stl.common.Source;
import ede.stl.gui.GuiJobs;
import ede.stl.gui.GuiJob.TextAreaType;
import ede.stl.gui.GuiJob.TextAreaNumbered;
import ede.stl.gui.GuiMachine;
import ede.stl.common.EdeCallable;
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

public class GuiEde extends JPanel {
    private GuiJobs Jobs;
    private GuiMachine Machine;
    private final Object stepLock = new Object();
    private boolean canStep;

    public GuiEde(double Width, double Height, int NumberOfBytesInRow, GuiRam.AddressFormat AddrFormat, GuiRam.MemoryFormat MemFormat){
        this.setLayout(new BorderLayout());

        int toolBarHeight = (int)(Height/12);
        this.canStep = false;

        JPanel toolBar = new JPanel();
        toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));
        toolBar.setPreferredSize(new Dimension((int)Width, toolBarHeight));
        toolBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, toolBarHeight));
        toolBar.setMinimumSize(new Dimension((int)Width, toolBarHeight));

        JButton clearMemory = new JButton("Clear Memory");
        clearMemory.setPreferredSize(new Dimension((int)(Width/4), toolBarHeight));
        clearMemory.setMaximumSize(new Dimension((int)(Width/4), toolBarHeight));
        clearMemory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event){
                Machine.clearMemory();
            }
        });
        
        JButton clearRegisters = new JButton("Clear Registers");
        clearRegisters.setPreferredSize(new Dimension((int)(Width/4), toolBarHeight));
        clearRegisters.setMaximumSize(new Dimension((int)(Width/4), toolBarHeight));
        clearRegisters.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event){
                Machine.clearRegisters();
            }
        });

        JButton clearStatus = new JButton("Clear Status");
        clearStatus.setPreferredSize(new Dimension((int)(Width/4), toolBarHeight));
        clearStatus.setMaximumSize(new Dimension((int)(Width/4), toolBarHeight));
        clearStatus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event){
                Machine.clearStatusValues();
            }
        });

        JButton takeStep = new JButton("Take Step");
        takeStep.setPreferredSize(new Dimension((int)(Width/4), toolBarHeight));
        takeStep.setMaximumSize(new Dimension((int)(Width/4), toolBarHeight));
        takeStep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event){
                synchronized(stepLock) {
                    canStep = true;
                    stepLock.notifyAll();
                }
            }
        });

        toolBar.add(clearRegisters);
        toolBar.add(clearMemory);
        toolBar.add(clearStatus);
        toolBar.add(takeStep);

        double jobsWidth = Width / 3;
        double mainHeight = Height - toolBarHeight;

        this.Jobs = new GuiJobs(jobsWidth, mainHeight);
        this.Machine = new GuiMachine(NumberOfBytesInRow, AddrFormat, MemFormat, Width - jobsWidth, mainHeight);
        
        JScrollPane jobsPane = this.Jobs.getJobsPane();
        jobsPane.setPreferredSize(new Dimension((int)jobsWidth, (int)mainHeight));
        jobsPane.setMinimumSize(new Dimension(100, 0));

        this.Machine.setMinimumSize(new Dimension(200, 0));

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jobsPane, this.Machine);
        mainSplit.setDividerLocation((int)jobsWidth);
        mainSplit.setResizeWeight(0.33);
        mainSplit.setContinuousLayout(true);
        
        this.add(toolBar, BorderLayout.NORTH);
        this.add(mainSplit, BorderLayout.CENTER);
    }
    
    public void setUpMemory(int numBytes) {
        this.Machine.setUpMemory(numBytes);
    }

    public void AddVerilogJob(String jobName, String mainModule, String verilogFile, String inputFile, String inputPane, String outputPane, String errorPane, boolean isInterpreted){
        this.Jobs.AddVerilogJob(jobName, mainModule, TextAreaNumbered.IS_NOT_NUMBERED, verilogFile, inputFile, inputPane, outputPane, errorPane, this, isInterpreted);
    }

    public void AddVerilogJob(String jobName, String mainModule, TextAreaNumbered numbered, String verilogFile, String inputFile, String inputPane, String outputPane, String errorPane, boolean isInterpreted){
        this.Jobs.AddVerilogJob(jobName, mainModule, numbered, verilogFile, inputFile, inputPane, outputPane, errorPane, this, isInterpreted);
    }

    public void AddExeJob(String jobName, TextAreaType type, String execString, String... keywords){
        this.Jobs.AddExeJob(jobName, type, TextAreaNumbered.IS_NOT_NUMBERED, execString, this, keywords);
    }

    public void AddExeJob(String jobName, TextAreaType type, TextAreaNumbered numbered, String execString, String... keywords){
        this.Jobs.AddExeJob(jobName, type, numbered, execString, this, keywords);
    }

    public void AddJavaJob(String jobName, TextAreaType type, EdeCallable functionToRun, String... keywords){
        this.Jobs.AddJavaJob(jobName, type, TextAreaNumbered.IS_NOT_NUMBERED, functionToRun, this, keywords);
    }

    public void AddJavaJob(String jobName, TextAreaType type, TextAreaNumbered numbered, EdeCallable functionToRun, String... keywords){
        this.Jobs.AddJavaJob(jobName, type, numbered, functionToRun, this, keywords);
    }

    public void AddFlag(String Name){
        this.Machine.AddGuiFlag(Name);
    }

    public void AddIoSection(String TabTitle, String PaneTitle, GuiIO.Editable editable){
        this.Machine.AddIoSection(TabTitle, PaneTitle, editable);
    }
    
    public void AddRegister(String title, Value start, Value end, GuiRegister.Format Format) {
        int len = Math.abs(start.intValue() - end.intValue());
        this.Machine.AddGuiRegister(title, len, Format);
    }

    public void AddRegister(String Title, int Length, GuiRegister.Format Format){
        this.Machine.AddGuiRegister(Title, Length, Format);
    }

    public void setRegisterValue(String registerName, long registerValue){
        SwingUtilities.invokeLater(() -> this.Machine.setRegisterValue(registerName, registerValue));
    }

    public void setMemoryValue(int memoryAddress, long registerValue){
        SwingUtilities.invokeLater(() -> this.Machine.setMemoryValue(memoryAddress, registerValue));
    }

    public void setStatusValue(String statusName, long registerName){
        SwingUtilities.invokeLater(() -> this.Machine.setStatusValue(statusName, registerName));
    }

    public long getRegisterValue(String regName){
        if(SwingUtilities.isEventDispatchThread()){
            return this.Machine.getRegisterValue(regName);
        }
        final long[] result = new long[1];
        try {
            SwingUtilities.invokeAndWait(() -> result[0] = this.Machine.getRegisterValue(regName));
        } catch(Exception e) { throw new RuntimeException(e); }
        return result[0];
    }

    public long getRegisterValue(int RegNumber){
        if(SwingUtilities.isEventDispatchThread()){
            return this.Machine.getRegisterValue(RegNumber);
        }
        final long[] result = new long[1];
        try {
            SwingUtilities.invokeAndWait(() -> result[0] = this.Machine.getRegisterValue(RegNumber));
        } catch(Exception e) { throw new RuntimeException(e); }
        return result[0];
    }

    public long getMemoryValue(int memoryAddress){
        if(SwingUtilities.isEventDispatchThread()){
            return this.Machine.getMemoryValue(memoryAddress);
        }
        final long[] result = new long[1];
        try {
            SwingUtilities.invokeAndWait(() -> result[0] = this.Machine.getMemoryValue(memoryAddress));
        } catch(Exception e) { throw new RuntimeException(e); }
        return result[0];
    }

    public long getStatusValue(String statusName){
        if(SwingUtilities.isEventDispatchThread()){
            return this.Machine.getStatusValue(statusName);
        }
        final long[] result = new long[1];
        try {
            SwingUtilities.invokeAndWait(() -> result[0] = this.Machine.getStatusValue(statusName));
        } catch(Exception e) { throw new RuntimeException(e); }
        return result[0];
    }

    public void setRegisterValue(int regNumber, long regValue){
        SwingUtilities.invokeLater(() -> this.Machine.setRegisterValue(regNumber, regValue));
    }

    public void writeIoText(String textAreaName, String textToWrite){
        SwingUtilities.invokeLater(() -> this.Machine.writeIoText(textAreaName, textToWrite));
    }

    public void appendIoText(String textAreaName, String textToAppend){
        SwingUtilities.invokeLater(() -> this.Machine.appendIoText(textAreaName, textToAppend));
    }

    public String readIoText(String textAreaName){
        if(SwingUtilities.isEventDispatchThread()){
            return this.Machine.readIoText(textAreaName);
        }
        final String[] result = new String[1];
        try {
            SwingUtilities.invokeAndWait(() -> result[0] = this.Machine.readIoText(textAreaName));
        } catch(Exception e) { throw new RuntimeException(e); }
        return result[0];
    }

    public void clearRegisters(){
        SwingUtilities.invokeLater(() -> this.Machine.clearRegisters());
    }

    public void clearMemory(){
        SwingUtilities.invokeLater(() -> this.Machine.clearMemory());
    }

    public void clearStatusValues(){
        SwingUtilities.invokeLater(() -> this.Machine.clearStatusValues());
    }

    public void linkJobs(){
        this.Jobs.linkJobs();
    }

    public boolean isDebuggerEnabled(){
        if(SwingUtilities.isEventDispatchThread()){
            return this.Machine.isDebuggerEnabled();
        }
        final boolean[] result = new boolean[1];
        try {
            SwingUtilities.invokeAndWait(() -> result[0] = this.Machine.isDebuggerEnabled());
        } catch(Exception e) { throw new RuntimeException(e); }
        return result[0];
    }

    public void waitForStep(){
        synchronized(stepLock) {
            while(!canStep) {
                try {
                    stepLock.wait();
                } catch(InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            canStep = false;
        }
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
            for(ModuleDeclaration decl : file.modules){
                MetaDataGatherer gatherer = new MetaDataGatherer(this, new StringWriter(), format);
                gatherer.visit(decl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
