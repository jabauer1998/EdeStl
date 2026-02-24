package ede.stl.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import ede.stl.gui.GuiEde;
import javax.swing.*;
import javax.swing.text.*;

public class ExeJob extends GuiJob {
    private String ExeString;
    private String InputType;
    private String OutputType;
    private String ErrorType;
    private GuiEde edeInstance;
    private GuiJob nextJob;

    public ExeJob(String ButtonText, TextAreaType type, double Width, double Height, String ExeString, String inputType, String outputType, String errorType, String[] keywords) { 
        super(ButtonText, type, Width, Height, keywords);
        this.ExeString = ExeString;
        this.InputFile = InputFile;
        this.OutputFile = OutputFile;
        this.ErrorFile = ErrorFile;
        this.guiJobs = guiJobs;
        this.edeInstance = edeInstance;
        this.errorTextAreaName = errorTextAreaName;
    }

    public void setNextJob(GuiJob job){
	this.nextJob = job;
    }

    public void RunJob(){
	String outStr = 
	if(outputType.equals("StandardOut") || outputType.equals("StdOut")){
		try {
		    Process proc = Runtime.getRuntime().exec(ExeString);
		    // Read the standard output
		    BufferedReader stdOutput = new BufferedReader(new InputStreamReader(process.getOutputStream()));
		} catch (IOException e) {
		    edeInstance.addIoText("StandardError", e.toString());
		} catch (InterruptedException e){
		    edeInstnace.addIoText("StandardError", e.toString());
		}
	} else {

	}
    }

    private void RunCommand(){
        
    }

    public void CreateFiles(){
        CreateInputFile();
    }

    private void CreateInputFile(){
        File File = new File(InputFile);
        if(File.exists()){
            File.delete();
        }
        CopyOverInputData();
    }

    private void CopyOverInputData(){
        File File = new File(InputFile);
        try {
            File.createNewFile();
            FileWriter Writer = new FileWriter(InputFile);
            JComponent inputSection = this.getInputSection();
            if(inputSection instanceof JTextPane){
                JTextPane ta = (JTextPane)inputSection;
                Writer.write(ta.getText());
            } else {
                JTextArea ta = (JTextArea)inputSection;
                Writer.write(ta.getText());
            }
            
            Writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void CopyOverOutputData(){
        int i;
        for(i = 0; i < guiJobs.size(); i++){
            if(this.getInputSection().hashCode() == guiJobs.get(i).hashCode()){
                break;
            }
        }

        JComponent OutputTextArea = guiJobs.get(i + 1);
        File OutputFilePtr = new File(OutputFile);

        if(OutputFilePtr.exists()){
            try {
                StringBuilder memText = new StringBuilder();
                FileReader outputReader = new FileReader(OutputFilePtr);
                while(true){
                    int outputCharFull = outputReader.read();
                    if(outputCharFull == -1)
                        break;
                    memText.append((char)outputCharFull);
                }

                if(OutputTextArea instanceof JTextPane){
                    JTextPane ta = (JTextPane)OutputTextArea;
                    ta.setText(memText.toString());
                } else if(OutputTextArea instanceof JTextArea){
                    JTextArea ta = (JTextArea)OutputTextArea;
                    ta.setText(memText.toString());
                }

                outputReader.close();
            } catch (FileNotFoundException e) {
                edeInstance.appendIoText(errorTextAreaName, e.toString());
            } catch (IOException e) {
                edeInstance.appendIoText(errorTextAreaName, e.toString());
            }
        }
    }

    private void CollectErrorData(){
        File errorFilePtr = new File(ErrorFile);
        if(errorFilePtr.exists()){
            try {
                FileReader fReader = new FileReader(errorFilePtr);
                StringBuilder memText = new StringBuilder();
                
                while(true){
                    int outputCharFull = fReader.read();
                     if(outputCharFull == -1){
                        break;
                     }
                     memText.append((char)outputCharFull);
                }

                edeInstance.appendIoText(errorTextAreaName, memText.toString());
            } catch (FileNotFoundException e) {
                edeInstance.appendIoText(errorTextAreaName, e.toString());
            } catch (IOException e) {
                edeInstance.appendIoText(errorTextAreaName, e.toString());
            }
        }
    }
}
