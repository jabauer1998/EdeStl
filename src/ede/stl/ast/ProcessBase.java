package ede.stl.ast;

import java.util.concurrent.Semaphore;
import ede.stl.common.Position;
import ede.stl.common.ErrorLog;
import ede.stl.common.ErrorItem;
import ede.stl.common.Utils;
import ede.stl.interpreter.Interpreter;
import ede.stl.ast.AstNode;
import ede.stl.ast.ModuleItem;
import ede.stl.ast.Statement;
import ede.stl.passes.ModuleVisitor;

public abstract class ProcessBase extends AstNode implements ModuleItem, Runnable{
    public final Statement statement;

    private Interpreter interpreter;
    private Semaphore semaphore;
    private ErrorLog errLog;
    
    
    protected ProcessBase(Position start, Statement statement){
        super(start);
        this.statement = statement;
    }

    public void initEnvironment(Interpreter interpreter, ErrorLog errLog, Semaphore semaphore){
        this.interpreter = interpreter;
        this.semaphore = semaphore;
        this.errLog = errLog;
    }

    public void run(){
      if (statement == null) {
        System.out.println("Error need to set a semaphore for AllwaysStatements");
        System.exit(1);
      } else {
        try {
          executeProcess(interpreter, semaphore);
        } catch (Exception e) {
          errLog.addItem(new ErrorItem("Exception " + e.toString() + "\n with \n" + e.getMessage() + "\n"));
          this.semaphore.release();
        }
      }
    }

    public abstract void executeProcess(Interpreter Interpreter, Semaphore semaphore) throws Exception;
    
    @Override
    public abstract <ModVisitType> ModVisitType accept(ModuleVisitor<ModVisitType> modVisitor, Object... argv);
}


























































