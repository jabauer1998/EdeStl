package ede.stl.gui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import ede.stl.gui.GuiEde;
import javax.swing.*;
import javax.swing.text.*;

public class ExeJob extends GuiJob {
    private String ExeString;
    private GuiEde edeInstance;
    private GuiJob nextJob;

    public ExeJob(String ButtonText, TextAreaType type, double Width, double Height, String ExeString, GuiEde edeInstance, String... keywords) { 
        super(ButtonText, type, Width, Height, keywords);
        this.ExeString = ExeString;
        this.edeInstance = edeInstance;
    }

    public void setNextJob(GuiJob job){
        this.nextJob = job;
    }

    @Override
    public void RunJob(){
        try {
            Process proc = Runtime.getRuntime().exec(ExeString);

            String inputText = this.getText();
            if(inputText != null && !inputText.isEmpty()){
                OutputStream stdin = proc.getOutputStream();
                stdin.write(inputText.getBytes());
                stdin.flush();
                stdin.close();
            }

            BufferedReader stdOutput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

            StringBuilder outputBuilder = new StringBuilder();
            String line;
            while((line = stdOutput.readLine()) != null){
                outputBuilder.append(line);
                outputBuilder.append('\n');
            }

            StringBuilder errorBuilder = new StringBuilder();
            while((line = stdError.readLine()) != null){
                errorBuilder.append(line);
                errorBuilder.append('\n');
            }

            proc.waitFor();

            if(errorBuilder.length() > 0){
                edeInstance.appendIoText("StandardError", errorBuilder.toString());
            }

            String output = outputBuilder.toString();
            if(nextJob != null && !output.isEmpty()){
                nextJob.setText(output);
            }

        } catch (IOException e) {
            edeInstance.appendIoText("StandardError", e.toString());
        } catch (InterruptedException e){
            edeInstance.appendIoText("StandardError", e.toString());
        }
    }
}
