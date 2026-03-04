package fr.ufrst.m1info.comp5.memory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InfoIdent {

    private String id;
    private Type type;
    private Sorte sorte;
    private InfoIdent next = null;
    private InfoInstance instance = null;

    public InfoIdent(String s, Type t, Sorte sor) throws MemoryException {
        if (s.isEmpty()) {
            throw new MemoryException("Id cant be empty");
        }
        id=s;
        type = t;
        sorte = sor;
    }


    public void addInstance (InfoInstance i){
        if(i==null){
            this.instance = null;
            return;
        }
        i.setInfo(this);
        if (this.instance != null) {
            i.addPrevious(this.instance);
        }
        this.instance = i;

    }
    public void handleExistingInstance(Object value){
        if(!hasInstance()){
            handleNoInstance(value);
        }
        InfoInstance inst;
        if(value instanceof InfoInstance){
            inst = (InfoInstance) value;
            inst.addPrevious(this.getInstance());
        } else {
            inst = new InfoInstance(value,this.getInstance());
        }
        addInstance(inst);
    }
    public void handleNoInstance(Object value){
        if(this.hasInstance()){
            handleExistingInstance(value);
        }
        InfoInstance inst;
        if(value instanceof InfoInstance){
            inst = (InfoInstance) value;
        } else {
            inst = new InfoInstance(value);
        }
        addInstance(inst);
    }
    public String getID(){
        return id;
    }
    public Type getType(){
        return type;
    }
    public Sorte getSorte(){
        return sorte;
    }
    public void setId(String s){
        id = s;
    }
    public void setType(Type t){
        type = t;
    }
    public void setSorte(Sorte s){
        sorte = s;
    }
    public void setInstance(InfoInstance a){
        instance = a;
    }
    public void setNext(InfoIdent n){
        next = n;
    }
    public InfoIdent getNext(){
        return next;
    }
    public boolean hasInstance(){
        return instance!=null;
    }
    public InfoInstance getInstance(){
        return instance;
    }
    public boolean hasNext(){
        return next != null;
    }


    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }
        if( !(o instanceof InfoIdent other)){
            return false;
        }
        // same id, same type, same sorte
        if(this.id.equals(other.id) && this.type == other.type  && this.sorte == other.sorte){
          return true;
        }
        //same id but one is a method
        if(this.id.equals(other.id) && (this.sorte == Sorte.METH || other.sorte == Sorte.METH) ){
            return false;
        }
        return this.id.equals(other.id);

    }
    @Override
    public int hashCode(){
        return super.hashCode();
    }
    public InfoIdent returnLast(){
        if(next==null){
            return this;
        }
        return next.returnLast();
    }
    public List<InfoIdent> getListOfNext(){
        LinkedList<InfoIdent> myList = new LinkedList<>();
        myList.add(this);
        InfoIdent currId = this;
        while(currId.hasNext()){
            myList.add(currId.getNext());
            currId = currId.getNext();
        }
        return myList;

    }
    public List<InfoInstance> getListOfInstance(){
        ArrayList<InfoInstance> myList = new ArrayList<>();
        InfoInstance inst = instance;
        while(inst!=null){
            myList.add(inst);
            inst = inst.getValeurPrecedente();
        }
        return myList;
    }



}
