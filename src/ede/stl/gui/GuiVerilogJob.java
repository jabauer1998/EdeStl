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
import java.util.concurrent.Callable;

public class GuiVerilogJob extends GuiJob {
    private GuiEde edeInstance;
    private String errorPane;
    private String verilogFile;
    private String inputFile;
    private String outputPane;
    private String inputPane;
    private boolean isInterpreted;
    private Callable<Void> toRun;

    public GuiVerilogJob(String JobName, TextAreaNumbered numbered, double Width, double Height, String verilogFile, String inputFile, String inputPane, String outputPane, String errorPane, GuiEde Ede){
        super(JobName, TextAreaType.DEFAULT, numbered, Width, Height);
        this.edeInstance = Ede;
        this.errorPane = errorPane;
        this.verilogFile = verilogFile;
        this.inputFile = inputFile;
        this.outputPane = outputPane;
        this.inputPane = inputPane;
	this.isInterpreted = true;
    }

    public GuiVerilogJob(String JobName, TextAreaNumbered numbered, double width, double height, String inputFile, String inputPane, String outputPane, String errorPane, GuiEde ede, Callable<Void> edeCallable){
	super(JobName, TextAreaType.DEFAULT, numbered, width, height);
	this.verilogFile = null; //Only used for interpreted version
	this.edeInstance = ede; //Should be set to be used in CopyDataToInputFile
	this.inputPane = inputPane; //Propably not used
	this.outputPane = outputPane; //Probably not utilized
	this.errorPane = errorPane;
	this.isInterpreted = false;
	this.toRun = edeCallable;
    }

    @Override
    public void RunJob(){
	edeInstance.clearRegisters();
        edeInstance.clearMemory();
        edeInstance.clearStatusValues();
	edeInstance.writeIoText(outputPane, "");
        edeInstance.writeIoText(errorPane, "");
	CopyDataToInputFile();
        if(isInterpreted){
	    StringWriter writer = new StringWriter();
	    Destination Dest = new Destination(writer);
	    ErrorLog errLog = new ErrorLog(Dest);
	    EdeInterpreter interpreter = new EdeInterpreter(errLog, edeInstance, outputPane, inputPane);
	    interpreter.interpretFile(verilogFile);
	    errLog.printLog();
	    edeInstance.appendIoText(errorPane, writer.toString());
	} else {
	    try {
		this.toRun.call(); //Ignore arg 
	    } catch(Exception exp){
		edeInstance.appendIoText(errorPane, exp.toString());
	    }
	}
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
