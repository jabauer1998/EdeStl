package ede.stl.gui;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import ede.stl.gui.GuiEde;
import ede.stl.gui.GuiJob.TextAreaType;
import javax.swing.*;
import java.awt.*;

public class GuiJobs extends JPanel {
    private JScrollPane JobsPane;

    private double JobHeight;
    private double JobWidth;

    private List<JComponent> Jobs;
    
    public GuiJobs(double Width, double Height){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JobsPane = new JScrollPane(this);

        this.JobHeight = Height / 3;
        this.JobWidth = Width;

        Jobs = new LinkedList<>();
    }

    public void AddExeJob(String JobName, TextAreaType type, String ExecString, String InputFile, String OutputFile, String ErrorFile, String errorTextBox, GuiEde edeInstance, String... keywords){
        ExeJob Job = new ExeJob(JobName, type, JobWidth, JobHeight, ExecString, InputFile, OutputFile, ErrorFile, errorTextBox, Jobs, edeInstance, keywords);
        this.add(Job);
        Jobs.add(Job.getInputSection());
    }

    public void AddJavaJob(String JobName, TextAreaType type, Callable<Void> functionToRun, String InputFile, String OutputFile, String errorPane, GuiEde edeInstance, String... keywords){
        JavaJob Job = new JavaJob(JobName, type, JobWidth, JobHeight, functionToRun, InputFile, OutputFile, errorPane, keywords, Jobs, edeInstance);
        this.add(Job);
        Jobs.add(Job.getInputSection());
    }

    public void AddVerilogJob(String JobName, String verilogFile, String inputFile, String inputPane, String outputPane, String errorPane, GuiEde edeInstance){
        VerilogJob Job = new VerilogJob(JobName, JobWidth, JobHeight, verilogFile, inputFile, inputPane, outputPane, errorPane, edeInstance);
        this.add(Job);
        Jobs.add(Job.getInputSection());
    }

    public JScrollPane getJobsPane(){
        return JobsPane;
    }
}
