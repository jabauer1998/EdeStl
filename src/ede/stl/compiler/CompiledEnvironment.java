package ede.stl.compiler;

import ede.stl.gui.GuiEde;
import java.util.LinkedList;
import java.util.ArrayList;
import ede.stl.common.Pointer;
import java.lang.Runnable;
import java.lang.Thread;
import java.util.concurrent.Semaphore;
import java.util.concurrent.Callable;
import ede.stl.common.FormattedScanner;
import java.io.FileWriter;
import java.io.FileReader;
import ede.stl.common.Environment;

public class CompiledEnvironment extends Environment{
    private GuiEde edeInstance;
    private LinkedList<RunnableThread> threads;
    private int tickets;
    private Pointer<Semaphore> sema;
	
    public CompiledEnvironment(GuiEde edeInstance){
        super();
	   this.edeInstance = edeInstance;
	   this.tickets = 0;
    }

    public void addThread(Callable<Void> thread){
        this.tickets++;
	   this.threads.add(new RunnableThread(thread, sema));
    }

    public void runThreads(){
    	sema.assign(new Semaphore(-tickets + 1));
    	for(RunnableThread thread: threads){
    	    Thread realThread = new Thread(thread);
    	    realThread.start();
    	}
    	try{
    	    sema.deRefrence().acquire();
    	} catch(InterruptedException exp){
    	    addErrorText("Interupt occured when trying to get semaphore after running all the processes");
    	}
    }

    public void addErrorText(String errorText){
	edeInstance.appendIoText("StandardError", errorText);
    }

    public void addOutputText(String ioText){
	edeInstance.appendIoText("StandardOutput", ioText);
    }
}
