package ede.stl.compiler;

public class CompiledEnvironment{
    private GuiEde edeInstance;
    private LinkedList<RunnableThread> threads;
    private int tickets;
    private Pointer<Semaphore> sema;

    private ArrayList<FormattedScanner> readOnlyDescriptorArray;
    private ArrayList<FileWriter> writerFileDescriptorArray;
	
    public CompiledEnvironment(GuiEde edeInstance){
	this.edeInstance = edeInstance;
	this.tickets = 0;
    }

    public void addThread(Callable<Void> thread){
        this.tickets++;
	this.threads.add(new RunnableThread(thread, sema));
    }

    public void runThreads(){
	sema.setValue(new Semaphore(-tickets + 1));
	for(RunnableThread thread: threads){
	    Thread realThread = new Thread(thread);
	    realThread.start();
	}
	try{
	    sema.getValue().acquire();
	} catch(InteruptedException exp){
	    addErrorText("Interupt occured when trying to get semaphore after running all the processes");
	}
    }

    public int createReadOnlyFileDescriptor(String fileName){
	try{
            FileReader reader = new FileReader(fileName);
            FormattedScanner scanner = new FormattedScanner(reader); 
            for(int i = 0; i < readOnlyFileDescriptorArray.size(); i++){
                if(readOnlyFileDescriptorArray.get(i) == null){
                    readOnlyFileDescriptorArray.set(i, scanner);
                    return i;
                }
            }

            readOnlyFileDescriptorArray.add(scanner);
            return readOnlyFileDescriptorArray.size() - 1;
        } catch(Exception exp) {
            return -1;
        }
    }

    public int createWritableFileDescriptor(String fileName){
	try{
            FileWriter writer = new FileWriter(fileName);
            for(int i = 0; i < writableFileDescriptorArray.size(); i++){
                if(writableFileDescriptorArray.get(i) == null){
                    writableFileDescriptorArray.set(i, writer);
                    return i;
                }
            }

            writableFileDescriptorArray.add(writer);
            return writableFileDescriptorArray.size() - 1;
        } catch(Exception exp) {
            return -1;
        }
    }

    public FormattedScanner getFileReader(int fileDescriptor){
	return readOnlyFileDescriptorArray.get(fileDescriptor);
    }

    public FileWriter getFileWriter(int fileDescriptor){
	return writeableFileDescriptorArray.get(fileDescriptor);
    }

    public void clearFileReader(int fileDescriptor){
	readOnltFileDescriptorArray.set(fileDesctiptor, null);
    }

    public void clearFileWriter(int fileDescriptor){
	writableFileDescriptorArray.set(fileDescriptor, null);
    }

    public void addErrorText(String errorText){
	edeInstance.addIoText("StandardError", errorText);
    }

    public void addOutputText(String ioText){
	edeInstance.addIoText("StandardOutput", ioText);
    }
}
