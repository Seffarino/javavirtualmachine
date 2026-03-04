package fr.ufrst.m1info.comp5.memory;

public class Memory {
    private final SymbolTable symbolTable;
    private final Tas tas;

    public Memory() throws TasException {
        tas = new Tas();
        symbolTable = new SymbolTable();
    }
    public Memory(int heapSize) throws TasException {
        tas = new Tas(heapSize);
        symbolTable = new SymbolTable();
    }
    public SymbolTable getSymbolTable(){
        return this.symbolTable;
    }

    public Tas getTas(){
        return this.tas;
    }

    public void declVar(String id, Object value, Type type) throws MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        InfoIdent info = new InfoIdent(id,type, Sorte.VAR);
        symbolTable.add(info);
        symbolTable.addInstance(id,value);
    }
    public void declCst(String id, Object value, Type type ) throws MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        InfoIdent info;
        if(value ==null || value.equals("w")  ){
            info = new InfoIdent(id,type, Sorte.VCST);
        } else {
            info = new InfoIdent(id,type,Sorte.CST);
        }
        symbolTable.add(info);
        //a value can be null.
        symbolTable.addInstance(id,value);
    }

    public void declMeth(String id,Object value, Type type) throws MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        InfoIdent info = new InfoIdent(id,type, Sorte.METH);
        symbolTable.add(info);
        symbolTable.addInstance(id,value);
    }

    public Object pop() throws MemoryException, SymbolTableException.UnknownSymbolException {
        InfoInstance top = null;
        if(symbolTable.getTopOfStack() != null){
            top = symbolTable.getTopOfStack();
            symbolTable.removeFromStack(symbolTable.getTopOfStack());
            if(top.getInfo()!=null){
                symbolTable.remove(top.getInfo());
            }
            return top.getValue();
        }
        throw new MemoryException("Empty Stack");
    }
    public void swap(){
        symbolTable.swap();
    }

    public void push(InfoInstance inst){
        if(inst != null){
            symbolTable.addToStack(inst);
        }

    }
    public void push(Object o){
        if(!(o instanceof InfoInstance) && o!=null){
            InfoInstance i = new InfoInstance(o);
            push(i);
        }
    }

    public void identVal(String id,Type type,int pos) throws MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.StackOverflowException {
        InfoIdent infoIdent = new InfoIdent(id,type,Sorte.VAR);
        symbolTable.add(infoIdent);
        infoIdent.addInstance(symbolTable.getNthInstance(pos));
    }
    public void affecterVal(String id,Object newValue) throws SymbolTableException.UnknownSymbolException, TasException {
        InfoIdent infoIdent = symbolTable.get(id,false);
        if(infoIdent.getSorte() == Sorte.VCST){
            InfoInstance infoInstance = infoIdent.getInstance();
            infoInstance.setValue(newValue);
            if(!newValue.equals("w")){
                infoIdent.setSorte(Sorte.CST);
            }
            return;
        }
        if(infoIdent.getSorte() == Sorte.CST){
            return;
        }
        if(infoIdent.getSorte() == Sorte.VAR){
            if(infoIdent.hasInstance()){
                infoIdent.getInstance().setValue(newValue);
            } else {
                InfoInstance infoInstance = new InfoInstance(newValue);
                infoIdent.addInstance(infoInstance);
                symbolTable.addToStack(infoInstance);
            }
            return;
        }
        if(infoIdent.getSorte() == Sorte.TAB){
            int newRef = (int) newValue;
            InfoInstance infoInstance = infoIdent.getInstance();
            int idTab = (int) infoInstance.getValue();
            tas.removeArray(idTab);
            InfoTab infoTab = tas.getArrayWithId(newRef);
            infoTab.incrementRef();
            infoInstance.setValue(newValue);
        }
    }
    public void declTab(String id,int taille, Type t) throws MemoryException, TasException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        InfoIdent infoIdent = new InfoIdent(id,t,Sorte.TAB);
        int idTab = tas.addBlock(taille);
        symbolTable.add(infoIdent);
        symbolTable.addInstance(id, idTab);
    }
    public void affecterValT(String id, int index, Object value) throws SymbolTableException.UnknownSymbolException, TasException {
        InfoIdent infoIdent = symbolTable.get(id,false);
        InfoInstance infoInstance = infoIdent.getInstance();
        int idTab = (int) infoInstance.getValue();
        tas.setValueOfArray(idTab,index,value);
    }
    public Object val(String id) throws SymbolTableException.UnknownSymbolException {
        InfoIdent infoIdent = symbolTable.get(id,false);
        if(infoIdent.hasInstance()){
            return infoIdent.getInstance().getValue();
        }
        return null;
    }
    public Object valT(String id, int index) throws SymbolTableException.UnknownSymbolException, TasException {
        InfoIdent infoIdent = symbolTable.get(id,false);
        if(infoIdent.hasInstance()){
            int idTab = (int) infoIdent.getInstance().getValue();
            return tas.getValueOfArray(idTab,index);
        }
        return null;
    }




}
