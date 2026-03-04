package fr.ufrst.m1info.comp5.memory;

import java.util.List;

public class SymbolTable {
    private static final int TABLE_SIZE = 199;
    private final InfoIdent[] table;
    private InfoInstance topOfStack;

    public SymbolTable() {
        this.table = new InfoIdent[TABLE_SIZE];
        topOfStack = null;

    }
    public InfoInstance getTopOfStack(){
        return topOfStack;
    }
    public void add(InfoIdent q) throws SymbolTableException.ExistingSymbolException {
        int hashcode = hash(q.getID());
        if(table[hashcode]==null){
            table[hashcode]= q ;
        } else {
            for(InfoIdent currId : table[hashcode].getListOfNext()){
                if(currId.equals(q)){
                    throw new SymbolTableException.ExistingSymbolException();
                }
            }
            table[hashcode].returnLast().setNext(q);

        }
    }
    public boolean remove(InfoIdent q) throws SymbolTableException.UnknownSymbolException{
        List<InfoInstance> myList;
        int hashcode = hash(q.getID());
        if(table[hashcode]==null){
            throw new SymbolTableException.UnknownSymbolException();
        }
        //Remove all instance from stack
        myList = q.getListOfInstance();
        for(InfoInstance i : myList){
            removeFromStack(i);
        }
        //q is the first element
        if(table[hashcode].equals(q)){
            if(q.hasNext()){
                table[hashcode] = q.getNext();
            } else {
                table[hashcode] = null;
            }

            return true;
        }
        InfoIdent currId = table[hashcode];
        while(currId.hasNext()){
            InfoIdent next = currId.getNext();
            if(next.equals(q)){
                if(next.hasNext()){
                    currId.setNext(next.getNext());
                } else {
                    currId.setNext(null);
                }
                return true;
            }
            currId = next;
        }
        throw new SymbolTableException.UnknownSymbolException();

    }
    public InfoIdent get(InfoIdent q) throws SymbolTableException.UnknownSymbolException{
        int hashcode = hash(q.getID());
        InfoIdent headId = table[hashcode];
        if(headId==null){
            throw new SymbolTableException.UnknownSymbolException();
        }
        for(InfoIdent currId : headId.getListOfNext()){
            if(currId.equals(q) && currId.getType() == q.getType() && currId.getSorte() == q.getSorte()){

                return currId;
            }
        }
        throw new SymbolTableException.UnknownSymbolException();
    }
    public InfoIdent get(String id, boolean isMethod) throws SymbolTableException.UnknownSymbolException {
        int hashcode = hash(id);
        InfoIdent headId = table[hashcode];
        if(headId==null){
            throw new SymbolTableException.UnknownSymbolException();
        }
        for(InfoIdent currId : headId.getListOfNext()){
            if(currId.getID().equals(id) && ((isMethod && currId.getSorte() == Sorte.METH) || (!isMethod && currId.getSorte() != Sorte.METH))){
                    return currId ;

            }
        }
        throw new SymbolTableException.UnknownSymbolException();
    }
    public void addInstance(String id, Object value) throws SymbolTableException.UnknownSymbolException {
        int hashcode = hash(id);
        InfoIdent headId = table[hashcode];
        if(headId==null){
            throw new SymbolTableException.UnknownSymbolException();
        }
        for(InfoIdent currId : headId.getListOfNext()){
            if(currId.getID().equals(id)){
                if(currId.hasInstance()){
                    currId.handleExistingInstance(value);
                } else {
                    currId.handleNoInstance(value);
                }
                addToStack(currId.getInstance());
                return;
            }
        }
        throw new SymbolTableException.UnknownSymbolException();

    }
    public void addToStack(InfoInstance inst){
        if(topOfStack != null){
            inst.setSuivantPile(topOfStack);
            topOfStack.setPrecedantPile(inst);
            topOfStack.setIsTopFalse();
        }
        topOfStack = inst;
    }

    public void removeFromStack(InfoInstance inst){
        if(inst==null){
            return;
        }

        InfoInstance suivant = inst.getSuivantPile();
        InfoInstance precedant = inst.getPrecedantPile();
        if(suivant == null && precedant == null){
            topOfStack = null;
            if(inst.getInfo() != null) {
                inst.linkBeforeAndAfter();
            }
            return;
        }

        if(topOfStack==inst){
            topOfStack =suivant;
            if(suivant !=null){
                suivant.setPrecedantToNull();
            }
        } else {
            if(suivant == null){
                precedant.setSuivantToNull();

            } else {
                suivant.setPrecedantPile(precedant);
                precedant.setSuivantPile(suivant);

            }
        }
        if(inst.getInfo() != null) {
            inst.linkBeforeAndAfter();
        }
        inst.setPrecedantToNull();
        inst.setSuivantToNull();
    }
    public void removeAllInstanceFromStack(InfoIdent id){
        for(InfoInstance inst : id.getListOfInstance()){
            removeFromStack(inst);
        }
        id.addInstance(null);
    }
    public int hash(String id){
        int hashcode = 0;
        for(int i =0; i<id.length() ; i++){
            hashcode = (hashcode * TABLE_SIZE) + id.charAt(i);
        }
        return Math.abs(hashcode) % TABLE_SIZE;
    }
    public void swap(){
        if(topOfStack != null && topOfStack.getSuivantPile()!= null){
            InfoInstance a = topOfStack;
            InfoInstance b = topOfStack.getSuivantPile();
            InfoInstance c = b.getSuivantPile();
            if(c!=null){
                c.setPrecedantPile(a);
                a.setSuivantPile(c);
            }
            a.setPrecedantPile(b);
            a.setSuivantPile(c);
            b.setPrecedantPile(null);
            b.setSuivantPile(a);
            topOfStack = b;

        }
    }
    public InfoInstance getNthInstance(int pos) throws SymbolTableException.StackOverflowException {
        if(topOfStack == null){
            throw new SymbolTableException.StackOverflowException();
        }
        if(pos == 0){
            return topOfStack;
        }
        int counter = 0  ;
        InfoInstance currentInstance = topOfStack;
        while(pos >= counter){
            if(pos == counter){
                return currentInstance;
            }
            if(currentInstance.getSuivantPile() == null){
                throw new SymbolTableException.StackOverflowException();
            }
            currentInstance = currentInstance.getSuivantPile();
            counter++;
        }
        throw new SymbolTableException.StackOverflowException();
    }

    /**
     * ToString function for the stack
     * @return the stack in string
     */
    @Override
    public String toString(){
        StringBuilder res = new StringBuilder();
        InfoInstance currInstance = topOfStack;
        while(currInstance != null){
            res.append("<");
            if(currInstance.hasInfo()){
                res.append(currInstance.getInfo().getID());
                res.append(",");
                res.append(currInstance.getValue());
                res.append(",");
                res.append(currInstance.getInfo().getSorte());
                res.append(",");
                res.append(currInstance.getInfo().getType());

            } else {
                res.append("?,");
                res.append(currInstance.getValue());
                res.append(",?,?");
            }
            if(currInstance.getSuivantPile() == null){
                res.append(">.[]");
                return res.toString();
            }
            res.append(">.\n");
            currInstance = currInstance.getSuivantPile();
        }
        return res.toString();
    }
}
