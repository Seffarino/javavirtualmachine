package fr.ufrst.m1info.comp5.compiler;


import fr.ufrst.m1info.comp5.minijaja.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class CompilerVisitor implements MiniJajaAnalyserVisitor {
    private final LinkedList<String> jjcStack;

    private final String SCOPE_CLASS = "class";
    private final String SCOPE_MAIN = "main";
    private final Map<String, String> miniSymboleTableVar = new HashMap<>();
    private final Map<String, String> miniSymboleTableMeth = new HashMap<>();



    private void addToJjc(String n){
        jjcStack.addLast(n);
    }
    public CompilerVisitor(LinkedList<String>jjcStack){
        this.jjcStack=jjcStack;
    }
    @Override
    public Object visit(SimpleNode node, Object data) {
        return null;
    }


    @Override
    public Object visit(ASTMjjStart node, Object data) {
        if (node.jjtGetNumChildren() > 0) {
            SimpleNode nodeClass = (SimpleNode) node.jjtGetChild(0);
            addToJjc("start");
            nodeClass.jjtAccept(this, data);
        }
        return null;
    }

    @Override
    public Object visit(ASTMjjclasse node, Object data) {
        LinkedList<String>scopeStack= new LinkedList<>();
        scopeStack.add(SCOPE_CLASS);
        CompilerData compilerData = new CompilerData(scopeStack,CompilerMode.DECL,1);
        SimpleNode nodeDecls = (SimpleNode) node.jjtGetChild(1);
        SimpleNode nodeMain = (SimpleNode) node.jjtGetChild(2);
        int n = compilerData.getN();
        addToJjc("init;");
        LinkedList<String> copyForNodeDecls = new LinkedList<>(scopeStack);
        int ndss = (int) nodeDecls.jjtAccept(this, new CompilerData(copyForNodeDecls, CompilerMode.DECL, n + 1));

        LinkedList<String> copyForNodeMain = new LinkedList<>(scopeStack);
        int nmma = (int) nodeMain.jjtAccept(this, new CompilerData(copyForNodeMain, CompilerMode.DECL, n + ndss + 1));

        int nrdss = (int) nodeDecls.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.RETRAIT, n + ndss +nmma+ 1));

        addToJjc("pop;");
        addToJjc("jcstop;");

        return ndss + nmma + nrdss + 3;
    }



    @Override
    public Object visit(ASTMjjident node, Object data) {
        String value = (String) node.jjtGetValue();
        CompilerData compilerData = (CompilerData) data;
        String valueScope= searchVarInScope(value,compilerData);
        addToJjc("load("+valueScope+");");
        return 1;

    }


    private String searchVarInScope(String value,CompilerData compilerData){
        String fullScope = compilerData.getFullScopeString();
        if(miniSymboleTableVar.containsKey(fullScope+"@"+value)){
            fullScope=compilerData.getFullScopeString();
        }else{
            fullScope = compilerData.getEnclosingScopeString();
        }
        return fullScope+"@"+value;
    }

    private String searchTypeVarInScope(String value,CompilerData compilerData,Map<String, String> miniSymboleTable){
        String fullScope = compilerData.getFullScopeString();
        if(miniSymboleTable.containsKey(fullScope+"@"+value)){
            fullScope=compilerData.getFullScopeString();
        }else{
            fullScope = compilerData.getEnclosingScopeString();
        }
        return "@"+miniSymboleTable.get(fullScope+"@"+value);
    }

    private int processVarsOrDecls(SimpleNode node, CompilerData compilerData) {
        SimpleNode nodeFirst = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeSecond = (SimpleNode) node.jjtGetChild(1);

        int n = compilerData.getN();
        CompilerMode mode = compilerData.getCompilerMode();
        LinkedList<String> scopeStack = compilerData.getScopeStack();

        int firstResult = (int) nodeFirst.jjtAccept(this, new CompilerData(scopeStack, mode, n));
        int secondResult = (int) nodeSecond.jjtAccept(this, new CompilerData(scopeStack, mode, n + firstResult));

        return firstResult + secondResult;
    }

    @Override
    public Object visit(ASTMjjdecls node, Object data) {
        return processVarsOrDecls(node, (CompilerData) data);
    }

    @Override
    public Object visit(ASTMjjmethode node, Object data) {
        SimpleNode nodeType = (SimpleNode) node.jjtGetChild(0);
        String identMeth= (String) ((SimpleNode) node.jjtGetChild(1)).jjtGetValue();
        SimpleNode nodeEntetes = (SimpleNode) node.jjtGetChild(2);
        SimpleNode nodeVars = (SimpleNode) node.jjtGetChild(3);
        SimpleNode nodeInstrs = (SimpleNode) node.jjtGetChild(4);

        CompilerData compilerData = (CompilerData) data;
        int n = compilerData.getN();
        CompilerMode mode = compilerData.getCompilerMode();
        int nombresEntetes= getNombreEntetes(nodeEntetes);
        LinkedList<String>scopeStack=compilerData.getScopeStack();
        String paramsType="";
        if(!(nodeEntetes instanceof ASTMjjenil)){
            paramsType += getEntetesType(nodeEntetes);
        }
        compilerData.enterScope(identMeth+paramsType);
        if(mode == CompilerMode.DECL){
            addToJjc("push("+(n+3)+");");
            String type ="";
            String typeMap ="";
            switch (nodeType.toString()) {
                case "entier" :
                    type="entier";
                    typeMap="int";
                    break;
                case "rien" :
                    type="void";
                    typeMap="void";
                    break;
                case "bool" :
                    type="booleen";
                    typeMap="boolean";
                    break;
            };

            addToJjc("new(class@"+identMeth+paramsType+","+type+",meth,0);");
            miniSymboleTableMeth.put("class@"+identMeth+paramsType,typeMap);
            addToJjc("goto(todo);");
            int ens = (int) nodeEntetes.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n+3,nombresEntetes));
            int dvs = (int) nodeVars.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n+ens+3));
            int iss = (int) nodeInstrs.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n+dvs+ens+3));
            if(!nodeType.toString().equals("rien")){
                int rdvs = (int) nodeVars.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.RETRAIT,n+dvs+iss+ens+3));
                jjcStack.set((n+2), "goto("+(n+ ens + dvs+ iss + rdvs + 5)+");");
                addToJjc("swap;");
                addToJjc("return;");
                compilerData.exitScope();
                return ens + dvs+ iss + rdvs + 5;
            }else{
                addToJjc("push(0);");
                int rdvs = (int) nodeVars.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.RETRAIT,n+dvs+iss+ens+4));
                jjcStack.set((n+2), "goto("+(n+ ens + dvs+ iss + rdvs + 6)+");");
                addToJjc("swap;");
                addToJjc("return;");
                compilerData.exitScope();
                return ens + dvs+ iss + rdvs + 6;
            }


        }else{
            addToJjc("swap;");
            addToJjc("pop;");
            return 2;
        }
    }

    @Override
    public Object visit(ASTMjjtableau node, Object data) {
        return handleVariableDeclaration(node, (CompilerData) data, "newarray(%s@%s,%s);");
    }

    @Override
    public Object visit(ASTMjjvar node, Object data) {
        return handleVariableDeclaration(node, (CompilerData) data, "new(%s@%s,%s,var,0);");
    }

    @Override
    public Object visit(ASTMjjcst node, Object data) {
        return handleVariableDeclaration(node, (CompilerData) data, "new(%s@%s,%s,cst,0);");
    }

    private int handleVariableDeclaration(SimpleNode node, CompilerData compilerData, String instructionTemplate) {
        SimpleNode nodeType = (SimpleNode) node.jjtGetChild(0);
        String identVar = (String) ((SimpleNode) node.jjtGetChild(1)).jjtGetValue();
        SimpleNode nodeExp = (SimpleNode) node.jjtGetChild(2);

        int n = compilerData.getN();
        CompilerMode mode = compilerData.getCompilerMode();
        LinkedList<String> scopeStack = compilerData.getScopeStack();
        String fullScope = compilerData.getFullScopeString();

        if (mode == CompilerMode.DECL) {
            int ne = (int) nodeExp.jjtAccept(this, new CompilerData(scopeStack, mode, n));
            String type = nodeType instanceof ASTMjjentier ? "entier" : "booleen";
            String typeMap = nodeType instanceof ASTMjjentier ? "int" : "boolean";

            addToJjc(String.format(instructionTemplate, fullScope, identVar, type));
            miniSymboleTableVar.put(fullScope + "@" + identVar, typeMap);

            return ne + 1;
        } else {
            addToJjc("swap;");
            addToJjc("pop;");
            return 2;
        }
    }

    @Override
    public Object visit(ASTMjjvars node, Object data) {
        return processVarsOrDecls(node, (CompilerData) data);
    }

    @Override
    public Object visit(ASTMjjmain node, Object data) {
        SimpleNode nodeVars = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeInstrs= (SimpleNode) node.jjtGetChild(1);

        CompilerData compilerData = (CompilerData) data;
        int n = compilerData.getN();
        CompilerMode mode = compilerData.getCompilerMode();
        compilerData.enterScope(SCOPE_MAIN);
        LinkedList<String>scopeStack = compilerData.getScopeStack();


        int ndvs = (int) nodeVars.jjtAccept(this,new CompilerData(scopeStack,mode,n));
        int niss = (int) nodeInstrs.jjtAccept(this,new CompilerData(scopeStack,mode,n+ndvs));
        addToJjc("push(0);");
        int nrdvs = (int) nodeVars.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.RETRAIT,n + ndvs + niss + 1));
        compilerData.exitScope();
        return ndvs + niss + nrdvs + 1;
    }


    @Override
    public Object visit(ASTMjjentetes node, Object data) {
        SimpleNode nodeEntete = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nextNode = (SimpleNode) node.jjtGetChild(1);

        CompilerData compilerData = (CompilerData) data;
        int n = compilerData.getN();
        int nbEntetes = compilerData.getNbEntetes();
        LinkedList<String>scopeStack = compilerData.getScopeStack();

        int ens = (int) nextNode.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n,nbEntetes));
        int en = (int) nodeEntete.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n+ens,nbEntetes));

        return en+ens;
    }

    @Override
    public Object visit(ASTMjjentete node, Object data) {
        SimpleNode nodeType = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeIdent = (SimpleNode) node.jjtGetChild(1);
        String identVar = (String) nodeIdent.jjtGetValue();

        CompilerData compilerData = (CompilerData) data;
        int nbEntetes = compilerData.getNbEntetes();
        String fullScope = compilerData.getFullScopeString();
        String type ="";
        String typeMap = "";
        if(nodeType instanceof ASTMjjentier){
            type ="entier";
            typeMap ="int";
        }else{
            type="booleen";
            typeMap ="boolean";
        }
        addToJjc("new("+fullScope+"@"+identVar+","+type+",var,"+nbEntetes+");");
        miniSymboleTableVar.put(fullScope+"@"+identVar,typeMap);
        return 1;

    }

    private static int getNombreEntetes(SimpleNode mjjentetes) {
        int value = 0;

        while (mjjentetes instanceof ASTMjjentetes) {
            value += 1;

            mjjentetes = (SimpleNode) mjjentetes.jjtGetChild(1);
        }

        return value;
    }

    private static String getEntetesType(SimpleNode mjjentetes){
        StringBuilder paramTypes = new StringBuilder();
        SimpleNode nodeEntete = (SimpleNode) mjjentetes.jjtGetChild(0);
        SimpleNode nextNode = (SimpleNode) mjjentetes.jjtGetChild(1);

        String enteteType = nodeEntete.jjtGetChild(0).toString();
        if(enteteType.equals("entier")){
            paramTypes.append("@int");
        }else{
            paramTypes.append("@boolean");
        }

        if (!(nextNode instanceof ASTMjjenil)) {
            paramTypes.append(getEntetesType(nextNode));
        }

        return paramTypes.toString();
    }
    private String getListExpType(SimpleNode nodeListExp,CompilerData compilerData){
        StringBuilder paramTypes = new StringBuilder();
        SimpleNode nodeExp = (SimpleNode) nodeListExp.jjtGetChild(0);
        SimpleNode nextNode = (SimpleNode) nodeListExp.jjtGetChild(1);

        if(nodeExp instanceof ASTMjjmoins ||
                nodeExp instanceof ASTMjjaddition ||
                nodeExp instanceof ASTMjjsoustraction||
                nodeExp instanceof ASTMjjdiv ||
                nodeExp instanceof ASTMjjmult ||
                nodeExp instanceof ASTMjjlongueur ||
                nodeExp instanceof ASTMjjnbre
        ){
            paramTypes.append("@int");
        }
        if (nodeExp instanceof ASTMjjnon ||
                nodeExp instanceof ASTMjjet ||
                nodeExp instanceof ASTMjjou ||
                nodeExp instanceof ASTMjjegal ||
                nodeExp instanceof ASTMjjsup ||
                nodeExp instanceof ASTMjjvrai||
                nodeExp instanceof ASTMjjfaux
        ){
            paramTypes.append("@boolean");
        }

        if(nodeExp instanceof ASTMjjident ||
                nodeExp instanceof ASTMjjtab){
            String type = searchTypeVarInScope((String)nodeExp.jjtGetValue(),compilerData,miniSymboleTableVar);
            paramTypes.append(type);
        }
        if(nodeExp instanceof ASTMjjappelE){
            String identMeth = (String) ((SimpleNode) nodeExp.jjtGetChild(0)).jjtGetValue();
            String type = searchTypeVarInScope(identMeth,compilerData,miniSymboleTableMeth);
            paramTypes.append(type);
        }

        if (!(nextNode instanceof ASTMjjexnil)) {
            paramTypes.append(getListExpType(nextNode,compilerData));
        }
        return paramTypes.toString();
    }

    @Override
    public Object visit(ASTMjjinstrs node, Object data) {
        SimpleNode nodeInstr = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeInstrs = (SimpleNode) node.jjtGetChild(1);

        CompilerData compilerData = (CompilerData) data;
        int n = compilerData.getN();
        CompilerMode mode = compilerData.getCompilerMode();
        LinkedList<String>scopeStack = compilerData.getScopeStack();


        int is = (int) nodeInstr.jjtAccept(this,new CompilerData(scopeStack,mode,n));
        if(!(nodeInstr instanceof ASTMjjretour)){
            int iss = (int) nodeInstrs.jjtAccept(this, new CompilerData(scopeStack,mode,n+is));
            return is + iss;
        }else{
            return is;
        }
    }

    @Override
    public Object visit(ASTMjjaffectation node, Object data) {
        SimpleNode nodeIdent=(SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeExp= (SimpleNode) node.jjtGetChild(1);

        CompilerData compilerData = (CompilerData) data;
        int n = compilerData.getN();
        LinkedList<String>scopeStack = compilerData.getScopeStack();

        if(nodeIdent instanceof ASTMjjtab){
            SimpleNode nodeI = (SimpleNode) nodeIdent.jjtGetChild(1);
            int e1 = (int) nodeI.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n));
            int e = (int) nodeExp.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n+e1));
            String identVar = (String) ((SimpleNode) nodeIdent.jjtGetChild(0)).jjtGetValue();
            String valueScope= searchVarInScope(identVar,compilerData);
            addToJjc("astore("+valueScope+");");
            return e1 + e + 1;

        }else{
            int e = (int) nodeExp.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n));
            String identVar = (String) nodeIdent.jjtGetValue();
            String valueScope= searchVarInScope(identVar,compilerData);
            addToJjc("store("+valueScope+");");
            return e+1;
        }
    }

    @Override
    public Object visit(ASTMjjsomme node, Object data) {
        SimpleNode nodeIdent=(SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeExp= (SimpleNode) node.jjtGetChild(1);

        CompilerData compilerData = (CompilerData) data;
        int n = compilerData.getN();
        LinkedList<String>scopeStack = compilerData.getScopeStack();

        if(nodeIdent instanceof ASTMjjtab){
            SimpleNode nodeI = (SimpleNode) nodeIdent.jjtGetChild(1);
            int e1 = (int) nodeI.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n));
            int e = (int) nodeExp.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n+e1));
            String identVar = (String) ((SimpleNode) nodeIdent.jjtGetChild(0)).jjtGetValue();
            String valueScope= searchVarInScope(identVar,compilerData);
            addToJjc("ainc("+valueScope+");");
            return e1 + e + 1;

        }else{
            int e = (int) nodeExp.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n));
            String identVar = (String) nodeIdent.jjtGetValue();
            String valueScope= searchVarInScope(identVar,compilerData);
            addToJjc("inc("+valueScope+");");
            return e+1;
        }
    }

    @Override
    public Object visit(ASTMjjincrement node, Object data) {
        SimpleNode nodeIdent=(SimpleNode) node.jjtGetChild(0);
        CompilerData compilerData = (CompilerData) data;
        int n = compilerData.getN();
        LinkedList<String>scopeStack = compilerData.getScopeStack();

        if(nodeIdent instanceof ASTMjjtab){
            SimpleNode nodeI = (SimpleNode) nodeIdent.jjtGetChild(1);
            int e = (int) nodeI.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n));
            addToJjc("push(1);");
            String identVar = (String) ((SimpleNode) nodeIdent.jjtGetChild(0)).jjtGetValue();
            String valueScope= searchVarInScope(identVar,compilerData);
            addToJjc("ainc("+valueScope+");");
            return e + 2;

        }else{
            String identVar = (String) nodeIdent.jjtGetValue();
            addToJjc("push(1);");
            String valueScope= searchVarInScope(identVar,compilerData);
            addToJjc("inc("+valueScope+");");
            return 2;
        }
    }

    @Override
    public Object visit(ASTMjjappelI node, Object data) {
        String identVar = (String) ((SimpleNode) node.jjtGetChild(0)).jjtGetValue();
        SimpleNode nodeListExp = (SimpleNode) node.jjtGetChild(1);

        CompilerData compilerData = (CompilerData) data;
        int n = compilerData.getN();
        LinkedList<String>scopeStack = compilerData.getScopeStack();

        int lexp = (int) nodeListExp.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n));
        String listExpType="";
        if(!(nodeListExp instanceof ASTMjjexnil)){
            listExpType = getListExpType(nodeListExp,compilerData);
        }
        addToJjc("invoke(class@"+identVar+listExpType+");");
        int rlexp = (int) nodeListExp.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.RETRAIT,n+lexp+1));
        addToJjc("pop;");

        return lexp + rlexp + 2;
    }


    @Override
    public Object visit(ASTMjjretour node, Object data) {
        SimpleNode nodeExp = (SimpleNode) node.jjtGetChild(0);
        CompilerData compilerData = (CompilerData) data;
        LinkedList<String>scopeStack = compilerData.getScopeStack();

        int n = compilerData.getN();
        int e = (int) nodeExp.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n));
        return e;
    }
    @Override
    public Object visit(ASTMjjecrire node, Object data) {
        return handleEcrireOperation(node, (CompilerData) data, "write;");
    }@Override
    public Object visit(ASTMjjecrireln node, Object data) {
        return handleEcrireOperation(node, (CompilerData) data, "writeln;");
    }
    private int handleEcrireOperation(SimpleNode node, CompilerData compilerData, String jjcInstruction) {
        SimpleNode nodeExp = (SimpleNode) node.jjtGetChild(0);
        int n = compilerData.getN();
        LinkedList<String> scopeStack = compilerData.getScopeStack();

        int e = (int) nodeExp.jjtAccept(this, new CompilerData(scopeStack, CompilerMode.DECL, n));
        addToJjc(jjcInstruction);
        return e + 1;
    }
    @Override public Object visit(ASTMjjsi node, Object data) {
        SimpleNode nodeExp = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeInstrsIf = (SimpleNode) node.jjtGetChild(1);
        SimpleNode nodeInstrsElse = (SimpleNode) node.jjtGetChild(2);

        CompilerData compilerData = (CompilerData) data;
        int n = compilerData.getN();
        LinkedList<String>scopeStack = compilerData.getScopeStack();


        int e = (int) nodeExp.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n));
        addToJjc("if(todo);");
        int iss1 = (int) nodeInstrsElse.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n+e+1));
        addToJjc("goto(todo);");
        int iss = (int) nodeInstrsIf.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n+e+iss1+2));

        jjcStack.set((n+e), "if("+(n+e+iss1+2)+");");
        jjcStack.set((n+e+iss1+1), "goto("+(n+e+iss1+iss+2)+");");
        return e +iss1+iss+2;
    }

    @Override
    public Object visit(ASTMjjtantque node, Object data) {
        SimpleNode nodeExp = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeInstrs= (SimpleNode) node.jjtGetChild(1);

        CompilerData compilerData = (CompilerData) data;
        int n = compilerData.getN();
        LinkedList<String>scopeStack = compilerData.getScopeStack();

        int e = (int) nodeExp.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n));
        addToJjc("not;");
        addToJjc("if(todo);");
        int iss = (int) nodeInstrs.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n+e+2));
        addToJjc("goto("+n+");");

        jjcStack.set((n+e+1), "if("+(n+e+iss+3)+");");
        return e+iss+3;
    }

    @Override
    public Object visit(ASTMjjchaine node, Object data) {
        String value = (String) node.jjtGetValue();
        addToJjc("push("+value+");");
        return 1;
    }

    @Override
    public Object visit(ASTMjjlistexp node, Object data) {
        SimpleNode nodeExp = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeListExp = (SimpleNode) node.jjtGetChild(1);

        CompilerData compilerData = (CompilerData) data;
        int n = compilerData.getN();
        CompilerMode mode = compilerData.getCompilerMode();
        LinkedList<String>scopeStack = compilerData.getScopeStack();

        if(mode == CompilerMode.DECL){
            int e = (int) nodeExp.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n));
            int lexp = (int) nodeListExp.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n+e));
            return e+lexp;
        }else{
            addToJjc("swap;");
            addToJjc("pop;");
            int rlexp = (int) nodeListExp.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.RETRAIT,n));
            return rlexp+2;
        }
    }
    private Object processBinaryOperation2(SimpleNode node, Object data, String operation) {
        SimpleNode nodeLeft = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeRight = (SimpleNode) node.jjtGetChild(1);

        CompilerData compilerData = (CompilerData) data;
        int n = compilerData.getN();
        LinkedList<String> scopeStack = compilerData.getScopeStack();

        int e1 = (int) nodeLeft.jjtAccept(this, new CompilerData(scopeStack, CompilerMode.DECL, n));
        int e2 = (int) nodeRight.jjtAccept(this, new CompilerData(scopeStack, CompilerMode.DECL, n + e1));

        addToJjc(operation);

        return e1 + e2 + 1;
    }

    private Object processBinaryOperation1(SimpleNode node, Object data, String operation) {
        SimpleNode nodeExp = (SimpleNode) node.jjtGetChild(0);

        CompilerData compilerData = (CompilerData) data;
        int n = compilerData.getN();
        LinkedList<String>scopeStack = compilerData.getScopeStack();

        int e1 = (int) nodeExp.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n));
        addToJjc(operation);

        return e1+1;
    }

    @Override
    public Object visit(ASTMjjnon node, Object data) {
        return processBinaryOperation1(node, data, "not;");
    }


    @Override
    public Object visit(ASTMjjet node, Object data) {
        return processBinaryOperation2(node, data, "and;");
    }

    @Override
    public Object visit(ASTMjjou node, Object data) {
        return processBinaryOperation2(node, data, "or;");
    }

    @Override
    public Object visit(ASTMjjegal node, Object data) {
        return processBinaryOperation2(node, data, "cmp;");
    }

    @Override
    public Object visit(ASTMjjsup node, Object data) {
        return processBinaryOperation2(node, data, "sup;");
    }

    @Override
    public Object visit(ASTMjjmoins node, Object data) {
        return processBinaryOperation1(node, data, "neg;");
    }

    @Override
    public Object visit(ASTMjjaddition node, Object data) {
        return processBinaryOperation2(node, data, "add;");
    }

    @Override
    public Object visit(ASTMjjsoustraction node, Object data) {
        return processBinaryOperation2(node, data, "sub;");
    }

    @Override
    public Object visit(ASTMjjmult node, Object data) {
        return processBinaryOperation2(node, data, "mul;");
    }

    @Override
    public Object visit(ASTMjjdiv node, Object data) {
        return processBinaryOperation2(node, data, "div;");
    }

    @Override
    public Object visit(ASTMjjappelE node, Object data) {
        String identVar = (String) ((SimpleNode) node.jjtGetChild(0)).jjtGetValue();
        SimpleNode nodeListExp = (SimpleNode) node.jjtGetChild(1);

        CompilerData compilerData = (CompilerData) data;
        int n = compilerData.getN();
        LinkedList<String>scopeStack = compilerData.getScopeStack();
        String listExpType="";
        if(!(nodeListExp instanceof ASTMjjexnil)){
            listExpType = getListExpType(nodeListExp,compilerData);
        }
        int lexp = (int) nodeListExp.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n));
        addToJjc("invoke(class@"+identVar+listExpType+");");
        int rlexp = (int) nodeListExp.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.RETRAIT,n+lexp+1));

        return lexp + rlexp + 1;
    }

    @Override public Object visit(ASTMjjtab node, Object data) {
        String identVar = (String) ((SimpleNode) node.jjtGetChild(0)).jjtGetValue();
        SimpleNode nodeExp = (SimpleNode) node.jjtGetChild(1);
        CompilerData compilerData = (CompilerData) data;
        int n = compilerData.getN();
        LinkedList<String>scopeStack = compilerData.getScopeStack();

        int e = (int) nodeExp.jjtAccept(this,new CompilerData(scopeStack,CompilerMode.DECL,n));
        String valueScope= searchVarInScope(identVar,compilerData);
        addToJjc("aload("+valueScope+");");
        return e+1;
    }
    @Override
    public Object visit(ASTMjjlongueur node, Object data) {
        String identVar= (String) ((SimpleNode) node.jjtGetChild(0)).jjtGetValue();
        CompilerData compilerData = (CompilerData) data;
        String valueScope= searchVarInScope(identVar,compilerData);
        addToJjc("length("+valueScope+");");
        return 1;
    }

    @Override
    public Object visit(ASTMjjvrai node, Object data) {
        addToJjc("push(true);");
        return 1;
    }

    @Override
    public Object visit(ASTMjjfaux node, Object data) {
        addToJjc("push(false);");
        return 1;
    }

    @Override
    public Object visit(ASTMjjrien node, Object data) {
        return 0;
    }

    @Override
    public Object visit(ASTMjjentier node, Object data) {
        return 0;
    }

    @Override
    public Object visit(ASTMjjbool node, Object data) {
        return 0;
    }

    @Override
    public Object visit(ASTMjjnbre node, Object data) {
        int value= (int) node.jjtGetValue();
        addToJjc("push("+value+");");
        return 1;
    }

    @Override
    public Object visit(ASTMjjvnil node, Object data) {
        return 0;
    }

    @Override
    public Object visit(ASTMjjenil node, Object data) {
        return 0;
    }

    @Override
    public Object visit(ASTMjjinil node, Object data) {
        return 0;
    }

    @Override public Object visit(ASTMjjexnil node, Object data) {
        return 0;
    }

    @Override public Object visit(ASTMjjomega node, Object data) {
        addToJjc("push(w);");
        return 1;
    }
}