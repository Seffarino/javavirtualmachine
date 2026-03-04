package fr.ufrst.m1info.comp5.interpreters.jajacode;

import fr.ufrst.m1info.comp5.interpreters.minijaja.InterpreterError.InterpreterError;
import fr.ufrst.m1info.comp5.jajacode.*;
import fr.ufrst.m1info.comp5.memory.*;

import java.util.ArrayList;
import java.util.List;

public class InterpreterJjcVisitor implements JajaCodeAnalyserVisitor {

    private Memory memory;
    private List<InterpreterError> errors;
    private int adr=1;
    private int s = 0;
    private int nbRecur = -1;
    private String lastCalledFct = "";


    private boolean isDebug;
    private int line;
    private boolean jumpToNextBreakpoint;
    public int getAdr() {
        return adr;
    }
    public int getLine(){ return line;}
    public void setLine(int line){
        this.line = line;
    }
    public void setJumpBreakpoint(boolean b){
        this.jumpToNextBreakpoint = b;
    }
    public boolean getJumpBreakpoint(){
        return jumpToNextBreakpoint;
    }
    private ArrayList<Integer> breakpoint;

    public void setBreakpoint(ArrayList<Integer> breakpoint) {
        this.breakpoint = breakpoint;
    }

    private Type getTypeWithLabel(String type){
        return switch (type) {
            case "entier" -> Type.ENTIER;
            case "void" -> Type.VOID;
            case "booleen" -> Type.BOOL;
            case "w" -> Type.OMEGA;
            default -> null;
        };
    }

    private String recurCmp(){
        String recurCmp="";
        if(nbRecur>0){
            recurCmp= String.valueOf(this.nbRecur);
        }else{
            return "";
        }
        return "@"+recurCmp;
    }


    public InterpreterJjcVisitor(Memory memory, List<InterpreterError> errors, boolean debug, ArrayList<Integer> breakpoint) {
        this.memory=memory;
        this.errors=errors;
        this.isDebug = debug;
        this.breakpoint = breakpoint;
        if (breakpoint == null || breakpoint.isEmpty() ) {
            this.jumpToNextBreakpoint = false;
        } else {
            this.jumpToNextBreakpoint = true;
        }
    }

    @Override
    public Object visit(SimpleNode simpleNode, Object o) {
        return null;
    }

    @Override
    public Object visit(ASTJJCStart astjjcStart, Object o) {
        SimpleNode nodeInit = (SimpleNode) astjjcStart.jjtGetChild(0);
        nodeInit.jjtAccept(this,o);
        return null;
    }

    @Override
    public Object visit(ASTJJCinstrs node, Object data) {
        while(adr!=-1){
            SimpleNode nodeInstr =(SimpleNode) node.jjtGetChild(adr-1);
            if(nodeInstr instanceof ASTJJCjcnil){
                break;
            }
            nodeInstr.jjtAccept(this,data);
        }
        return null;
    }


