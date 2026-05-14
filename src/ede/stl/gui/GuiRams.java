package ede.stl.gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import javax.swing.*;
import java.awt.*;

public class GuiRams extends JPanel{
    private HashMap<String, GuiRam> rams;

    private JScrollPane pane;
    private JSplitPane mainSplit;
    private AddressFormat addrs;
    private MemoryFormat mems;
    private int bytesPerRow;
    private int width;
    private int height;
    

    public enum AddressFormat{
        BINARY,
        HEXIDECIMAL,
        OCTAL,
        DECIMAL
    }

    public enum MemoryFormat{
        BINARY,
        HEXADECIMAL,
    }

    public GuiRams(int bytesPerRow, AddressFormat addrFormat, MemoryFormat memFormat, double width, double height){
	this.bytesPerRow = bytesPerRow;
	this.addrs = addrFormat;
	this.mems = memFormat;
	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	
	this.pane = new JScrollPane(this);
	this.pane.setPreferredSize(new Dimension((int)width, (int)height));
	this.pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

	this.width = (int)width;
	this.height = (int)height;
	this.rams = new HashMap<String, GuiRam>();
    }

    public JScrollPane getScrollPane(){
	return this.pane;
    }

    public void addMemory(String name, int numBytes){
	if(!rams.containsKey(name)){
	    GuiRam ram = new GuiRam(name, numBytes, bytesPerRow, addrs, mems, width, height);
	    rams.put(name, ram);
	    this.add(ram);
	    revalidate();
            repaint();
            if (getParent() != null) {
               getParent().revalidate();
               getParent().repaint();
            }
	}
    }

    public void setMemoryValue(String name, int address, long dataValue){
	if(rams.containsKey(name)){
	    GuiRam ram = rams.get(name);
	    ram.setMemoryValue(address, dataValue);
	}
    }

    public long getMemoryValue(String name, int address){
	if(rams.containsKey(name)){
	    GuiRam ram = rams.get(name);
	    return ram.getMemoryValue(address);
	}
	throw new RuntimeException("Error cant find memory value in  "+ name + " at address " + address);
    }

    public void clearMemory(String name){
	if(rams.containsKey(name)){
	    GuiRam ram = rams.get(name);
	    ram.clearMemory();
	}
    }

    public void clearMemory(){
	for(String key: rams.keySet()){
	    GuiRam ram = rams.get(key);
	    ram.clearMemory();
	}
    }
}
