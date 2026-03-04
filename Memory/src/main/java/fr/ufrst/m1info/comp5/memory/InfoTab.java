package fr.ufrst.m1info.comp5.memory;

public class InfoTab {
    private int id;
    private int adr;
    private int taille;
    private int ref;
    public InfoTab(int id,int adr,int taille,int ref){
       this.id = id;
       this.taille = taille;
       this.adr = adr;
       this.ref = ref;
    }
    public int getTaille(){
        return taille;
    }
    public int getAdr(){
        return adr;
    }
    public void setAdr(int adr){
        this.adr = adr;
    }
    public int getRef(){
        return ref;
    }
    public void setRef(int ref){
        this.ref = ref;
    }
    public void incrementRef(){ this.ref++;}
    public void decrementRef(){
        this.ref--;
    }
    public int getId(){ return id;}
}
