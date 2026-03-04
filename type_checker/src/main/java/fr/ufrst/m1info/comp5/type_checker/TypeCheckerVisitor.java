package fr.ufrst.m1info.comp5.type_checker;
import fr.ufrst.m1info.comp5.memory.*;
import fr.ufrst.m1info.comp5.minijaja.*;
import fr.ufrst.m1info.comp5.type_checker.TypeError.*;

import java.util.List;

public class TypeCheckerVisitor implements MiniJajaAnalyserVisitor {
    public static final int FIRST_PASS = 0;
    public static final int SECOND_PASS = 1;
    private final String SCOPE_CLASS = "class";
    private final String SCOPE_MAIN = "main";
    private int pass_mode;
    private List<TypeCheckerError> errors;
    private SymbolTable symbolTable ;
    private Memory memory;
    // Initialisation de la HashMap data


    public TypeCheckerVisitor(int pass_mode,Memory memory,List<TypeCheckerError> errors) {
        this.pass_mode = pass_mode;
        this.memory=memory;
        this.symbolTable=memory.getSymbolTable();
        this.errors=errors;
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

        visitData visitData = new visitData(SCOPE_CLASS, CheckMode.ANY);


        String identClass= (String) nodeIdent.jjtGetValue();

        try {
            if(this.pass_mode == FIRST_PASS){
                memory.declVar(identClass,"w",Type.ENTIER);
            }
        } catch (SymbolTableException.ExistingSymbolException | MemoryException |
                 SymbolTableException.UnknownSymbolException e) {
            System.err.println(e.getMessage());
        }

        nodeDecls.jjtAccept(this, visitData);
        nodeMain.jjtAccept(this, visitData);
        return null;
    }

    private InfoIdent getIdent(String id) throws SymbolTableException.UnknownSymbolException {
        try {
            return symbolTable.get(id,false);
        }catch(SymbolTableException.UnknownSymbolException e){
            try{
                return symbolTable.get(id,true);

            }catch(SymbolTableException.UnknownSymbolException e2){
                throw e2;
            }
        }
    }

    private InfoIdent getIdentAllClass(String scopeIdent,String ident,visitData visitData) throws SymbolTableException.UnknownSymbolException {
        try {
            return getIdent(scopeIdent);
        } catch (SymbolTableException.UnknownSymbolException e) {
            String broaderScopeIdent = visitData.getEnclosingScopeString() + "@" + ident;
            try {
                return getIdent(broaderScopeIdent);
            } catch (SymbolTableException.UnknownSymbolException e2) {
                throw e2;
            }
        }
    }

    @Override
    public Object visit(ASTMjjident node, Object data) {
        String ident = (String) node.jjtGetValue();
        visitData visitData = (visitData) data;

        String fullScope = visitData.getFullScopeString();
        CheckMode check_mode = visitData.getCheckMode();

        String scopeIdent = fullScope + "@" + ident;
        InfoIdent info=null;

        try{
            info = getIdentAllClass(scopeIdent,ident,visitData);
        }catch(SymbolTableException.UnknownSymbolException e){
            errors.add(new CanNotFindSymbolError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,ident));

        }

