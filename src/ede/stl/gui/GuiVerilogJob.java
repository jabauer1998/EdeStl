package ede.stl.gui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.Callable;
import ede.stl.common.ErrorLog;
import ede.stl.common.Destination;
import ede.stl.compiler.CompiledEnvironment;

import ede.stl.gui.GuiEde;
import ede.stl.interpreter.EdeInterpreter;
import javax.swing.*;
import javax.swing.text.*;

public class GuiVerilogJob extends GuiJob {
    private GuiEde edeInstance;
    private String errorPane;
    private String verilogFile;
    private String inputFile;
    private String outputPane;
    private String inputPane;
    private boolean isInterpreted;

    public GuiVerilogJob(String JobName, TextAreaNumbered numbered, double Width, double Height, String verilogFile, String inputFile, String inputPane, String outputPane, String errorPane, GuiEde Ede, boolean isInterpreted){
        super(JobName, TextAreaType.DEFAULT, numbered, Width, Height);
        this.edeInstance = Ede;
        this.errorPane = errorPane;
        this.verilogFile = verilogFile;
        this.inputFile = inputFile;
        this.outputPane = outputPane;
        this.inputPane = inputPane;
        this.isInterpreted = isInterpreted;
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
            try {
                interpreter.interpretFile(verilogFile);
            } catch(Exception exp){
                edeInstance.appendIoText(errorPane, exp.toString());
            }
            errLog.printLog();
            edeInstance.appendIoText(errorPane, writer.toString());
        } else {
            CompiledEnvironment env = new CompiledEnvironment(edeInstance);

            try {
                URL[] urls = { new File("ede/instance").toURI().toURL() };
                URLClassLoader classLoader = new URLClassLoader(urls, getClass().getClassLoader());
                Class<?> targetClass = classLoader.loadClass("Processes");

                for (Method method : targetClass.getDeclaredMethods()) {
                    int modifiers = method.getModifiers();
                    Class<?>[] params = method.getParameterTypes();
                    boolean isProcessMethod = Modifier.isPublic(modifiers)
                            && Modifier.isStatic(modifiers)
                            && params.length == 1
                            && params[0].equals(CompiledEnvironment.class);
                    if (isProcessMethod) {
                        final Method m = method;
                        env.addThread(new Callable<Void>(){
                            public Void call() throws Exception {
                                m.invoke(null, env);
                                return null;
                            }
                        });
                    }
                }

                env.runThreads();
            } catch (ClassNotFoundException e) {
                edeInstance.appendIoText(errorPane, "Compiled class not found: " + e.getMessage());
            } catch (Exception e) {
                edeInstance.appendIoText(errorPane, e.toString());
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
