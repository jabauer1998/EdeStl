package ede.stl.common;

@FunctionalInterface
public interface EdeCallable {
    String call(String input) throws Exception;
}
