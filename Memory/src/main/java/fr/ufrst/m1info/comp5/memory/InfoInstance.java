package fr.ufrst.m1info.comp5.memory;

public class InfoInstance {
    private Object value;
    private InfoInstance suivantPile;
    private InfoInstance precedantPile;
    private InfoInstance valeurPrecedente;
    private InfoIdent info;
    private boolean isTop = true;
    public InfoInstance(Object o){
        this.value = o;
        this.info = null;
        suivantPile = null;
        valeurPrecedente= null;
        precedantPile = null;

    }

    public InfoInstance(Object o, InfoInstance p) {
        this(o);
        this.suivantPile = p;
    }
    public void setIsTopFalse(){
        isTop = false;
    }
    public void setSuivantPile(InfoInstance i){
        suivantPile = i;
    }
    public void setPrecedantPile(InfoInstance i){
        precedantPile = i;
    }
    public void setInfo(InfoIdent i){
        this.info = i;
    }
    public InfoInstance getSuivantPile(){
        return suivantPile;
    }
    public InfoInstance getPrecedantPile(){ return precedantPile;}
    public InfoInstance getValeurPrecedente(){
        return valeurPrecedente;
    }
    public InfoIdent getInfo(){ return info;}
    public void setValeurPrecedente(InfoInstance a){
        if(a==null){
            this.valeurPrecedente = null;
        }
        this.valeurPrecedente = a;
    }
    public boolean isTopStack() { return isTop; }
    public boolean hasInfo(){
        return info != null;
    }
    public void setSuivantToNull(){
        suivantPile = null;
    }
    public void setPrecedantToNull(){
        precedantPile = null;
    }
    public void addPrevious(InfoInstance i) { valeurPrecedente = i ; }
    public void setValue(Object newValue){ this.value = newValue;}
    public Object getValue(){
        return this.value;
    }

    public boolean linkBeforeAndAfter(){
        InfoInstance firstInst = info.getInstance();
        if(firstInst==this){
           info.setInstance(firstInst.getValeurPrecedente());
           return true;
        }
        InfoInstance curr = firstInst;
        while(curr!=null){
            if(curr.getValeurPrecedente()!=null && curr.getValeurPrecedente()==this){
                InfoInstance i = curr.getValeurPrecedente();
                curr.setValeurPrecedente(i.getValeurPrecedente());
                return true;
            }
            curr = curr.getValeurPrecedente();

        }
        return false;

    }


}
