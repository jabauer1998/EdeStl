package ede.stl.interpreter;

import javax.swing.tree.ExpandVetoException;
import ede.stl.gui.Machine;
import ede.stl.common.Pointer;
import ede.stl.common.debug.ErrorLog;
import ede.stl.common.debug.item.ErrorItem;
import ede.stl.gui.GuiEde;
import ede.stl.common.Utils;
import ede.stl.Value .IntVal;
import ede.stl.Value .LongVal;
import ede.stl.Value .Value;
import ede.stl.Value .EdeMemVal;
import ede.stl.Value .EdeRegVal;
import ede.stl.Value .EdeStatVal;
import ede.stl.ast.Expression;
import ede.stl.ast.SystemFunctionCall;
import ede.stl.ast.Element;
import ede.stl.ast.Identifier;
import ede.stl.ast.Slice;
import ede.stl.ast.Input;
import ede.stl.ast.Output;
import ede.stl.ast.Reg;
import ede.stl.ast.BlockingAssignment;
import ede.stl.ast.SystemTaskStatement;

public class EdeInterpreter extends VerilogInterpreter {
    private Machine guiInstance;
    private String standardOutputPane;
    private String standardInputPane;
    
    public EdeInterpreter(ErrorLog errLog, Machine guiInstance, String standardOutputPane, String standardInputPane){
        super(errLog);
        this.guiInstance = guiInstance;
        this.standardOutputPane = standardOutputPane;
        this.standardInputPane = standardInputPane;
    }

