package ede.stl.gui;

public interface Machine extends Memory, RegFile, Flags {
    public void appendIoText(String Name, String ioText);

    public void writeIoText(String Name, String toWrite);

    public String readIoText(String Name);
}


























































