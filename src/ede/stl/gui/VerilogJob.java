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
        edeInstance.clearRegisters();
        edeInstance.clearMemory();
        edeInstance.clearStatusValues();
        edeInstance.writeIoText(outputPane, "");
        edeInstance.writeIoText(errorPane, "");
        CopyDataToInputFile();
        StringWriter writer = new StringWriter();
        Destination Dest = new Destination(writer);
        ErrorLog errLog = new ErrorLog(Dest);
        EdeInterpreter interpreter = new EdeInterpreter(errLog, edeInstance, outputPane, inputPane);
        interpreter.interpretFile(verilogFile);
        errLog.printLog();
        edeInstance.appendIoText(errorPane, writer.toString());
    }

    private void CopyDataToInputFile(){
        File file = new File(inputFile);
        if(file.exists()){
            file.delete();
        }

        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(this.getText());
            writer.close();
        } catch (IOException e) {
            edeInstance.appendIoText(errorPane, e.toString());
        }
    }
}
