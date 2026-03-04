package fr.ufrst.m1info.comp5.interpreters.minijaja;

import fr.ufrst.m1info.comp5.interpreters.minijaja.InterpreterError.*;
import fr.ufrst.m1info.comp5.memory.*;
import fr.ufrst.m1info.comp5.minijaja.*;

import java.util.ArrayList;
import java.util.List;

public class InterpreterVisitor implements MiniJajaAnalyserVisitor {
    private final String SCOPE_CLASS = "class";
    private final String SCOPE_MAIN = "main";
    private List<InterpreterError> errors;
    private Memory memory;

    private boolean isDebug;
    private boolean jumpToNextBreakpoint;
    private ArrayList<Integer> breakpoint;

    private int lineNumber;

    public InterpreterVisitor(Memory m,List<InterpreterError> errors, boolean debug, ArrayList<Integer> breakpoint) {
        this.memory=m;
        this.errors=errors;
        this.isDebug = debug;
        this.breakpoint = breakpoint;
        if (breakpoint == null || breakpoint.isEmpty() ) {
            this.jumpToNextBreakpoint = false;
        } else {
            this.jumpToNextBreakpoint = true;
        }
    }


    public void setLine(int l) {
        lineNumber = l;
    }

    public int getLine() {
        return lineNumber;
    }

    public ArrayList<Integer> getBreakpoint() {
        return this.breakpoint;
    }
    public void setBreakpoint(ArrayList<Integer> breakpoint) {
        this.breakpoint = breakpoint;
    }
    public void setJumpBreakpoint(boolean b) {
        this.jumpToNextBreakpoint = b;
    }
    @Override
    public Object visit(SimpleNode node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjStart node, Object data) {
        if (node.jjtGetNumChildren() > 0) {
            SimpleNode nodeClass = (SimpleNode) node.jjtGetChild(0);
            nodeClass.jjtAccept(this, data);
        }
        return null;
    }

    @Override
    public Object visit(ASTMjjclasse node, Object data) {
        SimpleNode nodeIdent = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeDecls = (SimpleNode) node.jjtGetChild(1);
        SimpleNode nodeMain = (SimpleNode) node.jjtGetChild(2);
        String identClass= (String) nodeIdent.jjtGetValue();
        try{
            memory.affecterVal(identClass,"w");
        } catch (Exception ignored){};

        InterpreterData interpreterData = new InterpreterData(SCOPE_CLASS, InterpreterMode.DECL);

        nodeDecls.jjtAccept(this, interpreterData);
        nodeMain.jjtAccept(this, interpreterData);
        interpreterData.setInterpreterMode(InterpreterMode.RETRAIT);
        nodeDecls.jjtAccept(this, interpreterData);

        try {
            InfoIdent r = memory.getSymbolTable().get(identClass,false);
            InfoInstance instance = r.getInstance();
            memory.getSymbolTable().removeFromStack(instance);
        }catch (Exception ignored){};

        return null;
    }

    private InfoIdent getIdent(String id) throws SymbolTableException.UnknownSymbolException {
        return memory.getSymbolTable().get(id,false);
    }

    private InfoIdent getIdentAllClass(String scopeIdent,String ident,InterpreterData visitData) throws SymbolTableException.UnknownSymbolException {
        try {
            return getIdent(scopeIdent);
        } catch (SymbolTableException.UnknownSymbolException e) {
            String broaderScopeIdent = visitData.getEnclosingScopeString() + "@" + ident;
            return getIdent(broaderScopeIdent);
        }
    }

    @Override
    public Object visit(ASTMjjident node, Object data) {
        String ident = (String) node.jjtGetValue();
        InterpreterData interpreterData = (InterpreterData) data;
        String fullScope = interpreterData.getFullScopeString();
        String scopeIdent = fullScope + "@" + ident;
        InfoIdent info=null;
        try{
            info = getIdentAllClass(scopeIdent,ident,interpreterData);
        }catch (Exception ignored){};

        if(info==null){
            return null;
        }
        InfoInstance i = info.getInstance();
        Object value = null;
        if(i!=null){
             value = i.getValue();
        }
        if(value==null || value.equals("w")){
            errors.add(new NotInitializedError(node.jjtGetFirstToken().beginLine,
                    node.jjtGetFirstToken().beginColumn,
                    "Variable " + ident + " might not have been initialized."));
            if (info.getType()==Type.ENTIER){
                return 0;
            }else{
                return false;
            }

        }
        return value;
    }

