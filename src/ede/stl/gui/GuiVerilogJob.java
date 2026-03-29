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
import java.util.List;
import java.util.ArrayList;
import java.io.FilenameFilter;
import ede.stl.gui.GuiEde;
import ede.stl.interpreter.EdeInterpreter;
import java.io.File;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import javax.swing.*;
import javax.swing.text.*;
import java.util.HashSet;
import java.lang.reflect.Field;

public class GuiVerilogJob extends GuiJob {
    private GuiEde edeInstance;
    private String errorPane;
    private String verilogFile;
    private String inputFile;
    private String outputPane;
    private String inputPane;
    private String mainModule;
    private boolean isInterpreted;

    public GuiVerilogJob(String JobName, String mainModule, TextAreaNumbered numbered, double Width, double Height, String verilogFile, String inputFile, String inputPane, String outputPane, String errorPane, GuiEde Ede, boolean isInterpreted){
        super(JobName, TextAreaType.DEFAULT, numbered, Width, Height);
        this.edeInstance = Ede;
        this.errorPane = errorPane;
        this.verilogFile = verilogFile;
        this.inputFile = inputFile;
        this.outputPane = outputPane;
        this.inputPane = inputPane;
        this.mainModule = mainModule;
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
                edeInstance.appendIoText(errorPane, "\n");
                edeInstance.appendIoText(errorPane, exp.getStackTrace().toString());
            }
        } else {
            CompiledEnvironment env = new CompiledEnvironment(edeInstance);

            try {
                HashSet<String> visitedClasses = new HashSet<String>();
                HashSet<String> allVerilogModules = collectModules();
                Class<?> rootClass = Class.forName("ede.instance.mods." + mainModule);
                Object rootInstance = rootClass.getDeclaredConstructor(edeInstance.getClass()).newInstance(edeInstance);
                visitRootClass(rootInstance, visitedClasses, allVerilogModules, env);
                env.runThreads();
            } catch (ClassNotFoundException e) {
                edeInstance.appendIoText(errorPane, "Compiled class not found: " + e.getMessage());
            } catch (Exception e) {
                edeInstance.appendIoText(errorPane, e.toString());
            }
        }
    }

    private HashSet<String> collectModules(){
            HashSet<String> classNames = new HashSet<>();
            File directory = new File("ede/instance/mods");

            // Filter to accept only .class files
            FilenameFilter classFilter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".class");
                }
            };

            File[] classFiles = directory.listFiles(classFilter);

            if (classFiles != null) {
                for (File file : classFiles) {
                    String fileName = file.getName();
                    // Remove the ".class" extension
                    int lastDotIndex = fileName.lastIndexOf('.');
                    if (lastDotIndex != -1) {
                        classNames.add(fileName.substring(0, lastDotIndex));
                    }
                }
            }

            return classNames;// ... use loadedClasses for reflection or instantiation ...
    }

    private void visitRootClass(Object obj, HashSet<String> visitedClasses, HashSet<String> allVerilogModules, CompiledEnvironment env) throws ClassNotFoundException, Exception{
        visitClass(obj, visitedClasses, allVerilogModules, env);
    }

    private void visitClass(Object obj, HashSet<String> visitedClasses, HashSet<String> allVerilogModules, CompiledEnvironment env) throws Exception{
        Class<?> clazz = obj.getClass();
        String className = clazz.getName();
        if(visitedClasses.contains(className))
            return;
        else {
            visitedClasses.add(className);
            // Step 2: Obtain the Field object for the field named "privateField"
            // Use getDeclaredField for private fields
            for(Field field : clazz.getDeclaredFields()){
                field.setAccessible(true);
                if(allVerilogModules.contains(field.getType().getSimpleName()))
                    visitClass(field.get(obj), visitedClasses, allVerilogModules, env);
            }

            clazz.getMethod("loadProcesses", GuiEde.class, CompiledEnvironment.class).invoke(obj, edeInstance, env);
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
