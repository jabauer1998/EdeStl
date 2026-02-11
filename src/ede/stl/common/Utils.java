package ede.stl.common;

import java.io.File;
import java.util.List;
import javax.management.RuntimeErrorException;
import org.objectweb.asm.Opcodes;
import ede.stl.common.Pointer;
import ede.stl.common.FormattedScanner;
import ede.stl.interpreter.Environment;
import ede.stl.Value .BoolVal;
import ede.stl.Value .ByteVal;
import ede.stl.Value .IntVal;
import ede.stl.Value .LongVal;
import ede.stl.Value .RealVal;
import ede.stl.Value .ShortVal;
import ede.stl.Value .StrVal;
import ede.stl.Value .UnsignedByteVal;
import ede.stl.Value .UnsignedIntVal;
import ede.stl.Value .UnsignedLongVal;
import ede.stl.Value .UnsignedShortVal;
import ede.stl.Value .Value;
import ede.stl.Value .VectorVal;
import ede.stl.Value .ArrayIntVal;
import ede.stl.Value .ArrayRegVal;
import ede.stl.Value .ArrayVal;
import ede.stl.Value .ArrayVectorVal;
import ede.stl.circuit.CircuitElem;
import ede.stl.Value .HalfAdder;
import ede.stl.Value .RippleCarryAdder;
import ede.stl.Value .RippleCarrySubtractor;
import ede.stl.Value .RegVal;
import ede.stl.circuit.AndGate;
import ede.stl.circuit.NotGate;
import ede.stl.circuit.OrGate;
import ede.stl.circuit.XNorGate;
import ede.stl.circuit.XorGate;
import ede.stl.Value .WireVal;
import ede.stl.Value .Pattern;
import ede.stl.ast.Expression;
import ede.stl.ast.ModuleItem;
import ede.stl.ast.Input;
import ede.stl.ast.Int;
import ede.stl.ast.Reg;

public class Utils {
	/**
	 * The checksize method is used to check if the size on the right hand side of the ' is
	 * equal to the amount of characters on the left hand size of the equals. Ex:
	 * "8'b10101010" (Returns True) 2'b10101010(Returns False)
	 * 
	 * @param val the string representation of the number passed in
	 */
	public static boolean checkSize(String val){ return getSize(val) == (val.substring(val.indexOf('\'') + 2).length()); }

	/*
	 * The get size method returns the Size of the string located on the right of the '
	 * 20'b00000000... the size of this is 20
	 */
	private static int getSize(String val){
		int upTo = val.indexOf('\'');
		return Integer.parseInt(val.substring(0, upTo));
	}

	/**
	 * The getBase method returns the base of the number given the base letter b is binary d
	 * is decimal 0 is octal etc...
	 * 
	 * @param val string representation of the number
	 */

	public static int getBase(String val){
		int id = val.indexOf('\'') + 1;

		switch(Character.toLowerCase(val.charAt(id))){
			case 'd':
				return 10;
			case 'h':
				return 16;
			case 'o':
				return 8;
			case 'b':
				return 2;
		}

		return -1;
	}

	/**
	 * Returns the numerical version of the number
	 * 
	 * @param val the string representation of the number
	 */
	public static long getBinary(String val){

		if (checkSize(val)) {
			return Long.parseLong(val.substring(val.indexOf('\'') + 2), 2);
		} else {
			System.out.println("Sizes to dot match on integer " + val);
			return -1;
		}

	}

	/**
	 * Returns the Hexidecimal version of the number
	 * 
	 * @param val Number representation of the number
	 */

	public static long getHexidecimal(String val){

		if (checkSize(val)) {
			return Long.parseLong(val.substring(val.indexOf('\'') + 2), 16);
		} else {
			System.out.println("Sizes to dot match on integer " + val);
			return -1;
		}

	}

	/**
	 * Returns the Octal version of the number
	 * 
	 * @param val String representation of the number
	 */
	public static long getOctal(String val){

		if (checkSize(val)) {
			return Long.parseLong(val.substring(val.indexOf('\'') + 2), 8);
		} else {
			System.out.println("Sizes to dot match on integer " + val);
			return -1;
		}

	}

	/**
	 * Returns the Decimal version of the number
	 * 
	 * @param val String representing the number
	 */
	public static long getDecimal(String val){

		if (checkSize(val)) {
			return Long.parseLong(val.substring(val.indexOf('\'') + 2));
		} else {
			System.out.println("Sizes to dot match on integer " + val);
			return -1;
		}

	}

	/**
	 * Below are some errorHandling routines
	 * @throws Exception
	 */
	public static void errorAndExit(String errorParam) throws Exception{ 
		throw new Exception(errorParam);
	}

	public static void errorAndExit(String errorParam, io.github.h20man13.emulator_ide.common.Position position) throws Exception{
		Utils.errorAndExit(errorParam + position.toString());
	}

	public static String GetRuntimeDir(){
		return new File("").getAbsolutePath();
	}

	public static IntVal errorOccured(){
		return new IntVal(-1);
	}

	public static IntVal success(){
		return new IntVal(0);
	}

	public static StrVal fetchFunctionName(ModuleItem functionDeclaration) throws Exception{
		if(functionDeclaration instanceof Reg.Scalar.Ident){
			return new StrVal(((Reg.Scalar.Ident)functionDeclaration).declarationIdentifier);
		} else if(functionDeclaration instanceof Reg.Vector.Ident) {
			return new StrVal(((Reg.Vector.Ident)functionDeclaration).declarationIdentifier);
		} else if(functionDeclaration instanceof Int.Ident){
			return new StrVal(((Int.Ident)functionDeclaration).declarationIdentifier);
		} else {
			Utils.errorAndExit("Unknown Function Type found " + functionDeclaration.toString());
			return null;
		}
	}

    public static Value dd(Value left, Value right) throws Exception{
        if(left.isReal() && right.isReal()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isReal() && right.isBool()) return new RealVal(left.realValue() + right.realValue());
        else if(left.isReal() && right.isUnsignedByteValue()) return new RealVal(left.realValue() + right.realValue()); 
        else if(left.isReal() && right.isByte()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isReal() && right.isUnsignedInt()) return new RealVal(left.realValue() + right.realValue());
        else if(left.isReal() && right.isInt()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isReal() && right.isUnsignedShort()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isReal() && right.isShort()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isReal() && right.isUnsignedLong()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isReal() && right.isLong()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isReal() && right.isRegister()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isReal() && right.isVector()) return new RealVal(left.realValue() + right.realValue());
		
		else if(left.isBool() && right.isReal()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isBool() && right.isBool()) return new UnsignedByteVal(left.byteValue() + right.byteValue());
        else if(left.isBool() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() + right.byteValue()); 
        else if(left.isBool() && right.isByte()) return new ByteVal(left.byteValue() + right.byteValue());
		else if(left.isBool() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() + right.intValue());
        else if(left.isBool() && right.isInt()) return new IntVal(left.intValue() + right.intValue());
		else if(left.isBool() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() + right.shortValue());
		else if(left.isBool() && right.isShort()) return new ShortVal(left.shortValue() + right.shortValue());
		else if(left.isBool() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isBool() && right.isLong()) return new LongVal(left.longValue() + right.longValue());
		else if(left.isBool() && right.isRegister()) return new UnsignedByteVal(left.byteValue() + right.byteValue());
		else if(left.isBool() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedIntVal(left.byteValue() + right.byteValue());
			else if(size <= 16) return new UnsignedIntVal(left.shortValue() + right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() + right.intValue());
			else return new UnsignedLongVal(left.longValue() + right.longValue());
		}

		else if(left.isUnsignedByteValue() && right.isReal()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isUnsignedByteValue() && right.isBool()) return new UnsignedByteVal(left.byteValue() + right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.byteValue() + right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByte()) return new UnsignedIntVal(left.byteValue() + right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() + right.intValue());
        else if(left.isUnsignedByteValue() && right.isInt()) return new UnsignedIntVal(left.intValue() + right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShort()) return new UnsignedIntVal(left.shortValue() + right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShort()) return new UnsignedIntVal(left.shortValue() + right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedByteValue() && right.isLong()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() + right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedIntVal(left.byteValue() + right.byteValue());
			else if(size <= 16) return new UnsignedIntVal(left.shortValue() + right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() + right.intValue());
			else return new UnsignedLongVal(left.longValue() + right.longValue());
		}

		else if(left.isByte() && right.isReal()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isByte() && right.isBool()) return new ByteVal(left.byteValue() + right.byteValue());
        else if(left.isByte() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.byteValue() + right.byteValue()); 
        else if(left.isByte() && right.isByte()) return new IntVal(left.byteValue() + right.byteValue());
		else if(left.isByte() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() + right.intValue());
        else if(left.isByte() && right.isInt()) return new IntVal(left.intValue() + right.intValue());
		else if(left.isByte() && right.isUnsignedShort()) return new UnsignedIntVal(left.shortValue() + right.shortValue());
		else if(left.isByte() && right.isShort()) return new IntVal(left.shortValue() + right.shortValue());
		else if(left.isByte() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isByte() && right.isLong()) return new LongVal(left.longValue() + right.longValue());
		else if(left.isByte() && right.isRegister()) return new UnsignedByteVal(left.byteValue() + right.byteValue());
		else if(left.isByte() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedIntVal(left.byteValue() + right.byteValue());
			else if(size <= 16) return new UnsignedIntVal(left.shortValue() + right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() + right.intValue());
			else return new UnsignedLongVal(left.longValue() + right.longValue());
		}

		else if(left.isUnsignedShort() && right.isReal()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isUnsignedShort() && right.isBool()) return new UnsignedShortVal(left.shortValue() + right.shortValue());
        else if(left.isUnsignedShort() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.shortValue() + right.shortValue()); 
        else if(left.isUnsignedShort() && right.isByte()) return new UnsignedIntVal(left.shortValue() + right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() + right.intValue());
        else if(left.isUnsignedShort() && right.isInt()) return new UnsignedIntVal(left.intValue() + right.intValue());
		else if(left.isUnsignedShort() && right.isUnsignedShort()) return new UnsignedIntVal(left.shortValue() + right.shortValue());
		else if(left.isUnsignedShort() && right.isShort()) return new UnsignedIntVal(left.shortValue() + right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedShort() && right.isLong()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedShort() && right.isRegister()) return new UnsignedIntVal(left.shortValue() + right.shortValue());
		else if(left.isUnsignedShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedIntVal(left.shortValue() + right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() + right.intValue());
			else return new UnsignedLongVal(left.longValue() + right.longValue());
		}

		else if(left.isShort() && right.isReal()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isShort() && right.isBool()) return new UnsignedShortVal(left.shortValue() + right.shortValue());
        else if(left.isShort() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.shortValue() + right.shortValue()); 
        else if(left.isShort() && right.isByte()) return new IntVal(left.shortValue() + right.shortValue());
		else if(left.isShort() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() + right.intValue());
        else if(left.isShort() && right.isInt()) return new IntVal(left.intValue() + right.intValue());
		else if(left.isShort() && right.isUnsignedShort()) return new UnsignedIntVal(left.shortValue() + right.shortValue());
		else if(left.isShort() && right.isShort()) return new IntVal(left.shortValue() + right.shortValue());
		else if(left.isShort() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isShort() && right.isLong()) return new LongVal(left.longValue() + right.longValue());
		else if(left.isShort() && right.isRegister()) return new UnsignedShortVal(left.byteValue() + right.byteValue());
		else if(left.isShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedIntVal(left.shortValue() + right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() + right.intValue());
			else return new UnsignedLongVal(left.longValue() + right.longValue());
		}

		else if(left.isUnsignedInt() && right.isReal()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isUnsignedInt() && right.isBool()) return new UnsignedIntVal(left.intValue() + right.intValue());
        else if(left.isUnsignedInt() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() + right.intValue()); 
        else if(left.isUnsignedInt() && right.isByte()) return new UnsignedIntVal(left.shortValue() + right.shortValue());
		else if(left.isUnsignedInt() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() + right.intValue());
        else if(left.isUnsignedInt() && right.isInt()) return new UnsignedIntVal(left.intValue() + right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedShort()) return new UnsignedIntVal(left.intValue() + right.intValue());
		else if(left.isUnsignedInt() && right.isShort()) return new UnsignedIntVal(left.intValue() + right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedInt() && right.isLong()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedInt() && right.isRegister()) return new UnsignedIntVal(left.intValue() + right.intValue());
		else if(left.isUnsignedInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() + right.intValue());
			else return new UnsignedLongVal(left.longValue() + right.longValue());
		}

