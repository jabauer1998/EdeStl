package ede.stl.compiler;

public class RunnableThread extends Runnable{
    private Callable<Void> toRun;
    private Pointer<Semaphore> sema;
    
    public RunnableThread(Callable<Void> funcToRun, Pointer<Semaphore> sema){
	this.toRun = funcToRun;
	this.sema = sema;
    }

    public void run(){
	try{
	    toRun.call();
	} catch(RuntimeException exp){
	    sema.getValue().aquire();
	    throw exp;
	} catch(Exception exp){
	    sema.getValue().aquire();
	    throw new RuntimeException(exp);
	}
    }
}
