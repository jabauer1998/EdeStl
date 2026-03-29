package sample.ede;

import javax.swing.*;
import java.awt.*;
import ede.stl.gui.GuiEde;
import ede.stl.gui.GuiJob.TextAreaType;
import ede.stl.gui.GuiJob.TextAreaNumbered;
import ede.stl.gui.GuiRegister;
import ede.stl.common.EdeCallable;
import ede.stl.gui.GuiRam;
import declan.backend.assembler.ArmAssemblerParser;
import declan.backend.assembler.ArmAssemblerParser.ProgramContext;
import declan.backend.assembler.ArmAssemblerLexer;
import declan.backend.assembler.AssemblerVisitor;
import java.util.List;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CharStream;
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
        
                int screenWidth = screenSize.width;
                int screenHeight = screenSize.height;
        
                JFrame frame = new JFrame("Emulator Development Environment");

                int numBytesInRow = 4;
                GuiEde EdeInstance = new GuiEde(screenWidth, screenHeight, numBytesInRow, GuiRam.AddressFormat.DECIMAL, GuiRam.MemoryFormat.HEXADECIMAL);

                EdeInstance.gatherMetaDataFromVerilogFile("./sample/processor/ARM7TDMIS.v", GuiRegister.Format.BINARY);

                String[] keywords = {"ADD", "SUB", "MOV", "LDR", "STR", "B", "BL", "CMP", "BEQ", "BNE", "BGT", "BLT", "BGE"};

                EdeInstance.AddJavaJob("Assemble", TextAreaType.KEYWORD, TextAreaNumbered.IS_NUMBERED, new EdeCallable() {
			@Override
                        public String call(String input) throws Exception {
                                CharStream byteStream = CharStreams.fromString(input);
                                ArmAssemblerLexer lex = new ArmAssemblerLexer(byteStream);
                                CommonTokenStream tokStream = new CommonTokenStream(lex);
                                ArmAssemblerParser parse = new ArmAssemblerParser(tokStream);
                                ProgramContext ctx = parse.program();
                                AssemblerVisitor visitor = new AssemblerVisitor();
                                List<Integer> assembledCode = visitor.assembleCode(ctx);

                                StringBuilder output = new StringBuilder();
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

                                        output.append(rawBinaryString);
                                        output.append('\n');
                                }
                                return output.toString();
                        }
                }, keywords);

                EdeInstance.AddVerilogJob("Execute", "Arm", TextAreaNumbered.IS_NUMBERED, "./sample/processor/ARM7TDMIS.v", "default", "StandardInput", "StandardOutput", "StandardError", true);

                EdeInstance.linkJobs();

                EdeInstance.AddIoSection("Errors", "StandardError", ede.stl.gui.GuiIO.Editable.READ_ONLY);
                EdeInstance.AddIoSection("Io", "StandardInput", ede.stl.gui.GuiIO.Editable.EDITABLE);
                EdeInstance.AddIoSection("Io", "StandardOutput", ede.stl.gui.GuiIO.Editable.READ_ONLY);

                frame.setPreferredSize(screenSize);
                frame.add(EdeInstance);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
        }
}
