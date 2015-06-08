package net.sourceforge.powerswing.util.multireturn;


public class Quadruple <E, F, G, H> {

    private E e;
    private F f;
    private G g;
    private H h;
    
    public Quadruple (E theFirst, F theSecond, G theThird, H theFourth){
        this.e = theFirst;
        this.f = theSecond;
        this.g = theThird;
        this.h = theFourth;
    }
    
    public E getFirst(){
        return e;
    }
    
    public F getSecond(){
        return f;
    }
    
    public G getThird(){
        return g;
    }
    
    public H getFourth(){
        return h;
    }
}
