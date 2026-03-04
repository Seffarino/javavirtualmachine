package fr.ufrst.m1info.comp5.memory;

import java.util.ArrayList;
import java.util.List;

public class Pile {
    private final List<InfoIdent> pile;
    private int topOfStackIndex;
    public Pile(){
        this.pile = new ArrayList<>();
        topOfStackIndex = -1;
    }

    public Pile(List<InfoIdent> pile) {
        this.pile = pile;
        topOfStackIndex = -1;
    }
    public List<InfoIdent> getPile(){
        return pile;
    }
    public boolean isEmpty(){
        return pile.isEmpty();
    }
    public void addQuad(InfoIdent q){
        pile.add(q);
        topOfStackIndex += 1;

    }
    public InfoIdent removeTop(){
        if(this.isEmpty()){
            //Ajouter exception
            return null;
        }
        InfoIdent q = pile.remove(topOfStackIndex);
        topOfStackIndex -= 1;
        return q;
    }
    public InfoIdent getTop(){
        if(topOfStackIndex < 1){
            return null;
        }
        return pile.get(topOfStackIndex);
    }

    /**
     * Inverse le sommet et le sous sommet
     */
    public void swap(){
        if(topOfStackIndex < 1){
            return;
        }
        InfoIdent top = removeTop();
        InfoIdent subtop = removeTop();
        addQuad(top);
        addQuad(subtop);
    }

}