        if(info !=null) {
            switch (check_mode) {
                case VAR:
                    if(info.getSorte() != Sorte.VAR){
                        errors.add(new SorteError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"Symbol '"+ident+"' must be a variable"));
                    }
                    break;
                case CST:
                    if(info.getSorte() != Sorte.CST){
                        errors.add(new SorteError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"Symbol '"+ident+"' must be a constant"));
                    }
                    break;
                case VALUE:
                    if (info.getSorte() != Sorte.VAR && info.getSorte() != Sorte.CST && info.getSorte() != Sorte.VCST) {
                        errors.add(new SorteError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"Symbol '"+ident+"' must be a value ( Integer or Integer constant )"));
                    }
                    break;
                case TAB:
                    if(info.getSorte() != Sorte.TAB){
                        errors.add(new SorteError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"Symbol '"+ident+"' must be an array"));
                    }
                    break;
                case METH:
                    if(info.getSorte() != Sorte.METH){
                        errors.add(new SorteError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"Symbol '"+ident+"' must be a methode"));
                    }
                    break;
                case PARAM:
                    if (info.getSorte() != Sorte.VAR &&
                            info.getSorte() != Sorte.CST &&
                            info.getSorte() != Sorte.VCST &&
                            info.getSorte() != Sorte.TAB) {
                        errors.add(new SorteError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"Symbol '"+ident+"' must be a variable or assigned constant of type Integer, Boolean"));
                    }
                    break;
                case ANY:
                    break;
            }
            return MjType.getLabelFromType(info.getType());
        }
        return null;
    }

    @Override
    public Object visit(ASTMjjdecls node, Object data) {
        SimpleNode nodeDecl = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeDecls = (SimpleNode) node.jjtGetChild(1);

        visitData visitData = (visitData) data;
        visitData NewVisitData = new visitData(SCOPE_CLASS,visitData.getCheckMode());

        nodeDecl.jjtAccept(this, NewVisitData);
        nodeDecls.jjtAccept(this, NewVisitData);

        return null;
    }

    @Override
    public Object visit(ASTMjjmethode node, Object data) {
        SimpleNode nodeType = (SimpleNode) node.jjtGetChild(0);
        String identMeth = (String) ((SimpleNode) node.jjtGetChild(1)).jjtGetValue();

        SimpleNode nodeEntetes = (SimpleNode) node.jjtGetChild(2);
        SimpleNode nodeVars = (SimpleNode) node.jjtGetChild(3);
        SimpleNode nodeInstrs= (SimpleNode) node.jjtGetChild(4);

        visitData visitData = (visitData) data;
        String paramTypes = (String) nodeEntetes.jjtAccept(this, null);

        if (!paramTypes.isEmpty()) {
            visitData.enterScope(identMeth+paramTypes);
        }else{
            visitData.enterScope(identMeth);
        }

        String scope = visitData.getFullScopeString();
        String typeLabel = (String) nodeType.jjtAccept(this, visitData);
        MjType type = MjType.fromLabel(typeLabel);
        if(pass_mode==FIRST_PASS){
            nodeEntetes.jjtAccept(this, visitData);
            try {
                memory.declMeth(scope,null,type.toType());
            }catch(SymbolTableException.ExistingSymbolException | MemoryException |
                   SymbolTableException.UnknownSymbolException e ){
                errors.add(new DuplicationError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,identMeth));
            }
        }
        nodeVars.jjtAccept(this, visitData);
        if(pass_mode==SECOND_PASS){
            nodeInstrs.jjtAccept(this, visitData);

            if (type != MjType.RIEN && !hasReturn(nodeInstrs)) {
                errors.add(new MissingReturnError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn));
            }
        }
        return null;
    }
    @Override
    public Object visit(ASTMjjtableau node, Object data) {
        SimpleNode nodeType = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeExp = (SimpleNode) node.jjtGetChild(2);

        visitData visitData = (visitData) data;
        String scope = visitData.getFullScopeString();

        String identVar= (String) ((SimpleNode) node.jjtGetChild(1)).jjtGetValue();
        String typeLabel = (String) nodeType.jjtAccept(this, visitData);

        if(pass_mode==SECOND_PASS){
            MjType type = MjType.fromLabel(typeLabel);

            if (type == MjType.RIEN) {
                errors.add(new VoidTypeError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn));
            }else{
                try {
                    String id = scope+"@"+identVar;
                    InfoIdent q = new InfoIdent(id, type.toType(), Sorte.TAB);
                    symbolTable.add(q);
                }catch(SymbolTableException.ExistingSymbolException | MemoryException e){
                    errors.add(new DuplicationError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,identVar));
                }
            }
            if(nodeExp.toString().equals("moins")){
                errors.add(new NegativeArraySizeError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn));
            }
            String ExpTypeLabel = (String) nodeExp.jjtAccept(this, visitData);
            if(ExpTypeLabel==null || !ExpTypeLabel.equals("int") ){
                errors.add(new TabDeclarationError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn));
            }
        }
        return null;
    }

    @Override
    public Object visit(ASTMjjvar node, Object data) {
        SimpleNode nodeType = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeExp = (SimpleNode) node.jjtGetChild(2);

        visitData visitData = (visitData) data;
        String scope = visitData.getFullScopeString();

        String identVar= (String) ((SimpleNode) node.jjtGetChild(1)).jjtGetValue();
        String typeLabel = (String) nodeType.jjtAccept(this, visitData);

        if(pass_mode==SECOND_PASS){
            MjType type = MjType.fromLabel(typeLabel);

            if (type == MjType.RIEN) {
                errors.add(new VoidTypeError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn));
            }else{
                try {
                    String id = scope+"@"+identVar;
                    memory.declVar(id,null,type.toType());
                }catch(SymbolTableException.ExistingSymbolException | MemoryException |
                       SymbolTableException.UnknownSymbolException e ){
                    errors.add(new DuplicationError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,identVar));

                }
            }
            String ExpTypeLabel = (String) nodeExp.jjtAccept(this, visitData);
            if(ExpTypeLabel!=null && !typeLabel.equals(ExpTypeLabel) && !ExpTypeLabel.equals("omega")){
                errors.add(new TypeMismatchError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"Can not affect "+ ExpTypeLabel+" in "+typeLabel));
            }
        }
        return null;
    }

    @Override
    public Object visit(ASTMjjcst node, Object data) {
        SimpleNode nodeType = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeExp = (SimpleNode) node.jjtGetChild(2);

        visitData visitData = (visitData) data;
        String scope = visitData.getFullScopeString();

        String ExpTypeLabel = (String) nodeExp.jjtAccept(this, visitData);
        String identCst= (String) ((SimpleNode) node.jjtGetChild(1)).jjtGetValue();
        String typeLabel = (String) nodeType.jjtAccept(this, visitData);
        if(pass_mode==SECOND_PASS){
            MjType type = MjType.fromLabel(typeLabel);
            try {
                String id = scope+"@"+identCst;
                memory.declCst(id,"w",type.toType());
            }catch(SymbolTableException.ExistingSymbolException | MemoryException |
                   SymbolTableException.UnknownSymbolException e ){
                errors.add(new DuplicationError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,identCst));
            }
            if(ExpTypeLabel!=null && !typeLabel.equals(ExpTypeLabel) && !ExpTypeLabel.equals("omega")){
                errors.add(new TypeMismatchError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"Can not affect "+ ExpTypeLabel+" in "+typeLabel));
            }
        }
        return null;
    }

    @Override
    public Object visit(ASTMjjvars node, Object data) {
        SimpleNode nodeVar = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeVars= (SimpleNode) node.jjtGetChild(1);

        nodeVar.jjtAccept(this, data);
        nodeVars.jjtAccept(this, data);

        return null;
    }
    @Override
    public Object visit(ASTMjjmain node, Object data) {
        SimpleNode nodeVars = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeInstrs= (SimpleNode) node.jjtGetChild(1);

        visitData visitData = (visitData) data;
        visitData.enterScope(SCOPE_MAIN);

        nodeVars.jjtAccept(this, visitData);
        if(pass_mode==SECOND_PASS){
            nodeInstrs.jjtAccept(this, visitData);
        }
        return null;
    }

    @Override
    public Object visit(ASTMjjentetes node, Object data) {
        if(data==null){
            StringBuilder paramTypes = new StringBuilder();
            // Visitez le premier enfant (ASTMjjentete) pour obtenir le type
            SimpleNode nodeEntete = (SimpleNode) node.jjtGetChild(0);
            paramTypes.append((String) nodeEntete.jjtAccept(this, data));

            // Visitez le second enfant, qui peut être ASTMjjentetes ou ASTMjjenil
            SimpleNode nextNode = (SimpleNode) node.jjtGetChild(1);
            if (!(nextNode instanceof ASTMjjenil)) {
                // Si ce n'est pas un ASTMjjenil, nous ajoutons le séparateur et continuons la récursion.
                paramTypes.append((String) nextNode.jjtAccept(this, data));
            }
            return paramTypes.toString();
        }else{
            SimpleNode nodeEntete = (SimpleNode) node.jjtGetChild(0);
            SimpleNode nodeEntetes = (SimpleNode) node.jjtGetChild(1);

            nodeEntete.jjtAccept(this, data);
            nodeEntetes.jjtAccept(this, data);
            return null;
        }

    }

    @Override
    public Object visit(ASTMjjentete node, Object data) {
        SimpleNode nodeType = (SimpleNode) node.jjtGetChild(0);
        String identEntete = (String) ((SimpleNode) node.jjtGetChild(1)).jjtGetValue();
        String typeLabel = (String) nodeType.jjtAccept(this, data);
        MjType type = MjType.fromLabel(typeLabel);

        if(data==null){
            return "@"+typeLabel;
        }else{
            visitData visitData = (visitData) data;
            String scope = visitData.getFullScopeString();
            try {
                String id = scope+"@"+identEntete;
                memory.declVar(id,null,type.toType());
            }catch(SymbolTableException.ExistingSymbolException | MemoryException |
                   SymbolTableException.UnknownSymbolException e ){
                errors.add(new DuplicationError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,identEntete));

            }
            return null;
        }
    }

    @Override
    public Object visit(ASTMjjinstrs node, Object data) {

        SimpleNode nodeInstr = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeInstrs = (SimpleNode) node.jjtGetChild(1);

        nodeInstr.jjtAccept(this, data);
        nodeInstrs.jjtAccept(this, data);
        return null;
    }

    @Override
    public Object visit(ASTMjjaffectation node, Object data) {
        SimpleNode nodeIdent = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeExp = (SimpleNode) node.jjtGetChild(1);

        visitData visitData = (visitData) data;
        String scope = visitData.getFullScopeString();
        String identValue = (String) nodeIdent.jjtGetValue();

        if(nodeIdent instanceof ASTMjjtab){
            String identType = (String) nodeIdent.jjtAccept(this,visitData);

            visitData.setCheckMode(CheckMode.VALUE);
            String expType = (String) nodeExp.jjtAccept(this,visitData);
            if(!identType.equals(expType)){
                errors.add(new TypeMismatchError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"Can not affect "+ expType+" in "+identType));

            }

        }else{
            try{
                visitData.setCheckMode(CheckMode.ANY);
                String identType = (String) nodeIdent.jjtAccept(this,visitData);
                InfoIdent info = getIdentAllClass(scope+"@"+identValue,identValue,visitData);
                if(info.getSorte()==Sorte.METH){
                    errors.add(new InvalidCallError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,identValue));
                }
                if(info.getSorte()==Sorte.TAB){
                    visitData.setCheckMode(CheckMode.TAB);
                    String expType = (String) nodeExp.jjtAccept(this,visitData);
                    if(!identType.equals(expType)){
                        errors.add(new TypeMismatchError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"Can not affect "+ expType+" in "+identType));

                    }
                    if(!nodeExp.toString().equals("ident")){
                        errors.add(new InvalidAssignementError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"Ca not affect value of type '"+identType + "' to the array '" + identValue+"'"));
                    }
                }
                if(info.getSorte()==Sorte.VAR || info.getSorte()==Sorte.VCST){
                    visitData.setCheckMode(CheckMode.VALUE);
                    String expType = (String) nodeExp.jjtAccept(this,visitData);
                    if(!identType.equals(expType)){
                        errors.add(new TypeMismatchError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"Can not affect "+ expType+" in "+identType));
                    }
                }

            } catch (SymbolTableException.UnknownSymbolException ignored) {}
        }

        return null;
    }

    @Override
    public Object visit(ASTMjjsomme node, Object data) {
        visitData visitData = (visitData) data;

        SimpleNode nodeIdent = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeExp = (SimpleNode) node.jjtGetChild(1);

        visitData.setCheckMode(CheckMode.VAR);
        String typeLabel = (String) nodeIdent.jjtAccept(this, visitData);

        visitData.setCheckMode(CheckMode.VALUE);
        String expLabel = (String) nodeExp.jjtAccept(this, visitData);

        if(!typeLabel.equals("int")){
            errors.add(new InvalidOperationError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"Symbol"+nodeIdent.jjtGetValue()+"must be an Integer"));
        };

        if(!expLabel.equals("int")){
            errors.add(new TypeMismatchError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"Expression must be an Integer"));

        };

        return "int";
    }

    @Override
    public Object visit(ASTMjjincrement node, Object data) {
        visitData visitData = (visitData) data;
        visitData.setCheckMode(CheckMode.VAR);
        SimpleNode nodeIdent = (SimpleNode) node.jjtGetChild(0);
        String typeLabel = (String) nodeIdent.jjtAccept(this, visitData);
        if(!typeLabel.equals("int")){
            errors.add(new InvalidOperationError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"Symbol"+nodeIdent.jjtGetValue()+"must be an Integer"));
        };
        return "int";
    }

    @Override
    public Object visit(ASTMjjappelI node, Object data) {
        String identMeth = (String) ((SimpleNode) node.jjtGetChild(0)).jjtGetValue();
        SimpleNode listExp = (SimpleNode) node.jjtGetChild(1);

        String listExpString = (String) listExp.jjtAccept(this, data);

        String methWithParam = "class@"+identMeth;
        if(listExpString!=null && !listExpString.isEmpty()){
            methWithParam+="@"+listExpString;
        }
        try {
            InfoIdent info = symbolTable.get(methWithParam,true);
            if(info.getType()!=Type.VOID){
                errors.add(new InvalidCallError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,identMeth));

            }
        }catch(SymbolTableException.UnknownSymbolException e){
            errors.add(new CanNotFindSymbolError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,identMeth));

        }
        return null;
    }

    @Override
    public Object visit(ASTMjjretour node, Object data) {
        visitData visitData = (visitData) data;
        String scope = visitData.getFullScopeString();
        if(scope.equals(SCOPE_CLASS) || scope.equals(SCOPE_CLASS+"@"+SCOPE_MAIN)){
            errors.add(new InvalidReturnError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn," Can not return something outside of a method"));
            return null;
        }

        SimpleNode exp = (SimpleNode) node.jjtGetChild(0);
        String typeLabel= (String) exp.jjtAccept(this, visitData);

        try {
            InfoIdent q = symbolTable.get(scope,true);
            if(q.getType()==Type.VOID){
                errors.add(new InvalidReturnError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn," Can not return something in Void methode"));
                return null;

            }
            String methType = MjType.getLabelFromType(q.getType());
            if(methType == null ||!methType.equals(typeLabel)){
                errors.add(new InvalidReturnError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn," Can not return '"+ typeLabel +"' in '"+ methType + "' methode"));

                return null;
            }
        } catch (SymbolTableException.UnknownSymbolException e) {
            errors.add(new InvalidReturnError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn));
        }

        return null;
    }

    private boolean hasReturn(SimpleNode node){
        boolean hasReturn = false;
        while(node.toString().equals("instrs")){
            SimpleNode nodeInstr = (SimpleNode) node.jjtGetChild(0);
            if(nodeInstr.toString().equals("si")){
                hasReturn = hasReturn((SimpleNode)nodeInstr.jjtGetChild(1)) && hasReturn((SimpleNode)nodeInstr.jjtGetChild(2));
            }
            if(nodeInstr.toString().equals("retour")){
                hasReturn = true;
            }
            node= (SimpleNode) node.jjtGetChild(1);

        }
        return hasReturn;
    }


    @Override
    public Object visit(ASTMjjecrire node, Object data) {
        SimpleNode child = (SimpleNode) node.jjtGetChild(0);
        visitData visitData = (visitData) data;
        visitData.setCheckMode(CheckMode.VALUE);
        String typeLabel= (String) child.jjtAccept(this, visitData);
        if(typeLabel==null){
            return null;
        }
        if(!typeLabel.equals("String") &&
                !typeLabel.equals("int") &&
                !typeLabel.equals("boolean")){
            errors.add(new TypeMismatchError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"Expression must be an Integer, a Boolean or a String"));
        }
        return null;
    }

    @Override
    public Object visit(ASTMjjecrireln node, Object data) {
        SimpleNode child = (SimpleNode) node.jjtGetChild(0);
        visitData visitData = (visitData) data;
        visitData.setCheckMode(CheckMode.VALUE);
        String typeLabel= (String) child.jjtAccept(this, visitData);
        if(typeLabel==null){
            return null;
        }
        if(!typeLabel.equals("String") &&
                !typeLabel.equals("int") &&
                !typeLabel.equals("boolean")){
            errors.add(new TypeMismatchError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"Expression must be an Integer, a Boolean or a String"));

        }
        return null;
    }


    @Override
    public Object visit(ASTMjjsi node, Object data) {
        SimpleNode exp = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeInstrsIf = (SimpleNode) node.jjtGetChild(1);
        SimpleNode nodeInstrsElse = (SimpleNode) node.jjtGetChild(2);

        String typeLabel= (String) exp.jjtAccept(this, data);
        if(!typeLabel.equals("boolean")){
            errors.add(new InvalidOperationError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"The expression must be an Boolean"));
        };

        if (nodeInstrsIf.toString().equals("inil")
                || (nodeInstrsIf.toString().equals("inil") &&
                !nodeInstrsElse.toString().equals("inil"))){
            errors.add(new EmptyBlockError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn));
        }

        nodeInstrsIf.jjtAccept(this, data);
        nodeInstrsElse.jjtAccept(this, data);

        return null;
    }

    @Override
    public Object visit(ASTMjjtantque node, Object data) {
        SimpleNode exp = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeInstrs = (SimpleNode) node.jjtGetChild(1);

        String typeLabel= (String) exp.jjtAccept(this, data);
        if(!typeLabel.equals("boolean")){
            errors.add(new InvalidOperationError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"The expression must be an Boolean"));
        };

        if (nodeInstrs.toString().equals("inil")){
            errors.add(new EmptyBlockError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn));
        }
        nodeInstrs.jjtAccept(this, data);

        return null;
    }

    @Override
    public Object visit(ASTMjjchaine node, Object data) {
        return "String";
    }

    @Override
    public Object visit(ASTMjjlistexp node, Object data) {
        visitData visitData = (visitData) data;
        visitData.setCheckMode(CheckMode.PARAM);
        SimpleNode exp = (SimpleNode) node.jjtGetChild(0);
        String expLabel= (String) exp.jjtAccept(this, visitData);
        SimpleNode ListExp = (SimpleNode) node.jjtGetChild(1);
        String listExpString= (String) ListExp.jjtAccept(this, visitData);

        if(ListExp.toString().equals("exnil")) {
            return expLabel;
        }
        return expLabel +"@"+ listExpString;
    }

    @Override
    public Object visit(ASTMjjnon node, Object data) {
        SimpleNode exp = (SimpleNode) node.jjtGetChild(0);
        String typeLabel= (String) exp.jjtAccept(this, data);
        if(!typeLabel.equals("boolean")){
            errors.add(new InvalidOperationError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"The expression must be an Boolean"));
        };
        return "boolean";
    }

    @Override
    public Object visit(ASTMjjet node, Object data) {
        check2OpChild(node,data,"boolean",CheckMode.VALUE);
        return "boolean";
    }

    @Override
    public Object visit(ASTMjjou node, Object data) {
        check2OpChild(node,data,"boolean",CheckMode.VALUE);
        return "boolean";
    }

    @Override
    public Object visit(ASTMjjegal node, Object data) {
        check2OpChild(node,data,"int",CheckMode.VALUE);
        return "boolean";
    }

    @Override
    public Object visit(ASTMjjsup node, Object data) {
        check2OpChild(node,data,"int",CheckMode.VALUE);
        return "boolean";
    }

    @Override
    public Object visit(ASTMjjmoins node, Object data) {
        SimpleNode exp = (SimpleNode) node.jjtGetChild(0);
        String typeLabel= (String) exp.jjtAccept(this, data);
        if(!typeLabel.equals("int")){
            errors.add(new InvalidOperationError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"The expression must be an Integer"));
        };
        return "int";
    }

    private void check2OpChild(SimpleNode node,
                               Object data,
                               String type,
                               CheckMode check_mode){

        SimpleNode nodeLeft = (SimpleNode) node.jjtGetChild(0);
        SimpleNode nodeRight = (SimpleNode) node.jjtGetChild(1);

        visitData visitData = (visitData) data;
        visitData.setCheckMode(check_mode);

        String typeLabelLeft = (String) nodeLeft.jjtAccept(this, visitData);
        String typeLabelRight = (String) nodeRight.jjtAccept(this, visitData);

        if(!typeLabelLeft.equals(type) || !typeLabelRight.equals(type)){
            if(type.equals("int")){
                errors.add(new InvalidOperationError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"Left and right expressions must be an Integer"));
            }else{
                errors.add(new InvalidOperationError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"Left and right expressions must be a Boolean"));

            }
        }
    }

    @Override
    public Object visit(ASTMjjaddition node, Object data) {
        check2OpChild(node,data,"int",CheckMode.VALUE);
        return "int";
    }

    @Override
    public Object visit(ASTMjjsoustraction node, Object data) {
        check2OpChild(node,data,"int",CheckMode.VALUE);
        return "int";
    }

    @Override
    public Object visit(ASTMjjmult node, Object data) {
        check2OpChild(node,data,"int",CheckMode.VALUE);
        return "int";
    }

    @Override
    public Object visit(ASTMjjdiv node, Object data) {
        check2OpChild(node,data,"int",CheckMode.VALUE);
        return "int";
    }

    @Override
    public Object visit(ASTMjjappelE node, Object data) {
        String identMeth = (String) ((SimpleNode) node.jjtGetChild(0)).jjtGetValue();
        SimpleNode listExp = (SimpleNode) node.jjtGetChild(1);

        String listExpString = (String) listExp.jjtAccept(this, data);

        String methWithParam = "class@"+identMeth;
        if(listExpString!=null && !listExpString.isEmpty()){
            methWithParam+="@"+listExpString;
        }

        try {
            InfoIdent info = symbolTable.get(methWithParam,true);
            if(info.getType()==Type.VOID){
                errors.add(new InvalidCallError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,identMeth));
            }
            return MjType.getLabelFromType(info.getType());
        }catch(SymbolTableException.UnknownSymbolException e){
            errors.add(new CanNotFindSymbolError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,identMeth));
        }
        return null;
    }

    @Override
    public Object visit(ASTMjjtab node, Object data) {
        SimpleNode nodeIdent = (SimpleNode) node.jjtGetChild(0);
        visitData visitData = (visitData) data;
        visitData.setCheckMode(CheckMode.TAB);
        SimpleNode nodeExp  = (SimpleNode) node.jjtGetChild(1);
        String typeLabel= (String) nodeIdent.jjtAccept(this, data);
        visitData.setCheckMode(CheckMode.VALUE);
        String expLabel = (String) nodeExp.jjtAccept(this,data);

        if(expLabel!=null && !expLabel.equals("int")){
            errors.add(new TypeMismatchError(node.jjtGetFirstToken().beginLine,node.jjtGetFirstToken().beginColumn,"Index must be an Integer"));
        }

        if(typeLabel.equals("int")){
            return "int";
        }else{
            return "boolean";
        }
    }

    @Override
    public Object visit(ASTMjjlongueur node, Object data) {
        SimpleNode nodeIdent = (SimpleNode) node.jjtGetChild(0);
        visitData visitData = (visitData) data;
        visitData.setCheckMode(CheckMode.TAB);
        nodeIdent.jjtAccept(this, data);

        return "int";
    }

    @Override
    public Object visit(ASTMjjvrai node, Object data) {
        return "boolean";
    }

    @Override
    public Object visit(ASTMjjfaux node, Object data) {
        return "boolean";
    }

    @Override
    public Object visit(ASTMjjrien node, Object data) {
        return "void";
    }

    @Override
    public Object visit(ASTMjjentier node, Object data) {
        return "int";
    }

    @Override
    public Object visit(ASTMjjbool node, Object data) {
        return "boolean";
    }

    @Override
    public Object visit(ASTMjjnbre node, Object data) {
        return "int";
    }

    @Override
    public Object visit(ASTMjjvnil node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjenil node, Object data) {
        return "";
    }

    @Override
    public Object visit(ASTMjjinil node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTMjjexnil node, Object data) {
        return "";
    }

    @Override
    public Object visit(ASTMjjomega node, Object data) {
        return node.toString();
    }

    @Override
    public Object visit(SimpleNode simpleNode, Object o) {
        return null;
    }
}
