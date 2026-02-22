package sample.ede;

import javax.swing.*;
import java.awt.*;
import ede.stl.gui.GuiEde;
import ede.stl.gui.JavaJob;
import ede.stl.gui.VerilogJob;
import ede.stl.gui.GuiRam;
import declan.backend.assembler.ArmAssemblerParser;
import declan.backend.assembler.ArmAssemblerLexer;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.List;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;


public class Processor {
    public static void main(String[] args){
	javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                buildDefaultMachine();
            }
        });
    }
    
    private static void buildDefaultMachine(){
		Toolkit toolkit = Toolkit.getDefaultToolkit();
                Dimension screenSize = toolkit.getScreenSize();
        
                // Extract the width and height
                int screenWidth = screenSize.width;
                int screenHeight = screenSize.height;
	
	        JFrame frame = new JFrame("Emulator Development Environment");

		int numBytesInRow = 1;
		GuiEde EdeInstance = new GuiEde(screenWidth, screenHeight, numBytesInRow, GuiRam.AddressFormat.DECIMAL, GuiRam.MemoryFormat.HEXADECIMAL);

		EdeInstance.gatherMetaDataFromVerilogFile("./processor/ARM7TDMIS.v", GuiRegister.Format.BINARY);

		EdeInstance.AddJavaJob("Assemble", TextAreaType.DEFAULT, new Callable<Void>() {
			public Void call(){
				try{
					FileReader Reader = new FileReader("InputAssembly.a");
					ANTLRInputStream byteStream = new ANTLRInputStream(Reader);
					ArmAssemblerLexer lex = new ArmAssemblerLexer(byteStream);
					CommonTokenStream tokStream = new CommonTokenStream(lex);
					ArmAssemblerParser parse = new ArmAssemblerParser(tokStream);
					ProgramContext ctx = parse.program();
					ArmAssemblerVisitor visitor = new ArmAssemblerVisitor();
					List<Integer> assembledCode = visitor.assembleCode(ctx);

					//Now we just need to write to the Output File
					FileWriter writer = new FileWriter("OutputBinary.bin");
					for(Integer assembledCodeInstr : assembledCode){
						StringBuilder resultBuilder = new StringBuilder();
						String rawBinaryString = Integer.toBinaryString(assembledCodeInstr);
						if(rawBinaryString.length() > 32){
							rawBinaryString = rawBinaryString.substring(rawBinaryString.length() - 32);
						}
						
						if(rawBinaryString.length() < 32){
							for(int i = 0; i < 32 - rawBinaryString.length(); i++){
								resultBuilder.append('0');
							}
							resultBuilder.append(rawBinaryString);
							rawBinaryString = resultBuilder.toString();
						}

						writer.append(rawBinaryString);
						writer.append('\n');
					}
					writer.close();
				} catch(Exception exp){
					EdeInstance.appendIoText("StandardError", exp.toString());
				}
				return null;
			}
		}, "InputAssembly.a", "OutputBinary.bin", "StandardError");

		EdeInstance.AddVerilogJob("Execute", "./processor/ARM7TDMIS.v", "default", "StandardInput", "StandardOutput", "StandardError");

		EdeInstance.AddFlag("C");
		EdeInstance.AddFlag("V");
		EdeInstance.AddFlag("N");
		EdeInstance.AddFlag("O");
		EdeInstance.AddFlag("Z");

		EdeInstance.AddIoSection("Errors", "StandardError");
		EdeInstance.AddIoSection("Io", "StandardInput", "StandardOutput");

		frame.setPreferredSize(screenSize);
		frame.add(EdeInstance);
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.pack();
                frame.setVisible(true);
	}
}
