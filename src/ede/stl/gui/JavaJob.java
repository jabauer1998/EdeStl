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

public class JavaJob extends GuiJob{
    private EdeCallable<Void> functionToRun;
    private GuiEde edeInstance;
    private GuiJob nextJob;

    public JavaJob(String buttonText, TextAreaType type, double width, double height, GuiEde edeInstance, Callable<Void> functionToRun){
        super(buttonText, type, width, height);
        this.edeInstance = edeInstance;
    }

    public JavaJob(String buttonText, TextAreaType type, double width, double height, GuiEde edeInstance, String[] keywords, Callable<Void> functionToRun){
        super(buttonText, type, width, height, keywords);
        this.functionToRun = functionToRun;
        this.edeInstance = edeInstance;
    }

    public void setNextJob(GuiJob next){
	this.nextJob = next;
    }

    @Override
    public void RunJob(){
        try {
            functionToRun.call();
        } catch (Exception e) {
           edeInstance.appendIoText("StandardError", e.toString());
        }
    }
}
