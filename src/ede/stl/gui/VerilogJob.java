package ede.stl.gui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import ede.stl.common.ErrorLog;
import ede.stl.common.Destination;
import ede.stl.gui.GuiEde;
import ede.stl.interpreter.EdeInterpreter;
import javax.swing.*;
import javax.swing.text.*;

public class VerilogJob extends GuiJob {
    private GuiEde edeInstance;
    private String errorPane;
    private String verilogFile;
    private String inputFile;
    private String outputPane;
    private String inputPane;

    public VerilogJob(String JobName, double Width, double Height, String verilogFile, String inputFile, String inputPane, String outputPane, String errorPane, GuiEde Ede){
        super(JobName, TextAreaType.DEFAULT, Width, Height);
        this.edeInstance = Ede;
        this.errorPane = errorPane;
        this.verilogFile = verilogFile;
        this.inputFile = inputFile;
        this.outputPane = outputPane;
        this.inputPane = inputPane;
    }

    @Override
    public void RunJob(){
        CopyDataToOutputFile();
        StringWriter writer = new StringWriter();
        Destination Dest = new Destination(writer);
        ErrorLog errLog = new ErrorLog(Dest);
        EdeInterpreter interpreter = new EdeInterpreter(errLog, edeInstance, outputPane, inputPane);
        interpreter.interpretFile(verilogFile);
        errLog.printLog();
        edeInstance.appendIoText(errorPane, writer.toString());
    }

    public void CopyDataToOutputFile(){
        File File = new File(inputFile);
        if(File.exists()){
            File.delete();
        }

        try {
            File.createNewFile();
            FileWriter Writer = new FileWriter(File);
            JComponent tr = this.getInputSection();
            
            if(tr instanceof JTextPane){
                JTextPane ta = (JTextPane)tr;
                Writer.write(ta.getText());
            } else {
                JTextArea ta = (JTextArea)tr;
                Writer.write(ta.getText());
            }
            
            Writer.close();
        } catch (IOException e) {
            edeInstance.appendIoText(errorPane, e.toString());
        }
    }
}
