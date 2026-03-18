package ede.stl.compiler;

import ede.stl.common.Pointer;
import java.lang.Runnable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.Callable;

public class RunnableThread implements Runnable{
    private Callable<Void> toRun;
    private Pointer<Semaphore> sema;
    
    public RunnableThread(Callable<Void> funcToRun, Pointer<Semaphore> sema){
	this.toRun = funcToRun;
	this.sema = sema;
    }

    public void run(){
	try{
	    toRun.call();
	    sema.deRefrence().acquire();
	} catch(RuntimeException exp){
	    throw exp;
	} catch(Exception exp){
	    throw new RuntimeException(exp);
	}
    }
}