    @Override
    public Object visit(ASTJJCinstr astjjCinstr, Object o) {

        int adresseValue = (int) ((SimpleNode) astjjCinstr.jjtGetChild(0)).jjtGetValue();

        if(isDebug){
            setLine(astjjCinstr.jjtGetFirstToken().beginLine);
            if (breakpoint == null || breakpoint.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                if (this.jumpToNextBreakpoint) {
                    if(breakpoint.contains(astjjCinstr.jjtGetFirstToken().beginLine)) {
                        try {
                            setJumpBreakpoint(false);
                            wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        if(adresseValue==adr){
            SimpleNode nodeInstr =(SimpleNode) astjjCinstr.jjtGetChild(1);
            nodeInstr.jjtAccept(this,o);
        }else{
            errors.add(new InterpreterJjcError(astjjCinstr.jjtGetFirstToken().beginLine,
                    astjjCinstr.jjtGetFirstToken().beginColumn,"Address error"));
        }

        return null;
    }

    @Override
    public Object visit(ASTJJCinit astjjCinit, Object o) {
        adr += 1;
        return null;
    }

    @Override
    public Object visit(ASTJJCswap astjjCswap, Object o) {
        adr+=1;
        memory.swap();
        return null;
    }

    @Override
    public Object visit(ASTJJCNew astjjcNew, Object o) {
        adr+=1;
        SimpleNode nodeIdent= (SimpleNode) astjjcNew.jjtGetChild(0);
        SimpleNode nodeType = (SimpleNode) astjjcNew.jjtGetChild(1);
        SimpleNode nodeSorte = (SimpleNode) astjjcNew.jjtGetChild(2);
        SimpleNode nodeNumber = (SimpleNode) astjjcNew.jjtGetChild(3);

        String ident = (String) nodeIdent.jjtGetValue();
        Type type = getTypeWithLabel((String) nodeType.jjtGetValue());
        String sorte = (String) nodeSorte.jjtGetValue();
        int number = (int) nodeNumber.jjtGetValue();
        try {
            switch (sorte){
                case "var" :
                    if(number==0){

                        memory.identVal(ident+recurCmp(),type,0);
                    }else{
                        s+=1;
                        memory.identVal(ident+recurCmp(),type,s);
                        if(s>number-1){
                            s=0;
                        }
                    }
                    break;
                case "cst" :
                    memory.declCst(ident,memory.pop()+recurCmp(),type);
                    break;
                case "meth":
                    memory.declMeth(ident,memory.pop(),type);
                    break;
                default:
                    errors.add(new InterpreterJjcError(astjjcNew.jjtGetFirstToken().beginLine,
                        astjjcNew.jjtGetFirstToken().beginColumn,"Sorte not recognized"));
            }
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjcNew.jjtGetFirstToken().beginLine,
                    astjjcNew.jjtGetFirstToken().beginColumn,e.getMessage()));
        };
        return null;
    }

    @Override
    public Object visit(ASTJJCnewarray astjjCnewarray, Object o) {
        adr+=1;
        SimpleNode nodeIdent= (SimpleNode) astjjCnewarray.jjtGetChild(0);
        SimpleNode nodeType = (SimpleNode) astjjCnewarray.jjtGetChild(1);
        Type type = getTypeWithLabel((String) nodeType.jjtGetValue());
        String ident =(String) nodeIdent.jjtGetValue();
        try {
            int size = Integer.parseInt(memory.pop().toString());
            memory.declTab(ident+recurCmp(),size,type);
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjCnewarray.jjtGetFirstToken().beginLine,
                    astjjCnewarray.jjtGetFirstToken().beginColumn,e.getMessage()));
        }
        return null;
    }

    @Override
    public Object visit(ASTJJCinvoke astjjCinvoke, Object o) {

        SimpleNode nodeIdent= (SimpleNode) astjjCinvoke.jjtGetChild(0);
        String ident = (String) nodeIdent.jjtGetValue();
        try{
            memory.push(adr+1);
            InfoIdent info = memory.getSymbolTable().get(ident,true);
            adr = (int) info.getInstance().getValue();
            if(lastCalledFct.equals(ident)){
                nbRecur+=1;
            }else{
                nbRecur=0;
            }
            lastCalledFct = ident;
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjCinvoke.jjtGetFirstToken().beginLine,
                    astjjCinvoke.jjtGetFirstToken().beginColumn,e.getMessage()));
        };



