package net.sourceforge.powerswing.util.multireturn;


public class Quintuple <E, F, G, H, I> {

    private E e;
    private F f;
    private G g;
    private H h;
    private I i;
    
    public Quintuple (E theFirst, F theSecond, G theThird, H theFourth, I theFifth){
        this.e = theFirst;
        this.f = theSecond;
        this.g = theThird;
        this.h = theFourth;
        this.i = theFifth;
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
    
    public I getFifth(){
        return i;
    }
}
