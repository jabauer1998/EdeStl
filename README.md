# This is the Repo for the Emulator Development Environment Standard Library
This Standard Library is utilized to generate an Ede Instance. It contains all of the Gui Compoments and Methods to create a Ede Instance with the GuiEde class. The EdeStl also contains several other componenets that can be utilized in other projects as well and it is why the EdeStl is seperated from the [EdeGen](https://github.com/jabauer1998/EdeGen)(Emulator Debug Environment Genertor tool) repository.

##  Directory Structure

The EdeStl is broken down into several sub components and sub directories.

1) ede/stl/gui -> Everything to build the gui Ede instance
2) ede/stl/interpreter -> Contains a Verilog interpreter that can either report to the Ede Instance(EdeInterpreter) or the console(Verilog Interpreter)
3) ede/stl/compiler -> Contains a Verilog to Java Byte Code compiler
4) ede/stl/common -> Contains a bunch of utility files that are utilized elsewhere in the code
5) ede/stl/ast -> Contains all of the ast nodes of Verilog
6) ede/stl/parser -> Contains the Verilog Parser and Lexer

## Emulator Debug Environment?
## What is it?
The EDE is software that is inspired by the PEP9 virtual computer. PEP9 is an educational tool that allows students to learn the basics of how a computer works without having to dive into any actual hardware. It is a much more cost effective solution for universities to teach undergrad/introductory level assembly/computer systems courses. The clever GUI also allows the students to easily visualize whats going on inside the CPU. It does have its limits. Since PEP 9 is not an actual computer, the assembly syntax is useless in industry. It also offers a very impractical register file. To account for this the EDE is a system where an instructor(or anyone else) can create an emulator with a Hardware Description Language(Verilog). This gui can be utilized to like the pep9 to teach a computer systems course, however with a customized processor. The HDL component of the project can be utilized to teach a computer architecture course without having to buy expensive FPGA's. The EDE can also be used in industry as a high level final step pre-silicon verification tool. Fabless semiconductor companies can use it to verify that there architecture works prior to sending the design off to get manufactered and if it doesn't they can use the gui to give them insight about where the error is occuring.

## Software Used in this Project
Swing - API to create the IDE <br>
Java - Programming language <br>
Junit - Unit testing interface <br>
Verilog - Used to Design Test Processor
  
# Features in Development

<p>Currently, the interpreter and compiler are updated to support behavorial verilog. I am working on fixing all the issues with the Parser

# Future Plans(reach out to me if you would like to work on some of these)
<ul>
  <li> FPGA synthesis tool built into the GUI to upload designs to an FPGA </li>
  <li> Built in Logic Analysis to analyze wave-forms etc... </li>
</ul>






