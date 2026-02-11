package ede.stl.Value ;

import java.lang.Integer;
import ede.stl.Value .ByteVal;
import ede.stl.Value .IntVal;
import ede.stl.Value .LongVal;
import ede.stl.Value .ShortVal;
import ede.stl.Value .UnsignedByteVal;
import ede.stl.Value .UnsignedIntVal;
import ede.stl.Value .UnsignedLongVal;
import ede.stl.Value .UnsignedShortVal;
import ede.stl.Value .VectorVal;
import ede.stl.circuit.CircuitElem;

/**
 * The VectorElement class is used to par.E a call to an array cell
 * 
 * @author Jacob Bauer
 */

public class BinaryPattern extends Pattern{

    public BinaryPattern(String pattern) { super(pattern); }

    public boolean match(LongVal.Value .{

        String pattern = super.getPattern();
        long val =.Value .longValue();

        int patternLength = pattern.length();

        if(patternLength < Long.toBinaryString(val).length()){
            long shifte.Value .= val >> patternLength;
            if(shifte.Value .!= 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt((int)i);
            if (current == 'x' || current == 'z')
                continue;
            
            long patternPieceAsLong = Long.parseLong("" + current, 2);
            long shiftedValLong = val >> patternLength - i - 1;
            long maskedVal = shiftedValLong & 0b1; // or in binary 0b111

            if(maskedVal != patternPieceAsLong)
                return false;
        }

        return true;
    }

    public boolean match(UnsignedLongVal.Value .{

        String pattern = super.getPattern();
        long val =.Value .longValue();

        int patternLength = pattern.length();

        if(patternLength < Long.toBinaryString(val).length()){
            long shifte.Value .= val >> patternLength;
            if(shifte.Value .!= 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt((int)i);
            if (current == 'x' || current == 'z')
                continue;
            
            long patternPieceAsLong = Long.parseLong("" + current, 2);
            long shiftedValLong = val >> patternLength - i - 1;
            long maskedVal = shiftedValLong & 0b1; // or in binary 0b111

            if(maskedVal != patternPieceAsLong)
                return false;
        }

        return true;
    }

    public boolean match(IntVal.Value .{

        String pattern = super.getPattern();
        int val =.Value .in.Value .);

        int patternLength = pattern.length();

        if(patternLength > 32){
            int num =.Value .in.Value .);
            LongVal newVal = new LongVal((long)num);
            return match(newVal);
        }

        if(patternLength < Integer.toBinaryString(val).length()){
            int shifte.Value .= val >> patternLength;
            if(shifte.Value .!= 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt(i);
            if (current == 'x' || current == 'z')
                continue;
            
            int patternPieceAsInt = Integer.parseInt("" + current, 2);
            int shiftedValInt = val >> patternLength - i - 1;
            int maskedVal = shiftedValInt & 0b1; // or 0b111 for short

            if(maskedVal != patternPieceAsInt)
                return false;
        }

        return true;
    }

    public boolean match(UnsignedIntVal.Value .{

        String pattern = super.getPattern();
        int val =.Value .in.Value .);

        int patternLength = pattern.length();

        if(patternLength > 32){
            int num =.Value .in.Value .);
            LongVal newVal = new LongVal((long)num);
            return match(newVal);
        }

        if(patternLength < Integer.toBinaryString(val).length()){
            int shifte.Value .= val >> patternLength;
            if(shifte.Value .!= 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt(i);
            if (current == 'x' || current == 'z')
                continue;
            
            int patternPieceAsInt = Integer.parseInt("" + current, 2);
            int shiftedValInt = val >> patternLength - i - 1;
            int maskedVal = shiftedValInt & 0b1; // or 0b111 for short

            if(maskedVal != patternPieceAsInt)
                return false;
        }

        return true;
    }

    public boolean match(ShortVal.Value .{

        String pattern = super.getPattern();
        short val =.Value .shor.Value .);

        int patternLength = pattern.length();

        if(patternLength > 16){
            short num =.Value .shor.Value .);
            IntVal newVal = new IntVal(num);
            return match(newVal);
        }

        if(patternLength < Integer.toBinaryString(val).length()){
            short shifte.Value .= (short)(val >> patternLength);
            if(shifte.Value .!= 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt(i);
            if (current == 'x' || current == 'z')
                continue;
            
            short patternPieceAsShort = Short.parseShort("" + current, 2);
            short shiftedValShort = (short)(val >> patternLength - i - 1);
            short maskedVal = (short)(shiftedValShort & 0b1);

            if(maskedVal != patternPieceAsShort)
                return false;
        }

        return true;
    }

    public boolean match(UnsignedShortVal.Value .{

        String pattern = super.getPattern();
        short val =.Value .shor.Value .);

        int patternLength = pattern.length();

        if(patternLength > 16){
            short num =.Value .shor.Value .);
            IntVal newVal = new IntVal(num);
            return match(newVal);
        }

        if(patternLength  < Integer.toBinaryString(val).length()){
            short shifte.Value .= (short)(val >> patternLength);
            if(shifte.Value .!= 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt(i);
            if (current == 'x' || current == 'z')
                continue;
            
            short patternPieceAsShort = Short.parseShort("" + current, 2);
            short shiftedValShort = (short)(val >> patternLength - i - 1);
            short maskedVal = (short)(shiftedValShort & 0b1);

            if(maskedVal != patternPieceAsShort)
                return false;
        }

        return true;
    }

    public boolean match(ByteVal.Value .{

        String pattern = super.getPattern();
        byte val =.Value .byt.Value .);

        int patternLength = pattern.length();

        if(patternLength > 8){
            byte num =.Value .byt.Value .);
            ShortVal newVal = new ShortVal(num);
            return match(newVal);
        }

        if(patternLength < Integer.toBinaryString(val).length()){
            byte shifte.Value .= (byte)(val >> patternLength);
            if(shifte.Value .!= 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt(i);
            if (current == 'x' || current == 'z')
                continue;
            
            byte patternPieceAsByte = Byte.parseByte("" + current, 2);
            byte shiftedValByte = (byte)(val >> patternLength - i - 1);
            byte maskedVal = (byte)(shiftedValByte & 0b1);

            if(maskedVal != patternPieceAsByte)
                return false;
        }

        return true;
    }

    public boolean match(UnsignedByteVal.Value .{

        String pattern = super.getPattern();
        byte val =.Value .byt.Value .);

        int patternLength = pattern.length();

        if(patternLength > 8){
            byte num =.Value .byt.Value .);
            ShortVal newVal = new ShortVal(num);
            return match(newVal);
        }

        if(patternLength < Integer.toBinaryString(val).length()){
            byte shifte.Value .= (byte)(val >> patternLength);
            if(shifte.Value .!= 0){
                return false;
            }
        }

        for (int i = 0; i < patternLength; i++) {
            char current = pattern.charAt(i);
            if (current == 'x' || current == 'z')
                continue;
            
            byte patternPieceAsByte = Byte.parseByte("" + current, 2);
            byte shiftedValByte = (byte)(val >> patternLength - i - 1);
            byte maskedVal = (byte)(shiftedValByte & 0b1);

            if(maskedVal != patternPieceAsByte)
                return false;
        }

        return true;
    }

    public boolean match(CircuitElem elem){
        byte elemSignalAsByte = (byte)(elem.getStateSignal() ? 1 : 0);
        ByteVal retByte = new ByteVal(elemSignalAsByte);
        return match(retByte);
    }

    public boolean match(VectorVal.Value .{

        String pattern = super.getPattern();
        int patternLength = pattern.length();

        int bitIncr = .Value .getIndex1() <.Value .getIndex2()) ? 1 : -1;
        int binIncr = bitIncr;

        int endOverflow =.Value .getIndex2() + (.Value .getIndex1() >.Value .getIndex2())? patternLength - 1 : -patternLength + 1);

        if(patternLength * 4 <.Value .getSize()){
            for(int i =.Value .getIndex1(); i != endOverflow; i+=bitIncr){
                //If the vector length is grater then the pattern length * 4 then all of the signals need to be set to false
                //The reasoning is that if one of them is set true then it is impossible for the two elements to match because the one on the right is a bigger number
                if.Value .getValue(i).getStateSignal()){
                    return false;
                }
            }
        }

        for (int i = 0, vi = endOverflow; i < patternLength; i++, vi+=binIncr) {
            char current = pattern.charAt(i);
            if (current == 'x' || current == 'z')
                continue;
            
            byte patternPieceAsByte = Byte.parseByte("" + current, 2);
            boolean patternSignal = patternPieceAsByte != 0;

            if(patternSignal !=.Value .getValue(vi).getStateSignal()){
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isBool(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isShort(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isUnsignedShort(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isByte(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isUnsignedByteValue(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isInt(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isUnsignedInt(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isLong(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isUnsignedLong(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isReal(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isString(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isVector(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isRegister(){ // TODO Auto-generated method stub
    return false; }

    @Override
    public boolean isWire(){ // TODO Auto-generated method stub
    return false; }
}


























































