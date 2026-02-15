package ede.stl.gui;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.concurrent.Callable;
import ede.stl.gui.GuiEde;
import javax.swing.*;
import javax.swing.text.*;

public class JavaJob extends GuiJob {
    private Callable<Void> functionToRun;
    private GuiEde edeInstance;
    private String errorPane;
    private String inputFile;
    private String outputFile;
    private List<JComponent> guiJobs;

    public JavaJob(String buttonText, TextAreaType type, double width, double height, Callable<Void> functionToRun, String inputFile, String outputFile, String errorPane, String[] keywords, List<JComponent> guiJobs, GuiEde edeInstance){
        super(buttonText, type, width, height, keywords);
        this.functionToRun = functionToRun;
        this.edeInstance = edeInstance;
        this.inputFile = inputFile;
        this.errorPane = errorPane;
        this.outputFile = outputFile;
        this.guiJobs = guiJobs;
    }

    private void copyOverDataToInputFile(){
        JComponent reg = this.getInputSection();
        String textToCopy;

        if(reg instanceof JTextPane){
            JTextPane ta = (JTextPane)reg;
            textToCopy = ta.getText();
        } else {
            JTextArea ta = (JTextArea)reg;
            textToCopy = ta.getText();
        }

        File iFile = new File(inputFile);
        if(!iFile.exists()){
            iFile.delete();
        }

        try {
            iFile.createNewFile();
            FileWriter Writer = new FileWriter(iFile);
            Writer.write(textToCopy);
            Writer.flush();
            Writer.close();
        } catch(Exception exp){
            edeInstance.appendIoText(errorPane, exp.toString());
        }
    }

    private void collectDataFromOutputFile(){
        for(int i = 0; i < guiJobs.size(); i++){
            JComponent localArea = guiJobs.get(i);
            if(localArea.hashCode() == this.getInputSection().hashCode()){
                JComponent nextTextArea = guiJobs.get(i + 1);
                try{
                    FileReader reader = new FileReader(outputFile);
                    StringBuilder sb = new StringBuilder();
                    while(reader.ready()){
                        sb.append((char)reader.read());
                    }
                    reader.close();

                    if(nextTextArea instanceof JTextPane){
                        JTextPane ta = (JTextPane)nextTextArea;
                        ta.setText(sb.toString());
                    } else {
                        JTextArea ta = (JTextArea)nextTextArea;
                        ta.setText(sb.toString());
                    }
                } catch(Exception exp){
                    edeInstance.appendIoText(errorPane, exp.toString());
                }
            }
        }
    }

    @Override
    public void RunJob(){
        copyOverDataToInputFile();
        try {
            functionToRun.call();
        } catch (Exception e) {
           edeInstance.appendIoText(errorPane, e.toString());
        }
        collectDataFromOutputFile();
    }
}
