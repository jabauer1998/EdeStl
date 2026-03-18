package ede.stl.gui;

import ede.stl.gui.GuiEde;
import javax.swing.*;
import javax.swing.text.*;

import ede.stl.common.EdeCallable;

public class GuiJavaJob extends GuiJob{
    private EdeCallable functionToRun;
    private GuiEde edeInstance;
    private GuiJob nextJob;

    public GuiJavaJob(String buttonText, TextAreaType type, TextAreaNumbered numbered, double width, double height, GuiEde edeInstance, EdeCallable functionToRun){
        super(buttonText, type, numbered, width, height);
        this.functionToRun = functionToRun;
        this.edeInstance = edeInstance;
    }

    public GuiJavaJob(String buttonText, TextAreaType type, TextAreaNumbered numbered, double width, double height, GuiEde edeInstance, String[] keywords, EdeCallable functionToRun){
        super(buttonText, type, numbered, width, height, keywords);
        this.functionToRun = functionToRun;
        this.edeInstance = edeInstance;
    }

    public void setNextJob(GuiJob next){
        this.nextJob = next;
    }

    @Override
    public void RunJob(){
        try {
            String input = this.getText();
            String output = functionToRun.call(input);
            if(nextJob != null && output != null){
                nextJob.setText(output);
            }
        } catch (Exception e) {
            edeInstance.appendIoText("StandardError", e.toString());
        }
    }
}
