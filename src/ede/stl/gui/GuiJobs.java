package ede.stl.gui;

import java.util.LinkedList;
import java.util.List;
import ede.stl.gui.GuiEde;
import ede.stl.gui.GuiJob.TextAreaType;
import ede.stl.gui.GuiJob.TextAreaNumbered;
import ede.stl.common.EdeCallable;
import javax.swing.*;
import java.awt.*;

public class GuiJobs extends JPanel {
    private JScrollPane JobsPane;

    private double JobHeight;
    private double JobWidth;

    private List<GuiJob> Jobs;
    
    public GuiJobs(double Width, double Height){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JobsPane = new JScrollPane(this);

        this.JobHeight = Height / 3;
        this.JobWidth = Width;

        Jobs = new LinkedList<>();
    }

    public void AddExeJob(String JobName, TextAreaType type, TextAreaNumbered numbered, String ExecString, GuiEde edeInstance, String... keywords){
        GuiExeJob Job = new GuiExeJob(JobName, type, numbered, JobWidth, JobHeight, ExecString, edeInstance, keywords);
        this.add(Job);
        Jobs.add(Job);
    }

    public void AddJavaJob(String JobName, TextAreaType type, TextAreaNumbered numbered, EdeCallable functionToRun, GuiEde edeInstance, String... keywords){
        GuiJavaJob Job;
        if(keywords.length > 0){
            Job = new GuiJavaJob(JobName, type, numbered, JobWidth, JobHeight, edeInstance, keywords, functionToRun);
        } else {
            Job = new GuiJavaJob(JobName, type, numbered, JobWidth, JobHeight, edeInstance, functionToRun);
        }
        this.add(Job);
        Jobs.add(Job);
    }

    public void AddVerilogJob(String JobName, TextAreaNumbered numbered, String verilogFile, String inputFile, String inputPane, String outputPane, String errorPane, GuiEde edeInstance){
        GuiVerilogJob Job = new GuiVerilogJob(JobName, numbered, JobWidth, JobHeight, verilogFile, inputFile, inputPane, outputPane, errorPane, edeInstance);
        this.add(Job);
        Jobs.add(Job);
    }

    public void linkJobs(){
        for(int i = 0; i < Jobs.size() - 1; i++){
            GuiJob current = Jobs.get(i);
            GuiJob next = Jobs.get(i + 1);
            if(current instanceof GuiJavaJob){
                ((GuiJavaJob)current).setNextJob(next);
            } else if(current instanceof GuiExeJob){
                ((GuiExeJob)current).setNextJob(next);
            }
        }
    }

    public JScrollPane getJobsPane(){
        return JobsPane;
    }
}