    @Override
    public Object visit(ASTMjjdecls node, Object data) {
        SimpleNode nodeDecl = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeDecls = (SimpleNode) node.jjtGetChild(1);

        InterpreterData interpreterData = (InterpreterData) data;
        InterpreterData NewInterpreterData = new InterpreterData(SCOPE_CLASS,interpreterData.getInterpreterMode());

        nodeDecl.jjtAccept(this, NewInterpreterData);
        nodeDecls.jjtAccept(this, NewInterpreterData);

        return null;
    }

    @Override
    public Object visit(ASTMjjmethode node, Object data) {
        InterpreterData interpreterData = (InterpreterData) data;
        SimpleNode nodeEntetes = (SimpleNode) node.jjtGetChild(2);
        String paramTypes = (String) nodeEntetes.jjtAccept(this, null);
        String scope = interpreterData.getFullScopeString();
        String identMeth= (String) ((SimpleNode) node.jjtGetChild(1)).jjtGetValue();
        if(paramTypes==null){
            paramTypes="";
        }
        String fullScope=scope+"@"+identMeth+paramTypes;
        if(interpreterData.getInterpreterMode()==InterpreterMode.DECL){

            try{
                memory.getSymbolTable().addInstance(fullScope,node);
            }catch (Exception ignored){};
        } else if (interpreterData.getInterpreterMode()==InterpreterMode.RETRAIT) {
            try {
                InfoIdent r = memory.getSymbolTable().get(fullScope,true);
                memory.getSymbolTable().removeAllInstanceFromStack(r);
            }catch (Exception ignored){};
        }
        return null;
    }

    @Override
    public Object visit(ASTMjjtableau node, Object data) {
        SimpleNode nodeExp = (SimpleNode) node.jjtGetChild(2);
        InterpreterData interpreterData = (InterpreterData) data;
        String scope = interpreterData.getFullScopeString();
        String identVar= (String) ((SimpleNode) node.jjtGetChild(1)).jjtGetValue();
        if(interpreterData.getInterpreterMode()==InterpreterMode.DECL){
            try {
                int tailleTab = (int) nodeExp.jjtAccept(this,interpreterData);
                InfoIdent infoTab = memory.getSymbolTable().get(scope+"@"+identVar,false);
                Type tabType = infoTab.getType();
                memory.getSymbolTable().remove(infoTab);
                memory.declTab(scope+"@"+identVar,tailleTab,tabType);

            }catch (SymbolTableException.UnknownSymbolException | MemoryException | TasException |
                    SymbolTableException.ExistingSymbolException e ){
                System.out.println(e.getMessage());
            }
        } else if (interpreterData.getInterpreterMode()==InterpreterMode.RETRAIT) {
            try {
                InfoIdent r = memory.getSymbolTable().get(scope+"@"+identVar,false);
                memory.getSymbolTable().removeAllInstanceFromStack(r);
            }catch (Exception ignored){};
        }

        return null;
    }

