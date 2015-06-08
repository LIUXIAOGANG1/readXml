package net.sourceforge.powerswing.util.multireturn;


public class Triple <E, F, G> {

    private E e;
    private F f;
    private G g;
    
    public Triple (E theFirst, F theSecond, G theThird){
        this.e = theFirst;
        this.f = theSecond;
        this.g = theThird;
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
}