        return null;
    }

    @Override
    public Object visit(ASTJJCReturn astjjcReturn, Object o) {
        try {
            s=0;
            adr = (int) memory.pop();
            nbRecur-=1;
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjcReturn.jjtGetFirstToken().beginLine,
                    astjjcReturn.jjtGetFirstToken().beginColumn,e.getMessage()));
        };
        return null;
    }

    @Override
    public Object visit(ASTJJCpush astjjCpush, Object o) {
        adr+=1;
        SimpleNode nodeNbre = (SimpleNode) astjjCpush.jjtGetChild(0);
        Object value = nodeNbre.jjtGetValue();
        try {
            memory.push(value);
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjCpush.jjtGetFirstToken().beginLine,
                    astjjCpush.jjtGetFirstToken().beginColumn,e.getMessage()));
        };
        return null;
    }

    @Override
    public Object visit(ASTJJCpop astjjCpop, Object o) {
        adr+=1;
        try {
            memory.pop();
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjCpop.jjtGetFirstToken().beginLine,
                    astjjCpop.jjtGetFirstToken().beginColumn,e.getMessage()));
        };
        return null;
    }

    private InfoIdent getInfoRecur(String ident) {
        try{
            return  memory.getSymbolTable().get(ident+recurCmp(),false);
        }catch (SymbolTableException.UnknownSymbolException e){
            try{
                return memory.getSymbolTable().get(ident+recurCmp(),false);
            }catch (SymbolTableException.UnknownSymbolException e1){
                return null;
            }

        }
    }

    @Override
    public Object visit(ASTJJCload astjjCload, Object o) {
        adr+=1;
        SimpleNode identNode = (SimpleNode) astjjCload.jjtGetChild(0);
        String identValue = (String) identNode.jjtGetValue();
        try {
                InfoIdent i = getInfoRecur(identValue);
                if(i!=null){
                    Object value = i.getInstance().getValue();
                    memory.push(value);
                }
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjCload.jjtGetFirstToken().beginLine,
                    astjjCload.jjtGetFirstToken().beginColumn,e.getMessage()));
        };

        return null;
    }

    @Override
    public Object visit(ASTJJCaload astjjCaload, Object o) {
        adr+=1;
        SimpleNode identNode = (SimpleNode) astjjCaload.jjtGetChild(0);
        String identValue = (String) identNode.jjtGetValue();
        try {
            int index = (int) memory.pop();
            Object value = memory.valT(identValue,index);
            memory.push(value);
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjCaload.jjtGetFirstToken().beginLine,
                    astjjCaload.jjtGetFirstToken().beginColumn,e.getMessage()));
        };
        return null;
    }

    @Override
    public Object visit(ASTJJCstore astjjCstore, Object o) {
        adr+=1;
        SimpleNode identNode = (SimpleNode) astjjCstore.jjtGetChild(0);
        String identValue = (String) identNode.jjtGetValue();

        try {
            Object value = memory.pop();
            memory.affecterVal(identValue+recurCmp(),value);
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjCstore.jjtGetFirstToken().beginLine,
                    astjjCstore.jjtGetFirstToken().beginColumn,e.getMessage()));
        };
        return null;
    }

    @Override
    public Object visit(ASTJJCastore astjjCastore, Object o) {
        adr+=1;
        SimpleNode identNode = (SimpleNode) astjjCastore.jjtGetChild(0);
        String identValue = (String) identNode.jjtGetValue();
        try {
            Object value = memory.pop();
            int index = (int) memory.pop();
            memory.affecterValT(identValue,index,value);
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjCastore.jjtGetFirstToken().beginLine,
                    astjjCastore.jjtGetFirstToken().beginColumn,e.getMessage()));
        };

        return null;
    }

    @Override
    public Object visit(ASTJJCwrite astjjCwrite, Object o) {
        adr+=1;
        try {
            Object i = memory.pop();
            System.out.print(i);
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjCwrite.jjtGetFirstToken().beginLine,
                    astjjCwrite.jjtGetFirstToken().beginColumn,e.getMessage()));
        };
        return null;
    }

    @Override
    public Object visit(ASTJJCwriteln astjjCwriteln, Object o) {
        adr+=1;
        try {
            Object i = memory.pop();
            System.out.println(i);
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjCwriteln.jjtGetFirstToken().beginLine,
                    astjjCwriteln.jjtGetFirstToken().beginColumn,e.getMessage()));
        };
        return null;
    }

    @Override
    public Object visit(ASTJJClength astjjClength, Object o) {
        adr+=1;
        SimpleNode identNode = (SimpleNode) astjjClength.jjtGetChild(0);
        String identValue = (String) identNode.jjtGetValue();
        try {
            InfoIdent info = memory.getSymbolTable().get(identValue,false);
            int idTab = (int) info.getInstance().getValue();
            Object value = memory.getTas().getArrayWithId(idTab).getTaille();
            memory.push(value);
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjClength.jjtGetFirstToken().beginLine,
                    astjjClength.jjtGetFirstToken().beginColumn,e.getMessage()));
        };

        return null;
    }

    @Override
    public Object visit(ASTJJCIf astjjcIf, Object o) {
        SimpleNode nodeAddress = (SimpleNode) astjjcIf.jjtGetChild(0);
        int address = (int) nodeAddress.jjtGetValue();
        try {
            boolean condition = (boolean) memory.pop();
            if(condition){
                adr=address;
            }else{
                adr+=1;
            }
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjcIf.jjtGetFirstToken().beginLine,
                    astjjcIf.jjtGetFirstToken().beginColumn,e.getMessage()));
        };
        return null;
    }

    @Override
    public Object visit(ASTJJCGoto astjjcGoto, Object o) {
        SimpleNode nodeAdresse = (SimpleNode) astjjcGoto.jjtGetChild(0);
        adr = (int) nodeAdresse.jjtGetValue();
        return null;
    }

    @Override
    public Object visit(ASTJJCinc astjjCinc, Object o) {
        adr+=1;
        SimpleNode identNode = (SimpleNode) astjjCinc.jjtGetChild(0);
        String identValue = (String) identNode.jjtGetValue();
        try {
            int value =(int) memory.pop();
            InfoIdent i = getInfoRecur(identValue);
            if (i!=null){
                int valueToInc =(int) i.getInstance().getValue();
                memory.affecterVal(identValue+recurCmp(),value+valueToInc);
            }
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjCinc.jjtGetFirstToken().beginLine,
                    astjjCinc.jjtGetFirstToken().beginColumn,e.getMessage()));
        };
        return null;
    }

    @Override
    public Object visit(ASTJJCainc astjjCainc, Object o) {
        adr+=1;
        SimpleNode identNode = (SimpleNode) astjjCainc.jjtGetChild(0);
        String identValue = (String) identNode.jjtGetValue();
        try {
            int value = (int) memory.pop();
            int index = (int) memory.pop();
            int currentVal = Integer.parseInt(memory.valT(identValue,index).toString());
            memory.affecterValT(identValue,index,value+currentVal);
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjCainc.jjtGetFirstToken().beginLine,
                    astjjCainc.jjtGetFirstToken().beginColumn,e.getMessage()));
        };
        return null;
    }

    @Override
    public Object visit(ASTJJCoper astjjCoper, Object o) {
            SimpleNode nodeOper = (SimpleNode) astjjCoper.jjtGetChild(0);
            nodeOper.jjtAccept(this,o);
        return null;
    }

    @Override
    public Object visit(ASTJJCnop astjjCnop, Object o) {
        adr+=1;
        return null;
    }

    @Override
    public Object visit(ASTJJCjcstop astjjCjcstop, Object o) {
        adr = -1;
        return null;
    }

    @Override
    public Object visit(ASTJJCjcident astjjCjcident, Object o) {
        return null;
    }

    @Override
    public Object visit(ASTJJCjcstring astjjCjcstring, Object o) {
        return null;
    }

    @Override
    public Object visit(ASTJJCjcvrai astjjCjcvrai, Object o) {
        return null;
    }

    @Override
    public Object visit(ASTJJCjcfaux astjjCjcfaux, Object o) {
        return null;
    }

    @Override
    public Object visit(ASTJJCjcadress astjjCjcadress, Object o) {
        return null;
    }

    @Override
    public Object visit(ASTJJCjcnbre astjjCjcnbre, Object o) {
        return null;
    }

    @Override
    public Object visit(ASTJJCneg astjjCneg, Object o) {
        adr+=1;
        try {
            int i = Integer.parseInt(memory.pop().toString());
            memory.push(-i);
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjCneg.jjtGetFirstToken().beginLine,
                    astjjCneg.jjtGetFirstToken().beginColumn,e.getMessage()));
        };
        return null;
    }

    @Override
    public Object visit(ASTJJCnot astjjCnot, Object o) {
        adr+=1;
        try {
            boolean i = Boolean.parseBoolean(memory.pop().toString());
            memory.push(!i);
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjCnot.jjtGetFirstToken().beginLine,
                    astjjCnot.jjtGetFirstToken().beginColumn,e.getMessage()));
        };
        return null;
    }

    @Override
    public Object visit(ASTJJCadd astjjCadd, Object o) {
        adr+=1;
        try {
            int i2 = Integer.parseInt(memory.pop().toString());
            int i1 = Integer.parseInt(memory.pop().toString());
            memory.push(i1+i2);
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjCadd.jjtGetFirstToken().beginLine,
                    astjjCadd.jjtGetFirstToken().beginColumn,e.getMessage()));
        };
        return null;
    }

    @Override
    public Object visit(ASTJJCsub astjjCsub, Object o) {
        adr+=1;
        try {
            int i2 = Integer.parseInt(memory.pop().toString());
            int i1 = Integer.parseInt(memory.pop().toString());
            memory.push(i1-i2);
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjCsub.jjtGetFirstToken().beginLine,
                    astjjCsub.jjtGetFirstToken().beginColumn,e.getMessage()));
        };
        return null;
    }

    @Override
    public Object visit(ASTJJCmul astjjCmul, Object o) {
        adr += 1;
        try {
            int i2 = Integer.parseInt(memory.pop().toString());
            int i1 = Integer.parseInt(memory.pop().toString());
            memory.push(i1*i2);
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjCmul.jjtGetFirstToken().beginLine,
                    astjjCmul.jjtGetFirstToken().beginColumn,e.getMessage()));
        };
        return null;
    }

    @Override
    public Object visit(ASTJJCdiv astjjCdiv, Object o) {
        adr += 1;
        try {
            int i2 = Integer.parseInt(memory.pop().toString());
            int i1 = Integer.parseInt(memory.pop().toString());
            memory.push(i1/i2);
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjCdiv.jjtGetFirstToken().beginLine,
                    astjjCdiv.jjtGetFirstToken().beginColumn,e.getMessage()));
        };
        return null;
    }

    @Override
    public Object visit(ASTJJCcmp astjjCcmp, Object o) {
        adr += 1;
        try {
            int i2 = Integer.parseInt(memory.pop().toString());
            int i1 = Integer.parseInt(memory.pop().toString());
            memory.push(i1==i2);
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjCcmp.jjtGetFirstToken().beginLine,
                    astjjCcmp.jjtGetFirstToken().beginColumn,e.getMessage()));
        };
        return null;
    }

    @Override
    public Object visit(ASTJJCsup astjjCsup, Object o) {
        adr += 1;
        try {
            int i2 = Integer.parseInt(memory.pop().toString());
            int i1 = Integer.parseInt(memory.pop().toString());
            memory.push(i1>i2);
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjCsup.jjtGetFirstToken().beginLine,
                    astjjCsup.jjtGetFirstToken().beginColumn,e.getMessage()));
        };
        return null;
    }

    @Override
    public Object visit(ASTJJCor astjjCor, Object o) {
        adr += 1;
        try {
            boolean i2 = Boolean.parseBoolean(memory.pop().toString());
            boolean i1 = Boolean.parseBoolean(memory.pop().toString());
            memory.push(i1||i2);
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjCor.jjtGetFirstToken().beginLine,
                    astjjCor.jjtGetFirstToken().beginColumn,e.getMessage()));
        };
        return null;
    }

    @Override
    public Object visit(ASTJJCand astjjCand, Object o) {
        adr += 1;
        try {
            boolean i2 = Boolean.parseBoolean(memory.pop().toString());
            boolean i1 = Boolean.parseBoolean(memory.pop().toString());
            memory.push(i1&&i2);
        }catch (Exception e){
            errors.add(new InterpreterJjcError(astjjCand.jjtGetFirstToken().beginLine,
                    astjjCand.jjtGetFirstToken().beginColumn,e.getMessage()));
        };
        return null;
    }

    @Override
    public Object visit(ASTJJCjcsorte astjjCjcsorte, Object o) {
        return null;
    }

    @Override
    public Object visit(ASTJJCjctype astjjCjctype, Object o) {
        return null;
    }

    @Override
    public Object visit(ASTJJCjcnil astjjCjcnil, Object o) {
        return null;
    }
}
