package ede.stl.compiler;

import ede.stl.gui.GuiEde;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

public class VerilogAsJavaBase {
    public VerilogAsJavaBase() {
    }
    
    private static boolean methodExists(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            clazz.getDeclaredMethod(methodName, parameterTypes);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void loadProcesses(GuiEde edeInstance, CompiledEnvironment env) {
        try {
            Class<?> currentClass = this.getClass();
            int processNumber = 0;
            while (methodExists(currentClass, "process" + processNumber, GuiEde.class, CompiledEnvironment.class)) {
                String methodName = "process" + processNumber;
                Method method = currentClass.getDeclaredMethod(methodName, GuiEde.class, CompiledEnvironment.class);
                env.addThread(new Callable<Void>() {
                    public Void call() throws Exception {
                        try {
                            method.invoke(VerilogAsJavaBase.this, edeInstance, env);
                        } catch (InvocationTargetException ite) {
                            Throwable cause = ite.getCause();
                            if (cause instanceof RuntimeException) {
                                throw (RuntimeException) cause;
                            } else if (cause instanceof Exception) {
                                throw (Exception) cause;
                            } else if (cause instanceof Error) {
                                throw (Error) cause;
                            } else {
                                throw ite;
                            }
                        }
                        return null;
                    }
                });
                processNumber++;
            }
        } catch (NoSuchMethodException e) {
            System.err.println("Method not found: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }
}
