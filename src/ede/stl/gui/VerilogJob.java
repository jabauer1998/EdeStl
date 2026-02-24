package ede.stl.gui;

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
    private String outputPane;
    private String inputPane;

    public VerilogJob(String JobName, double Width, double Height, String verilogFile, String inputPane, String outputPane, String errorPane, GuiEde Ede){
        super(JobName, TextAreaType.DEFAULT, Width, Height);
        this.edeInstance = Ede;
        this.errorPane = errorPane;
        this.verilogFile = verilogFile;
        this.outputPane = outputPane;
        this.inputPane = inputPane;
    }

    @Override
    public void RunJob(){
        String paneText = this.getText();
        if(paneText != null && !paneText.isEmpty()){
            edeInstance.writeIoText(inputPane, paneText);
        }
        StringWriter writer = new StringWriter();
        Destination Dest = new Destination(writer);
        ErrorLog errLog = new ErrorLog(Dest);
        EdeInterpreter interpreter = new EdeInterpreter(errLog, edeInstance, outputPane, inputPane);
        interpreter.interpretFile(verilogFile);
        errLog.printLog();
        edeInstance.appendIoText(errorPane, writer.toString());
    }
}