    @Override
    public Object visit(ASTMjjvar node, Object data) {
        SimpleNode nodeExp = (SimpleNode) node.jjtGetChild(2);
        InterpreterData interpreterData = (InterpreterData) data;
        String scope = interpreterData.getFullScopeString();
        String identVar= (String) ((SimpleNode) node.jjtGetChild(1)).jjtGetValue();

        if(interpreterData.getInterpreterMode()==InterpreterMode.DECL){
            if(isDebug) {
                setLine(node.jjtGetFirstToken().beginLine);
                if(breakpoint == null || breakpoint.isEmpty()) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    if (this.jumpToNextBreakpoint) {
                        if(breakpoint.contains(node.jjtGetFirstToken().beginLine)) {
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
            Object value = nodeExp.jjtAccept(this,interpreterData);
            try{
                memory.affecterVal(scope+"@"+identVar,value);
            }catch (Exception ignored){};
        } else if (interpreterData.getInterpreterMode()==InterpreterMode.RETRAIT) {
            try {
                InfoIdent r = memory.getSymbolTable().get(scope+"@"+identVar,false);
                memory.getSymbolTable().removeAllInstanceFromStack(r);
            }catch (Exception ignored){};
        }
        return null;
    }

    @Override
    public Object visit(ASTMjjcst node, Object data) {
        SimpleNode nodeExp = (SimpleNode) node.jjtGetChild(2);
        InterpreterData interpreterData = (InterpreterData) data;
        String scope = interpreterData.getFullScopeString();
        String identVar= (String) ((SimpleNode) node.jjtGetChild(1)).jjtGetValue();

        if(interpreterData.getInterpreterMode()==InterpreterMode.DECL){
            Object value = nodeExp.jjtAccept(this,interpreterData);

            try{
                memory.affecterVal(scope+"@"+identVar,value);
            }catch (Exception ignored){};
        } else if (interpreterData.getInterpreterMode()==InterpreterMode.RETRAIT) {
            try {
                InfoIdent r = memory.getSymbolTable().get(scope+"@"+identVar,false);
                memory.getSymbolTable().removeAllInstanceFromStack(r);
            }catch (Exception ignored){};
        }
        return null;
    }

    @Override
    public Object visit(ASTMjjvars node, Object data) {
        SimpleNode nodeVar = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeVars= (SimpleNode) node.jjtGetChild(1);

        InterpreterData interpreterData = (InterpreterData) data;
        InterpreterData copyOfInterpreterData = new InterpreterData(interpreterData);

        nodeVar.jjtAccept(this, copyOfInterpreterData);
        nodeVars.jjtAccept(this, interpreterData);

        return null;
    }

    @Override
    public Object visit(ASTMjjmain node, Object data) {
        SimpleNode nodeVars = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeInstrs= (SimpleNode) node.jjtGetChild(1);

        InterpreterData interpreterData = (InterpreterData) data;
        interpreterData.enterScope(SCOPE_MAIN);

        nodeVars.jjtAccept(this, interpreterData);
        InterpreterData iData1 = new InterpreterData(SCOPE_CLASS,InterpreterMode.DECL);
        iData1.enterScope(SCOPE_MAIN);
        nodeInstrs.jjtAccept(this, iData1);

        InterpreterData iData = new InterpreterData(SCOPE_CLASS,InterpreterMode.RETRAIT);
        iData.enterScope(SCOPE_MAIN);

        nodeVars.jjtAccept(this, iData);
        interpreterData.exitScope();
        return null;


    }

    @Override
    public Object visit(ASTMjjentetes node, Object data) {
        SimpleNode nodeEntete = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nextNode = (SimpleNode) node.jjtGetChild(1);
        if(data==null){
            StringBuilder paramTypes = new StringBuilder();
            paramTypes.append((String) nodeEntete.jjtAccept(this, data));
            if (!(nextNode instanceof ASTMjjenil)) {
                paramTypes.append((String) nextNode.jjtAccept(this, data));
            }
            return paramTypes.toString();
        }
        InterpreterData interpreterData = (InterpreterData) data;
        if(interpreterData.getInterpreterMode()==InterpreterMode.RETRAIT){
            nodeEntete.jjtAccept(this, data);
            nextNode.jjtAccept(this, data);
        }
        return null;
    }

    @Override
    public Object visit(ASTMjjentete node, Object data) {
        SimpleNode nodeType = (SimpleNode) node.jjtGetChild(0);
        String typeLabel = (String) nodeType.jjtAccept(this, data);
        if(data==null){
            return "@"+typeLabel;
        }

        InterpreterData interpreterData = (InterpreterData) data;
        if(interpreterData.getInterpreterMode()==InterpreterMode.RETRAIT){
            SimpleNode nodeIdent = (SimpleNode) node.jjtGetChild(1);
            String identEntete = (String) nodeIdent.jjtGetValue();
            String fullScope = interpreterData.getFullScopeString()+"@"+identEntete;
            try {
                InfoIdent r = memory.getSymbolTable().get(fullScope,false);
                memory.getSymbolTable().removeAllInstanceFromStack(r);
            }catch (Exception ignored){};
        }
        return null;
    }

    @Override
    public Object visit(ASTMjjinstrs node, Object data) {
        SimpleNode nodeInstr = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeInstrs = (SimpleNode) node.jjtGetChild(1);

        InterpreterData interpreterData = (InterpreterData) data;
        InterpreterData copyOfInterpreterData = new InterpreterData(interpreterData);

        if(isDebug) {
            setLine(nodeInstr.jjtGetFirstToken().beginLine);
            if(breakpoint == null || breakpoint.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                if (this.jumpToNextBreakpoint) {
                    if(breakpoint.contains(nodeInstr.jjtGetFirstToken().beginLine)) {
                        try {
                            setJumpBreakpoint(false);
                            wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
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
        Object value = nodeInstr.jjtAccept(this, copyOfInterpreterData);
        if(value!=null){
            return value;
        }
        value = nodeInstrs.jjtAccept(this, interpreterData);
        return value;
    }

    @Override
    public Object visit(ASTMjjaffectation node, Object data) {
        SimpleNode nodeIdent = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeExp = (SimpleNode) node.jjtGetChild(1);
        InterpreterData interpreterData = (InterpreterData) data;
        String scope = interpreterData.getFullScopeString();
        Object value = nodeExp.jjtAccept(this,interpreterData);
        String identValue ="";
        boolean isTab = false;

        if(nodeIdent instanceof ASTMjjtab){
            SimpleNode tabIdent = (SimpleNode) nodeIdent.jjtGetChild(0);
            identValue = (String) tabIdent.jjtGetValue();
            isTab = true;
        }else{
            identValue = (String) nodeIdent.jjtGetValue();
        }

        try{
            affectVal(interpreterData,scope+"@"+identValue,value,node,isTab,nodeIdent);
        }catch (SymbolTableException.UnknownSymbolException | TasException e) {
            try {
                scope = interpreterData.getEnclosingScopeString() + "@" + identValue;
                affectVal(interpreterData,scope,value,node,isTab,nodeIdent);
            }catch (Exception ignored){};
        }
        return null;
    }

    private void affectVal(InterpreterData interpreterData, String identValue, Object value, SimpleNode node, boolean isTab, SimpleNode nodeIdent) throws SymbolTableException.UnknownSymbolException, TasException {
        InfoIdent info = memory.getSymbolTable().get(identValue,false);
        if(info.getSorte()!= Sorte.CST){
            if(info.getSorte()==Sorte.TAB){
                if(isTab){
                    int tabID =(int) memory.val(identValue);
                    SimpleNode tabExp = (SimpleNode) nodeIdent.jjtGetChild(1);
                    int tabExpValue = (int) tabExp.jjtAccept(this,interpreterData);
                    if(checkLength(tabExpValue,tabID,node)) {
                        memory.affecterValT(identValue,tabExpValue,value);
                    }
                }else{
                    memory.affecterVal(identValue,value);
                }
            }else{
                memory.affecterVal(identValue,value);
            }
        }else{
            errors.add(new ReAssignementError(node.jjtGetFirstToken().beginLine,
                    node.jjtGetFirstToken().beginColumn));
        }
        if (info.getSorte()==Sorte.VCST) {
            info.setSorte(Sorte.CST);
        }
    }

    private void incVal (InterpreterData interpreterData, String ident,String identValue, int value, SimpleNode node, boolean isTab, SimpleNode nodeIdent) throws SymbolTableException.UnknownSymbolException, TasException {
        Object currentValueMem = memory.val(identValue);
        if(currentValueMem!=null && !currentValueMem.equals("w")){
            int currentValue = (int) currentValueMem;
            checkMaxInt((long) value + currentValue,node);
            if(isTab){
                SimpleNode tabExp = (SimpleNode) nodeIdent.jjtGetChild(1);
                int tabExpValue = (int) tabExp.jjtAccept(this,interpreterData);
                if(checkLength(tabExpValue,currentValue,node)) {
                    Object valueOfArray = memory.getTas().getValueOfArray(currentValue,tabExpValue);
                    if(valueOfArray==null){
                        errors.add(new NotInitializedError(node.jjtGetFirstToken().beginLine,
                                node.jjtGetFirstToken().beginColumn,
                                "Variable "  + ident + " with index "+currentValue+" must be initialized"));
                    }else{
                        int valueTab = (int) valueOfArray;
                        checkMaxInt((long) value + valueTab,node);
                        memory.affecterValT(identValue, tabExpValue, value + valueTab);
                    }
                }
            }else{
                memory.affecterVal(identValue,(value+currentValue));
            }
        }else{
            errors.add(new NotInitializedError(node.jjtGetFirstToken().beginLine,
                    node.jjtGetFirstToken().beginColumn,
                    "Variable "  + ident + " must be initialized"));
        }
    }

    @Override
    public Object visit(ASTMjjsomme node, Object data) {
        SimpleNode nodeIdent = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeExp = (SimpleNode) node.jjtGetChild(1);
        InterpreterData interpreterData = (InterpreterData) data;
        String scope = interpreterData.getFullScopeString();
        String identValue = "";

        int value = (int) nodeExp.jjtAccept(this,interpreterData);
        boolean isTab = false;
        if(nodeIdent instanceof ASTMjjtab){
            SimpleNode tabIdent = (SimpleNode) nodeIdent.jjtGetChild(0);
            identValue = (String) tabIdent.jjtGetValue();
            isTab = true;
        }else{
            identValue = (String) nodeIdent.jjtGetValue();
        }

        try{
            incVal(interpreterData,identValue,scope+"@"+identValue,value,node,isTab,nodeIdent);
        }catch (SymbolTableException.UnknownSymbolException | TasException e  ) {
            try {
                scope = interpreterData.getEnclosingScopeString() + "@" + identValue;
                incVal(interpreterData,identValue,scope,value,node,isTab,nodeIdent);
            }catch (Exception ignored){};
        }
        return null;
    }

    @Override
    public Object visit(ASTMjjincrement node, Object data) {
        SimpleNode nodeIdent = (SimpleNode) node.jjtGetChild(0);
        InterpreterData interpreterData = (InterpreterData) data;
        String scope = interpreterData.getFullScopeString();
        String identValue = "";

        boolean isTab = false;
        if(nodeIdent instanceof ASTMjjtab){
            SimpleNode tabIdent = (SimpleNode) nodeIdent.jjtGetChild(0);
            identValue = (String) tabIdent.jjtGetValue();
            isTab = true;
        }else{
            identValue = (String) nodeIdent.jjtGetValue();
        }

        try{
            incVal(interpreterData,identValue,scope+"@"+identValue,1,node,isTab,nodeIdent);
        }catch (SymbolTableException.UnknownSymbolException | TasException e  ) {
            try {
                scope = interpreterData.getEnclosingScopeString() + "@" + identValue;
                incVal(interpreterData,identValue,scope,1,node,isTab,nodeIdent);
            }catch (Exception ignored){};
        }
        return null;
    }

    private Object appelI(String identMeth, SimpleNode listExp ,Object data,boolean isAppelE){
        String paramType = (String) listExp.jjtAccept(this, data);
        if(paramType==null){
            paramType="";
        }
        String fullScope = "class@" + identMeth + paramType;
        ASTMjjmethode mjjmethode=null;

        try {
            InfoIdent info = memory.getSymbolTable().get(fullScope, true);
            mjjmethode = (ASTMjjmethode) info.getInstance().getValue();
        } catch (Exception ignored){};

        if(mjjmethode==null){
            return null;
        }

        SimpleNode entetes = (SimpleNode) mjjmethode.jjtGetChild(2);
        SimpleNode vars = (SimpleNode) mjjmethode.jjtGetChild(3);
        SimpleNode instrs = (SimpleNode) mjjmethode.jjtGetChild(4);

        while(listExp instanceof ASTMjjlistexp && entetes instanceof ASTMjjentetes){
            SimpleNode exp = (SimpleNode) listExp.jjtGetChild(0);
            SimpleNode entete = (SimpleNode) entetes.jjtGetChild(0);

            Object value = exp.jjtAccept(this,data);
            String enteteIdent = (String) ((SimpleNode) entete.jjtGetChild(1)).jjtGetValue();
            try{
                memory.affecterVal(fullScope+"@"+enteteIdent,value);
            }catch (Exception ignored){};

            listExp = (SimpleNode) listExp.jjtGetChild(1);
            entetes = (SimpleNode) entetes.jjtGetChild(1);
        }


        InterpreterData interpreterData = (InterpreterData) data;
        interpreterData.exitScope();
        interpreterData.enterScope( identMeth + paramType);

        vars.jjtAccept(this,data);
        Object value = null;
        if(isAppelE){
            value = instrs.jjtAccept(this,data);
            interpreterData.setInterpreterMode(InterpreterMode.RETRAIT);
            SimpleNode REntetes = (SimpleNode) mjjmethode.jjtGetChild(2);
            SimpleNode RVars = (SimpleNode) mjjmethode.jjtGetChild(3);
            RVars.jjtAccept(this,data);
            REntetes.jjtAccept(this,data);
            return value;
        }else{
            instrs.jjtAccept(this,data);
        }

        interpreterData.setInterpreterMode(InterpreterMode.RETRAIT);
        SimpleNode REntetes = (SimpleNode) mjjmethode.jjtGetChild(2);
        SimpleNode RVars = (SimpleNode) mjjmethode.jjtGetChild(3);
        RVars.jjtAccept(this,data);
        REntetes.jjtAccept(this,data);

        return value;
    }


    @Override
    public Object visit(ASTMjjappelI node, Object data) {
        String identMeth = (String) ((SimpleNode) node.jjtGetChild(0)).jjtGetValue();
        SimpleNode listExp = (SimpleNode) node.jjtGetChild(1);
        return appelI(identMeth,listExp,data,false);
    }

    @Override
    public Object visit(ASTMjjretour node, Object data) {
        SimpleNode nodeExp = (SimpleNode) node.jjtGetChild(0);
        Object value =  nodeExp.jjtAccept(this,data);
        return value;
    }

    @Override
    public Object visit(ASTMjjecrire node, Object data) {
        SimpleNode nodeIdent = (SimpleNode) node.jjtGetChild(0);
        Object dataToPrint = nodeIdent.jjtAccept(this,data);
        System.out.print(dataToPrint);
        return null;
    }

    @Override
    public Object visit(ASTMjjecrireln node, Object data) {
        SimpleNode nodeIdent = (SimpleNode) node.jjtGetChild(0);
        Object dataToPrint = nodeIdent.jjtAccept(this,data);
        System.out.println(dataToPrint);
        return null;
    }

    @Override
    public Object visit(ASTMjjsi node, Object data) {
        SimpleNode exp = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeInstrsIf = (SimpleNode) node.jjtGetChild(1);
        SimpleNode nodeInstrsElse = (SimpleNode) node.jjtGetChild(2);

        InterpreterData interpreterData = (InterpreterData) data;
        InterpreterData copyOfInterpreterData = new InterpreterData(interpreterData);

        boolean expr = (boolean) exp.jjtAccept(this,copyOfInterpreterData);

        Object value =null;

        if(expr){
            value=nodeInstrsIf.jjtAccept(this,interpreterData);
        }else{
            value=nodeInstrsElse.jjtAccept(this,interpreterData);
        }
        return value;
    }

    @Override
    public Object visit(ASTMjjtantque node, Object data) {
        SimpleNode exp = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeInstrs= (SimpleNode) node.jjtGetChild(1);

        long startTime = System.currentTimeMillis();
        long maxDuration = 10000;

        InterpreterData interpreterData = (InterpreterData) data;
        InterpreterData copyOfInterpreterData = new InterpreterData(interpreterData);


        while ((boolean) exp.jjtAccept(this,copyOfInterpreterData)){
            if (System.currentTimeMillis() - startTime > maxDuration) {
                errors.add(new MaximumExecutionError(node.jjtGetFirstToken().beginLine,
                        node.jjtGetFirstToken().beginColumn,
                        "Maximum execution time of 60 seconds exceeded"));
                break;
            }
            Object value =nodeInstrs.jjtAccept(this,interpreterData);
            if(value!=null){
                return value;
            }
        }
        return null;
    }

    @Override
    public Object visit(ASTMjjchaine node, Object data) {
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTMjjlistexp node, Object data) {
        StringBuilder paramTypes = new StringBuilder();
        SimpleNode exp = (SimpleNode) node.jjtGetChild(0);
        Object value = exp.jjtAccept(this, data);

        if (value instanceof Integer) {
            paramTypes.append("@int");
        } else if (value instanceof Boolean) {
            paramTypes.append("@boolean");
        }

        SimpleNode nextNode = (SimpleNode) node.jjtGetChild(1);
        if (!(nextNode instanceof ASTMjjexnil)) {
            paramTypes.append((String) nextNode.jjtAccept(this, data));
        }
        return paramTypes.toString();
    }

    @Override
    public Object visit(ASTMjjnon node, Object data) {
        SimpleNode nodeLeft = (SimpleNode) node.jjtGetChild(0);
        boolean left = (boolean) nodeLeft.jjtAccept(this,data);
        return !left;
    }

    @Override
    public Object visit(ASTMjjet node, Object data) {
        SimpleNode nodeLeft = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeRight = (SimpleNode) node.jjtGetChild(1);

        boolean left = (boolean) nodeLeft.jjtAccept(this,data);
        boolean right = (boolean) nodeRight.jjtAccept(this,data);

        return left && right;
    }

    @Override
    public Object visit(ASTMjjou node, Object data) {
        SimpleNode nodeLeft = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeRight = (SimpleNode) node.jjtGetChild(1);

        boolean left = (boolean) nodeLeft.jjtAccept(this,data);
        boolean right = (boolean) nodeRight.jjtAccept(this,data);

        return left || right;
    }

    @Override
    public Object visit(ASTMjjegal node, Object data) {
        SimpleNode nodeLeft = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeRight = (SimpleNode) node.jjtGetChild(1);

        int left = (int) nodeLeft.jjtAccept(this,data);
        int right = (int) nodeRight.jjtAccept(this,data);

        return left == right;
    }

    @Override
    public Object visit(ASTMjjsup node, Object data) {
        SimpleNode nodeLeft = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeRight = (SimpleNode) node.jjtGetChild(1);

        int left = (int) nodeLeft.jjtAccept(this,data);
        int right = (int) nodeRight.jjtAccept(this,data);

        return left > right;
    }

    @Override
    public Object visit(ASTMjjmoins node, Object data) {
        SimpleNode exp = (SimpleNode) node.jjtGetChild(0);
        int expResult = (int) exp.jjtAccept(this, data);
        return (-expResult);
    }

    private void checkMaxInt(long value,SimpleNode node){
        if(value > (long) Integer.MAX_VALUE){
            errors.add(new ArithmeticError(node.jjtGetFirstToken().beginLine,
                    node.jjtGetFirstToken().beginColumn,
                    value +"is too large to be an integer"));
        }
    }

    @Override
    public Object visit(ASTMjjaddition node, Object data) {
        SimpleNode nodeLeft = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeRight = (SimpleNode) node.jjtGetChild(1);

        int left = (int) nodeLeft.jjtAccept(this,data);
        int right = (int) nodeRight.jjtAccept(this,data);

        checkMaxInt((long) left + right,node);

        return left + right;
    }

    @Override
    public Object visit(ASTMjjsoustraction node, Object data) {
        SimpleNode nodeLeft = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeRight = (SimpleNode) node.jjtGetChild(1);

        int left = (int) nodeLeft.jjtAccept(this,data);
        int right = (int) nodeRight.jjtAccept(this,data);

        checkMaxInt((long) left - right,node);

        return left - right;
    }



    @Override
    public Object visit(ASTMjjmult node, Object data) {
        SimpleNode nodeLeft = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeRight = (SimpleNode) node.jjtGetChild(1);

        int left = (int) nodeLeft.jjtAccept(this,data);
        int right = (int) nodeRight.jjtAccept(this,data);
        checkMaxInt((long) left * right,node);

        return left * right;
    }

    @Override
    public Object visit(ASTMjjdiv node, Object data) {
        SimpleNode nodeLeft = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeRight = (SimpleNode) node.jjtGetChild(1);

        int left = (int) nodeLeft.jjtAccept(this,data);
        int right = (int) nodeRight.jjtAccept(this,data);

        if (right == 0) {
            errors.add(new ArithmeticError(node.jjtGetFirstToken().beginLine,
                    node.jjtGetFirstToken().beginColumn,
                    "Division by zero is not allowed"));
            return null;
        }
        checkMaxInt((long) left / right,node);


        return left / right;
    }

    @Override
    public Object visit(ASTMjjappelE node, Object data) {
        String identMeth = (String) ((SimpleNode) node.jjtGetChild(0)).jjtGetValue();
        SimpleNode listExp = (SimpleNode) node.jjtGetChild(1);
        return appelI(identMeth,listExp,data,true);
    }

    @Override
    public Object visit(ASTMjjtab node, Object data) {
        SimpleNode NodeIdent = (SimpleNode) node.jjtGetChild(0);
        int idTab = (int) NodeIdent.jjtAccept(this,data);
        String ident = (String) NodeIdent.jjtGetValue();
        SimpleNode nodeExp  = (SimpleNode) node.jjtGetChild(1);
        int index = (int) nodeExp.jjtAccept(this,data);
        try {
            if(checkLength(index,idTab,node)){
                Object value = memory.getTas().getValueOfArray(idTab,index);
                if(value == null){
                    errors.add((new NotInitializedError(node.jjtGetFirstToken().beginLine,
                            node.jjtGetFirstToken().beginColumn,
                            "Variable "  + ident + " with index "+index+" might not have been initialized.")));
                }
                return value;
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return null;
    }

    private boolean checkLength(int index, int idTab,SimpleNode node){
        boolean res = true;
        try{
            int length = memory.getTas().getArrayWithId(idTab).getTaille();
            if (index > length-1){
                res =false;
                errors.add(new OutOfBoundError(node.jjtGetFirstToken().beginLine,
                        node.jjtGetFirstToken().beginColumn));
            }
        }catch (Exception ignored){}

        return res;
    }

    @Override
    public Object visit(ASTMjjlongueur node, Object data) {
        SimpleNode NodeIdent = (SimpleNode) node.jjtGetChild(0);
        int idTab = (int) NodeIdent.jjtAccept(this,data);
        try{
            return memory.getTas().getArrayWithId(idTab).getTaille();
        }catch (Exception ignored){}
        return null;
    }

    @Override
    public Object visit(ASTMjjvrai node, Object data) {
        return true;
    }

    @Override
    public Object visit(ASTMjjfaux node, Object data) {
        return false;
    }

    @Override
    public Object visit(ASTMjjrien node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjentier node, Object data) {
        if(data==null){
            return "int";
        }
        return null;
    }

    @Override
    public Object visit(ASTMjjbool node, Object data) {
        if(data==null){
            return "boolean";
        }
        return null;
    }

    @Override
    public Object visit(ASTMjjnbre node, Object data) {
        return node.jjtGetValue();
    }

    @Override
    public Object visit(ASTMjjvnil node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjenil node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjinil node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjexnil node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjomega node, Object data) {
        return "w";
    }


}