		else if(left.isInt() && right.isReal()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isInt() && right.isBool()) return new UnsignedIntVal(left.intValue() + right.intValue());
        else if(left.isInt() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() + right.intValue()); 
        else if(left.isInt() && right.isByte()) return new IntVal(left.intValue() + right.intValue());
		else if(left.isInt() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() + right.intValue());
        else if(left.isInt() && right.isInt()) return new IntVal(left.intValue() + right.intValue());
		else if(left.isInt() && right.isUnsignedShort()) return new UnsignedIntVal(left.intValue() + right.intValue());
		else if(left.isInt() && right.isShort()) return new IntVal(left.intValue() + right.intValue());
		else if(left.isInt() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isInt() && right.isLong()) return new LongVal(left.longValue() + right.longValue());
		else if(left.isInt() && right.isRegister()) return new UnsignedIntVal(left.intValue() + right.intValue());
		else if(left.isInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() + right.intValue());
			else return new UnsignedLongVal(left.longValue() + right.longValue());
		}

		else if(left.isUnsignedLong() && right.isReal()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isUnsignedLong() && right.isBool()) return new UnsignedLongVal(left.longValue() + right.longValue());
        else if(left.isUnsignedLong() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() + right.longValue()); 
        else if(left.isUnsignedLong() && right.isByte()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedInt()) return new UnsignedLongVal(left.longValue() + right.longValue());
        else if(left.isUnsignedLong() && right.isInt()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedShort()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedLong() && right.isShort()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedLong() && right.isLong()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedLong() && right.isRegister()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isUnsignedLong() && right.isVector()) return new UnsignedLongVal(left.longValue() + right.longValue());

		else if(left.isLong() && right.isReal()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isLong() && right.isBool()) return new UnsignedLongVal(left.longValue() + right.longValue());
        else if(left.isLong() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() + right.longValue()); 
        else if(left.isLong() && right.isByte()) return new LongVal(left.longValue() + right.longValue());
		else if(left.isLong() && right.isUnsignedInt()) return new UnsignedLongVal(left.longValue() + right.longValue());
        else if(left.isLong() && right.isInt()) return new LongVal(left.longValue() + right.longValue());
		else if(left.isLong() && right.isUnsignedShort()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isLong() && right.isShort()) return new LongVal(left.longValue() + right.longValue());
		else if(left.isLong() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isLong() && right.isLong()) return new LongVal(left.longValue() + right.longValue());
		else if(left.isLong() && right.isRegister()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isLong() && right.isVector()) return new UnsignedLongVal(left.longValue() + right.longValue());

		else if(left.isRegister() && right.isReal()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isRegister() && right.isBool()) return new UnsignedByteVal(left.byteValue() + right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.byteValue() + right.byteValue()); 
        else if(left.isRegister() && right.isByte()) return new UnsignedIntVal(left.byteValue() + right.byteValue());
		else if(left.isRegister() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() + right.intValue());
        else if(left.isRegister() && right.isInt()) return new UnsignedIntVal(left.intValue() + right.intValue());
		else if(left.isRegister() && right.isUnsignedShort()) return new UnsignedIntVal(left.shortValue() + right.shortValue());
		else if(left.isRegister() && right.isShort()) return new UnsignedIntVal(left.shortValue() + right.shortValue());
		else if(left.isRegister() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isRegister() && right.isLong()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isRegister() && right.isRegister()) return new UnsignedByteVal(left.byteValue() + right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedIntVal(left.byteValue() + right.byteValue());
			else if(size <= 16) return new UnsignedIntVal(left.shortValue() + right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() + right.intValue());
			else return new UnsignedLongVal(left.longValue() + right.longValue());
		}

		else if(left.isVector() && right.isReal()) return new RealVal(left.realValue() + right.realValue());
		else if(left.isVector() && right.isBool()) return new UnsignedByteVal(left.byteValue() + right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() + right.byteValue()); 
        else if(left.isVector() && right.isByte()) return new UnsignedByteVal(left.byteValue() + right.byteValue());
		else if(left.isVector() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() + right.intValue());
        else if(left.isVector() && right.isInt()) return new UnsignedIntVal(left.intValue() + right.intValue());
		else if(left.isVector() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() + right.shortValue());
		else if(left.isVector() && right.isShort()) return new UnsignedShortVal(left.shortValue() + right.shortValue());
		else if(left.isVector() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isVector() && right.isLong()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else if(left.isVector() && right.isRegister()) return new UnsignedByteVal(left.byteValue() + right.byteValue());
		else if(left.isVector() && right.isVector()) return new UnsignedLongVal(left.longValue() + right.longValue());
		else {
			errorAndExit("Inavlid Addition operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

	private static Value addVector(VectorVal left, VectorVal right) throws Exception{
		int leftSize = left.getSize();
		int rightSize = right.getSize();

		int largestSize = (leftSize >= rightSize)? leftSize : rightSize;

		VectorVal resultVec = new VectorVal(0, largestSize - 1);

		int startLeft = left.getStart();
		int startRight = right.getStart();
		int startResult = resultVec.getStart();

		int endLeft = left.getEnd();
		int endRight = right.getEnd();
		int endResult = resultVec.getEnd();

		boolean carryIn = false;
		for(int i = 0; i < largestSize; i++){
			boolean leftBit = false;
			if(startLeft <= endLeft)
				leftBit = left.getValue(startLeft).getStateSignal();
			
			boolean rightBit = false;
			if(startRight <= endRight)
				rightBit = right.getValue(startRight).getStateSignal();

			if(leftBit == true && rightBit == true && carryIn == true){
				resultVec.setValue(startResult, new RegVal(true));
				carryIn = true;
			} else if(leftBit == true && rightBit == true && carryIn == false){
				resultVec.setValue(startResult, new RegVal(false));
				carryIn = true;
			} else if(leftBit == true && rightBit == false && carryIn == true){
				resultVec.setValue(startResult, new RegVal(false));
				carryIn = true;
			} else if(leftBit == false && rightBit == true && carryIn == true){
				resultVec.setValue(startResult, new RegVal(false));
				carryIn = true;
			} else if(leftBit == false && rightBit == false && carryIn == true){
				resultVec.setValue(startResult, new RegVal(true));
				carryIn = false;
			} else if(leftBit == false && rightBit == true && carryIn == false){
				resultVec.setValue(startResult, new RegVal(true));
				carryIn = false;
			} else if(leftBit == true && rightBit == false && carryIn == false){
				resultVec.setValue(startResult, new RegVal(true));
				carryIn = false;
			} else if(leftBit == false && rightBit == false && carryIn == false){
				resultVec.setValue(startResult, new RegVal(false));
				carryIn = false;
			} else {
				errorAndExit("Unable to add vector signals in an addition operation with left=" + leftBit + " right=" + rightBit + " carryIn=" + carryIn);
			}

			startLeft++;
			startRight++;
			startResult++;
		}

		return resultVec;
	}

	public static Value createAdder(Value left, Value right) throws Exception{
		return createRippleCarryAdder(left, right);
	}

	public static Value createRippleCarryAdder(Value left, Value right) throws Exception{
		if(left.isWire() && right.isWire()){
			WireVal Input1 = (WireVal)left;
			WireVal Input2 = (WireVal)right;

			WireVal output = new WireVal();
			WireVal carry = new WireVal();

			new HalfAdder(output, carry, Input1, Input2);

			return output;
		} else if (left.isVector() && right.isVector()){
			WireVal CarryOut = new WireVal();
			VectorVal Output = new VectorVal(0, ((VectorVal)right).getSize() - 1); 
			new RippleCarryAdder(CarryOut, Output, ((VectorVal)right), (VectorVal)left);

			return Output;
		} else {
			Utils.errorAndExit("Error Invalid Types for creating an Adder (Left -> " + left.getClass().getName() + " | Right -> " + right.getClass().getName() + ")");
		}

		return Utils.errorOccured();
	}

    public static Value inus(Value left, Value right) throws Exception{
        if(left.isReal() && right.isReal()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isReal() && right.isBool()) return new RealVal(left.realValue() - right.realValue());
        else if(left.isReal() && right.isUnsignedByteValue()) return new RealVal(left.realValue() - right.realValue()); 
        else if(left.isReal() && right.isByte()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isReal() && right.isUnsignedInt()) return new RealVal(left.realValue() - right.realValue());
        else if(left.isReal() && right.isInt()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isReal() && right.isUnsignedShort()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isReal() && right.isShort()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isReal() && right.isUnsignedLong()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isReal() && right.isLong()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isReal() && right.isRegister()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isReal() && right.isVector()) return new RealVal(left.realValue() - right.realValue());
		
		else if(left.isBool() && right.isReal()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isBool() && right.isBool()) return new UnsignedByteVal(left.byteValue() - right.byteValue());
        else if(left.isBool() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() - right.byteValue()); 
        else if(left.isBool() && right.isByte()) return new ByteVal(left.byteValue() - right.byteValue());
		else if(left.isBool() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() - right.intValue());
        else if(left.isBool() && right.isInt()) return new IntVal(left.intValue() - right.intValue());
		else if(left.isBool() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() - right.shortValue());
		else if(left.isBool() && right.isShort()) return new ShortVal(left.shortValue() - right.shortValue());
		else if(left.isBool() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isBool() && right.isLong()) return new LongVal(left.longValue() - right.longValue());
		else if(left.isBool() && right.isRegister()) return new UnsignedByteVal(left.byteValue() - right.byteValue());
		else if(left.isBool() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedIntVal(left.byteValue() - right.byteValue());
			else if(size <= 16) return new UnsignedIntVal(left.shortValue() - right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() - right.intValue());
			else return new UnsignedLongVal(left.longValue() - right.longValue());
		}

		else if(left.isUnsignedByteValue() && right.isReal()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isUnsignedByteValue() && right.isBool()) return new UnsignedByteVal(left.byteValue() - right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.byteValue() - right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByte()) return new UnsignedIntVal(left.byteValue() - right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() - right.intValue());
        else if(left.isUnsignedByteValue() && right.isInt()) return new UnsignedIntVal(left.intValue() - right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShort()) return new UnsignedIntVal(left.shortValue() - right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShort()) return new UnsignedIntVal(left.shortValue() - right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedByteValue() && right.isLong()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() - right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedIntVal(left.byteValue() - right.byteValue());
			else if(size <= 16) return new UnsignedIntVal(left.shortValue() - right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() - right.intValue());
			else return new UnsignedLongVal(left.longValue() - right.longValue());
		}

		else if(left.isByte() && right.isReal()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isByte() && right.isBool()) return new ByteVal(left.byteValue() - right.byteValue());
        else if(left.isByte() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.byteValue() - right.byteValue()); 
        else if(left.isByte() && right.isByte()) return new IntVal(left.byteValue() - right.byteValue());
		else if(left.isByte() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() - right.intValue());
        else if(left.isByte() && right.isInt()) return new IntVal(left.intValue() - right.intValue());
		else if(left.isByte() && right.isUnsignedShort()) return new UnsignedIntVal(left.shortValue() - right.shortValue());
		else if(left.isByte() && right.isShort()) return new IntVal(left.shortValue() - right.shortValue());
		else if(left.isByte() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isByte() && right.isLong()) return new LongVal(left.longValue() - right.longValue());
		else if(left.isByte() && right.isRegister()) return new UnsignedByteVal(left.byteValue() - right.byteValue());
		else if(left.isByte() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedIntVal(left.byteValue() - right.byteValue());
			else if(size <= 16) return new UnsignedIntVal(left.shortValue() - right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() - right.intValue());
			else return new UnsignedLongVal(left.longValue() - right.longValue());
		}

		else if(left.isUnsignedShort() && right.isReal()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isUnsignedShort() && right.isBool()) return new UnsignedShortVal(left.shortValue() - right.shortValue());
        else if(left.isUnsignedShort() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.shortValue() - right.shortValue()); 
        else if(left.isUnsignedShort() && right.isByte()) return new UnsignedIntVal(left.shortValue() - right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() - right.intValue());
        else if(left.isUnsignedShort() && right.isInt()) return new UnsignedIntVal(left.intValue() - right.intValue());
		else if(left.isUnsignedShort() && right.isUnsignedShort()) return new UnsignedIntVal(left.shortValue() - right.shortValue());
		else if(left.isUnsignedShort() && right.isShort()) return new UnsignedIntVal(left.shortValue() - right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedShort() && right.isLong()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedShort() && right.isRegister()) return new UnsignedIntVal(left.shortValue() - right.shortValue());
		else if(left.isUnsignedShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedIntVal(left.shortValue() - right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() - right.intValue());
			else return new UnsignedLongVal(left.longValue() - right.longValue());
		}

		else if(left.isShort() && right.isReal()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isShort() && right.isBool()) return new UnsignedShortVal(left.shortValue() - right.shortValue());
        else if(left.isShort() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.shortValue() - right.shortValue()); 
        else if(left.isShort() && right.isByte()) return new IntVal(left.shortValue() - right.shortValue());
		else if(left.isShort() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() - right.intValue());
        else if(left.isShort() && right.isInt()) return new IntVal(left.intValue() - right.intValue());
		else if(left.isShort() && right.isUnsignedShort()) return new UnsignedIntVal(left.shortValue() - right.shortValue());
		else if(left.isShort() && right.isShort()) return new IntVal(left.shortValue() - right.shortValue());
		else if(left.isShort() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isShort() && right.isLong()) return new LongVal(left.longValue() - right.longValue());
		else if(left.isShort() && right.isRegister()) return new UnsignedShortVal(left.byteValue() - right.byteValue());
		else if(left.isShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedIntVal(left.shortValue() - right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() - right.intValue());
			else return new UnsignedLongVal(left.longValue() - right.longValue());
		}

		else if(left.isUnsignedInt() && right.isReal()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isUnsignedInt() && right.isBool()) return new UnsignedIntVal(left.intValue() - right.intValue());
        else if(left.isUnsignedInt() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() - right.intValue()); 
        else if(left.isUnsignedInt() && right.isByte()) return new UnsignedIntVal(left.shortValue() - right.shortValue());
		else if(left.isUnsignedInt() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() - right.intValue());
        else if(left.isUnsignedInt() && right.isInt()) return new UnsignedIntVal(left.intValue() - right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedShort()) return new UnsignedIntVal(left.intValue() - right.intValue());
		else if(left.isUnsignedInt() && right.isShort()) return new UnsignedIntVal(left.intValue() - right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedInt() && right.isLong()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedInt() && right.isRegister()) return new UnsignedIntVal(left.intValue() - right.intValue());
		else if(left.isUnsignedInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() - right.intValue());
			else return new UnsignedLongVal(left.longValue() - right.longValue());
		}

		else if(left.isInt() && right.isReal()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isInt() && right.isBool()) return new UnsignedIntVal(left.intValue() - right.intValue());
        else if(left.isInt() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() - right.intValue()); 
        else if(left.isInt() && right.isByte()) return new IntVal(left.intValue() - right.intValue());
		else if(left.isInt() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() - right.intValue());
        else if(left.isInt() && right.isInt()) return new IntVal(left.intValue() - right.intValue());
		else if(left.isInt() && right.isUnsignedShort()) return new UnsignedIntVal(left.intValue() - right.intValue());
		else if(left.isInt() && right.isShort()) return new IntVal(left.intValue() - right.intValue());
		else if(left.isInt() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isInt() && right.isLong()) return new LongVal(left.longValue() - right.longValue());
		else if(left.isInt() && right.isRegister()) return new UnsignedIntVal(left.intValue() - right.intValue());
		else if(left.isInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() - right.intValue());
			else return new UnsignedLongVal(left.longValue() - right.longValue());
		}

		else if(left.isUnsignedLong() && right.isReal()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isUnsignedLong() && right.isBool()) return new UnsignedLongVal(left.longValue() - right.longValue());
        else if(left.isUnsignedLong() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() - right.longValue()); 
        else if(left.isUnsignedLong() && right.isByte()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedInt()) return new UnsignedLongVal(left.longValue() - right.longValue());
        else if(left.isUnsignedLong() && right.isInt()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedShort()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedLong() && right.isShort()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedLong() && right.isLong()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedLong() && right.isRegister()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isUnsignedLong() && right.isVector()) return new UnsignedLongVal(left.longValue() - right.longValue());

		else if(left.isLong() && right.isReal()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isLong() && right.isBool()) return new UnsignedLongVal(left.longValue() - right.longValue());
        else if(left.isLong() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() - right.longValue()); 
        else if(left.isLong() && right.isByte()) return new LongVal(left.longValue() - right.longValue());
		else if(left.isLong() && right.isUnsignedInt()) return new UnsignedLongVal(left.longValue() - right.longValue());
        else if(left.isLong() && right.isInt()) return new LongVal(left.longValue() - right.longValue());
		else if(left.isLong() && right.isUnsignedShort()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isLong() && right.isShort()) return new LongVal(left.longValue() - right.longValue());
		else if(left.isLong() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isLong() && right.isLong()) return new LongVal(left.longValue() - right.longValue());
		else if(left.isLong() && right.isRegister()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isLong() && right.isVector()) return new UnsignedLongVal(left.longValue() - right.longValue());

		else if(left.isRegister() && right.isReal()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isRegister() && right.isBool()) return new UnsignedByteVal(left.byteValue() - right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.byteValue() - right.byteValue()); 
        else if(left.isRegister() && right.isByte()) return new UnsignedIntVal(left.byteValue() - right.byteValue());
		else if(left.isRegister() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() - right.intValue());
        else if(left.isRegister() && right.isInt()) return new UnsignedIntVal(left.intValue() - right.intValue());
		else if(left.isRegister() && right.isUnsignedShort()) return new UnsignedIntVal(left.shortValue() - right.shortValue());
		else if(left.isRegister() && right.isShort()) return new UnsignedIntVal(left.shortValue() - right.shortValue());
		else if(left.isRegister() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isRegister() && right.isLong()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isRegister() && right.isRegister()) return new UnsignedByteVal(left.byteValue() - right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedIntVal(left.byteValue() - right.byteValue());
			else if(size <= 16) return new UnsignedIntVal(left.shortValue() - right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() - right.intValue());
			else return new UnsignedLongVal(left.longValue() - right.longValue());
		}

		else if(left.isVector() && right.isReal()) return new RealVal(left.realValue() - right.realValue());
		else if(left.isVector() && right.isBool()) return new UnsignedByteVal(left.byteValue() - right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() - right.byteValue()); 
        else if(left.isVector() && right.isByte()) return new UnsignedByteVal(left.byteValue() - right.byteValue());
		else if(left.isVector() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() - right.intValue());
        else if(left.isVector() && right.isInt()) return new UnsignedIntVal(left.intValue() - right.intValue());
		else if(left.isVector() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() - right.shortValue());
		else if(left.isVector() && right.isShort()) return new UnsignedShortVal(left.shortValue() - right.shortValue());
		else if(left.isVector() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isVector() && right.isLong()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else if(left.isVector() && right.isRegister()) return new UnsignedByteVal(left.byteValue() - right.byteValue());
		else if(left.isVector() && right.isVector()) return new UnsignedLongVal(left.longValue() - right.longValue());
		else {
			errorAndExit("Inavlid Addition operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

	private static Value subVector(VectorVal left, VectorVal right) throws Exception{
		right = invertVector(right);
		return addVector(left, right);
	}

	private static VectorVal invertVector(VectorVal left){
		VectorVal vec = new VectorVal(0, left.getSize() - 1);
		int vecStart = vec.getStart();
		int leftStart = left.getStart();

		int vecEnd = vec.getEnd();
		int leftEnd = left.getEnd();

		for(int i = vecStart, j = leftStart; i <= vecEnd && j <= leftEnd; i++, j++){
			boolean signal = left.getValue(j).getStateSignal();
			vec.setValue(i, new RegVal(!signal));
		}

		return vec;
	}

	public static Value createSubtractor(Value left, Value right) throws Exception{
		if(left.isVector() && right.isVector()){
			//If they are both vectors we will create a RippleAdder
			WireVal CarryOut = new WireVal();
			VectorVal Output = new VectorVal(0, ((VectorVal)right).getSize() - 1); 
			new RippleCarrySubtractor(CarryOut, Output, ((VectorVal)right), (VectorVal)left);
			
			return Output;
		} else if(left.isWire() && right.isWire()) {
			WireVal leftWire = (WireVal)left;
			WireVal rightWire = (WireVal)right;
			WireVal realRight = new WireVal();

			//To Implement Subtraction we will just put one of the Wires through a Not Gate
			new NotGate(realRight, rightWire);
			rightWire = realRight;
			WireVal Output = new WireVal();
			WireVal Carry = new WireVal(); //Summy Variable Carry Out will not be returned as part of the Asssignment

			new HalfAdder(Output, Carry, leftWire, rightWire);

			return Output;
		} else {
			Utils.errorAndExit("Can't create a subtractor with the types " + right.getClass().getName() + " and " + left.getClass().getName());
			return Utils.errorOccured();
		}
	}

    public static Value imes(Value left, Value right) throws Exception{
        if(left.isReal() && right.isReal()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isReal() && right.isBool()) return new RealVal(left.realValue() * right.realValue());
        else if(left.isReal() && right.isUnsignedByteValue()) return new RealVal(left.realValue() * right.realValue()); 
        else if(left.isReal() && right.isByte()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isReal() && right.isUnsignedInt()) return new RealVal(left.realValue() * right.realValue());
        else if(left.isReal() && right.isInt()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isReal() && right.isUnsignedShort()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isReal() && right.isShort()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isReal() && right.isUnsignedLong()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isReal() && right.isLong()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isReal() && right.isRegister()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isReal() && right.isVector()) return new RealVal(left.realValue() * right.realValue());
		
		else if(left.isBool() && right.isReal()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isBool() && right.isBool()) return new UnsignedByteVal(left.byteValue() * right.byteValue());
        else if(left.isBool() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() * right.byteValue()); 
        else if(left.isBool() && right.isByte()) return new ByteVal(left.byteValue() * right.byteValue());
		else if(left.isBool() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() * right.intValue());
        else if(left.isBool() && right.isInt()) return new IntVal(left.intValue() * right.intValue());
		else if(left.isBool() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() * right.shortValue());
		else if(left.isBool() && right.isShort()) return new ShortVal(left.shortValue() * right.shortValue());
		else if(left.isBool() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isBool() && right.isLong()) return new LongVal(left.longValue() * right.longValue());
		else if(left.isBool() && right.isRegister()) return new UnsignedByteVal(left.byteValue() * right.byteValue());
		else if(left.isBool() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedIntVal(left.byteValue() * right.byteValue());
			else if(size <= 16) return new UnsignedIntVal(left.shortValue() * right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() * right.intValue());
			else return new UnsignedLongVal(left.longValue() * right.longValue());
		}

		else if(left.isUnsignedByteValue() && right.isReal()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isUnsignedByteValue() && right.isBool()) return new UnsignedByteVal(left.byteValue() * right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.byteValue() * right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByte()) return new UnsignedIntVal(left.byteValue() * right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() * right.intValue());
        else if(left.isUnsignedByteValue() && right.isInt()) return new UnsignedIntVal(left.intValue() * right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShort()) return new UnsignedIntVal(left.shortValue() * right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShort()) return new UnsignedIntVal(left.shortValue() * right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedByteValue() && right.isLong()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() * right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedIntVal(left.byteValue() * right.byteValue());
			else if(size <= 16) return new UnsignedIntVal(left.shortValue() * right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() * right.intValue());
			else return new UnsignedLongVal(left.longValue() * right.longValue());
		}

		else if(left.isByte() && right.isReal()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isByte() && right.isBool()) return new ByteVal(left.byteValue() * right.byteValue());
        else if(left.isByte() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.byteValue() * right.byteValue()); 
        else if(left.isByte() && right.isByte()) return new IntVal(left.byteValue() * right.byteValue());
		else if(left.isByte() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() * right.intValue());
        else if(left.isByte() && right.isInt()) return new IntVal(left.intValue() * right.intValue());
		else if(left.isByte() && right.isUnsignedShort()) return new UnsignedIntVal(left.shortValue() * right.shortValue());
		else if(left.isByte() && right.isShort()) return new IntVal(left.shortValue() * right.shortValue());
		else if(left.isByte() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isByte() && right.isLong()) return new LongVal(left.longValue() * right.longValue());
		else if(left.isByte() && right.isRegister()) return new UnsignedByteVal(left.byteValue() * right.byteValue());
		else if(left.isByte() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedIntVal(left.byteValue() * right.byteValue());
			else if(size <= 16) return new UnsignedIntVal(left.shortValue() * right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() * right.intValue());
			else return new UnsignedLongVal(left.longValue() * right.longValue());
		}

		else if(left.isUnsignedShort() && right.isReal()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isUnsignedShort() && right.isBool()) return new UnsignedShortVal(left.shortValue() * right.shortValue());
        else if(left.isUnsignedShort() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.shortValue() * right.shortValue()); 
        else if(left.isUnsignedShort() && right.isByte()) return new UnsignedIntVal(left.shortValue() * right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() * right.intValue());
        else if(left.isUnsignedShort() && right.isInt()) return new UnsignedIntVal(left.intValue() * right.intValue());
		else if(left.isUnsignedShort() && right.isUnsignedShort()) return new UnsignedIntVal(left.shortValue() * right.shortValue());
		else if(left.isUnsignedShort() && right.isShort()) return new UnsignedIntVal(left.shortValue() * right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedShort() && right.isLong()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedShort() && right.isRegister()) return new UnsignedIntVal(left.shortValue() * right.shortValue());
		else if(left.isUnsignedShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedIntVal(left.shortValue() * right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() * right.intValue());
			else return new UnsignedLongVal(left.longValue() * right.longValue());
		}

		else if(left.isShort() && right.isReal()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isShort() && right.isBool()) return new UnsignedShortVal(left.shortValue() * right.shortValue());
        else if(left.isShort() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.shortValue() * right.shortValue()); 
        else if(left.isShort() && right.isByte()) return new IntVal(left.shortValue() * right.shortValue());
		else if(left.isShort() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() * right.intValue());
        else if(left.isShort() && right.isInt()) return new IntVal(left.intValue() * right.intValue());
		else if(left.isShort() && right.isUnsignedShort()) return new UnsignedIntVal(left.shortValue() * right.shortValue());
		else if(left.isShort() && right.isShort()) return new IntVal(left.shortValue() * right.shortValue());
		else if(left.isShort() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isShort() && right.isLong()) return new LongVal(left.longValue() * right.longValue());
		else if(left.isShort() && right.isRegister()) return new UnsignedShortVal(left.byteValue() * right.byteValue());
		else if(left.isShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedIntVal(left.shortValue() * right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() * right.intValue());
			else return new UnsignedLongVal(left.longValue() * right.longValue());
		}

		else if(left.isUnsignedInt() && right.isReal()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isUnsignedInt() && right.isBool()) return new UnsignedIntVal(left.intValue() * right.intValue());
        else if(left.isUnsignedInt() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() * right.intValue()); 
        else if(left.isUnsignedInt() && right.isByte()) return new UnsignedIntVal(left.shortValue() * right.shortValue());
		else if(left.isUnsignedInt() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() * right.intValue());
        else if(left.isUnsignedInt() && right.isInt()) return new UnsignedIntVal(left.intValue() * right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedShort()) return new UnsignedIntVal(left.intValue() * right.intValue());
		else if(left.isUnsignedInt() && right.isShort()) return new UnsignedIntVal(left.intValue() * right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedInt() && right.isLong()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedInt() && right.isRegister()) return new UnsignedIntVal(left.intValue() * right.intValue());
		else if(left.isUnsignedInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() * right.intValue());
			else return new UnsignedLongVal(left.longValue() * right.longValue());
		}

		else if(left.isInt() && right.isReal()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isInt() && right.isBool()) return new UnsignedIntVal(left.intValue() * right.intValue());
        else if(left.isInt() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() * right.intValue()); 
        else if(left.isInt() && right.isByte()) return new IntVal(left.intValue() * right.intValue());
		else if(left.isInt() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() * right.intValue());
        else if(left.isInt() && right.isInt()) return new IntVal(left.intValue() * right.intValue());
		else if(left.isInt() && right.isUnsignedShort()) return new UnsignedIntVal(left.intValue() * right.intValue());
		else if(left.isInt() && right.isShort()) return new IntVal(left.intValue() * right.intValue());
		else if(left.isInt() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isInt() && right.isLong()) return new LongVal(left.longValue() * right.longValue());
		else if(left.isInt() && right.isRegister()) return new UnsignedIntVal(left.intValue() * right.intValue());
		else if(left.isInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() * right.intValue());
			else return new UnsignedLongVal(left.longValue() * right.longValue());
		}

		else if(left.isUnsignedLong() && right.isReal()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isUnsignedLong() && right.isBool()) return new UnsignedLongVal(left.longValue() * right.longValue());
        else if(left.isUnsignedLong() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() * right.longValue()); 
        else if(left.isUnsignedLong() && right.isByte()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedInt()) return new UnsignedLongVal(left.longValue() * right.longValue());
        else if(left.isUnsignedLong() && right.isInt()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedShort()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedLong() && right.isShort()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedLong() && right.isLong()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedLong() && right.isRegister()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isUnsignedLong() && right.isVector()) return new UnsignedLongVal(left.longValue() * right.longValue());

		else if(left.isLong() && right.isReal()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isLong() && right.isBool()) return new UnsignedLongVal(left.longValue() * right.longValue());
        else if(left.isLong() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() * right.longValue()); 
        else if(left.isLong() && right.isByte()) return new LongVal(left.longValue() * right.longValue());
		else if(left.isLong() && right.isUnsignedInt()) return new UnsignedLongVal(left.longValue() * right.longValue());
        else if(left.isLong() && right.isInt()) return new LongVal(left.longValue() * right.longValue());
		else if(left.isLong() && right.isUnsignedShort()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isLong() && right.isShort()) return new LongVal(left.longValue() * right.longValue());
		else if(left.isLong() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isLong() && right.isLong()) return new LongVal(left.longValue() * right.longValue());
		else if(left.isLong() && right.isRegister()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isLong() && right.isVector()) return new UnsignedLongVal(left.longValue() * right.longValue());

		else if(left.isRegister() && right.isReal()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isRegister() && right.isBool()) return new UnsignedByteVal(left.byteValue() * right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.byteValue() * right.byteValue()); 
        else if(left.isRegister() && right.isByte()) return new UnsignedIntVal(left.byteValue() * right.byteValue());
		else if(left.isRegister() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() * right.intValue());
        else if(left.isRegister() && right.isInt()) return new UnsignedIntVal(left.intValue() * right.intValue());
		else if(left.isRegister() && right.isUnsignedShort()) return new UnsignedIntVal(left.shortValue() * right.shortValue());
		else if(left.isRegister() && right.isShort()) return new UnsignedIntVal(left.shortValue() * right.shortValue());
		else if(left.isRegister() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isRegister() && right.isLong()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isRegister() && right.isRegister()) return new UnsignedByteVal(left.byteValue() * right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedIntVal(left.byteValue() * right.byteValue());
			else if(size <= 16) return new UnsignedIntVal(left.shortValue() * right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() * right.intValue());
			else return new UnsignedLongVal(left.longValue() * right.longValue());
		}

		else if(left.isVector() && right.isReal()) return new RealVal(left.realValue() * right.realValue());
		else if(left.isVector() && right.isBool()) return new UnsignedByteVal(left.byteValue() * right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() * right.byteValue()); 
        else if(left.isVector() && right.isByte()) return new UnsignedByteVal(left.byteValue() * right.byteValue());
		else if(left.isVector() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() * right.intValue());
        else if(left.isVector() && right.isInt()) return new UnsignedIntVal(left.intValue() * right.intValue());
		else if(left.isVector() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() * right.shortValue());
		else if(left.isVector() && right.isShort()) return new UnsignedShortVal(left.shortValue() * right.shortValue());
		else if(left.isVector() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isVector() && right.isLong()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else if(left.isVector() && right.isRegister()) return new UnsignedByteVal(left.byteValue() * right.byteValue());
		else if(left.isVector() && right.isVector()) return new UnsignedLongVal(left.longValue() * right.longValue());
		else {
			errorAndExit("Inavlid Addition operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

    public static Value iv(Value left, Value right) throws Exception{
        if(left.isReal() && right.isReal()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isReal() && right.isBool()) return new RealVal(left.realValue() / right.realValue());
        else if(left.isReal() && right.isUnsignedByteValue()) return new RealVal(left.realValue() / right.realValue()); 
        else if(left.isReal() && right.isByte()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isReal() && right.isUnsignedInt()) return new RealVal(left.realValue() / right.realValue());
        else if(left.isReal() && right.isInt()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isReal() && right.isUnsignedShort()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isReal() && right.isShort()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isReal() && right.isUnsignedLong()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isReal() && right.isLong()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isReal() && right.isRegister()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isReal() && right.isVector()) return new RealVal(left.realValue() / right.realValue());
		
		else if(left.isBool() && right.isReal()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isBool() && right.isBool()) return new RealVal(left.byteValue() / right.byteValue());
        else if(left.isBool() && right.isUnsignedByteValue()) return new RealVal(left.byteValue() / right.byteValue()); 
        else if(left.isBool() && right.isByte()) return new RealVal(left.byteValue() / right.byteValue());
		else if(left.isBool() && right.isUnsignedInt()) return new RealVal(left.intValue() / right.intValue());
        else if(left.isBool() && right.isInt()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isBool() && right.isUnsignedShort()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isBool() && right.isShort()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isBool() && right.isUnsignedLong()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isBool() && right.isLong()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isBool() && right.isRegister()) return new RealVal(left.byteValue() / right.byteValue());
		else if(left.isBool() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new RealVal(left.byteValue() / right.byteValue());
			else if(size <= 16) return new RealVal(left.shortValue() / right.shortValue());
			else if(size <= 32) return new RealVal(left.intValue() / right.intValue());
			else return new RealVal(left.longValue() / right.longValue());
		}

		else if(left.isUnsignedByteValue() && right.isReal()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isUnsignedByteValue() && right.isBool()) return new RealVal(left.byteValue() / right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new RealVal(left.byteValue() / right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByte()) return new RealVal(left.byteValue() / right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedInt()) return new RealVal(left.intValue() / right.intValue());
        else if(left.isUnsignedByteValue() && right.isInt()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShort()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShort()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLong()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedByteValue() && right.isLong()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new RealVal(left.byteValue() / right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new RealVal(left.byteValue() / right.byteValue());
			else if(size <= 16) return new RealVal(left.shortValue() / right.shortValue());
			else if(size <= 32) return new RealVal(left.intValue() / right.intValue());
			else return new RealVal(left.longValue() / right.longValue());
		}

		else if(left.isByte() && right.isReal()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isByte() && right.isBool()) return new RealVal(left.byteValue() / right.byteValue());
        else if(left.isByte() && right.isUnsignedByteValue()) return new RealVal(left.byteValue() / right.byteValue()); 
        else if(left.isByte() && right.isByte()) return new RealVal(left.byteValue() / right.byteValue());
		else if(left.isByte() && right.isUnsignedInt()) return new RealVal(left.intValue() / right.intValue());
        else if(left.isByte() && right.isInt()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isByte() && right.isUnsignedShort()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isByte() && right.isShort()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isByte() && right.isUnsignedLong()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isByte() && right.isLong()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isByte() && right.isRegister()) return new RealVal(left.byteValue() / right.byteValue());
		else if(left.isByte() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new RealVal(left.byteValue() / right.byteValue());
			else if(size <= 16) return new RealVal(left.shortValue() / right.shortValue());
			else if(size <= 32) return new RealVal(left.intValue() / right.intValue());
			else return new RealVal(left.longValue() / right.longValue());
		}

		else if(left.isUnsignedShort() && right.isReal()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isUnsignedShort() && right.isBool()) return new RealVal(left.shortValue() / right.shortValue());
        else if(left.isUnsignedShort() && right.isUnsignedByteValue()) return new RealVal(left.shortValue() / right.shortValue()); 
        else if(left.isUnsignedShort() && right.isByte()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedInt()) return new RealVal(left.intValue() / right.intValue());
        else if(left.isUnsignedShort() && right.isInt()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isUnsignedShort() && right.isUnsignedShort()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isUnsignedShort() && right.isShort()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedLong()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedShort() && right.isLong()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedShort() && right.isRegister()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isUnsignedShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new RealVal(left.shortValue() / right.shortValue());
			else if(size <= 32) return new RealVal(left.intValue() / right.intValue());
			else return new RealVal(left.longValue() / right.longValue());
		}

		else if(left.isShort() && right.isReal()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isShort() && right.isBool()) return new RealVal(left.shortValue() / right.shortValue());
        else if(left.isShort() && right.isUnsignedByteValue()) return new RealVal(left.shortValue() / right.shortValue()); 
        else if(left.isShort() && right.isByte()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isShort() && right.isUnsignedInt()) return new RealVal(left.intValue() / right.intValue());
        else if(left.isShort() && right.isInt()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isShort() && right.isUnsignedShort()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isShort() && right.isShort()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isShort() && right.isUnsignedLong()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isShort() && right.isLong()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isShort() && right.isRegister()) return new RealVal(left.byteValue() / right.byteValue());
		else if(left.isShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new RealVal(left.shortValue() / right.shortValue());
			else if(size <= 32) return new RealVal(left.intValue() / right.intValue());
			else return new RealVal(left.longValue() / right.longValue());
		}

		else if(left.isUnsignedInt() && right.isReal()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isUnsignedInt() && right.isBool()) return new RealVal(left.intValue() / right.intValue());
        else if(left.isUnsignedInt() && right.isUnsignedByteValue()) return new RealVal(left.intValue() / right.intValue()); 
        else if(left.isUnsignedInt() && right.isByte()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isUnsignedInt() && right.isUnsignedInt()) return new RealVal(left.intValue() / right.intValue());
        else if(left.isUnsignedInt() && right.isInt()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedShort()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isUnsignedInt() && right.isShort()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedLong()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedInt() && right.isLong()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedInt() && right.isRegister()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isUnsignedInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new RealVal(left.intValue() / right.intValue());
			else return new RealVal(left.longValue() / right.longValue());
		}

		else if(left.isInt() && right.isReal()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isInt() && right.isBool()) return new RealVal(left.intValue() / right.intValue());
        else if(left.isInt() && right.isUnsignedByteValue()) return new RealVal(left.intValue() / right.intValue()); 
        else if(left.isInt() && right.isByte()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isInt() && right.isUnsignedInt()) return new RealVal(left.intValue() / right.intValue());
        else if(left.isInt() && right.isInt()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isInt() && right.isUnsignedShort()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isInt() && right.isShort()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isInt() && right.isUnsignedLong()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isInt() && right.isLong()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isInt() && right.isRegister()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new RealVal(left.intValue() / right.intValue());
			else return new RealVal(left.longValue() / right.longValue());
		}

		else if(left.isUnsignedLong() && right.isReal()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isUnsignedLong() && right.isBool()) return new RealVal(left.longValue() / right.longValue());
        else if(left.isUnsignedLong() && right.isUnsignedByteValue()) return new RealVal(left.longValue() / right.longValue()); 
        else if(left.isUnsignedLong() && right.isByte()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedInt()) return new RealVal(left.longValue() / right.longValue());
        else if(left.isUnsignedLong() && right.isInt()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedShort()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedLong() && right.isShort()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedLong()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedLong() && right.isLong()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedLong() && right.isRegister()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isUnsignedLong() && right.isVector()) return new RealVal(left.longValue() / right.longValue());

		else if(left.isLong() && right.isReal()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isLong() && right.isBool()) return new RealVal(left.longValue() / right.longValue());
        else if(left.isLong() && right.isUnsignedByteValue()) return new RealVal(left.longValue() / right.longValue()); 
        else if(left.isLong() && right.isByte()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isLong() && right.isUnsignedInt()) return new RealVal(left.longValue() / right.longValue());
        else if(left.isLong() && right.isInt()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isLong() && right.isUnsignedShort()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isLong() && right.isShort()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isLong() && right.isUnsignedLong()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isLong() && right.isLong()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isLong() && right.isRegister()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isLong() && right.isVector()) return new RealVal(left.longValue() / right.longValue());

		else if(left.isRegister() && right.isReal()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isRegister() && right.isBool()) return new RealVal(left.byteValue() / right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new RealVal(left.byteValue() / right.byteValue()); 
        else if(left.isRegister() && right.isByte()) return new RealVal(left.byteValue() / right.byteValue());
		else if(left.isRegister() && right.isUnsignedInt()) return new RealVal(left.intValue() / right.intValue());
        else if(left.isRegister() && right.isInt()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isRegister() && right.isUnsignedShort()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isRegister() && right.isShort()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isRegister() && right.isUnsignedLong()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isRegister() && right.isLong()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isRegister() && right.isRegister()) return new RealVal(left.byteValue() / right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new RealVal(left.byteValue() / right.byteValue());
			else if(size <= 16) return new RealVal(left.shortValue() / right.shortValue());
			else if(size <= 32) return new RealVal(left.intValue() / right.intValue());
			else return new RealVal(left.longValue() / right.longValue());
		}

		else if(left.isVector() && right.isReal()) return new RealVal(left.realValue() / right.realValue());
		else if(left.isVector() && right.isBool()) return new RealVal(left.byteValue() / right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new RealVal(left.byteValue() / right.byteValue()); 
        else if(left.isVector() && right.isByte()) return new RealVal(left.byteValue() / right.byteValue());
		else if(left.isVector() && right.isUnsignedInt()) return new RealVal(left.intValue() / right.intValue());
        else if(left.isVector() && right.isInt()) return new RealVal(left.intValue() / right.intValue());
		else if(left.isVector() && right.isUnsignedShort()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isVector() && right.isShort()) return new RealVal(left.shortValue() / right.shortValue());
		else if(left.isVector() && right.isUnsignedLong()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isVector() && right.isLong()) return new RealVal(left.longValue() / right.longValue());
		else if(left.isVector() && right.isRegister()) return new RealVal(left.byteValue() / right.byteValue());
		else if(left.isVector() && right.isVector()) return new RealVal(left.longValue() / right.longValue());
		else {
			errorAndExit("Inavlid Addition operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}
    }

    public static Value od(Value left, Value right) throws Exception{
        if(left.isBool() && right.isBool()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
        else if(left.isBool() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() % right.byteValue()); 
        else if(left.isBool() && right.isByte()) return new ByteVal(left.byteValue() % right.byteValue());
		else if(left.isBool() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() % right.intValue());
        else if(left.isBool() && right.isInt()) return new IntVal(left.intValue() % right.intValue());
		else if(left.isBool() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isBool() && right.isShort()) return new ShortVal(left.shortValue() % right.shortValue());
		else if(left.isBool() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isBool() && right.isLong()) return new LongVal(left.longValue() % right.longValue());
		else if(left.isBool() && right.isRegister()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
		else if(left.isBool() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() % right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() % right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() % right.intValue());
			else return new UnsignedLongVal(left.longValue() % right.longValue());
		}

		if(left.isUnsignedByteValue() && right.isBool()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() % right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByte()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() % right.intValue());
        else if(left.isUnsignedByteValue() && right.isInt()) return new UnsignedIntVal(left.intValue() % right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShort()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedByteValue() && right.isLong()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() % right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() % right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() % right.intValue());
			else return new UnsignedLongVal(left.longValue() % right.longValue());
		}

		if(left.isByte() && right.isBool()) return new ByteVal(left.byteValue() % right.byteValue());
        else if(left.isByte() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() % right.byteValue()); 
        else if(left.isByte() && right.isByte()) return new ByteVal(left.byteValue() % right.byteValue());
		else if(left.isByte() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() % right.intValue());
        else if(left.isByte() && right.isInt()) return new IntVal(left.intValue() % right.intValue());
		else if(left.isByte() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isByte() && right.isShort()) return new ShortVal(left.shortValue() % right.shortValue());
		else if(left.isByte() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isByte() && right.isLong()) return new LongVal(left.longValue() % right.longValue());
		else if(left.isByte() && right.isRegister()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
		else if(left.isByte() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() % right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() % right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() % right.intValue());
			else return new UnsignedLongVal(left.longValue() % right.longValue());
		}

		else if(left.isUnsignedShort() && right.isBool()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
        else if(left.isUnsignedShort() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() % right.shortValue()); 
        else if(left.isUnsignedShort() && right.isByte()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() % right.intValue());
        else if(left.isUnsignedShort() && right.isInt()) return new UnsignedIntVal(left.intValue() % right.intValue());
		else if(left.isUnsignedShort() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isUnsignedShort() && right.isShort()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedShort() && right.isLong()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedShort() && right.isRegister()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isUnsignedShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() % right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() % right.intValue());
			else return new UnsignedLongVal(left.longValue() % right.longValue());
		}

		else if(left.isShort() && right.isBool()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
        else if(left.isShort() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() % right.shortValue()); 
        else if(left.isShort() && right.isByte()) return new ShortVal(left.shortValue() % right.shortValue());
		else if(left.isShort() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() % right.intValue());
        else if(left.isShort() && right.isInt()) return new IntVal(left.intValue() % right.intValue());
		else if(left.isShort() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isShort() && right.isShort()) return new ShortVal(left.shortValue() % right.shortValue());
		else if(left.isShort() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isShort() && right.isLong()) return new LongVal(left.longValue() % right.longValue());
		else if(left.isShort() && right.isRegister()) return new UnsignedShortVal(left.byteValue() % right.byteValue());
		else if(left.isShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() % right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() % right.intValue());
			else return new UnsignedLongVal(left.longValue() % right.longValue());
		}

		else if(left.isUnsignedInt() && right.isBool()) return new UnsignedIntVal(left.intValue() % right.intValue());
        else if(left.isUnsignedInt() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() % right.intValue()); 
        else if(left.isUnsignedInt() && right.isByte()) return new UnsignedIntVal(left.shortValue() % right.shortValue());
		else if(left.isUnsignedInt() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() % right.intValue());
        else if(left.isUnsignedInt() && right.isInt()) return new UnsignedIntVal(left.intValue() % right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedShort()) return new UnsignedIntVal(left.intValue() % right.intValue());
		else if(left.isUnsignedInt() && right.isShort()) return new UnsignedIntVal(left.intValue() % right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedInt() && right.isLong()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedInt() && right.isRegister()) return new UnsignedIntVal(left.intValue() % right.intValue());
		else if(left.isUnsignedInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() % right.intValue());
			else return new UnsignedLongVal(left.longValue() % right.longValue());
		}

		else if(left.isInt() && right.isBool()) return new UnsignedIntVal(left.intValue() % right.intValue());
        else if(left.isInt() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() % right.intValue()); 
        else if(left.isInt() && right.isByte()) return new IntVal(left.intValue() % right.intValue());
		else if(left.isInt() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() % right.intValue());
        else if(left.isInt() && right.isInt()) return new IntVal(left.intValue() % right.intValue());
		else if(left.isInt() && right.isUnsignedShort()) return new UnsignedIntVal(left.intValue() % right.intValue());
		else if(left.isInt() && right.isShort()) return new IntVal(left.intValue() % right.intValue());
		else if(left.isInt() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isInt() && right.isLong()) return new LongVal(left.longValue() % right.longValue());
		else if(left.isInt() && right.isRegister()) return new UnsignedIntVal(left.intValue() % right.intValue());
		else if(left.isInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() % right.intValue());
			else return new UnsignedLongVal(left.longValue() % right.longValue());
		}

		else if(left.isUnsignedLong() && right.isBool()) return new UnsignedLongVal(left.longValue() % right.longValue());
        else if(left.isUnsignedLong() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() % right.longValue()); 
        else if(left.isUnsignedLong() && right.isByte()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedInt()) return new UnsignedLongVal(left.longValue() % right.longValue());
        else if(left.isUnsignedLong() && right.isInt()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedShort()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedLong() && right.isShort()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedLong() && right.isLong()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedLong() && right.isRegister()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isUnsignedLong() && right.isVector()) return new UnsignedLongVal(left.longValue() % right.longValue());

		else if(left.isLong() && right.isBool()) return new UnsignedLongVal(left.longValue() % right.longValue());
        else if(left.isLong() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() % right.longValue()); 
        else if(left.isLong() && right.isByte()) return new LongVal(left.longValue() % right.longValue());
		else if(left.isLong() && right.isUnsignedInt()) return new UnsignedLongVal(left.longValue() % right.longValue());
        else if(left.isLong() && right.isInt()) return new LongVal(left.longValue() % right.longValue());
		else if(left.isLong() && right.isUnsignedShort()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isLong() && right.isShort()) return new LongVal(left.longValue() % right.longValue());
		else if(left.isLong() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isLong() && right.isLong()) return new LongVal(left.longValue() % right.longValue());
		else if(left.isLong() && right.isRegister()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isLong() && right.isVector()) return new UnsignedLongVal(left.longValue() % right.longValue());

		else if(left.isRegister() && right.isBool()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() % right.byteValue()); 
        else if(left.isRegister() && right.isByte()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
		else if(left.isRegister() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() % right.intValue());
        else if(left.isRegister() && right.isInt()) return new UnsignedIntVal(left.intValue() % right.intValue());
		else if(left.isRegister() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isRegister() && right.isShort()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isRegister() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isRegister() && right.isLong()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isRegister() && right.isRegister()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() % right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() % right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() % right.intValue());
			else return new UnsignedLongVal(left.longValue() % right.longValue());
		}

		else if(left.isVector() && right.isBool()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() % right.byteValue()); 
        else if(left.isVector() && right.isByte()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
		else if(left.isVector() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() % right.intValue());
        else if(left.isVector() && right.isInt()) return new UnsignedIntVal(left.intValue() % right.intValue());
		else if(left.isVector() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isVector() && right.isShort()) return new UnsignedShortVal(left.shortValue() % right.shortValue());
		else if(left.isVector() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isVector() && right.isLong()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else if(left.isVector() && right.isRegister()) return new UnsignedByteVal(left.byteValue() % right.byteValue());
		else if(left.isVector() && right.isVector()) return new UnsignedLongVal(left.longValue() % right.longValue());
		else {
			errorAndExit("Inavlid == operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

    public static Value azyEquality(Value left, Value right) throws Exception{
        if(left.isReal() && right.isReal()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isReal() && right.isBool()) return new BoolVal(left.realValue() == right.realValue());
        else if(left.isReal() && right.isUnsignedByteValue()) return new BoolVal(left.realValue() == right.realValue()); 
        else if(left.isReal() && right.isByte()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isReal() && right.isUnsignedInt()) return new BoolVal(left.realValue() == right.realValue());
        else if(left.isReal() && right.isInt()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isReal() && right.isUnsignedShort()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isReal() && right.isShort()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isReal() && right.isUnsignedLong()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isReal() && right.isLong()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isReal() && right.isRegister()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isReal() && right.isVector()) return new BoolVal(left.realValue() == right.realValue());
		
		else if(left.isBool() && right.isReal()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isBool() && right.isBool()) return new BoolVal(left.byteValue() == right.byteValue());
        else if(left.isBool() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() == right.byteValue()); 
        else if(left.isBool() && right.isByte()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isBool() && right.isUnsignedInt()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isBool() && right.isInt()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isBool() && right.isUnsignedShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isBool() && right.isShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isBool() && right.isUnsignedLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isBool() && right.isLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isBool() && right.isRegister()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isBool() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() == right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() == right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

		else if(left.isUnsignedByteValue() && right.isReal()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isUnsignedByteValue() && right.isBool()) return new BoolVal(left.byteValue() == right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() == right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByte()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedInt()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isUnsignedByteValue() && right.isInt()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedByteValue() && right.isLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() == right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() == right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

		else if(left.isByte() && right.isReal()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isByte() && right.isBool()) return new BoolVal(left.byteValue() == right.byteValue());
        else if(left.isByte() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() == right.byteValue()); 
        else if(left.isByte() && right.isByte()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isByte() && right.isUnsignedInt()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isByte() && right.isInt()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isByte() && right.isUnsignedShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isByte() && right.isShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isByte() && right.isUnsignedLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isByte() && right.isLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isByte() && right.isRegister()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isByte() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() == right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() == right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

		else if(left.isUnsignedShort() && right.isReal()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isUnsignedShort() && right.isBool()) return new BoolVal(left.shortValue() == right.shortValue());
        else if(left.isUnsignedShort() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() == right.shortValue()); 
        else if(left.isUnsignedShort() && right.isByte()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedInt()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isUnsignedShort() && right.isInt()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedShort() && right.isUnsignedShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedShort() && right.isShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedShort() && right.isLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedShort() && right.isRegister()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() == right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

		else if(left.isShort() && right.isReal()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isShort() && right.isBool()) return new BoolVal(left.shortValue() == right.shortValue());
        else if(left.isShort() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() == right.shortValue()); 
        else if(left.isShort() && right.isByte()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isShort() && right.isUnsignedInt()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isShort() && right.isInt()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isShort() && right.isUnsignedShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isShort() && right.isShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isShort() && right.isUnsignedLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isShort() && right.isLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isShort() && right.isRegister()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() == right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

		else if(left.isUnsignedInt() && right.isReal()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isUnsignedInt() && right.isBool()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isUnsignedInt() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() == right.intValue()); 
        else if(left.isUnsignedInt() && right.isByte()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedInt() && right.isUnsignedInt()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isUnsignedInt() && right.isInt()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedShort()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedInt() && right.isShort()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedInt() && right.isLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedInt() && right.isRegister()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

		else if(left.isInt() && right.isReal()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isInt() && right.isBool()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isInt() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() == right.intValue()); 
        else if(left.isInt() && right.isByte()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isInt() && right.isUnsignedInt()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isInt() && right.isInt()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isInt() && right.isUnsignedShort()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isInt() && right.isShort()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isInt() && right.isUnsignedLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isInt() && right.isLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isInt() && right.isRegister()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

		else if(left.isUnsignedLong() && right.isReal()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isUnsignedLong() && right.isBool()) return new BoolVal(left.longValue() == right.longValue());
        else if(left.isUnsignedLong() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() == right.longValue()); 
        else if(left.isUnsignedLong() && right.isByte()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedInt()) return new BoolVal(left.longValue() == right.longValue());
        else if(left.isUnsignedLong() && right.isInt()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedShort()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLong() && right.isShort()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLong() && right.isLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLong() && right.isRegister()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLong() && right.isVector()) return new BoolVal(left.longValue() == right.longValue());

		else if(left.isLong() && right.isReal()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isLong() && right.isBool()) return new BoolVal(left.longValue() == right.longValue());
        else if(left.isLong() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() == right.longValue()); 
        else if(left.isLong() && right.isByte()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLong() && right.isUnsignedInt()) return new BoolVal(left.longValue() == right.longValue());
        else if(left.isLong() && right.isInt()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLong() && right.isUnsignedShort()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLong() && right.isShort()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLong() && right.isUnsignedLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLong() && right.isLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLong() && right.isRegister()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLong() && right.isVector()) return new BoolVal(left.longValue() == right.longValue());

		else if(left.isRegister() && right.isReal()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isRegister() && right.isBool()) return new BoolVal(left.byteValue() == right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() == right.byteValue()); 
        else if(left.isRegister() && right.isByte()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isRegister() && right.isUnsignedInt()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isRegister() && right.isInt()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isRegister() && right.isUnsignedShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isRegister() && right.isShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isRegister() && right.isUnsignedLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isRegister() && right.isLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isRegister() && right.isRegister()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() == right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() == right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

		else if(left.isVector() && right.isReal()) return new BoolVal(left.realValue() == right.realValue());
		else if(left.isVector() && right.isBool()) return new BoolVal(left.byteValue() == right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() == right.byteValue()); 
        else if(left.isVector() && right.isByte()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isVector() && right.isUnsignedInt()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isVector() && right.isInt()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isVector() && right.isUnsignedShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isVector() && right.isShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isVector() && right.isUnsignedLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isVector() && right.isLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isVector() && right.isRegister()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isVector() && right.isVector()) return new BoolVal(left.longValue() == right.longValue());
		else {
			errorAndExit("Inavlid Addition operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

    public static Value trictEquality(Value left, Value right) throws Exception{
        if(left.isReal() && right.isReal()) return new BoolVal(left.realValue() == right.realValue());
		
		else if(left.isBool() && right.isBool()) return new BoolVal(left.byteValue() == right.byteValue());

        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() == right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByte()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedInt()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isUnsignedByteValue() && right.isInt()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedByteValue() && right.isLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() == right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() == right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

        else if(left.isByte() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() == right.byteValue()); 
        else if(left.isByte() && right.isByte()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isByte() && right.isUnsignedInt()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isByte() && right.isInt()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isByte() && right.isUnsignedShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isByte() && right.isShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isByte() && right.isUnsignedLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isByte() && right.isLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isByte() && right.isRegister()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isByte() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() == right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() == right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

        else if(left.isUnsignedShort() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() == right.shortValue()); 
        else if(left.isUnsignedShort() && right.isByte()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedInt()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isUnsignedShort() && right.isInt()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedShort() && right.isUnsignedShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedShort() && right.isShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedShort() && right.isLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedShort() && right.isRegister()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() == right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

        else if(left.isShort() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() == right.shortValue()); 
        else if(left.isShort() && right.isByte()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isShort() && right.isUnsignedInt()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isShort() && right.isInt()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isShort() && right.isUnsignedShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isShort() && right.isShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isShort() && right.isUnsignedLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isShort() && right.isLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isShort() && right.isRegister()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() == right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

        else if(left.isUnsignedInt() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() == right.intValue()); 
        else if(left.isUnsignedInt() && right.isByte()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isUnsignedInt() && right.isUnsignedInt()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isUnsignedInt() && right.isInt()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedShort()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedInt() && right.isShort()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedInt() && right.isLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedInt() && right.isRegister()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isUnsignedInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

        else if(left.isInt() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() == right.intValue()); 
        else if(left.isInt() && right.isByte()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isInt() && right.isUnsignedInt()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isInt() && right.isInt()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isInt() && right.isUnsignedShort()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isInt() && right.isShort()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isInt() && right.isUnsignedLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isInt() && right.isLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isInt() && right.isRegister()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

        else if(left.isUnsignedLong() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() == right.longValue()); 
        else if(left.isUnsignedLong() && right.isByte()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedInt()) return new BoolVal(left.longValue() == right.longValue());
        else if(left.isUnsignedLong() && right.isInt()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedShort()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLong() && right.isShort()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLong() && right.isLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLong() && right.isRegister()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isUnsignedLong() && right.isVector()) return new BoolVal(left.longValue() == right.longValue());

        else if(left.isLong() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() == right.longValue()); 
        else if(left.isLong() && right.isByte()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLong() && right.isUnsignedInt()) return new BoolVal(left.longValue() == right.longValue());
        else if(left.isLong() && right.isInt()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLong() && right.isUnsignedShort()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLong() && right.isShort()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLong() && right.isUnsignedLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLong() && right.isLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLong() && right.isRegister()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isLong() && right.isVector()) return new BoolVal(left.longValue() == right.longValue());

        else if(left.isRegister() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() == right.byteValue()); 
        else if(left.isRegister() && right.isByte()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isRegister() && right.isUnsignedInt()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isRegister() && right.isInt()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isRegister() && right.isUnsignedShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isRegister() && right.isShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isRegister() && right.isUnsignedLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isRegister() && right.isLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isRegister() && right.isRegister()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() == right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() == right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() == right.intValue());
			else return new BoolVal(left.longValue() == right.longValue());
		}

        else if(left.isVector() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() == right.byteValue()); 
        else if(left.isVector() && right.isByte()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isVector() && right.isUnsignedInt()) return new BoolVal(left.intValue() == right.intValue());
        else if(left.isVector() && right.isInt()) return new BoolVal(left.intValue() == right.intValue());
		else if(left.isVector() && right.isUnsignedShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isVector() && right.isShort()) return new BoolVal(left.shortValue() == right.shortValue());
		else if(left.isVector() && right.isUnsignedLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isVector() && right.isLong()) return new BoolVal(left.longValue() == right.longValue());
		else if(left.isVector() && right.isRegister()) return new BoolVal(left.byteValue() == right.byteValue());
		else if(left.isVector() && right.isVector()) return new BoolVal(left.longValue() == right.longValue());
		else {
			errorAndExit("Inavlid === operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

	public static Value azyInequality(Value left, Value right) throws Exception{
        if(left.isReal() && right.isReal()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isReal() && right.isBool()) return new BoolVal(left.realValue() != right.realValue());
        else if(left.isReal() && right.isUnsignedByteValue()) return new BoolVal(left.realValue() != right.realValue()); 
        else if(left.isReal() && right.isByte()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isReal() && right.isUnsignedInt()) return new BoolVal(left.realValue() != right.realValue());
        else if(left.isReal() && right.isInt()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isReal() && right.isUnsignedShort()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isReal() && right.isShort()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isReal() && right.isUnsignedLong()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isReal() && right.isLong()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isReal() && right.isRegister()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isReal() && right.isVector()) return new BoolVal(left.realValue() != right.realValue());
		
		else if(left.isBool() && right.isReal()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isBool() && right.isBool()) return new BoolVal(left.byteValue() != right.byteValue());
        else if(left.isBool() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() != right.byteValue()); 
        else if(left.isBool() && right.isByte()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isBool() && right.isUnsignedInt()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isBool() && right.isInt()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isBool() && right.isUnsignedShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isBool() && right.isShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isBool() && right.isUnsignedLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isBool() && right.isLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isBool() && right.isRegister()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isBool() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() != right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() != right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

		else if(left.isUnsignedByteValue() && right.isReal()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isUnsignedByteValue() && right.isBool()) return new BoolVal(left.byteValue() != right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() != right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByte()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedInt()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isUnsignedByteValue() && right.isInt()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedByteValue() && right.isLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() != right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() != right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

		else if(left.isByte() && right.isReal()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isByte() && right.isBool()) return new BoolVal(left.byteValue() != right.byteValue());
        else if(left.isByte() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() != right.byteValue()); 
        else if(left.isByte() && right.isByte()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isByte() && right.isUnsignedInt()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isByte() && right.isInt()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isByte() && right.isUnsignedShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isByte() && right.isShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isByte() && right.isUnsignedLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isByte() && right.isLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isByte() && right.isRegister()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isByte() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() != right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() != right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

		else if(left.isUnsignedShort() && right.isReal()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isUnsignedShort() && right.isBool()) return new BoolVal(left.shortValue() != right.shortValue());
        else if(left.isUnsignedShort() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() != right.shortValue()); 
        else if(left.isUnsignedShort() && right.isByte()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedInt()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isUnsignedShort() && right.isInt()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedShort() && right.isUnsignedShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedShort() && right.isShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedShort() && right.isLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedShort() && right.isRegister()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() != right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

		else if(left.isShort() && right.isReal()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isShort() && right.isBool()) return new BoolVal(left.shortValue() != right.shortValue());
        else if(left.isShort() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() != right.shortValue()); 
        else if(left.isShort() && right.isByte()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isShort() && right.isUnsignedInt()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isShort() && right.isInt()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isShort() && right.isUnsignedShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isShort() && right.isShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isShort() && right.isUnsignedLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isShort() && right.isLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isShort() && right.isRegister()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() != right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

		else if(left.isUnsignedInt() && right.isReal()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isUnsignedInt() && right.isBool()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isUnsignedInt() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() != right.intValue()); 
        else if(left.isUnsignedInt() && right.isByte()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedInt() && right.isUnsignedInt()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isUnsignedInt() && right.isInt()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedShort()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedInt() && right.isShort()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedInt() && right.isLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedInt() && right.isRegister()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

		else if(left.isInt() && right.isReal()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isInt() && right.isBool()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isInt() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() != right.intValue()); 
        else if(left.isInt() && right.isByte()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isInt() && right.isUnsignedInt()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isInt() && right.isInt()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isInt() && right.isUnsignedShort()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isInt() && right.isShort()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isInt() && right.isUnsignedLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isInt() && right.isLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isInt() && right.isRegister()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

		else if(left.isUnsignedLong() && right.isReal()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isUnsignedLong() && right.isBool()) return new BoolVal(left.longValue() != right.longValue());
        else if(left.isUnsignedLong() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() != right.longValue()); 
        else if(left.isUnsignedLong() && right.isByte()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedInt()) return new BoolVal(left.longValue() != right.longValue());
        else if(left.isUnsignedLong() && right.isInt()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedShort()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLong() && right.isShort()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLong() && right.isLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLong() && right.isRegister()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLong() && right.isVector()) return new BoolVal(left.longValue() != right.longValue());

		else if(left.isLong() && right.isReal()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isLong() && right.isBool()) return new BoolVal(left.longValue() != right.longValue());
        else if(left.isLong() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() != right.longValue()); 
        else if(left.isLong() && right.isByte()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLong() && right.isUnsignedInt()) return new BoolVal(left.longValue() != right.longValue());
        else if(left.isLong() && right.isInt()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLong() && right.isUnsignedShort()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLong() && right.isShort()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLong() && right.isUnsignedLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLong() && right.isLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLong() && right.isRegister()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLong() && right.isVector()) return new BoolVal(left.longValue() != right.longValue());

		else if(left.isRegister() && right.isReal()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isRegister() && right.isBool()) return new BoolVal(left.byteValue() != right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() != right.byteValue()); 
        else if(left.isRegister() && right.isByte()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isRegister() && right.isUnsignedInt()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isRegister() && right.isInt()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isRegister() && right.isUnsignedShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isRegister() && right.isShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isRegister() && right.isUnsignedLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isRegister() && right.isLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isRegister() && right.isRegister()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() != right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() != right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

		else if(left.isVector() && right.isReal()) return new BoolVal(left.realValue() != right.realValue());
		else if(left.isVector() && right.isBool()) return new BoolVal(left.byteValue() != right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() != right.byteValue()); 
        else if(left.isVector() && right.isByte()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isVector() && right.isUnsignedInt()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isVector() && right.isInt()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isVector() && right.isUnsignedShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isVector() && right.isShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isVector() && right.isUnsignedLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isVector() && right.isLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isVector() && right.isRegister()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isVector() && right.isVector()) return new BoolVal(left.longValue() != right.longValue());
		else {
			errorAndExit("Inavlid Addition operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

    public static Value trictInequality(Value left, Value right) throws Exception{
        if(left.isReal() && right.isReal()) return new BoolVal(left.realValue() != right.realValue());
		
		else if(left.isBool() && right.isBool()) return new BoolVal(left.byteValue() != right.byteValue());

        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() != right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByte()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedInt()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isUnsignedByteValue() && right.isInt()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedByteValue() && right.isLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() != right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() != right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

        else if(left.isByte() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() != right.byteValue()); 
        else if(left.isByte() && right.isByte()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isByte() && right.isUnsignedInt()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isByte() && right.isInt()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isByte() && right.isUnsignedShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isByte() && right.isShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isByte() && right.isUnsignedLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isByte() && right.isLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isByte() && right.isRegister()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isByte() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() != right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() != right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

        else if(left.isUnsignedShort() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() != right.shortValue()); 
        else if(left.isUnsignedShort() && right.isByte()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedInt()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isUnsignedShort() && right.isInt()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedShort() && right.isUnsignedShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedShort() && right.isShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedShort() && right.isLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedShort() && right.isRegister()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() != right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

        else if(left.isShort() && right.isUnsignedByteValue()) return new BoolVal(left.shortValue() != right.shortValue()); 
        else if(left.isShort() && right.isByte()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isShort() && right.isUnsignedInt()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isShort() && right.isInt()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isShort() && right.isUnsignedShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isShort() && right.isShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isShort() && right.isUnsignedLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isShort() && right.isLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isShort() && right.isRegister()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(left.shortValue() != right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

        else if(left.isUnsignedInt() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() != right.intValue()); 
        else if(left.isUnsignedInt() && right.isByte()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isUnsignedInt() && right.isUnsignedInt()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isUnsignedInt() && right.isInt()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedShort()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedInt() && right.isShort()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedInt() && right.isLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedInt() && right.isRegister()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isUnsignedInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

        else if(left.isInt() && right.isUnsignedByteValue()) return new BoolVal(left.intValue() != right.intValue()); 
        else if(left.isInt() && right.isByte()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isInt() && right.isUnsignedInt()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isInt() && right.isInt()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isInt() && right.isUnsignedShort()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isInt() && right.isShort()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isInt() && right.isUnsignedLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isInt() && right.isLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isInt() && right.isRegister()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

        else if(left.isUnsignedLong() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() != right.longValue()); 
        else if(left.isUnsignedLong() && right.isByte()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedInt()) return new BoolVal(left.longValue() != right.longValue());
        else if(left.isUnsignedLong() && right.isInt()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedShort()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLong() && right.isShort()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLong() && right.isLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLong() && right.isRegister()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isUnsignedLong() && right.isVector()) return new BoolVal(left.longValue() != right.longValue());

        else if(left.isLong() && right.isUnsignedByteValue()) return new BoolVal(left.longValue() != right.longValue()); 
        else if(left.isLong() && right.isByte()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLong() && right.isUnsignedInt()) return new BoolVal(left.longValue() != right.longValue());
        else if(left.isLong() && right.isInt()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLong() && right.isUnsignedShort()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLong() && right.isShort()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLong() && right.isUnsignedLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLong() && right.isLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLong() && right.isRegister()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isLong() && right.isVector()) return new BoolVal(left.longValue() != right.longValue());

        else if(left.isRegister() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() != right.byteValue()); 
        else if(left.isRegister() && right.isByte()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isRegister() && right.isUnsignedInt()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isRegister() && right.isInt()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isRegister() && right.isUnsignedShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isRegister() && right.isShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isRegister() && right.isUnsignedLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isRegister() && right.isLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isRegister() && right.isRegister()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(left.byteValue() != right.byteValue());
			else if(size <= 16) return new BoolVal(left.shortValue() != right.shortValue());
			else if(size <= 32) return new BoolVal(left.intValue() != right.intValue());
			else return new BoolVal(left.longValue() != right.longValue());
		}

        else if(left.isVector() && right.isUnsignedByteValue()) return new BoolVal(left.byteValue() != right.byteValue()); 
        else if(left.isVector() && right.isByte()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isVector() && right.isUnsignedInt()) return new BoolVal(left.intValue() != right.intValue());
        else if(left.isVector() && right.isInt()) return new BoolVal(left.intValue() != right.intValue());
		else if(left.isVector() && right.isUnsignedShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isVector() && right.isShort()) return new BoolVal(left.shortValue() != right.shortValue());
		else if(left.isVector() && right.isUnsignedLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isVector() && right.isLong()) return new BoolVal(left.longValue() != right.longValue());
		else if(left.isVector() && right.isRegister()) return new BoolVal(left.byteValue() != right.byteValue());
		else if(left.isVector() && right.isVector()) return new BoolVal(left.longValue() != right.longValue());
		else {
			errorAndExit("Inavlid === operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

    public static Value ogicalAnd(Value left, Value right){
        return new BoolVal(left.boolValue() && right.boolValue());
    }

    public static Value ogicalOr(Value left, Value right){
        return new BoolVal(left.boolValue() || right.boolValue());
    }

    public static Value essThanOrEqualTo(Value left, Value right) throws Exception{
        if(left.isReal() && right.isReal()) return new BoolVal(left.realValue() <= right.realValue());
        else if(left.isReal() && right.isUnsignedByteValue()) return new BoolVal(left.realValue() <= right.realValue()); 
        else if(left.isReal() && right.isByte()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isReal() && right.isUnsignedInt()) return new BoolVal(left.realValue() <= right.realValue());
        else if(left.isReal() && right.isInt()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isReal() && right.isUnsignedShort()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isReal() && right.isShort()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isReal() && right.isUnsignedLong()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isReal() && right.isLong()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isReal() && right.isRegister()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isReal() && right.isVector()) return new BoolVal(left.realValue() <= right.realValue());

		else if(left.isUnsignedByteValue() && right.isReal()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new BoolVal(Byte.compareUnsigned(left.byteValue(), right.byteValue()) <= 0); 
        else if(left.isUnsignedByteValue() && right.isUnsignedInt()) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) <= 0);
        else if(left.isUnsignedByteValue() && right.isUnsignedShort()) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) <= 0);
		else if(left.isUnsignedByteValue() && right.isUnsignedLong()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) <= 0);
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(Byte.compareUnsigned(left.byteValue(), right.byteValue()) <= 0);
			else if(size <= 16) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) <= 0);
			else if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) <= 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) <= 0);
		}

		else if(left.isByte() && right.isReal()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isByte() && right.isByte()) return new BoolVal(left.byteValue() <= right.byteValue());
		else if(left.isByte() && right.isInt()) return new BoolVal(left.intValue() <= right.intValue());
		else if(left.isByte() && right.isShort()) return new BoolVal(left.shortValue() <= right.shortValue());
		else if(left.isByte() && right.isLong()) return new BoolVal(left.longValue() <= right.longValue());

		else if(left.isUnsignedShort() && right.isReal()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isUnsignedShort() && right.isUnsignedByteValue()) return new BoolVal(Short.compareUnsigned(left.shortValue(),  right.shortValue()) <= 0); 
        else if(left.isUnsignedShort() && right.isUnsignedInt()) return new BoolVal(Integer.compareUnsigned(left.intValue() , right.intValue()) <= 0);
        else if(left.isUnsignedShort() && right.isUnsignedShort()) return new BoolVal(Short.compareUnsigned(left.shortValue() , right.shortValue()) <= 0);
		else if(left.isUnsignedShort() && right.isUnsignedLong()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) <= 0);
		else if(left.isUnsignedShort() && right.isRegister()) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) <= 0);
		else if(left.isUnsignedShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) <= 0);
			else if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) <= 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) <= 0);
		}

		else if(left.isShort() && right.isReal()) return new BoolVal(left.realValue() <= right.realValue());
        else if(left.isShort() && right.isByte()) return new BoolVal(left.shortValue() <= right.shortValue());
        else if(left.isShort() && right.isInt()) return new BoolVal(left.intValue() <= right.intValue());
		else if(left.isShort() && right.isShort()) return new BoolVal(left.shortValue() <= right.shortValue());
		else if(left.isShort() && right.isLong()) return new BoolVal(left.longValue() <= right.longValue());

		else if(left.isUnsignedInt() && right.isReal()) return new BoolVal(left.realValue() <= right.realValue());
        else if(left.isUnsignedInt() && right.isUnsignedByteValue()) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) <= 0); 
        else if(left.isUnsignedInt() && right.isUnsignedInt()) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) <= 0);
        else if(left.isUnsignedInt() && right.isUnsignedShort()) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) <= 0);
		else if(left.isUnsignedInt() && right.isUnsignedLong()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) <= 0);
		else if(left.isUnsignedInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) <= 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) <= 0);
		}

		else if(left.isInt() && right.isReal()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isInt() && right.isByte()) return new BoolVal(left.intValue() <= right.intValue());
		else if(left.isInt() && right.isInt()) return new BoolVal(left.intValue() <= right.intValue());
		else if(left.isInt() && right.isShort()) return new BoolVal(left.intValue() <= right.intValue());
		else if(left.isInt() && right.isLong()) return new BoolVal(left.longValue() <= right.longValue());

		else if(left.isUnsignedLong() && right.isReal()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isUnsignedLong() && right.isUnsignedByteValue()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) <= 0); 
        else if(left.isUnsignedLong() && right.isUnsignedInt()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) <= 0);
        else if(left.isUnsignedLong() && right.isUnsignedShort()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) <= 0);
		else if(left.isUnsignedLong() && right.isUnsignedLong()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) <= 0);
		else if(left.isUnsignedLong() && right.isVector()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) <= 0);

		else if(left.isLong() && right.isReal()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isLong() && right.isByte()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isLong() && right.isInt()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isLong() && right.isShort()) return new BoolVal(left.longValue() <= right.longValue());
		else if(left.isLong() && right.isLong()) return new BoolVal(left.longValue() <= right.longValue());
		
		else if(left.isVector() && right.isReal()) return new BoolVal(left.realValue() <= right.realValue());
		else if(left.isVector() && right.isUnsignedByteValue()){
			VectorVal vec = (VectorVal)left;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(Byte.compareUnsigned(left.byteValue(), right.byteValue()) <= 0);
			else if(size <= 16) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) <= 0);
			else if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) <= 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) <= 0);	
		}
        else if(left.isVector() && right.isUnsignedInt()){
			VectorVal vec = (VectorVal)left;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) <= 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) <= 0);	
		}
        else if(left.isVector() && right.isUnsignedShort()){
			VectorVal vec = (VectorVal)left;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) <= 0);
			else if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) <= 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) <= 0);	
		}
		else if(left.isVector() && right.isUnsignedLong()){
			return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) <= 0);	
		}
		else if(left.isVector() && right.isVector()) {
			return new BoolVal(left.longValue() <= right.longValue());
		} else {
			errorAndExit("Inavlid Addition operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}
    }

    public static Value essThan(Value left, Value right) throws Exception{
        if(left.isReal() && right.isReal()) return new BoolVal(left.realValue() < right.realValue());
        else if(left.isReal() && right.isUnsignedByteValue()) return new BoolVal(left.realValue() < right.realValue()); 
        else if(left.isReal() && right.isByte()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isReal() && right.isUnsignedInt()) return new BoolVal(left.realValue() < right.realValue());
        else if(left.isReal() && right.isInt()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isReal() && right.isUnsignedShort()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isReal() && right.isShort()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isReal() && right.isUnsignedLong()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isReal() && right.isLong()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isReal() && right.isRegister()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isReal() && right.isVector()) return new BoolVal(left.realValue() < right.realValue());

		else if(left.isUnsignedByteValue() && right.isReal()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new BoolVal(Byte.compareUnsigned(left.byteValue(), right.byteValue()) < 0); 
        else if(left.isUnsignedByteValue() && right.isUnsignedInt()) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) < 0);
        else if(left.isUnsignedByteValue() && right.isUnsignedShort()) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) < 0);
		else if(left.isUnsignedByteValue() && right.isUnsignedLong()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) < 0);
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(Byte.compareUnsigned(left.byteValue(), right.byteValue()) < 0);
			else if(size <= 16) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) < 0);
			else if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) < 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) < 0);
		}

		else if(left.isByte() && right.isReal()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isByte() && right.isByte()) return new BoolVal(left.byteValue() < right.byteValue());
		else if(left.isByte() && right.isInt()) return new BoolVal(left.intValue() < right.intValue());
		else if(left.isByte() && right.isShort()) return new BoolVal(left.shortValue() < right.shortValue());
		else if(left.isByte() && right.isLong()) return new BoolVal(left.longValue() < right.longValue());

		else if(left.isUnsignedShort() && right.isReal()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isUnsignedShort() && right.isUnsignedByteValue()) return new BoolVal(Short.compareUnsigned(left.shortValue(),  right.shortValue()) < 0); 
        else if(left.isUnsignedShort() && right.isUnsignedInt()) return new BoolVal(Integer.compareUnsigned(left.intValue() , right.intValue()) < 0);
        else if(left.isUnsignedShort() && right.isUnsignedShort()) return new BoolVal(Short.compareUnsigned(left.shortValue() , right.shortValue()) < 0);
		else if(left.isUnsignedShort() && right.isUnsignedLong()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) < 0);
		else if(left.isUnsignedShort() && right.isRegister()) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) < 0);
		else if(left.isUnsignedShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) < 0);
			else if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) < 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) < 0);
		}

		else if(left.isShort() && right.isReal()) return new BoolVal(left.realValue() < right.realValue());
        else if(left.isShort() && right.isByte()) return new BoolVal(left.shortValue() < right.shortValue());
        else if(left.isShort() && right.isInt()) return new BoolVal(left.intValue() < right.intValue());
		else if(left.isShort() && right.isShort()) return new BoolVal(left.shortValue() < right.shortValue());
		else if(left.isShort() && right.isLong()) return new BoolVal(left.longValue() < right.longValue());

		else if(left.isUnsignedInt() && right.isReal()) return new BoolVal(left.realValue() < right.realValue());
        else if(left.isUnsignedInt() && right.isUnsignedByteValue()) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) < 0); 
        else if(left.isUnsignedInt() && right.isUnsignedInt()) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) < 0);
        else if(left.isUnsignedInt() && right.isUnsignedShort()) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) < 0);
		else if(left.isUnsignedInt() && right.isUnsignedLong()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) < 0);
		else if(left.isUnsignedInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) < 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) < 0);
		}

		else if(left.isInt() && right.isReal()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isInt() && right.isByte()) return new BoolVal(left.intValue() < right.intValue());
		else if(left.isInt() && right.isInt()) return new BoolVal(left.intValue() < right.intValue());
		else if(left.isInt() && right.isShort()) return new BoolVal(left.intValue() < right.intValue());
		else if(left.isInt() && right.isLong()) return new BoolVal(left.longValue() < right.longValue());

		else if(left.isUnsignedLong() && right.isReal()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isUnsignedLong() && right.isUnsignedByteValue()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) < 0); 
        else if(left.isUnsignedLong() && right.isUnsignedInt()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) < 0);
        else if(left.isUnsignedLong() && right.isUnsignedShort()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) < 0);
		else if(left.isUnsignedLong() && right.isUnsignedLong()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) < 0);
		else if(left.isUnsignedLong() && right.isVector()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) < 0);

		else if(left.isLong() && right.isReal()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isLong() && right.isByte()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isLong() && right.isInt()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isLong() && right.isShort()) return new BoolVal(left.longValue() < right.longValue());
		else if(left.isLong() && right.isLong()) return new BoolVal(left.longValue() < right.longValue());
		
		else if(left.isVector() && right.isReal()) return new BoolVal(left.realValue() < right.realValue());
		else if(left.isVector() && right.isUnsignedByteValue()){
			VectorVal vec = (VectorVal)left;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(Byte.compareUnsigned(left.byteValue(), right.byteValue()) < 0);
			else if(size <= 16) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) < 0);
			else if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) < 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) < 0);	
		}
        else if(left.isVector() && right.isUnsignedInt()){
			VectorVal vec = (VectorVal)left;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) < 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) < 0);	
		}
        else if(left.isVector() && right.isUnsignedShort()){
			VectorVal vec = (VectorVal)left;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) < 0);
			else if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) < 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) < 0);	
		}
		else if(left.isVector() && right.isUnsignedLong()){
			return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) < 0);	
		}
		else if(left.isVector() && right.isVector()) {
			return new BoolVal(left.longValue() < right.longValue());
		} else {
			errorAndExit("Inavlid Addition operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}
    }

    public static Value reaterThanOrEqualTo(Value left, Value right) throws Exception{
        if(left.isReal() && right.isReal()) return new BoolVal(left.realValue() >= right.realValue());
        else if(left.isReal() && right.isUnsignedByteValue()) return new BoolVal(left.realValue() >= right.realValue()); 
        else if(left.isReal() && right.isByte()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isReal() && right.isUnsignedInt()) return new BoolVal(left.realValue() >= right.realValue());
        else if(left.isReal() && right.isInt()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isReal() && right.isUnsignedShort()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isReal() && right.isShort()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isReal() && right.isUnsignedLong()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isReal() && right.isLong()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isReal() && right.isRegister()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isReal() && right.isVector()) return new BoolVal(left.realValue() >= right.realValue());

		else if(left.isUnsignedByteValue() && right.isReal()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new BoolVal(Byte.compareUnsigned(left.byteValue(), right.byteValue()) >= 0); 
        else if(left.isUnsignedByteValue() && right.isUnsignedInt()) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) >= 0);
        else if(left.isUnsignedByteValue() && right.isUnsignedShort()) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) >= 0);
		else if(left.isUnsignedByteValue() && right.isUnsignedLong()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) >= 0);
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(Byte.compareUnsigned(left.byteValue(), right.byteValue()) >= 0);
			else if(size <= 16) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) >= 0);
			else if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) >= 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) >= 0);
		}

		else if(left.isByte() && right.isReal()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isByte() && right.isByte()) return new BoolVal(left.byteValue() >= right.byteValue());
		else if(left.isByte() && right.isInt()) return new BoolVal(left.intValue() >= right.intValue());
		else if(left.isByte() && right.isShort()) return new BoolVal(left.shortValue() >= right.shortValue());
		else if(left.isByte() && right.isLong()) return new BoolVal(left.longValue() >= right.longValue());

		else if(left.isUnsignedShort() && right.isReal()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isUnsignedShort() && right.isUnsignedByteValue()) return new BoolVal(Short.compareUnsigned(left.shortValue(),  right.shortValue()) >= 0); 
        else if(left.isUnsignedShort() && right.isUnsignedInt()) return new BoolVal(Integer.compareUnsigned(left.intValue() , right.intValue()) >= 0);
        else if(left.isUnsignedShort() && right.isUnsignedShort()) return new BoolVal(Short.compareUnsigned(left.shortValue() , right.shortValue()) >= 0);
		else if(left.isUnsignedShort() && right.isUnsignedLong()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) >= 0);
		else if(left.isUnsignedShort() && right.isRegister()) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) >= 0);
		else if(left.isUnsignedShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) >= 0);
			else if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) >= 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) >= 0);
		}

		else if(left.isShort() && right.isReal()) return new BoolVal(left.realValue() >= right.realValue());
        else if(left.isShort() && right.isByte()) return new BoolVal(left.shortValue() >= right.shortValue());
        else if(left.isShort() && right.isInt()) return new BoolVal(left.intValue() >= right.intValue());
		else if(left.isShort() && right.isShort()) return new BoolVal(left.shortValue() >= right.shortValue());
		else if(left.isShort() && right.isLong()) return new BoolVal(left.longValue() >= right.longValue());

		else if(left.isUnsignedInt() && right.isReal()) return new BoolVal(left.realValue() >= right.realValue());
        else if(left.isUnsignedInt() && right.isUnsignedByteValue()) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) >= 0); 
        else if(left.isUnsignedInt() && right.isUnsignedInt()) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) >= 0);
        else if(left.isUnsignedInt() && right.isUnsignedShort()) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) >= 0);
		else if(left.isUnsignedInt() && right.isUnsignedLong()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) >= 0);
		else if(left.isUnsignedInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) >= 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) >= 0);
		}

		else if(left.isInt() && right.isReal()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isInt() && right.isByte()) return new BoolVal(left.intValue() >= right.intValue());
		else if(left.isInt() && right.isInt()) return new BoolVal(left.intValue() >= right.intValue());
		else if(left.isInt() && right.isShort()) return new BoolVal(left.intValue() >= right.intValue());
		else if(left.isInt() && right.isLong()) return new BoolVal(left.longValue() >= right.longValue());

		else if(left.isUnsignedLong() && right.isReal()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isUnsignedLong() && right.isUnsignedByteValue()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) >= 0); 
        else if(left.isUnsignedLong() && right.isUnsignedInt()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) >= 0);
        else if(left.isUnsignedLong() && right.isUnsignedShort()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) >= 0);
		else if(left.isUnsignedLong() && right.isUnsignedLong()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) >= 0);
		else if(left.isUnsignedLong() && right.isVector()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) >= 0);

		else if(left.isLong() && right.isReal()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isLong() && right.isByte()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isLong() && right.isInt()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isLong() && right.isShort()) return new BoolVal(left.longValue() >= right.longValue());
		else if(left.isLong() && right.isLong()) return new BoolVal(left.longValue() >= right.longValue());
		
		else if(left.isVector() && right.isReal()) return new BoolVal(left.realValue() >= right.realValue());
		else if(left.isVector() && right.isUnsignedByteValue()){
			VectorVal vec = (VectorVal)left;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(Byte.compareUnsigned(left.byteValue(), right.byteValue()) >= 0);
			else if(size <= 16) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) >= 0);
			else if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) >= 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) >= 0);	
		}
        else if(left.isVector() && right.isUnsignedInt()){
			VectorVal vec = (VectorVal)left;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) >= 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) >= 0);	
		}
        else if(left.isVector() && right.isUnsignedShort()){
			VectorVal vec = (VectorVal)left;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) >= 0);
			else if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) >= 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) >= 0);	
		}
		else if(left.isVector() && right.isUnsignedLong()){
			return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) >= 0);	
		}
		else if(left.isVector() && right.isVector()) {
			return new BoolVal(left.longValue() >= right.longValue());
		} else {
			errorAndExit("Inavlid Addition operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}
    }
    public static Value reaterThan(Value left, Value right) throws Exception{
        if(left.isReal() && right.isReal()) return new BoolVal(left.realValue() > right.realValue());
        else if(left.isReal() && right.isUnsignedByteValue()) return new BoolVal(left.realValue() > right.realValue()); 
        else if(left.isReal() && right.isByte()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isReal() && right.isUnsignedInt()) return new BoolVal(left.realValue() > right.realValue());
        else if(left.isReal() && right.isInt()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isReal() && right.isUnsignedShort()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isReal() && right.isShort()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isReal() && right.isUnsignedLong()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isReal() && right.isLong()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isReal() && right.isRegister()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isReal() && right.isVector()) return new BoolVal(left.realValue() > right.realValue());

		else if(left.isUnsignedByteValue() && right.isReal()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new BoolVal(Byte.compareUnsigned(left.byteValue(), right.byteValue()) > 0); 
        else if(left.isUnsignedByteValue() && right.isUnsignedInt()) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) > 0);
        else if(left.isUnsignedByteValue() && right.isUnsignedShort()) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) > 0);
		else if(left.isUnsignedByteValue() && right.isUnsignedLong()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) > 0);
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(Byte.compareUnsigned(left.byteValue(), right.byteValue()) > 0);
			else if(size <= 16) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) > 0);
			else if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) > 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) > 0);
		}

		else if(left.isByte() && right.isReal()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isByte() && right.isByte()) return new BoolVal(left.byteValue() > right.byteValue());
		else if(left.isByte() && right.isInt()) return new BoolVal(left.intValue() > right.intValue());
		else if(left.isByte() && right.isShort()) return new BoolVal(left.shortValue() > right.shortValue());
		else if(left.isByte() && right.isLong()) return new BoolVal(left.longValue() > right.longValue());

		else if(left.isUnsignedShort() && right.isReal()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isUnsignedShort() && right.isUnsignedByteValue()) return new BoolVal(Short.compareUnsigned(left.shortValue(),  right.shortValue()) > 0); 
        else if(left.isUnsignedShort() && right.isUnsignedInt()) return new BoolVal(Integer.compareUnsigned(left.intValue() , right.intValue()) > 0);
        else if(left.isUnsignedShort() && right.isUnsignedShort()) return new BoolVal(Short.compareUnsigned(left.shortValue() , right.shortValue()) > 0);
		else if(left.isUnsignedShort() && right.isUnsignedLong()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) > 0);
		else if(left.isUnsignedShort() && right.isRegister()) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) > 0);
		else if(left.isUnsignedShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) > 0);
			else if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) > 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) > 0);
		}

		else if(left.isShort() && right.isReal()) return new BoolVal(left.realValue() > right.realValue());
        else if(left.isShort() && right.isByte()) return new BoolVal(left.shortValue() > right.shortValue());
        else if(left.isShort() && right.isInt()) return new BoolVal(left.intValue() > right.intValue());
		else if(left.isShort() && right.isShort()) return new BoolVal(left.shortValue() > right.shortValue());
		else if(left.isShort() && right.isLong()) return new BoolVal(left.longValue() > right.longValue());

		else if(left.isUnsignedInt() && right.isReal()) return new BoolVal(left.realValue() > right.realValue());
        else if(left.isUnsignedInt() && right.isUnsignedByteValue()) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) > 0); 
        else if(left.isUnsignedInt() && right.isUnsignedInt()) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) > 0);
        else if(left.isUnsignedInt() && right.isUnsignedShort()) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) > 0);
		else if(left.isUnsignedInt() && right.isUnsignedLong()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) > 0);
		else if(left.isUnsignedInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) > 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) > 0);
		}

		else if(left.isInt() && right.isReal()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isInt() && right.isByte()) return new BoolVal(left.intValue() > right.intValue());
		else if(left.isInt() && right.isInt()) return new BoolVal(left.intValue() > right.intValue());
		else if(left.isInt() && right.isShort()) return new BoolVal(left.intValue() > right.intValue());
		else if(left.isInt() && right.isLong()) return new BoolVal(left.longValue() > right.longValue());

		else if(left.isUnsignedLong() && right.isReal()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isUnsignedLong() && right.isUnsignedByteValue()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) > 0); 
        else if(left.isUnsignedLong() && right.isUnsignedInt()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) > 0);
        else if(left.isUnsignedLong() && right.isUnsignedShort()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) > 0);
		else if(left.isUnsignedLong() && right.isUnsignedLong()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) > 0);
		else if(left.isUnsignedLong() && right.isVector()) return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) > 0);

		else if(left.isLong() && right.isReal()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isLong() && right.isByte()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isLong() && right.isInt()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isLong() && right.isShort()) return new BoolVal(left.longValue() > right.longValue());
		else if(left.isLong() && right.isLong()) return new BoolVal(left.longValue() > right.longValue());
		
		else if(left.isVector() && right.isReal()) return new BoolVal(left.realValue() > right.realValue());
		else if(left.isVector() && right.isUnsignedByteValue()){
			VectorVal vec = (VectorVal)left;
			int size = vec.getSize();
			if(size <= 8) return new BoolVal(Byte.compareUnsigned(left.byteValue(), right.byteValue()) > 0);
			else if(size <= 16) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) > 0);
			else if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) > 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) > 0);	
		}
        else if(left.isVector() && right.isUnsignedInt()){
			VectorVal vec = (VectorVal)left;
			int size = vec.getSize();
			if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) > 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) > 0);	
		}
        else if(left.isVector() && right.isUnsignedShort()){
			VectorVal vec = (VectorVal)left;
			int size = vec.getSize();
			if(size <= 16) return new BoolVal(Short.compareUnsigned(left.shortValue(), right.shortValue()) > 0);
			else if(size <= 32) return new BoolVal(Integer.compareUnsigned(left.intValue(), right.intValue()) > 0);
			else return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) > 0);	
		}
		else if(left.isVector() && right.isUnsignedLong()){
			return new BoolVal(Long.compareUnsigned(left.longValue(), right.longValue()) > 0);	
		}
		else if(left.isVector() && right.isVector()) {
			return new BoolVal(left.longValue() > right.longValue());
		} else {
			errorAndExit("Inavlid Addition operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}
    }

	public static Value itwiseAnd(Value left, Value right) throws Exception{
        if(left.isBool() && right.isBool()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
        else if(left.isBool() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() & right.byteValue()); 
        else if(left.isBool() && right.isByte()) return new ByteVal(left.byteValue() & right.byteValue());
		else if(left.isBool() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() & right.intValue());
        else if(left.isBool() && right.isInt()) return new IntVal(left.intValue() & right.intValue());
		else if(left.isBool() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isBool() && right.isShort()) return new ShortVal(left.shortValue() & right.shortValue());
		else if(left.isBool() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isBool() && right.isLong()) return new LongVal(left.longValue() & right.longValue());
		else if(left.isBool() && right.isRegister()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
		else if(left.isBool() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() & right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() & right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() & right.intValue());
			else return new UnsignedLongVal(left.longValue() & right.longValue());
		}

		if(left.isUnsignedByteValue() && right.isBool()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() & right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByte()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() & right.intValue());
        else if(left.isUnsignedByteValue() && right.isInt()) return new UnsignedIntVal(left.intValue() & right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShort()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedByteValue() && right.isLong()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() & right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() & right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() & right.intValue());
			else return new UnsignedLongVal(left.longValue() & right.longValue());
		}

		if(left.isByte() && right.isBool()) return new ByteVal(left.byteValue() & right.byteValue());
        else if(left.isByte() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() & right.byteValue()); 
        else if(left.isByte() && right.isByte()) return new ByteVal(left.byteValue() & right.byteValue());
		else if(left.isByte() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() & right.intValue());
        else if(left.isByte() && right.isInt()) return new IntVal(left.intValue() & right.intValue());
		else if(left.isByte() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isByte() && right.isShort()) return new ShortVal(left.shortValue() & right.shortValue());
		else if(left.isByte() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isByte() && right.isLong()) return new LongVal(left.longValue() & right.longValue());
		else if(left.isByte() && right.isRegister()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
		else if(left.isByte() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() & right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() & right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() & right.intValue());
			else return new UnsignedLongVal(left.longValue() & right.longValue());
		}

		else if(left.isUnsignedShort() && right.isBool()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
        else if(left.isUnsignedShort() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() & right.shortValue()); 
        else if(left.isUnsignedShort() && right.isByte()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() & right.intValue());
        else if(left.isUnsignedShort() && right.isInt()) return new UnsignedIntVal(left.intValue() & right.intValue());
		else if(left.isUnsignedShort() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isUnsignedShort() && right.isShort()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedShort() && right.isLong()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedShort() && right.isRegister()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isUnsignedShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() & right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() & right.intValue());
			else return new UnsignedLongVal(left.longValue() & right.longValue());
		}

		else if(left.isShort() && right.isBool()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
        else if(left.isShort() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() & right.shortValue()); 
        else if(left.isShort() && right.isByte()) return new ShortVal(left.shortValue() & right.shortValue());
		else if(left.isShort() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() & right.intValue());
        else if(left.isShort() && right.isInt()) return new IntVal(left.intValue() & right.intValue());
		else if(left.isShort() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isShort() && right.isShort()) return new ShortVal(left.shortValue() & right.shortValue());
		else if(left.isShort() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isShort() && right.isLong()) return new LongVal(left.longValue() & right.longValue());
		else if(left.isShort() && right.isRegister()) return new UnsignedShortVal(left.byteValue() & right.byteValue());
		else if(left.isShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() & right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() & right.intValue());
			else return new UnsignedLongVal(left.longValue() & right.longValue());
		}

		else if(left.isUnsignedInt() && right.isBool()) return new UnsignedIntVal(left.intValue() & right.intValue());
        else if(left.isUnsignedInt() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() & right.intValue()); 
        else if(left.isUnsignedInt() && right.isByte()) return new UnsignedIntVal(left.shortValue() & right.shortValue());
		else if(left.isUnsignedInt() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() & right.intValue());
        else if(left.isUnsignedInt() && right.isInt()) return new UnsignedIntVal(left.intValue() & right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedShort()) return new UnsignedIntVal(left.intValue() & right.intValue());
		else if(left.isUnsignedInt() && right.isShort()) return new UnsignedIntVal(left.intValue() & right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedInt() && right.isLong()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedInt() && right.isRegister()) return new UnsignedIntVal(left.intValue() & right.intValue());
		else if(left.isUnsignedInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() & right.intValue());
			else return new UnsignedLongVal(left.longValue() & right.longValue());
		}

		else if(left.isInt() && right.isBool()) return new UnsignedIntVal(left.intValue() & right.intValue());
        else if(left.isInt() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() & right.intValue()); 
        else if(left.isInt() && right.isByte()) return new IntVal(left.intValue() & right.intValue());
		else if(left.isInt() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() & right.intValue());
        else if(left.isInt() && right.isInt()) return new IntVal(left.intValue() & right.intValue());
		else if(left.isInt() && right.isUnsignedShort()) return new UnsignedIntVal(left.intValue() & right.intValue());
		else if(left.isInt() && right.isShort()) return new IntVal(left.intValue() & right.intValue());
		else if(left.isInt() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isInt() && right.isLong()) return new LongVal(left.longValue() & right.longValue());
		else if(left.isInt() && right.isRegister()) return new UnsignedIntVal(left.intValue() & right.intValue());
		else if(left.isInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() & right.intValue());
			else return new UnsignedLongVal(left.longValue() & right.longValue());
		}

		else if(left.isUnsignedLong() && right.isBool()) return new UnsignedLongVal(left.longValue() & right.longValue());
        else if(left.isUnsignedLong() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() & right.longValue()); 
        else if(left.isUnsignedLong() && right.isByte()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedInt()) return new UnsignedLongVal(left.longValue() & right.longValue());
        else if(left.isUnsignedLong() && right.isInt()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedShort()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedLong() && right.isShort()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedLong() && right.isLong()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedLong() && right.isRegister()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isUnsignedLong() && right.isVector()) return new UnsignedLongVal(left.longValue() & right.longValue());

		else if(left.isLong() && right.isBool()) return new UnsignedLongVal(left.longValue() & right.longValue());
        else if(left.isLong() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() & right.longValue()); 
        else if(left.isLong() && right.isByte()) return new LongVal(left.longValue() & right.longValue());
		else if(left.isLong() && right.isUnsignedInt()) return new UnsignedLongVal(left.longValue() & right.longValue());
        else if(left.isLong() && right.isInt()) return new LongVal(left.longValue() & right.longValue());
		else if(left.isLong() && right.isUnsignedShort()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isLong() && right.isShort()) return new LongVal(left.longValue() & right.longValue());
		else if(left.isLong() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isLong() && right.isLong()) return new LongVal(left.longValue() & right.longValue());
		else if(left.isLong() && right.isRegister()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isLong() && right.isVector()) return new UnsignedLongVal(left.longValue() & right.longValue());

		else if(left.isRegister() && right.isBool()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() & right.byteValue()); 
        else if(left.isRegister() && right.isByte()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
		else if(left.isRegister() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() & right.intValue());
        else if(left.isRegister() && right.isInt()) return new UnsignedIntVal(left.intValue() & right.intValue());
		else if(left.isRegister() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isRegister() && right.isShort()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isRegister() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isRegister() && right.isLong()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isRegister() && right.isRegister()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() & right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() & right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() & right.intValue());
			else return new UnsignedLongVal(left.longValue() & right.longValue());
		}

		else if(left.isVector() && right.isBool()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() & right.byteValue()); 
        else if(left.isVector() && right.isByte()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
		else if(left.isVector() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() & right.intValue());
        else if(left.isVector() && right.isInt()) return new UnsignedIntVal(left.intValue() & right.intValue());
		else if(left.isVector() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isVector() && right.isShort()) return new UnsignedShortVal(left.shortValue() & right.shortValue());
		else if(left.isVector() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isVector() && right.isLong()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else if(left.isVector() && right.isRegister()) return new UnsignedByteVal(left.byteValue() & right.byteValue());
		else if(left.isVector() && right.isVector()) return new UnsignedLongVal(left.longValue() & right.longValue());
		else {
			errorAndExit("Inavlid bitwise and operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

	public static Value bitwiseAndCircuit(Value left, Value right) throws Exception{
		if(left.isWire() && right.isWire()){
			WireVal Output = new WireVal();
			new AndGate(Output, (WireVal)left, (WireVal)right);
			return Output;
		} else if (left.isVector() && right.isVector()){
			VectorVal leftVector = (VectorVal)left;
			VectorVal rightVector = (VectorVal)right;
			VectorVal Output = new VectorVal(0, leftVector.getSize());

			if(leftVector.getSize() == rightVector.getSize()){
				int leftStart = leftVector.getStart();
				int leftEnd = leftVector.getEnd();
				int rightStart = rightVector.getStart();
				int rightEnd = rightVector.getEnd();
				int OutputStart = Output.getStart();
				int OutputEnd = Output.getEnd();

				while(leftStart != leftEnd){
					WireVal Input1 = new WireVal();
					Input1.assignInput(leftVector.getValue(leftStart));

					WireVal Input2 = new WireVal();
					Input2.assignInput(rightVector.getValue(rightStart));

					WireVal OutputW = new WireVal();
					OutputW.addOutput(Output.getValue(OutputStart));
					
					new AndGate(OutputW, Input1, Input2);

					OutputStart++;
					leftStart++;
					rightStart++;
				}

				return Output;
			} else {
				Utils.errorAndExit("Cant have to different sized vectors in Bitwise and operation");
				return Utils.errorOccured();
			}
		}

		return null;
	}

    public static Value itwiseOr(Value left, Value right) throws Exception{
        if(left.isBool() && right.isBool()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
        else if(left.isBool() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() | right.byteValue()); 
        else if(left.isBool() && right.isByte()) return new ByteVal(left.byteValue() | right.byteValue());
		else if(left.isBool() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() | right.intValue());
        else if(left.isBool() && right.isInt()) return new IntVal(left.intValue() | right.intValue());
		else if(left.isBool() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isBool() && right.isShort()) return new ShortVal(left.shortValue() | right.shortValue());
		else if(left.isBool() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isBool() && right.isLong()) return new LongVal(left.longValue() | right.longValue());
		else if(left.isBool() && right.isRegister()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
		else if(left.isBool() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() | right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() | right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() | right.intValue());
			else return new UnsignedLongVal(left.longValue() | right.longValue());
		}

		if(left.isUnsignedByteValue() && right.isBool()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() | right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByte()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() | right.intValue());
        else if(left.isUnsignedByteValue() && right.isInt()) return new UnsignedIntVal(left.intValue() | right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShort()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedByteValue() && right.isLong()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() | right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() | right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() | right.intValue());
			else return new UnsignedLongVal(left.longValue() | right.longValue());
		}

		if(left.isByte() && right.isBool()) return new ByteVal(left.byteValue() | right.byteValue());
        else if(left.isByte() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() | right.byteValue()); 
        else if(left.isByte() && right.isByte()) return new ByteVal(left.byteValue() | right.byteValue());
		else if(left.isByte() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() | right.intValue());
        else if(left.isByte() && right.isInt()) return new IntVal(left.intValue() | right.intValue());
		else if(left.isByte() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isByte() && right.isShort()) return new ShortVal(left.shortValue() | right.shortValue());
		else if(left.isByte() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isByte() && right.isLong()) return new LongVal(left.longValue() | right.longValue());
		else if(left.isByte() && right.isRegister()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
		else if(left.isByte() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() | right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() | right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() | right.intValue());
			else return new UnsignedLongVal(left.longValue() | right.longValue());
		}

		else if(left.isUnsignedShort() && right.isBool()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
        else if(left.isUnsignedShort() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() | right.shortValue()); 
        else if(left.isUnsignedShort() && right.isByte()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() | right.intValue());
        else if(left.isUnsignedShort() && right.isInt()) return new UnsignedIntVal(left.intValue() | right.intValue());
		else if(left.isUnsignedShort() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isUnsignedShort() && right.isShort()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedShort() && right.isLong()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedShort() && right.isRegister()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isUnsignedShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() | right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() | right.intValue());
			else return new UnsignedLongVal(left.longValue() | right.longValue());
		}

		else if(left.isShort() && right.isBool()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
        else if(left.isShort() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() | right.shortValue()); 
        else if(left.isShort() && right.isByte()) return new ShortVal(left.shortValue() | right.shortValue());
		else if(left.isShort() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() | right.intValue());
        else if(left.isShort() && right.isInt()) return new IntVal(left.intValue() | right.intValue());
		else if(left.isShort() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isShort() && right.isShort()) return new ShortVal(left.shortValue() | right.shortValue());
		else if(left.isShort() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isShort() && right.isLong()) return new LongVal(left.longValue() | right.longValue());
		else if(left.isShort() && right.isRegister()) return new UnsignedShortVal(left.byteValue() | right.byteValue());
		else if(left.isShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() | right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() | right.intValue());
			else return new UnsignedLongVal(left.longValue() | right.longValue());
		}

		else if(left.isUnsignedInt() && right.isBool()) return new UnsignedIntVal(left.intValue() | right.intValue());
        else if(left.isUnsignedInt() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() | right.intValue()); 
        else if(left.isUnsignedInt() && right.isByte()) return new UnsignedIntVal(left.shortValue() | right.shortValue());
		else if(left.isUnsignedInt() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() | right.intValue());
        else if(left.isUnsignedInt() && right.isInt()) return new UnsignedIntVal(left.intValue() | right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedShort()) return new UnsignedIntVal(left.intValue() | right.intValue());
		else if(left.isUnsignedInt() && right.isShort()) return new UnsignedIntVal(left.intValue() | right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedInt() && right.isLong()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedInt() && right.isRegister()) return new UnsignedIntVal(left.intValue() | right.intValue());
		else if(left.isUnsignedInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() | right.intValue());
			else return new UnsignedLongVal(left.longValue() | right.longValue());
		}

		else if(left.isInt() && right.isBool()) return new UnsignedIntVal(left.intValue() | right.intValue());
        else if(left.isInt() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() | right.intValue()); 
        else if(left.isInt() && right.isByte()) return new IntVal(left.intValue() | right.intValue());
		else if(left.isInt() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() | right.intValue());
        else if(left.isInt() && right.isInt()) return new IntVal(left.intValue() | right.intValue());
		else if(left.isInt() && right.isUnsignedShort()) return new UnsignedIntVal(left.intValue() | right.intValue());
		else if(left.isInt() && right.isShort()) return new IntVal(left.intValue() | right.intValue());
		else if(left.isInt() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isInt() && right.isLong()) return new LongVal(left.longValue() | right.longValue());
		else if(left.isInt() && right.isRegister()) return new UnsignedIntVal(left.intValue() | right.intValue());
		else if(left.isInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() | right.intValue());
			else return new UnsignedLongVal(left.longValue() | right.longValue());
		}

		else if(left.isUnsignedLong() && right.isBool()) return new UnsignedLongVal(left.longValue() | right.longValue());
        else if(left.isUnsignedLong() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() | right.longValue()); 
        else if(left.isUnsignedLong() && right.isByte()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedInt()) return new UnsignedLongVal(left.longValue() | right.longValue());
        else if(left.isUnsignedLong() && right.isInt()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedShort()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedLong() && right.isShort()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedLong() && right.isLong()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedLong() && right.isRegister()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isUnsignedLong() && right.isVector()) return new UnsignedLongVal(left.longValue() | right.longValue());

		else if(left.isLong() && right.isBool()) return new UnsignedLongVal(left.longValue() | right.longValue());
        else if(left.isLong() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() | right.longValue()); 
        else if(left.isLong() && right.isByte()) return new LongVal(left.longValue() | right.longValue());
		else if(left.isLong() && right.isUnsignedInt()) return new UnsignedLongVal(left.longValue() | right.longValue());
        else if(left.isLong() && right.isInt()) return new LongVal(left.longValue() | right.longValue());
		else if(left.isLong() && right.isUnsignedShort()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isLong() && right.isShort()) return new LongVal(left.longValue() | right.longValue());
		else if(left.isLong() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isLong() && right.isLong()) return new LongVal(left.longValue() | right.longValue());
		else if(left.isLong() && right.isRegister()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isLong() && right.isVector()) return new UnsignedLongVal(left.longValue() | right.longValue());

		else if(left.isRegister() && right.isBool()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() | right.byteValue()); 
        else if(left.isRegister() && right.isByte()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
		else if(left.isRegister() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() | right.intValue());
        else if(left.isRegister() && right.isInt()) return new UnsignedIntVal(left.intValue() | right.intValue());
		else if(left.isRegister() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isRegister() && right.isShort()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isRegister() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isRegister() && right.isLong()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isRegister() && right.isRegister()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() | right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() | right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() | right.intValue());
			else return new UnsignedLongVal(left.longValue() | right.longValue());
		}

		else if(left.isVector() && right.isBool()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() | right.byteValue()); 
        else if(left.isVector() && right.isByte()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
		else if(left.isVector() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() | right.intValue());
        else if(left.isVector() && right.isInt()) return new UnsignedIntVal(left.intValue() | right.intValue());
		else if(left.isVector() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isVector() && right.isShort()) return new UnsignedShortVal(left.shortValue() | right.shortValue());
		else if(left.isVector() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isVector() && right.isLong()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else if(left.isVector() && right.isRegister()) return new UnsignedByteVal(left.byteValue() | right.byteValue());
		else if(left.isVector() && right.isVector()) return new UnsignedLongVal(left.longValue() | right.longValue());
		else {
			errorAndExit("Inavlid == operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

	public static Value itwiseOrCircuit(Value left, Value right) throws Exception{
		if(left.isWire() && right.isWire()){
			WireVal Output = new WireVal();
			new OrGate(Output, (WireVal)left, (WireVal)right);
			return Output;
		} else if (left.isVector() && right.isVector()){
			VectorVal leftVector = (VectorVal)left;
			VectorVal rightVector = (VectorVal)right;
			VectorVal Output = new VectorVal(0, leftVector.getSize());

			if(leftVector.getSize() == rightVector.getSize()){
				int leftStart = leftVector.getStart();
				int leftEnd = leftVector.getEnd();
				int rightStart = rightVector.getStart();
				int rightEnd = rightVector.getEnd();
				int OutputStart = Output.getStart();
				int OutputEnd = Output.getEnd();

				while(leftStart != leftEnd){
					WireVal Input1 = new WireVal();
					Input1.assignInput(leftVector.getValue(leftStart));

					WireVal Input2 = new WireVal();
					Input2.assignInput(rightVector.getValue(rightStart));

					WireVal OutputW = new WireVal();
					OutputW.addOutput(Output.getValue(OutputStart));
					
					new OrGate(OutputW, Input1, Input2);

					OutputStart++;
					leftStart++;
					rightStart++;
				}

				return Output;
			} else {
				Utils.errorAndExit("Cant have to different sized vectors in Bitwise and operation");
				return Utils.errorOccured();
			}
		}

		return null;
	}

    public static Value xclusiveOr(Value left, Value right) throws Exception{
        if(left.isBool() && right.isBool()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
        else if(left.isBool() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue()); 
        else if(left.isBool() && right.isByte()) return new ByteVal(left.byteValue() ^ right.byteValue());
		else if(left.isBool() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
        else if(left.isBool() && right.isInt()) return new IntVal(left.intValue() ^ right.intValue());
		else if(left.isBool() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isBool() && right.isShort()) return new ShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isBool() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isBool() && right.isLong()) return new LongVal(left.longValue() ^ right.longValue());
		else if(left.isBool() && right.isRegister()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
		else if(left.isBool() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() ^ right.intValue());
			else return new UnsignedLongVal(left.longValue() ^ right.longValue());
		}

		if(left.isUnsignedByteValue() && right.isBool()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByte()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
        else if(left.isUnsignedByteValue() && right.isInt()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShort()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedByteValue() && right.isLong()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() ^ right.intValue());
			else return new UnsignedLongVal(left.longValue() ^ right.longValue());
		}

		if(left.isByte() && right.isBool()) return new ByteVal(left.byteValue() ^ right.byteValue());
        else if(left.isByte() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue()); 
        else if(left.isByte() && right.isByte()) return new ByteVal(left.byteValue() ^ right.byteValue());
		else if(left.isByte() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
        else if(left.isByte() && right.isInt()) return new IntVal(left.intValue() ^ right.intValue());
		else if(left.isByte() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isByte() && right.isShort()) return new ShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isByte() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isByte() && right.isLong()) return new LongVal(left.longValue() ^ right.longValue());
		else if(left.isByte() && right.isRegister()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
		else if(left.isByte() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() ^ right.intValue());
			else return new UnsignedLongVal(left.longValue() ^ right.longValue());
		}

		else if(left.isUnsignedShort() && right.isBool()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
        else if(left.isUnsignedShort() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue()); 
        else if(left.isUnsignedShort() && right.isByte()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
        else if(left.isUnsignedShort() && right.isInt()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
		else if(left.isUnsignedShort() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isUnsignedShort() && right.isShort()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedShort() && right.isLong()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedShort() && right.isRegister()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isUnsignedShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() ^ right.intValue());
			else return new UnsignedLongVal(left.longValue() ^ right.longValue());
		}

		else if(left.isShort() && right.isBool()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
        else if(left.isShort() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue()); 
        else if(left.isShort() && right.isByte()) return new ShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isShort() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
        else if(left.isShort() && right.isInt()) return new IntVal(left.intValue() ^ right.intValue());
		else if(left.isShort() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isShort() && right.isShort()) return new ShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isShort() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isShort() && right.isLong()) return new LongVal(left.longValue() ^ right.longValue());
		else if(left.isShort() && right.isRegister()) return new UnsignedShortVal(left.byteValue() ^ right.byteValue());
		else if(left.isShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() ^ right.intValue());
			else return new UnsignedLongVal(left.longValue() ^ right.longValue());
		}

		else if(left.isUnsignedInt() && right.isBool()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
        else if(left.isUnsignedInt() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() ^ right.intValue()); 
        else if(left.isUnsignedInt() && right.isByte()) return new UnsignedIntVal(left.shortValue() ^ right.shortValue());
		else if(left.isUnsignedInt() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
        else if(left.isUnsignedInt() && right.isInt()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedShort()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
		else if(left.isUnsignedInt() && right.isShort()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedInt() && right.isLong()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedInt() && right.isRegister()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
		else if(left.isUnsignedInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() ^ right.intValue());
			else return new UnsignedLongVal(left.longValue() ^ right.longValue());
		}

		else if(left.isInt() && right.isBool()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
        else if(left.isInt() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() ^ right.intValue()); 
        else if(left.isInt() && right.isByte()) return new IntVal(left.intValue() ^ right.intValue());
		else if(left.isInt() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
        else if(left.isInt() && right.isInt()) return new IntVal(left.intValue() ^ right.intValue());
		else if(left.isInt() && right.isUnsignedShort()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
		else if(left.isInt() && right.isShort()) return new IntVal(left.intValue() ^ right.intValue());
		else if(left.isInt() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isInt() && right.isLong()) return new LongVal(left.longValue() ^ right.longValue());
		else if(left.isInt() && right.isRegister()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
		else if(left.isInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() ^ right.intValue());
			else return new UnsignedLongVal(left.longValue() ^ right.longValue());
		}

		else if(left.isUnsignedLong() && right.isBool()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
        else if(left.isUnsignedLong() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue()); 
        else if(left.isUnsignedLong() && right.isByte()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedInt()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
        else if(left.isUnsignedLong() && right.isInt()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedShort()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedLong() && right.isShort()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedLong() && right.isLong()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedLong() && right.isRegister()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isUnsignedLong() && right.isVector()) return new UnsignedLongVal(left.longValue() ^ right.longValue());

		else if(left.isLong() && right.isBool()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
        else if(left.isLong() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() ^ right.longValue()); 
        else if(left.isLong() && right.isByte()) return new LongVal(left.longValue() ^ right.longValue());
		else if(left.isLong() && right.isUnsignedInt()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
        else if(left.isLong() && right.isInt()) return new LongVal(left.longValue() ^ right.longValue());
		else if(left.isLong() && right.isUnsignedShort()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isLong() && right.isShort()) return new LongVal(left.longValue() ^ right.longValue());
		else if(left.isLong() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isLong() && right.isLong()) return new LongVal(left.longValue() ^ right.longValue());
		else if(left.isLong() && right.isRegister()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isLong() && right.isVector()) return new UnsignedLongVal(left.longValue() ^ right.longValue());

		else if(left.isRegister() && right.isBool()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue()); 
        else if(left.isRegister() && right.isByte()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
		else if(left.isRegister() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
        else if(left.isRegister() && right.isInt()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
		else if(left.isRegister() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isRegister() && right.isShort()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isRegister() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isRegister() && right.isLong()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isRegister() && right.isRegister()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() ^ right.intValue());
			else return new UnsignedLongVal(left.longValue() ^ right.longValue());
		}

		else if(left.isVector() && right.isBool()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue()); 
        else if(left.isVector() && right.isByte()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
		else if(left.isVector() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
        else if(left.isVector() && right.isInt()) return new UnsignedIntVal(left.intValue() ^ right.intValue());
		else if(left.isVector() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isVector() && right.isShort()) return new UnsignedShortVal(left.shortValue() ^ right.shortValue());
		else if(left.isVector() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isVector() && right.isLong()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else if(left.isVector() && right.isRegister()) return new UnsignedByteVal(left.byteValue() ^ right.byteValue());
		else if(left.isVector() && right.isVector()) return new UnsignedLongVal(left.longValue() ^ right.longValue());
		else {
			errorAndExit("Inavlid == operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

	public static Value itwiseXorCircuit(Value left, Value right) throws Exception{
		if(left.isWire() && right.isWire()){
			WireVal Output = new WireVal();
			new XorGate(Output, (WireVal)left, (WireVal)right);
			return Output;
		} else if (left.isVector() && right.isVector()){
			VectorVal leftVector = (VectorVal)left;
			VectorVal rightVector = (VectorVal)right;
			VectorVal Output = new VectorVal(0, leftVector.getSize());

			if(leftVector.getSize() == rightVector.getSize()){
				int leftStart = leftVector.getStart();
				int leftEnd = leftVector.getEnd();
				int rightStart = rightVector.getStart();
				int rightEnd = rightVector.getEnd();
				int OutputStart = Output.getStart();
				int OutputEnd = Output.getEnd();

				while(leftStart != leftEnd){
					WireVal Input1 = new WireVal();
					Input1.assignInput(leftVector.getValue(leftStart));

					WireVal Input2 = new WireVal();
					Input2.assignInput(rightVector.getValue(rightStart));

					WireVal OutputW = new WireVal();
					OutputW.addOutput(Output.getValue(OutputStart));
					
					new XorGate(OutputW, Input1, Input2);

					OutputStart++;
					leftStart++;
					rightStart++;
				}

				return Output;
			} else {
				Utils.errorAndExit("Cant have to different sized vectors in Bitwise and operation");
				return Utils.errorOccured();
			}
		}

		return null;
	}

    public static Value xclusiveNor(Value left, Value right) throws Exception{
        if(left.isBool() && right.isBool()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
        else if(left.isBool() && right.isUnsignedByteValue()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue())); 
        else if(left.isBool() && right.isByte()) return new ByteVal(~(left.byteValue() ^ right.byteValue()));
		else if(left.isBool() && right.isUnsignedInt()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
        else if(left.isBool() && right.isInt()) return new IntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isBool() && right.isUnsignedShort()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isBool() && right.isShort()) return new ShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isBool() && right.isUnsignedLong()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isBool() && right.isLong()) return new LongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isBool() && right.isRegister()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
		else if(left.isBool() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
			else if(size <= 16) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
			else if(size <= 32) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
			else return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		}

		if(left.isUnsignedByteValue() && right.isBool()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue())); 
        else if(left.isUnsignedByteValue() && right.isByte()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
		else if(left.isUnsignedByteValue() && right.isUnsignedInt()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
        else if(left.isUnsignedByteValue() && right.isInt()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isUnsignedByteValue() && right.isUnsignedShort()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isUnsignedByteValue() && right.isShort()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isUnsignedByteValue() && right.isUnsignedLong()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedByteValue() && right.isLong()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedByteValue() && right.isRegister()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
			else if(size <= 16) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
			else if(size <= 32) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
			else return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		}

		if(left.isByte() && right.isBool()) return new ByteVal(~(left.byteValue() ^ right.byteValue()));
        else if(left.isByte() && right.isUnsignedByteValue()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue())); 
        else if(left.isByte() && right.isByte()) return new ByteVal(~(left.byteValue() ^ right.byteValue()));
		else if(left.isByte() && right.isUnsignedInt()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
        else if(left.isByte() && right.isInt()) return new IntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isByte() && right.isUnsignedShort()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isByte() && right.isShort()) return new ShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isByte() && right.isUnsignedLong()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isByte() && right.isLong()) return new LongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isByte() && right.isRegister()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
		else if(left.isByte() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
			else if(size <= 16) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
			else if(size <= 32) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
			else return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		}

		else if(left.isUnsignedShort() && right.isBool()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
        else if(left.isUnsignedShort() && right.isUnsignedByteValue()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue())); 
        else if(left.isUnsignedShort() && right.isByte()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isUnsignedShort() && right.isUnsignedInt()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
        else if(left.isUnsignedShort() && right.isInt()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isUnsignedShort() && right.isUnsignedShort()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isUnsignedShort() && right.isShort()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isUnsignedShort() && right.isUnsignedLong()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedShort() && right.isLong()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedShort() && right.isRegister()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isUnsignedShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
			else if(size <= 32) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
			else return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		}

		else if(left.isShort() && right.isBool()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
        else if(left.isShort() && right.isUnsignedByteValue()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue())); 
        else if(left.isShort() && right.isByte()) return new ShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isShort() && right.isUnsignedInt()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
        else if(left.isShort() && right.isInt()) return new IntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isShort() && right.isUnsignedShort()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isShort() && right.isShort()) return new ShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isShort() && right.isUnsignedLong()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isShort() && right.isLong()) return new LongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isShort() && right.isRegister()) return new UnsignedShortVal(~(left.byteValue() ^ right.byteValue()));
		else if(left.isShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
			else if(size <= 32) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
			else return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		}

		else if(left.isUnsignedInt() && right.isBool()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
        else if(left.isUnsignedInt() && right.isUnsignedByteValue()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue())); 
        else if(left.isUnsignedInt() && right.isByte()) return new UnsignedIntVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isUnsignedInt() && right.isUnsignedInt()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
        else if(left.isUnsignedInt() && right.isInt()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isUnsignedInt() && right.isUnsignedShort()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isUnsignedInt() && right.isShort()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isUnsignedInt() && right.isUnsignedLong()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedInt() && right.isLong()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedInt() && right.isRegister()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isUnsignedInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
			else return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		}

		else if(left.isInt() && right.isBool()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
        else if(left.isInt() && right.isUnsignedByteValue()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue())); 
        else if(left.isInt() && right.isByte()) return new IntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isInt() && right.isUnsignedInt()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
        else if(left.isInt() && right.isInt()) return new IntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isInt() && right.isUnsignedShort()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isInt() && right.isShort()) return new IntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isInt() && right.isUnsignedLong()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isInt() && right.isLong()) return new LongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isInt() && right.isRegister()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
			else return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		}

		else if(left.isUnsignedLong() && right.isBool()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
        else if(left.isUnsignedLong() && right.isUnsignedByteValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue())); 
        else if(left.isUnsignedLong() && right.isByte()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedLong() && right.isUnsignedInt()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
        else if(left.isUnsignedLong() && right.isInt()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedLong() && right.isUnsignedShort()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedLong() && right.isShort()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedLong() && right.isUnsignedLong()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedLong() && right.isLong()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedLong() && right.isRegister()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isUnsignedLong() && right.isVector()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));

		else if(left.isLong() && right.isBool()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
        else if(left.isLong() && right.isUnsignedByteValue()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue())); 
        else if(left.isLong() && right.isByte()) return new LongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isLong() && right.isUnsignedInt()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
        else if(left.isLong() && right.isInt()) return new LongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isLong() && right.isUnsignedShort()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isLong() && right.isShort()) return new LongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isLong() && right.isUnsignedLong()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isLong() && right.isLong()) return new LongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isLong() && right.isRegister()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isLong() && right.isVector()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));

		else if(left.isRegister() && right.isBool()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
        else if(left.isRegister() && right.isUnsignedByteValue()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue())); 
        else if(left.isRegister() && right.isByte()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
		else if(left.isRegister() && right.isUnsignedInt()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
        else if(left.isRegister() && right.isInt()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isRegister() && right.isUnsignedShort()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isRegister() && right.isShort()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isRegister() && right.isUnsignedLong()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isRegister() && right.isLong()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isRegister() && right.isRegister()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
			else if(size <= 16) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
			else if(size <= 32) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
			else return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		}

		else if(left.isVector() && right.isBool()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
        else if(left.isVector() && right.isUnsignedByteValue()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue())); 
        else if(left.isVector() && right.isByte()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
		else if(left.isVector() && right.isUnsignedInt()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
        else if(left.isVector() && right.isInt()) return new UnsignedIntVal(~(left.intValue() ^ right.intValue()));
		else if(left.isVector() && right.isUnsignedShort()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isVector() && right.isShort()) return new UnsignedShortVal(~(left.shortValue() ^ right.shortValue()));
		else if(left.isVector() && right.isUnsignedLong()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isVector() && right.isLong()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else if(left.isVector() && right.isRegister()) return new UnsignedByteVal(~(left.byteValue() ^ right.byteValue()));
		else if(left.isVector() && right.isVector()) return new UnsignedLongVal(~(left.longValue() ^ right.longValue()));
		else {
			errorAndExit("Inavlid ^ operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}
    }

	public static Value itwiseXnorCircuit(Value left, Value right) throws Exception{
		if(left.isWire() && right.isWire()){
			WireVal Output = new WireVal();
			new XnorGate(Output, (WireVal)left, (WireVal)right);
			return Output;
		} else if (left.isVector() && right.isVector()){
			VectorVal leftVector = (VectorVal)left;
			VectorVal rightVector = (VectorVal)right;
			VectorVal Output = new VectorVal(0, leftVector.getSize());

			if(leftVector.getSize() == rightVector.getSize()){
				int leftStart = leftVector.getStart();
				int leftEnd = leftVector.getEnd();
				int rightStart = rightVector.getStart();
				int rightEnd = rightVector.getEnd();
				int OutputStart = Output.getStart();
				int OutputEnd = Output.getEnd();

				while(leftStart != leftEnd){
					WireVal Input1 = new WireVal();
					Input1.assignInput(leftVector.getValue(leftStart));

					WireVal Input2 = new WireVal();
					Input2.assignInput(rightVector.getValue(rightStart));

					WireVal OutputW = new WireVal();
					OutputW.addOutput(Output.getValue(OutputStart));
					
					new XnorGate(OutputW, Input1, Input2);

					OutputStart++;
					leftStart++;
					rightStart++;
				}

				return Output;
			} else {
				Utils.errorAndExit("Cant have to different sized vectors in Bitwise and operation");
				return Utils.errorOccured();
			}
		}

		return null;
	}

    public static Value eftShift(Value left, Value right) throws Exception{
        if(left.isBool() && right.isBool()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
        else if(left.isBool() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() << right.byteValue()); 
        else if(left.isBool() && right.isByte()) return new ByteVal(left.byteValue() << right.byteValue());
		else if(left.isBool() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() << right.intValue());
        else if(left.isBool() && right.isInt()) return new IntVal(left.intValue() << right.intValue());
		else if(left.isBool() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isBool() && right.isShort()) return new ShortVal(left.shortValue() << right.shortValue());
		else if(left.isBool() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isBool() && right.isLong()) return new LongVal(left.longValue() << right.longValue());
		else if(left.isBool() && right.isRegister()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
		else if(left.isBool() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() << right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() << right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() << right.intValue());
			else return new UnsignedLongVal(left.longValue() << right.longValue());
		}

		if(left.isUnsignedByteValue() && right.isBool()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() << right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByte()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() << right.intValue());
        else if(left.isUnsignedByteValue() && right.isInt()) return new UnsignedIntVal(left.intValue() << right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShort()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedByteValue() && right.isLong()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() << right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() << right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() << right.intValue());
			else return new UnsignedLongVal(left.longValue() << right.longValue());
		}

		if(left.isByte() && right.isBool()) return new ByteVal(left.byteValue() << right.byteValue());
        else if(left.isByte() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() << right.byteValue()); 
        else if(left.isByte() && right.isByte()) return new ByteVal(left.byteValue() << right.byteValue());
		else if(left.isByte() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() << right.intValue());
        else if(left.isByte() && right.isInt()) return new IntVal(left.intValue() << right.intValue());
		else if(left.isByte() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isByte() && right.isShort()) return new ShortVal(left.shortValue() << right.shortValue());
		else if(left.isByte() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isByte() && right.isLong()) return new LongVal(left.longValue() << right.longValue());
		else if(left.isByte() && right.isRegister()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
		else if(left.isByte() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() << right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() << right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() << right.intValue());
			else return new UnsignedLongVal(left.longValue() << right.longValue());
		}

		else if(left.isUnsignedShort() && right.isBool()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
        else if(left.isUnsignedShort() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() << right.shortValue()); 
        else if(left.isUnsignedShort() && right.isByte()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() << right.intValue());
        else if(left.isUnsignedShort() && right.isInt()) return new UnsignedIntVal(left.intValue() << right.intValue());
		else if(left.isUnsignedShort() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isUnsignedShort() && right.isShort()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedShort() && right.isLong()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedShort() && right.isRegister()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isUnsignedShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() << right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() << right.intValue());
			else return new UnsignedLongVal(left.longValue() << right.longValue());
		}

		else if(left.isShort() && right.isBool()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
        else if(left.isShort() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() << right.shortValue()); 
        else if(left.isShort() && right.isByte()) return new ShortVal(left.shortValue() << right.shortValue());
		else if(left.isShort() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() << right.intValue());
        else if(left.isShort() && right.isInt()) return new IntVal(left.intValue() << right.intValue());
		else if(left.isShort() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isShort() && right.isShort()) return new ShortVal(left.shortValue() << right.shortValue());
		else if(left.isShort() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isShort() && right.isLong()) return new LongVal(left.longValue() << right.longValue());
		else if(left.isShort() && right.isRegister()) return new UnsignedShortVal(left.byteValue() << right.byteValue());
		else if(left.isShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() << right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() << right.intValue());
			else return new UnsignedLongVal(left.longValue() << right.longValue());
		}

		else if(left.isUnsignedInt() && right.isBool()) return new UnsignedIntVal(left.intValue() << right.intValue());
        else if(left.isUnsignedInt() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() << right.intValue()); 
        else if(left.isUnsignedInt() && right.isByte()) return new UnsignedIntVal(left.shortValue() << right.shortValue());
		else if(left.isUnsignedInt() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() << right.intValue());
        else if(left.isUnsignedInt() && right.isInt()) return new UnsignedIntVal(left.intValue() << right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedShort()) return new UnsignedIntVal(left.intValue() << right.intValue());
		else if(left.isUnsignedInt() && right.isShort()) return new UnsignedIntVal(left.intValue() << right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedInt() && right.isLong()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedInt() && right.isRegister()) return new UnsignedIntVal(left.intValue() << right.intValue());
		else if(left.isUnsignedInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() << right.intValue());
			else return new UnsignedLongVal(left.longValue() << right.longValue());
		}

		else if(left.isInt() && right.isBool()) return new UnsignedIntVal(left.intValue() << right.intValue());
        else if(left.isInt() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() << right.intValue()); 
        else if(left.isInt() && right.isByte()) return new IntVal(left.intValue() << right.intValue());
		else if(left.isInt() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() << right.intValue());
        else if(left.isInt() && right.isInt()) return new IntVal(left.intValue() << right.intValue());
		else if(left.isInt() && right.isUnsignedShort()) return new UnsignedIntVal(left.intValue() << right.intValue());
		else if(left.isInt() && right.isShort()) return new IntVal(left.intValue() << right.intValue());
		else if(left.isInt() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isInt() && right.isLong()) return new LongVal(left.longValue() << right.longValue());
		else if(left.isInt() && right.isRegister()) return new UnsignedIntVal(left.intValue() << right.intValue());
		else if(left.isInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() << right.intValue());
			else return new UnsignedLongVal(left.longValue() << right.longValue());
		}

		else if(left.isUnsignedLong() && right.isBool()) return new UnsignedLongVal(left.longValue() << right.longValue());
        else if(left.isUnsignedLong() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() << right.longValue()); 
        else if(left.isUnsignedLong() && right.isByte()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedInt()) return new UnsignedLongVal(left.longValue() << right.longValue());
        else if(left.isUnsignedLong() && right.isInt()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedShort()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedLong() && right.isShort()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedLong() && right.isLong()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedLong() && right.isRegister()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isUnsignedLong() && right.isVector()) return new UnsignedLongVal(left.longValue() << right.longValue());

		else if(left.isLong() && right.isBool()) return new UnsignedLongVal(left.longValue() << right.longValue());
        else if(left.isLong() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() << right.longValue()); 
        else if(left.isLong() && right.isByte()) return new LongVal(left.longValue() << right.longValue());
		else if(left.isLong() && right.isUnsignedInt()) return new UnsignedLongVal(left.longValue() << right.longValue());
        else if(left.isLong() && right.isInt()) return new LongVal(left.longValue() << right.longValue());
		else if(left.isLong() && right.isUnsignedShort()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isLong() && right.isShort()) return new LongVal(left.longValue() << right.longValue());
		else if(left.isLong() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isLong() && right.isLong()) return new LongVal(left.longValue() << right.longValue());
		else if(left.isLong() && right.isRegister()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isLong() && right.isVector()) return new UnsignedLongVal(left.longValue() << right.longValue());

		else if(left.isRegister() && right.isBool()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() << right.byteValue()); 
        else if(left.isRegister() && right.isByte()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
		else if(left.isRegister() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() << right.intValue());
        else if(left.isRegister() && right.isInt()) return new UnsignedIntVal(left.intValue() << right.intValue());
		else if(left.isRegister() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isRegister() && right.isShort()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isRegister() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isRegister() && right.isLong()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isRegister() && right.isRegister()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() << right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() << right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() << right.intValue());
			else return new UnsignedLongVal(left.longValue() << right.longValue());
		}

		else if(left.isVector() && right.isBool()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() << right.byteValue()); 
        else if(left.isVector() && right.isByte()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
		else if(left.isVector() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() << right.intValue());
        else if(left.isVector() && right.isInt()) return new UnsignedIntVal(left.intValue() << right.intValue());
		else if(left.isVector() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isVector() && right.isShort()) return new UnsignedShortVal(left.shortValue() << right.shortValue());
		else if(left.isVector() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isVector() && right.isLong()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else if(left.isVector() && right.isRegister()) return new UnsignedByteVal(left.byteValue() << right.byteValue());
		else if(left.isVector() && right.isVector()) return new UnsignedLongVal(left.longValue() << right.longValue());
		else {
			errorAndExit("Inavlid == operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

    public static Value ightShift(Value left, Value right) throws Exception{
        if(left.isBool() && right.isBool()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
        else if(left.isBool() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() >> right.byteValue()); 
        else if(left.isBool() && right.isByte()) return new ByteVal(left.byteValue() >> right.byteValue());
		else if(left.isBool() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() >> right.intValue());
        else if(left.isBool() && right.isInt()) return new IntVal(left.intValue() >> right.intValue());
		else if(left.isBool() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isBool() && right.isShort()) return new ShortVal(left.shortValue() >> right.shortValue());
		else if(left.isBool() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isBool() && right.isLong()) return new LongVal(left.longValue() >> right.longValue());
		else if(left.isBool() && right.isRegister()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
		else if(left.isBool() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() >> right.intValue());
			else return new UnsignedLongVal(left.longValue() >> right.longValue());
		}

		if(left.isUnsignedByteValue() && right.isBool()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
        else if(left.isUnsignedByteValue() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() >> right.byteValue()); 
        else if(left.isUnsignedByteValue() && right.isByte()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() >> right.intValue());
        else if(left.isUnsignedByteValue() && right.isInt()) return new UnsignedIntVal(left.intValue() >> right.intValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isUnsignedByteValue() && right.isShort()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isUnsignedByteValue() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedByteValue() && right.isLong()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedByteValue() && right.isRegister()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
		else if(left.isUnsignedByteValue() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() >> right.intValue());
			else return new UnsignedLongVal(left.longValue() >> right.longValue());
		}

		if(left.isByte() && right.isBool()) return new ByteVal(left.byteValue() >> right.byteValue());
        else if(left.isByte() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() >> right.byteValue()); 
        else if(left.isByte() && right.isByte()) return new ByteVal(left.byteValue() >> right.byteValue());
		else if(left.isByte() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() >> right.intValue());
        else if(left.isByte() && right.isInt()) return new IntVal(left.intValue() >> right.intValue());
		else if(left.isByte() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isByte() && right.isShort()) return new ShortVal(left.shortValue() >> right.shortValue());
		else if(left.isByte() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isByte() && right.isLong()) return new LongVal(left.longValue() >> right.longValue());
		else if(left.isByte() && right.isRegister()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
		else if(left.isByte() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() >> right.intValue());
			else return new UnsignedLongVal(left.longValue() >> right.longValue());
		}

		else if(left.isUnsignedShort() && right.isBool()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
        else if(left.isUnsignedShort() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() >> right.shortValue()); 
        else if(left.isUnsignedShort() && right.isByte()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() >> right.intValue());
        else if(left.isUnsignedShort() && right.isInt()) return new UnsignedIntVal(left.intValue() >> right.intValue());
		else if(left.isUnsignedShort() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isUnsignedShort() && right.isShort()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isUnsignedShort() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedShort() && right.isLong()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedShort() && right.isRegister()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isUnsignedShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() >> right.intValue());
			else return new UnsignedLongVal(left.longValue() >> right.longValue());
		}

		else if(left.isShort() && right.isBool()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
        else if(left.isShort() && right.isUnsignedByteValue()) return new UnsignedShortVal(left.shortValue() >> right.shortValue()); 
        else if(left.isShort() && right.isByte()) return new ShortVal(left.shortValue() >> right.shortValue());
		else if(left.isShort() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() >> right.intValue());
        else if(left.isShort() && right.isInt()) return new IntVal(left.intValue() >> right.intValue());
		else if(left.isShort() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isShort() && right.isShort()) return new ShortVal(left.shortValue() >> right.shortValue());
		else if(left.isShort() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isShort() && right.isLong()) return new LongVal(left.longValue() >> right.longValue());
		else if(left.isShort() && right.isRegister()) return new UnsignedShortVal(left.byteValue() >> right.byteValue());
		else if(left.isShort() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 16) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() >> right.intValue());
			else return new UnsignedLongVal(left.longValue() >> right.longValue());
		}

		else if(left.isUnsignedInt() && right.isBool()) return new UnsignedIntVal(left.intValue() >> right.intValue());
        else if(left.isUnsignedInt() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() >> right.intValue()); 
        else if(left.isUnsignedInt() && right.isByte()) return new UnsignedIntVal(left.shortValue() >> right.shortValue());
		else if(left.isUnsignedInt() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() >> right.intValue());
        else if(left.isUnsignedInt() && right.isInt()) return new UnsignedIntVal(left.intValue() >> right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedShort()) return new UnsignedIntVal(left.intValue() >> right.intValue());
		else if(left.isUnsignedInt() && right.isShort()) return new UnsignedIntVal(left.intValue() >> right.intValue());
		else if(left.isUnsignedInt() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedInt() && right.isLong()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedInt() && right.isRegister()) return new UnsignedIntVal(left.intValue() >> right.intValue());
		else if(left.isUnsignedInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() >> right.intValue());
			else return new UnsignedLongVal(left.longValue() >> right.longValue());
		}

		else if(left.isInt() && right.isBool()) return new UnsignedIntVal(left.intValue() >> right.intValue());
        else if(left.isInt() && right.isUnsignedByteValue()) return new UnsignedIntVal(left.intValue() >> right.intValue()); 
        else if(left.isInt() && right.isByte()) return new IntVal(left.intValue() >> right.intValue());
		else if(left.isInt() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() >> right.intValue());
        else if(left.isInt() && right.isInt()) return new IntVal(left.intValue() >> right.intValue());
		else if(left.isInt() && right.isUnsignedShort()) return new UnsignedIntVal(left.intValue() >> right.intValue());
		else if(left.isInt() && right.isShort()) return new IntVal(left.intValue() >> right.intValue());
		else if(left.isInt() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isInt() && right.isLong()) return new LongVal(left.longValue() >> right.longValue());
		else if(left.isInt() && right.isRegister()) return new UnsignedIntVal(left.intValue() >> right.intValue());
		else if(left.isInt() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 32) return new UnsignedIntVal(left.intValue() >> right.intValue());
			else return new UnsignedLongVal(left.longValue() >> right.longValue());
		}

		else if(left.isUnsignedLong() && right.isBool()) return new UnsignedLongVal(left.longValue() >> right.longValue());
        else if(left.isUnsignedLong() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() >> right.longValue()); 
        else if(left.isUnsignedLong() && right.isByte()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedInt()) return new UnsignedLongVal(left.longValue() >> right.longValue());
        else if(left.isUnsignedLong() && right.isInt()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedShort()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedLong() && right.isShort()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedLong() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedLong() && right.isLong()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedLong() && right.isRegister()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isUnsignedLong() && right.isVector()) return new UnsignedLongVal(left.longValue() >> right.longValue());

		else if(left.isLong() && right.isBool()) return new UnsignedLongVal(left.longValue() >> right.longValue());
        else if(left.isLong() && right.isUnsignedByteValue()) return new UnsignedLongVal(left.longValue() >> right.longValue()); 
        else if(left.isLong() && right.isByte()) return new LongVal(left.longValue() >> right.longValue());
		else if(left.isLong() && right.isUnsignedInt()) return new UnsignedLongVal(left.longValue() >> right.longValue());
        else if(left.isLong() && right.isInt()) return new LongVal(left.longValue() >> right.longValue());
		else if(left.isLong() && right.isUnsignedShort()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isLong() && right.isShort()) return new LongVal(left.longValue() >> right.longValue());
		else if(left.isLong() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isLong() && right.isLong()) return new LongVal(left.longValue() >> right.longValue());
		else if(left.isLong() && right.isRegister()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isLong() && right.isVector()) return new UnsignedLongVal(left.longValue() >> right.longValue());

		else if(left.isRegister() && right.isBool()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
        else if(left.isRegister() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() >> right.byteValue()); 
        else if(left.isRegister() && right.isByte()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
		else if(left.isRegister() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() >> right.intValue());
        else if(left.isRegister() && right.isInt()) return new UnsignedIntVal(left.intValue() >> right.intValue());
		else if(left.isRegister() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isRegister() && right.isShort()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isRegister() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isRegister() && right.isLong()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isRegister() && right.isRegister()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
		else if(left.isRegister() && right.isVector()) {
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
			else if(size <= 16) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
			else if(size <= 32) return new UnsignedIntVal(left.intValue() >> right.intValue());
			else return new UnsignedLongVal(left.longValue() >> right.longValue());
		}

		else if(left.isVector() && right.isBool()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
        else if(left.isVector() && right.isUnsignedByteValue()) return new UnsignedByteVal(left.byteValue() >> right.byteValue()); 
        else if(left.isVector() && right.isByte()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
		else if(left.isVector() && right.isUnsignedInt()) return new UnsignedIntVal(left.intValue() >> right.intValue());
        else if(left.isVector() && right.isInt()) return new UnsignedIntVal(left.intValue() >> right.intValue());
		else if(left.isVector() && right.isUnsignedShort()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isVector() && right.isShort()) return new UnsignedShortVal(left.shortValue() >> right.shortValue());
		else if(left.isVector() && right.isUnsignedLong()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isVector() && right.isLong()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else if(left.isVector() && right.isRegister()) return new UnsignedByteVal(left.byteValue() >> right.byteValue());
		else if(left.isVector() && right.isVector()) return new UnsignedLongVal(left.longValue() >> right.longValue());
		else {
			errorAndExit("Inavlid == operation between " + left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
			return null;
		}

    }

    public static Value egation Value right) throws Exception{
        if (right.isReal()) return new RealVal(-right.realValue());
        else if(right.isUnsignedByteValue()) return new ShortVal(-right.byteValue());
		else if(right.isUnsignedShort()) return new UnsignedShortVal(-right.shortValue());
		else if(right.isUnsignedInt()) return new LongVal(-right.intValue());
		else if(right.isUnsignedLong()) return new LongVal(-right.longValue());
		else if(right.isByte()) return new ShortVal(-right.byteValue());
		else if(right.isShort()) return new IntVal(-right.shortValue());
		else if(right.isInt()) return new LongVal(-right.intValue());
		else if(right.isLong()) return new LongVal(-right.longValue());
		else if(right.isVector()){
			VectorVal vec = (VectorVal)right;
			int size = vec.getSize();
			if(size <= 8) return new ShortVal(-vec.byt.Value .));
			else if(size <= 16) return new IntVal(-vec.shor.Value .));
			else if(size <= 32) return new LongVal(-vec.in.Value .));
			else return new LongVal(-vec.longValue());
		} else {
			errorAndExit("Error invalid paramater type for negation operation of type " + right.getClass().getSimpleName());
			return null;
		}
    }

	public static Value otGateCircuit Value right) throws Exception{
		if(right.isWire()){
			WireVal Output = new WireVal();
			WireVal rightW = (WireVal)right;
			new NotGate(Output, rightW);

			return Output;
		} else if(right.isVector()){
			VectorVal rightV = (VectorVal)right;
			VectorVal Output = new VectorVal(0, rightV.getSize() - 1);

			int vecStart = rightV.getStart();
			int vecEnd = rightV.getEnd();

			int outStart = Output.getStart();
			int outEnd = Output.getEnd();

			while(vecStart != vecEnd){
				WireVal Input = new WireVal();
				Input.assignInput(rightV.getValue(vecStart));

				WireVal OutW = new WireVal();
				OutW.addOutput(Output.getValue(outStart));

				new NotGate(OutW, Input);

				outStart++;
				vecStart++;
			}

			return Output;
		} else {
			Utils.errorAndExit("Error invlid type for NotGate");
			return Utils.errorOccured();
		}
	}

    public static Value ogicalNegation Value right){
        return new BoolVal(!right.boolValue());
    }

    public static Value itwiseNegation Value right) throws Exception{
		if(right.isVector()) return bitwiseNegation((VectorVal)right);
		else if(right.isRegister()) return bitwiseNegation((RegVal)right);
		else if(right.isUnsignedByteValue()) return new UnsignedByteVal(~right.byteValue());
		else if(right.isUnsignedShort()) return new UnsignedShortVal(~right.shortValue());
		else if(right.isUnsignedInt()) return new UnsignedIntVal(~right.intValue());
		else if(right.isUnsignedLong()) return new UnsignedLongVal(~right.longValue());
		else if(right.isByte()) return new ByteVal(~right.byteValue());
		else if(right.isShort()) return new ShortVal(~right.shortValue());
		else if(right.isInt()) return new IntVal(~right.intValue());
		else if(right.isLong()) return new LongVal(~right.longValue());
		else {
			errorAndExit("Error Unknown type for bitwise negation " + right.getClass().getSimpleName());
			return null;
		}
    }

	private static VectorVal bitwiseNegation(VectorVal vec){
		VectorVal vec2 = new VectorVal(vec.getIndex1(), vec.getIndex2());

		int start = vec.getStart();
		int end = vec.getEnd();
		for(int i = start; i <= end; i++){
			boolean state = vec.getValue(i).getStateSignal();
			vec2.setValue(i, new RegVal(state));
		}

		return vec2;
	}

	private static RegVal bitwiseNegation(RegVal reg){
		boolean signal = reg.getStateSignal();
		return new RegVal(!signal);
	}

	/**
	 * This is used to visit casedz statements in verilog
	 * 
	 * @param assign
	 * @throws Exception
	 */

	public static boolean caseBoolean.Value .target,.Value .Val) throws Exception{
		if (Val instanceof Pattern) {
			Pattern pat = (Pattern)Val;
			return pat.match(target);
		} else {
			return target.longValue() == Val.longValue();
		}
	}

	public static String getParamaterName(ModuleItem Item){
		if(Item instanceof Input.Reg.Scalar.Ident){
			Input.Reg.Scalar.Ident InputItem = (Input.Reg.Scalar.Ident)Item;
			return InputItem.declarationIdentifier;
		} else if(Item instanceof Input.Reg.Vector.Ident){
			Input.Reg.Vector.Ident InputItem = (Input.Reg.Vector.Ident)Item;
			return InputItem.declarationIdentifier;
		} else if(Item instanceof Input.Wire.Scalar.Ident){
			Input.Wire.Scalar.Ident InputItem = (Input.Wire.Scalar.Ident)Item;
			return InputItem.declarationIdentifier;
		} else if(Item instanceof Input.Wire.Vector.Ident){
			Input.Wire.Vector.Ident InputItem = (Input.Wire.Vector.Ident)Item;
			return InputItem.declarationIdentifier;
		} else {
			return null;
		}
	}

    /**
	 * I created two types of assignments for verilog and the difference breaks down to how
	 * registers are assigned to one another In a shallow assignment the.Value .is coppied
	 * over from one register to another a register can hold a True or a False.Value .
	 * representing whether the output from that register is high or low So the assignment
	 * works by assigning the.Value .from one register to the.Value .of the current register.
	 * This is different then actually changing the literal register object in a deep
	 * assignment. After a shallow assignent if a signal change occours in the register that
	 * was assigned it will not affect the register that the.Value .was assigned to. In other
	 * words it makes a copy.
	 */

	public static void shallowAssign(VectorVal vec1, VectorVal vec2){
		if (vec1.getSize() == vec2.getSize()) {
			int start1 = vec1.getStart();
			int start2 = vec2.getStart();
			int end1 = vec1.getEnd();
			int end2 = vec2.getEnd();

			int sIndex1 = start1;
			int sIndex2 = start2;

			while(sIndex1 <= end1) {
				RegVal elem1 = (RegVal)vec1.getValue(sIndex1);
				boolean signal = vec2.getValue(sIndex2).getStateSignal();
				elem1.setSignal(signal);
				sIndex1++;
				sIndex2++;
			}

		} else if (vec1.getSize() < vec2.getSize()) {
			int start1 = vec1.getStart();
			int start2 = vec2.getStart();
			int end1 = vec1.getEnd();
			int end2 = vec2.getEnd();

			int sIndex1 = start1;
			int sIndex2 = start2 + (vec2.getSize() - vec1.getSize());

			while(sIndex1 <= end1) {
				RegVal elem1 = (RegVal)vec1.getValue(sIndex1);
				boolean signal = vec2.getValue(sIndex2).getStateSignal();
				elem1.setSignal(signal);
				sIndex1++;
				sIndex2++;
			}

		} else {
			int start1 = (vec1.getIndex1() < vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
			int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
			int end1 = (vec1.getIndex1() > vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
			int end2 = (vec2.getIndex1() > vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();

			int sIndex1 = start1;
			int sIndex2 = start2;

			while(sIndex2 <= end2) {
				RegVal elem1 = (RegVal)vec1.getValue(sIndex1);
				boolean signal = vec2.getValue(sIndex2).getStateSignal();
				elem1.setSignal(signal);
				sIndex1++;
				sIndex2++;
			}

			while(sIndex1 <= end1) {
				RegVal elem1 = (RegVal)vec1.getValue(sIndex1);
				elem1.setSignal(false);
				sIndex1++;
			}

		}

	}

	/**
	 * I created two types of assignments for verilog and the difference breaks down to how
	 * registers are assigned to one another In a shallow assignment the.Value .is coppied
	 * over from one register to another a register can hold a True or a False.Value .
	 * representing whether the output from that register is high or low So the assignment
	 * works by assigning the.Value .from one register to the.Value .of the current register.
	 * This is different then actually changing the literal register object in a deep
	 * assignment. After a shallow assignent if a signal change occours in the register that
	 * was assigned it will not affect the register that the.Value .was assigned to. In other
	 * words it makes a copy.
	 */
	public static void shallowAssign(VectorVal vec1, int index, VectorVal vec2){
		int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
		((RegVal)vec1.getValue(index)).setSignal(vec2.getValue(start2).getStateSignal());
	}

	public static void shallowAssign(VectorVal vec1, String str2){

		if (vec1.getSize() == str2.length()) {
			int start1 = vec1.getIndex1();
			int start2 = 0;
			int end1 = vec1.getIndex2();
			int end2 = str2.length() - 1;

			int sIndex2 = start2;

			if (start1 <= end1) {

				for (int i = start1; i <= end1; i++) {
					RegVal elem1 = (RegVal)vec1.getValue(i);
					boolean signal = str2.charAt(sIndex2) == '1' ? true : false;
					elem1.setSignal(signal);
					sIndex2++;
				}

			} else {

				for (int i = start1; i >= end1; i--) {
					RegVal elem1 = (RegVal)vec1.getValue(i);
					boolean signal = str2.charAt(sIndex2) == '1' ? true : false;
					elem1.setSignal(signal);
					sIndex2++;
				}

			}

		} else if (vec1.getSize() < str2.length()) {
			int start1 = vec1.getIndex1();
			int start2 = 0;
			int end1 = vec1.getIndex2();
			int end2 = str2.length() - 1;

			int sIndex2 = start2 + (str2.length() - vec1.getSize());

			if (start1 <= end1) {

				for (int i = start1; i <= end1; i++) {
					RegVal elem1 = (RegVal)vec1.getValue(i);
					boolean signal = str2.charAt(sIndex2) == '1' ? true : false;
					elem1.setSignal(signal);
					sIndex2++;
				}

			} else {

				for (int i = start1; i >= end1; i--) {
					RegVal elem1 = (RegVal)vec1.getValue(i);
					boolean signal = str2.charAt(sIndex2) == '1' ? true : false;
					elem1.setSignal(signal);
					sIndex2++;
				}

			}

		} else {
			int start1 = vec1.getIndex1();
			int start2 = 0;
			int end1 = vec1.getIndex2();
			int end2 = str2.length() - 1;

			int sIndex1 = start1;
			int sIndex2 = start2;
			int incr = vec1.getIndex1() <= vec1.getIndex2() ? 1 : -1;

			while(sIndex2 <= end2) {
				RegVal elem1 = (RegVal)vec1.getValue(sIndex1);
				boolean signal = str2.charAt(sIndex2) == '1' ? true : false;
				elem1.setSignal(signal);
				sIndex1 += incr;
				sIndex2++;
			}

			if (start1 <= end1) {

				for (int i = sIndex1; i <= end1; i++) {
					RegVal elem1 = (RegVal)vec1.getValue(i);
					elem1.setSignal(false);
				}

			} else {

				for (int i = sIndex1; i >= end1; i--) {
					RegVal elem1 = (RegVal)vec1.getValue(i);
					elem1.setSignal(false);
				}

			}

		}

	}

	/**
	 * I created two types of assignments for verilog and the difference breaks down to how
	 * registers are assigned to one another In a shallow assignment the.Value .is coppied
	 * over from one register to another a register can hold a True or a False.Value .
	 * representing whether the output from that register is high or low So the assignment
	 * works by assigning the.Value .from one register to the.Value .of the current register.
	 * This is different then actually changing the literal register object in a deep
	 * assignment. After a shallow assignent if a signal change occours in the register that
	 * was assigned it will not affect the register that the.Value .was assigned to. In other
	 * words it makes a copy.
	 */
	public static void shallowAssign(VectorVal vec1, int index1, int index2, VectorVal vec2){

		int sliceSize = ((index1 > index2) ? index1 : index2) - ((index1 < index2) ? index1 : index2) + 1;

		if (sliceSize == vec2.getSize()) {
			int start1 = (index1 < index2) ? index1 : index2;
			int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
			int end1 = (index1 > index2) ? index1 : index2;
			int end2 = (vec2.getIndex1() > vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();

			int sIndex1 = start1;
			int sIndex2 = start2;

			while(sIndex1 <= end1) {
				RegVal elem1 = (RegVal)vec1.getValue(sIndex1);
				boolean signal = vec2.getValue(sIndex2).getStateSignal();
				elem1.setSignal(signal);
				sIndex1++;
				sIndex2++;
			}

		} else if (sliceSize < vec2.getSize()) {
			int start1 = (index1 < index2) ? index1 : index2;
			int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
			int end1 = (index1 > index2) ? index1 : index2;
			int end2 = (vec2.getIndex1() > vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();

			int sIndex1 = start1;
			int sIndex2 = start2 + (vec2.getSize() - sliceSize);

			while(sIndex1 <= end1) {
				RegVal elem1 = (RegVal)vec1.getValue(sIndex1);
				boolean signal = vec2.getValue(sIndex2).getStateSignal();
				elem1.setSignal(signal);
				sIndex1++;
				sIndex2++;
			}

		} else {
			int start1 = (index1 < index2) ? index1 : index2;
			int start2 = (vec2.getIndex1() < vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();
			int end1 = (index1 > index2) ? index1 : index2;
			int end2 = (vec2.getIndex1() > vec2.getIndex2()) ? vec2.getIndex1() : vec2.getIndex2();

			int sIndex1 = start1;
			int sIndex2 = start2;

			while(sIndex2 <= end2) {
				RegVal elem1 = (RegVal)vec1.getValue(sIndex1);
				boolean signal = vec2.getValue(sIndex2).getStateSignal();
				elem1.setSignal(signal);
				sIndex1++;
				sIndex2++;
			}

			while(sIndex1 <= end1) {
				RegVal elem1 = (RegVal)vec1.getValue(sIndex1);
				elem1.setSignal(false);
				sIndex1++;
			}

		}

	}
	
	/**
	 * In a deep assignment the.Value .is assigned by reference. So the assignment works by
	 * literally replacing a register that is in their with another register. This is useful
	 * for a continuous assignment in verilog. As opposed to observing the expression on the
	 * right hand side with a loop and copying changes to the left hand side I am actually
	 * making the objects that are on the right hand side the things that are on the left
	 * hand side so changes come across in both variables.
	 * 
	 * @author Jacob Bauer
	 * @throws Exception
	 */

	public static void deepAssign(VectorVal vec1, int index,.Value .vec2) throws Exception{
		CircuitElem assignTo = vec1.getValue(index);
		Utils.deepAssign(assignTo, vec2);
	}

	public static void deepAssign(ArrayVal.Value . arr1, int index,.Value .vec2) throws Exception{
	.Value .arrVal = arr1.ElemAtIndex(index);
		Utils.deepAssign((CircuitElem)arrVal, vec2);
	}

	public static void deepAssign(VectorVal vec1, int index1, int index2,.Value .elem2) throws Exception{
		int start = vec1.getStart();
		int end = vec1.getEnd();
		Utils.deepAssign(vec1, start, elem2);
		int sIndex1 = start + 1;

		while(sIndex1 <= end) {
			Utils.deepAssign(vec1, sIndex1, elem2);
			sIndex1++;
		}
	}

	public static void deepAssign(VectorVal vec1, CircuitElem elem2){
		int start = (vec1.getIndex1() < vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
		int end = (vec1.getIndex1() > vec1.getIndex2()) ? vec1.getIndex1() : vec1.getIndex2();
		Utils.deepAssign(vec1, start, elem2);
		int sIndex1 = start + 1;

		while(sIndex1 <= end) {
			Utils.deepAssign(vec1, sIndex1, elem2);
			sIndex1++;
		}
	}

	public static void deepAssign(VectorVal vec1, int index1, CircuitElem elem2){ 
		WireVal vec.Value .= new WireVal();
		vec.Value .addOutput(vec1.getValue(index1));
		vec.Value .assignInput(elem2);
	}

	public static void deepAssign(CircuitElem elemTo,.Value .vec2) throws Exception{
		if(vec2.isWire()){
			WireVal connector = new WireVal();
			WireVal vec2Wire = (WireVal)vec2;
			connector.assignInput(elemTo);
			connector.addOutput(vec2Wire);
		} else if(vec2.isRegister()){
			WireVal connector = new WireVal();
			RegVal vec2Reg = (RegVal)vec2;
			connector.assignInput(elemTo);
			connector.addOutput(vec2Reg);
		} else {
			Utils.errorAndExit("Error and exit  : " + elemTo.getClass().getName() + " and " + vec2.getClass().getName());
		}
	}

	public static void deepAssign(VectorVal vec, VectorVal vector){
		if (vec.getSize() == vector.getSize()) {
			int start1 = vec.getStart();
			int start2 = vector.getStart();
			int end1 =  vec.getEnd();

			int sIndex1 = start1;
			int sIndex2 = start2;

			while(sIndex1 <= end1) {
				Utils.deepAssign(vec, sIndex1, vector.getValue(sIndex2));
				sIndex1++;
				sIndex2++;
			}
		} else if (vec.getSize() < vector.getSize()) {
			int start1 = vec.getStart();
			int start2 = vector.getStart();
			int end1 =  vec.getEnd();
			int end2 = vector.getEnd();

			int sIndex1 = start1;
			int sIndex2 = start2 + (vector.getSize() - vec.getSize());

			while(sIndex1 <= end1) {
				Utils.deepAssign(vec, sIndex1, vector.getValue(sIndex2));
				sIndex1++;
				sIndex2++;
			}

		} else {
			int start1 = vec.getStart();
			int start2 = vector.getStart();
			int end1 =  vec.getEnd();
			int end2 = vector.getEnd();

			int sIndex1 = start1;
			int sIndex2 = start2;

			while(sIndex2 <= end2) {
				Utils.deepAssign(vec, sIndex1, vector.getValue(sIndex2));
				sIndex1++;
				sIndex2++;
			}

			while(sIndex1 <= end1) {
				Utils.deepAssign(vec, sIndex1, vector.getValue(sIndex2));
				sIndex1++;
			}
		}
	}

	public static void deepAssign(VectorVal vec1, int index1, int index2, VectorVal vec2){
		int sliceSize = ((index1 > index2) ? index1 : index2) - ((index1 < index2) ? index1 : index2) + 1;

		if (sliceSize == vec2.getSize()) {
			int start1 = (index1 < index2) ? index1 : index2;
			int start2 = vec2.getStart();
			int end1 = (index1 > index2) ? index1 : index2;
			int end2 = vec2.getEnd();

			int sIndex1 = start1;
			int sIndex2 = start2;

			while(sIndex1 <= end1) {
				Utils.deepAssign(vec1, sIndex1, vec2.getValue(sIndex2));
				sIndex1++;
				sIndex2++;
			}

		} else if (sliceSize < vec2.getSize()) {
			int start1 = (index1 < index2) ? index1 : index2;
			int start2 = vec2.getStart();
			int end1 = (index1 > index2) ? index1 : index2;
			int end2 = vec2.getEnd();

			int sIndex1 = start1;
			int sIndex2 = start2 + (vec2.getSize() - sliceSize);

			while(sIndex1 <= end1) {
				Utils.deepAssign(vec1, sIndex1, vec2.getValue(sIndex2));
				sIndex1++;
				sIndex2++;
			}

		} else {
			int start1 = (index1 < index2) ? index1 : index2;
			int start2 = vec2.getStart();
			int end1 = (index1 > index2) ? index1 : index2;
			int end2 = vec2.getEnd();

			int sIndex1 = start1;
			int sIndex2 = start2;

			while(sIndex2 <= end2) {
				Utils.deepAssign(vec1, sIndex1, vec2.getValue(sIndex2));
				sIndex1++;
				sIndex2++;
			}

			while(sIndex1 <= end1) {
				Utils.deepAssign(vec1, sIndex1, vec2.getValue(sIndex2));
				sIndex1++;
			}
		}
	}

	public static void shallowAssign(CircuitElem elem1, VectorVal vec2) throws Exception{
		int index = vec2.getStart();
		Utils.shallowAssign(elem1, vec2.getValue(index));
	}

	public static void shallowAssign(VectorVal vec1, CircuitElem elem2) throws Exception{
		int start = vec1.getStart();
		int end = vec1.getEnd();
		Utils.shallowAssign(vec1.getValue(start), elem2);
		int sIndex1 = start + 1;

		while(sIndex1 <= end) {
			Utils.shallowAssign(vec1.getValue(sIndex1), elem2);
			sIndex1++;
		}
	}

	public static void shallowAssign(CircuitElem elem1, CircuitElem elem2) throws Exception{
		if(elem1.isRegister()){
			RegVal reg1 = (RegVal)elem1;
			reg1.setSignal(elem2.getStateSignal());
		} else {
			Utils.errorAndExit("CircuitElement shallow asignment cannoot take place with types " + elem1.getClass().getName() + " and " + elem2.getClass().getName());
		}
	}

	public static void shallowAssign(VectorVal vec1, int index1, int index2, CircuitElem elem2) throws Exception{
		int start = vec1.getStart();
		int end = vec1.getEnd();
		
		Utils.shallowAssign(vec1.getValue(start), elem2);
		int sIndex1 = start + 1;

		while(sIndex1 <= end) {
			Utils.shallowAssign(vec1.getValue(sIndex1), false);
			sIndex1++;
		}
	}

	public static void shallowAssign(VectorVal vec1, int index1, CircuitElem elem2){
		RegVal reg1 = (RegVal)vec1.getValue(index1);
		reg1.setSignal(elem2.getStateSignal());
	}

	public static void shallowAssign(VectorVal vec1, long int2){

		if (vec1.getSize() <= 64) {
			int over = 0;
			int start = vec1.getStart();
			int end = vec1.getEnd();

			for (int i = start; i <= end; i++) {
				boolean signal = ((int2 >> over) & 1) > 0;
				Utils.shallowAssign(vec1, i, signal);
				over++;
			}

		} else {
			int start = vec1.getStart();
			int end = vec1.getEnd();
			int sIndex1 = start;

			for (int i = 0; i < 64; i++) {
				boolean signal = ((int2 >> i) & 1) > 0;
				Utils.shallowAssign(vec1, sIndex1, signal);
				sIndex1++;
			}

			while(sIndex1 <= end) {
				Utils.shallowAssign(vec1, sIndex1, false);
				sIndex1++;
			}
		}

	}

	public static void shallowAssign(VectorVal vec1, int index1, int index2, long int2){
		int sliceSize = ((index1 > index2) ? index1 : index2) - ((index1 < index2) ? index1 : index2) + 1;

		if (sliceSize <= 64) {
			int start = (index1 < index2) ? index1 : index2;
			int end = (index1 > index2) ? index1 : index2;
			int over = 0;

			for (int i = start; i <= end; i++) {
				boolean signal = ((int2 >> over)& 1) > 0;
				Utils.shallowAssign(vec1, i, signal);
				over++;
			}

		} else {
			int start = (index1 < index2) ? index1 : index2;
			int end = (index1 > index2) ? index1 : index2;
			int sIndex1 = start;

			for (int i = 0; i < 64; i++) {
				boolean signal = ((int2 >> i) & 1) > 0;
				Utils.shallowAssign(vec1, sIndex1, signal);
				sIndex1++;
			}

			while(sIndex1 <= end) {
				Utils.shallowAssign(vec1, sIndex1, false);
				sIndex1++;
			}
		}
	}

	public static void shallowAssign(VectorVal vec1, int index1, long int2){
		RegVal reg1 = (RegVal)vec1.getValue(index1);
		boolean signal = (int2 & 1) > 0;
		reg1.setSignal(signal);
	}

	public static void shallowAssign(CircuitElem elem1, long int2){
		RegVal reg1 = (RegVal)elem1;
		boolean signal = (int2 & 1) > 0; 
		reg1.setSignal(signal); 
	}

	public static void shallowAssign(VectorVal vec1, boolean bool2){
		int start = vec1.getStart();
		int end = vec1.getEnd();

		Utils.shallowAssign(vec1, start, bool2);
		int sIndex1 = start + 1;

		for (int i = sIndex1; i <= end; i++) { 
			Utils.shallowAssign(vec1, i, false);
		}
	}

	public static void shallowAssign(VectorVal vec1, int index1, int index2, boolean bool2){
		int start = (index1 < index2) ? index1 : index2;
		int end = (index1 > index2) ? index1 : index2;
		Utils.shallowAssign(vec1, start, bool2);
		int sIndex1 = start + 1;

		for (int i = sIndex1; i <= end; i++) { 
			Utils.shallowAssign(vec1, i, false);
		}
	}

	public static void shallowAssign(VectorVal vec1, int index1, boolean bool2){
		RegVal reg1 = (RegVal)vec1.getValue(index1);
		reg1.setSignal(bool2);
	}

	public static void shallowAssign(CircuitElem elem1, boolean bool2){ 
		RegVal reg1 = (RegVal)elem1;
		reg1.setSignal(bool2);
	}

	public static boolean numberIsPattern(String Number){
		return Number.contains("x") || Number.contains("X") || Number.contains("z") || Number.contains("Z");
	}

	public static Object getRa.Value .Value val){
		if(val.isBool()) return val.in.Value .);
		else if(val.isReal()) return val.rea.Value .);
		else if(val.isString()) return val.toString();
		else if(val.isUnsignedByteValue()) return val.shor.Value .);
		else if(val.isByte()) return val.byt.Value .);
		else if(val.isUnsignedShort()) return val.in.Value .);
		else if(val.isShort()) return val.shor.Value .);
		else if(val.isUnsignedInt()) return val.longValue();
		else if(val.isInt()) return val.in.Value .);
		else return val.longValue();
	}

	public static String formatString(StrVal valString, List.Value ..Value .
		int.Value .ndex = 0;
		int valStrIndex = 0;
		int valStrSize = valString.length();

		StringBuilder result = new StringBuilder();
		while(valStrIndex < valStrSize){
			char currentChar = valString.charAt(valStrIndex);
			if(currentChar == '%'){
				if(valStrIndex + 1 < valStrSize){
					char nextChar = valString.charAt(valStrIndex + 1);
					if(nextChar == 'd'){
					.Value .nextVal =.Value .et.Value .ndex);
						result.append(nextVal.in.Value .));
						valStrIndex += 2;
					.Value .ndex++;
					} else if(nextChar == 'f'){
					.Value .nextVal =.Value .et.Value .ndex);
						result.append(nextVal.rea.Value .));
						valStrIndex +=2;
					.Value .ndex++;
					} else {
						result.append(currentChar);
						valStrIndex++;
					}
				} else {
					result.append(currentChar);
					valStrIndex++;
				}
			} else {
				result.append(currentChar);
				valStrIndex++;
			}
		}

		return result.toString();
	}

	public static Value onvertToRa.Value .Object obj){
		if(obj instanceof Integer) return new IntVal((int)obj);
		else if(obj instanceof Boolean) return new BoolVal((boolean)obj);
		else if(obj instanceof Long) return new LongVal((long)obj);
		else if(obj instanceof Short) return new ShortVal((short)obj);
		else if(obj instanceof Byte) return new ByteVal((byte)obj);
		else return new StrVal((String)obj);
	}

	public static Value etOptimalForm(VectorVal vec){
		if(vec.getSize() == 1) return new RegVal(vec.getValue(vec.getStart()).getStateSignal());
		else if(vec.getSize() == 8) return new UnsignedByteVal(vec.byt.Value .));
		else if(vec.getSize() == 16) return new UnsignedShortVal(vec.shor.Value .));
		else if(vec.getSize() == 32) return new UnsignedIntVal(vec.in.Value .));
		else if(vec.getSize() == 64) return new UnsignedLongVal(vec.longValue());
		else return vec;
	}

	public static Value etOptimalUnsignedForm(long.Value .{
		if.Value .<= 255) return new UnsignedByteVal((byte.Value .;
		else if.Value .<= 65535) return new UnsignedShortVal((short.Value .;
		else if.Value .<= 16777215) return new UnsignedIntVal((int.Value .;
		else return new UnsignedLongVal.Value .;
	}

	public static void shallowAssignElem(Value leftHandDeref,(Value leftHandIndex,.Value .expVal) throws Exception{
		if(leftHandDeref instanceof ArrayVectorVal){
			ArrayVectorVal leftHandArray = (ArrayVectorVal)leftHandDeref;
			VectorVal vec = leftHandArray.ElemAtIndex(leftHandIndex.in.Value .));
			Utils.shallowAssign(vec, expVal.longValue());
		} else if(leftHandDeref instanceof ArrayRegVal){
			ArrayRegVal leftHandArray = (ArrayRegVal)leftHandDeref;
			RegVal vec = leftHandArray.ElemAtIndex(leftHandIndex.in.Value .));
			vec.setSignal(expVal.boo.Value .));	
		} else if(leftHandDeref instanceof ArrayIntVal){
			ArrayIntVal leftHandArray = (ArrayIntVal)leftHandDeref;
			leftHandArray.SetElemAtIndex(leftHandIndex.in.Value .), new UnsignedIntVal(expVal.in.Value .)));
		} else if(leftHandDeref instanceof VectorVal){
			VectorVal leftHandVector = (VectorVal)leftHandDeref;
			CircuitElem elem = leftHandVector.getValue(leftHandIndex.in.Value .));
			if(elem instanceof RegVal){
				RegVal elemReg = (RegVal)elem;
				elemReg.setSignal(expVal.boo.Value .));
			} else {
				Utils.errorAndExit("Error: Invalid Type for soft assignment " + elem.getClass().getName());
			}
		} else {
			Utils.errorAndExit("Error: Invalid Type for left hand side of the assignment " + leftHandDeref.getClass().getName());
		}
	 }

	public static void shallowAssignSlice(Value leftHandDeref,(Value leftHandStartIndex,(Value leftHandEndIndex,.Value .expVal) throws Exception{
		if (leftHandDeref instanceof VectorVal) {
			VectorVal leftHandVector = (VectorVal)leftHandDeref;

			Utils.shallowAssign(leftHandVector, leftHandStartIndex.in.Value .), leftHandEndIndex.in.Value .), expVal.longValue());
		} else {
			Utils.errorAndExit("Invalid Type for the left hand side of the slice assingment " + leftHandDeref.getClass().getName());
		}
	}
	
	public static void shallowAssignIdent(Pointer.Value . leftHandPtr,.Value .expVal) {
	(Value leftHandDeref = leftHandPtr.deRefrence();
		if (leftHandDeref instanceof VectorVal) {
			// If it is a vector then we need to use the OpUtil.shallowAssign on the Vector
			VectorVal Vec = (VectorVal)leftHandDeref;
			Utils.shallowAssign(Vec, expVal.longValue());
		} else if (leftHandDeref instanceof RegVal) {
			RegVal reg = (RegVal)leftHandDeref;
			Utils.shallowAssign(reg, expVal.boo.Value .));
		} else {
			// If it is not a vector then just replace the.Value .with whatever is on the Right Hand
			// Side
			leftHandPtr.assign(expVal);
		}
	}

	public static void fClose.Value .fileDescriptor, Environment environment) throws Exception{
		FormattedScanner Scanner = environment.getFileReader(fileDescriptor.in.Value .));

		try {
			Scanner.close();
			environment.clearFileReader(fileDescriptor.in.Value .));
		} catch (Exception exp) {
			Utils.errorAndExit(exp.toString());
		}
	}

	public static int addVecSize.Value .res, int size){ // TODO Auto-generated method stub
		if (res.isVector()) {
			return size + ((VectorVal)res).getSize();
		} else {
			return size + 1;
		} 
	}

	public static int assignVectorInConcatenation(VectorVal newVec,.Value .valExp, int total){ // TODO Auto-generated method stub
		if (valExp.isVector()) {
			VectorVal vec = (VectorVal)valExp;

			if (vec.getIndex1() <= vec.getIndex2()) {

				for (int v = vec.getIndex1(); v <= vec.getIndex2(); v++, total--) {
					newVec.setValue(total, new RegVal(vec.getValue(v).getStateSignal()));
				}

			} else {

				for (int v = vec.getIndex1(); v >= vec.getIndex2(); v--, total--) {
					newVec.setValue(total, new RegVal(vec.getValue(v).getStateSignal()));
				}

			}

		} else {
			CircuitElem circ = (CircuitElem)valExp;
			newVec.setValue(total, circ);
			total--;
		}
		return total;
	}

	public static Value etShallowElemFromIndex.Value .expr,.Value .dataObject, String ident) throws Exception{ // TODO Auto-generated method stub
		if (dataObject instanceof ArrayVectorVal) {
			ArrayVectorVal arr = (ArrayVectorVal)dataObject;
			VectorVal vec = arr.ElemAtIndex(expr.in.Value .));
			return Utils.getOptimalForm(vec);
		} else if (dataObject instanceof ArrayRegVal) {
			ArrayRegVal arr = (ArrayRegVal)dataObject;
			return arr.ElemAtIndex(expr.in.Value .));
		} else if (dataObject instanceof ArrayIntVal) {
			ArrayIntVal arr = (ArrayIntVal)dataObject;
			return arr.ElemAtIndex(expr.in.Value .));
		} else if (dataObject instanceof VectorVal) {
			return ((VectorVal)dataObject).getValue(expr.in.Value .));
		} else {
			Utils.errorAndExit("Unkown array type for " + ident + " [ Type -> " + dataObject.getClass() + " ]");
			return Utils.errorOccured();
		}
	}

	public static Value etShallowSliceFromFromIndices.Value .startIndex,.Value .endIndex,.Value .dataObject, String ident) throws Exception{ // TODO Auto-generated method stub
		if(dataObject instanceof VectorVal) {
			VectorVal toRet = ((VectorVal)dataObject).getShallowSlice(startIndex.in.Value .), endIndex.in.Value .));
			return toRet;
		} else {
			Utils.errorAndExit("Unkown slice type for " + ident + " [ Type -> " + dataObject.getClass() + " ]");
			return Utils.errorOccured();
		}
	}

	public static void assignDeepElem.Value .deRefrence,.Value .in.Value ..Value .expressionResult) throws Exception{
		if (deRefrence instanceof VectorVal) {
			Utils.deepAssign((VectorVal)deRefrence, in.Value .in.Value .), expressionResult);
		} else if (deRefrence instanceof ArrayVal) {
			Utils.deepAssign((ArrayVal.Value .)deRefrence, in.Value .in.Value .), expressionResult);
		} else {
			Utils.errorAndExit("Error: Could not exit the program because the right side is of an invalid type "
				+ expressionResult.getClass().getName());
		}
	}

	public static void assignDeepSlice.Value .vector,.Value .begin,.Value .end,.Value .vector2) throws Exception{
		if (vector instanceof VectorVal){
			VectorVal Elems = (VectorVal)vector;
			Utils.deepAssign(Elems, begin.in.Value .), end.in.Value .), vector2);
		} else {
			Utils.errorAndExit("Error: Invalid Type for slice expression");
		}
	}
}


























