    protected IntVal interpretDeclaration(Reg.Vector.Array decl) throws Exception{
        String annotationLexeme = decl.annotationLexeme;
        if(annotationLexeme != null){
            if(annotationLexeme.toLowerCase().equals("@memory")){
               .Value .index1Val = interpretShallowOptimizedExpression(decl.GetIndex1());
                int intIndex1 = index1Val.in.Value .);
               .Value .index2Val = interpretShallowOptimizedExpression(decl.GetIndex2());
                int intIndex2 = index2Val.in.Value .);
                
                int size = (intIndex2 > intIndex1) ? intIndex2 - intIndex1 : intIndex1 - intIndex2;
                if(size != 8){
                    errorLog.addItem(new ErrorItem("Expected vector of size 8 for @Memory annotation but found reg ["+intIndex1+':'+intIndex2+']', decl.position));
                    return Utils.errorOccured();
                } else if(environment.localVariableExists(decl.declarationIdentifier)) {
                    errorLog.addItem(new ErrorItem("Error variable " + decl.declarationIdentifier + " allready exists inside the scope", decl.position));
                    return Utils.errorOccured();
                } else {
                   .Value .varVal = new EdeMemVal(guiInstance);
                    environment.addVariable(decl.declarationIdentifier, varVal);
                    return Utils.success();
                }
            } else {
                errorLog.addItem(new ErrorItem("Invalid annotation type for reg [] expected @Memory but found " + annotationLexeme, decl.position));
                return Utils.errorOccured();
            }
        }
        return super.interpretDeclaration(decl);
    }

    protected IntVal interpretDeclaration(Reg.Scalar.Ident decl) throws Exception{
        String annotationLexeme = decl.annotationLexeme;
        if(annotationLexeme != null){
            if(annotationLexeme.toLowerCase().equals("@status")){
                if(environment.localVariableExists(decl.declarationIdentifier)){
                    errorLog.addItem(new ErrorItem("Variable allready exists in scope with the name " + decl.declarationIdentifier, decl.position));
                    return Utils.errorOccured();
                } else {
                    environment.addVariable(decl.declarationIdentifier, new EdeStatVal(decl.declarationIdentifier, guiInstance));
                    return Utils.success();
                }
            } else {
                errorLog.addItem(new ErrorItem("Error expected annotation to be @Status but found " + annotationLexeme, decl.position));
                return Utils.errorOccured();
            }
        }
        return super.interpretDeclaration(decl);
    }

    protected IntVal interpretDeclaration(Reg.Vector.Ident decl) throws Exception{
        String annotationLexeme = decl.annotationLexeme;
        if(annotationLexeme != null){
            if(annotationLexeme.toLowerCase().equals("@register")){
                if(environment.localVariableExists(decl.declarationIdentifier)){
                    errorLog.addItem(new ErrorItem("Definition of " + decl.declarationIdentifier + " allready occured", decl.position));
                    return Utils.errorOccured();
                } else {
                    environment.addVariable(decl.declarationIdentifier, new EdeRegVal(decl.declarationIdentifier, guiInstance));
                    return Utils.success();
                }
            } else {
                errorLog.addItem(new ErrorItem("Error expected annotation to be @Register but found " + annotationLexeme, decl.position));
                return Utils.errorOccured();
            }
        }
        return super.interpretDeclaration(decl);
    }

    protected.Value .interpretSystemFunctionCall(SystemFunctionCall call) throws Exception{
        String identifier = call.functionName;
        if(identifier.equals("getRegister")){
            Expression regExp = call.argumentList.get(0);
           .Value .regName = interpretShallowOptimizedExpression(regExp);

            if(regName.isString()){
                return new LongVal(this.guiInstance.getRegiste.Value .regName.toString()));
            } else {
                return new LongVal(this.guiInstance.getRegiste.Value .regName.in.Value .)));
            }
        } else if(identifier.equals("getStatus")){
            Expression statusNameExp = call.argumentList.get(0);
           .Value .statusName = interpretShallowOptimizedExpression(statusNameExp);

            return new LongVal(this.guiInstance.getStatu.Value .statusName.toString()));
        } else if(identifier.equals("getMemory")){
            Expression memoryAddressExp = call.argumentList.get(0);
           .Value .memAddressVal = interpretShallowOptimizedExpression(memoryAddressExp);
            return new LongVal(this.guiInstance.getMemor.Value .memAddressVal.in.Value .)));
        } else {
            return super.interpretSystemFunctionCall(call);
        }
    }

    protected IntVal interpretSystemTaskCall(SystemTaskStatement stat) throws Exception{
        String identifier = stat.taskName;

        if(identifier.equals("display")){
           if (stat.argumentList.size() >= 2) {
              .Value .fString = interpretShallowOptimizedExpression(stat.argumentList.get(0));

               Object[] Params = new Object[stat.argumentList.size() - 1];
               for(int paramIndex = 0, i = 1; i < stat.argumentList.size(); i++, paramIndex++){
                   .Value . fData = interpretShallowOptimizedExpression(stat.argumentList.get(i));
                    Object ra.Value .= Utils.getRa.Value .fData);
                    Params[paramIndex] = ra.Value .
               }
               

               String formattedString = String.format(fString.toString(), Params);
               guiInstance.appendIoText(standardOutputPane, formattedString + "\r\n");
           } else if (stat.argumentList.size() == 1) {
              .Value .data = interpretShallowOptimizedExpression(stat.argumentList.get(0));
               guiInstance.appendIoText(standardOutputPane, data.toString() + "\r\n");
           } else {
               Utils.errorAndExit("Unknown number of print arguments in " + stat.taskName, stat.position);
           }
        } else if(identifier.equals("setRegister")){
            if(stat.argumentList.size() != 2){
               Utils.errorAndExit("Error: Invalid amount of Arguments for Set Register...\nExpected 2 but found " + stat.argumentList.size()); 
            } else {
                Expression registerNameExp = stat.argumentList.get(0);
                Expression registe.Value .xp = stat.argumentList.get(1);

               .Value .registerNameVal = interpretShallowOptimizedExpression(registerNameExp);
               .Value .registe.Value .al = interpretShallowOptimizedExpression(registe.Value .xp);

                if(registerNameVal.isString()){
                    guiInstance.setRegiste.Value .registerNameVal.toString(), registe.Value .al.longValue());
                } else {
                    guiInstance.setRegiste.Value .registerNameVal.in.Value .), registe.Value .al.longValue());
                }
            }
        } else if(identifier.equals("setStatus")){
            if(stat.argumentList.size() != 2){
                Utils.errorAndExit("Error: Invalid amount of arguments for Status...\nExpected 2 but found " + stat.argumentList.size());
            }

            Expression statusNameExp = stat.argumentList.get(0);
            Expression statu.Value .xp = stat.argumentList.get(1);

           .Value .statusNameVal = interpretShallowOptimizedExpression(statusNameExp);
           .Value .statu.Value .al = interpretShallowOptimizedExpression(statu.Value .xp);

            guiInstance.setStatu.Value .statusNameVal.toString(), statu.Value .al.longValue());
        } else if(identifier.equals("setMemory")){
            if(stat.argumentList.size() != 2){
                Utils.errorAndExit("Error: Invalid amount of aruments for settingMemory Address...\nExpected 2 but found " + stat.argumentList.size());
            }

            Expression memAddressExp = stat.argumentList.get(0);
            Expression memValExp = stat.argumentList.get(1);

           .Value .memAddressVal = interpretShallowOptimizedExpression(memAddressExp);
           .Value .memValVal = interpretShallowOptimizedExpression(memValExp);

            
            guiInstance.setMemor.Value .memAddressVal.in.Value .), memValVal.longValue());
        } else {
            return super.interpretSystemTaskCall(stat);
        }

        return Utils.success();
    }

    protected IntVal interpretShallowBlockingAssingment(BlockingAssignment assign) throws Exception{

        if(assign.leftHandSide instanceof Element){
            Element leftHandSide = (Element)assign.leftHandSide;
            Pointer.Value . val = environment.lookupVariable(leftHandSide.labelIdentifier);
           .Value .deref = val.deRefrence();
            if(deref instanceof EdeMemVal){
                EdeMemVal memory = (EdeMemVal)deref;
                Value rightHandSid.Value .= interpretShallowOptimizedExpression(assign.rightHandSide);
               .Value .inde.Value .= interpretShallowOptimizedExpression(leftHandSide.index1);
                memory.setElemAtIndex(inde.Value .in.Value .), rightHandSid.Value .in.Value .));
                return Utils.success();
            } else if(deref instanceof EdeRegVal){
                EdeRegVal register = (EdeRegVal)deref;
                Value rightHandSid.Value .= interpretShallowOptimizedExpression(assign.rightHandSide);
               .Value .inde.Value .= interpretShallowOptimizedExpression(leftHandSide.index1);
                register.setBitAtIndex(inde.Value .in.Value .), rightHandSid.Value .in.Value .));
                return Utils.success();
            }
        } else if(assign.leftHandSide instanceof Slice){
            Slice leftHandSide = (Slice)assign.leftHandSide;
            Pointer.Value . val = environment.lookupVariable(leftHandSide.labelIdentifier);
           .Value .deref = val.deRefrence();
            if(deref instanceof EdeRegVal){
                EdeRegVal register = (EdeRegVal)deref;
                Value rightHandSid.Value .= interpretShallowOptimizedExpression(assign.rightHandSide);
               .Value .index.Value .= interpretShallowOptimizedExpression(leftHandSide.index1);
               .Value .index.Value .= interpretShallowOptimizedExpression(leftHandSide.index2);
                register.setBitsAtIndex(index.Value .in.Value .), index.Value .in.Value .), rightHandSid.Value .in.Value .));
                return Utils.success();
            }
        } else if(assign.leftHandSide instanceof Identifier){
            Identifier leftHandSide = (Identifier)assign.leftHandSide;
            Pointer.Value . val = environment.lookupVariable(leftHandSide.labelIdentifier);
           .Value .deref = val.deRefrence();
            if(deref instanceof EdeStatVal){
                EdeStatVal status = (EdeStatVal)deref;
                Value rightHandSide = interpretShallowOptimizedExpression(assign.rightHandSide);
                status.setStatu Value rightHandSide.in.Value .));
                return Utils.success();
            } else if(deref instanceof EdeRegVal){
                EdeRegVal reg = (EdeRegVal)deref;
                Value rightHandSide = interpretShallowOptimizedExpression(assign.rightHandSide);
                reg.setAllBits(rightHandSide.in.Value .));
                return Utils.success();
            }
        }

        return super.interpretShallowBlockingAssignment(assign);
    }

    public.Value .interpretShallowOptimizedIdentifier(Identifier ident) throws Exception{
        if (environment.variableExists(ident.labelIdentifier)) {
			Pointer.Value . data = environment.lookupVariable(ident.labelIdentifier);
		.Value .typeData = data.deRefrence();
            if(typeData instanceof EdeStatVal){
                EdeStatVal stat = (EdeStatVal)typeData;
                return new LongVal(stat.in.Value .));
            } else if(typeData instanceof EdeRegVal){
                EdeRegVal reg = (EdeRegVal)typeData;
                return new LongVal(reg.longValue());
            }
		}
        return super.interpretShallowOptimizedIdentifier(ident);
    }

    public.Value .interpretShallowOptimizedElement(Element elem) throws Exception{
        if(environment.variableExists(elem.labelIdentifier)){
            Pointer.Value . data = environment.lookupVariable(elem.labelIdentifier);
		.Value .typeData = data.deRefrence();
            if(typeData instanceof EdeMemVal){
                EdeMemVal edeMemVal = (EdeMemVal)typeData;
               .Value .result = interpretShallowOptimizedExpression(elem.index1);
                return new LongVal(edeMemVal.elemAtIndex(result.in.Value .)));
            } else if(typeData instanceof EdeRegVal){
                EdeRegVal edeRegVal = (EdeRegVal)typeData;
               .Value .result = interpretShallowOptimizedExpression(elem.index1);
                return new LongVal(edeRegVal.getBitAtIndex(result.in.Value .)));
            }
        }
        return super.interpretShallowOptimizedElement(elem);
    }

    public.Value .interpretShallowOptimizedSlice(Slice elem) throws Exception{
        if(environment.variableExists(elem.labelIdentifier)){
            Pointer.Value . data = environment.lookupVariable(elem.labelIdentifier);
		.Value .typeData = data.deRefrence();
            if(typeData instanceof EdeRegVal){
                EdeRegVal edeRegVal = (EdeRegVal)typeData;
               .Value .result1 = interpretShallowOptimizedExpression(elem.index1);
               .Value .result2 = interpretShallowOptimizedExpression(elem.index2);    
                return new LongVal(edeRegVal.getBitsInRange(result1.in.Value .), result2.in.Value .)));
            }
        }
        return super.interpretShallowOptimizedSlice(elem);
    }
}


























































