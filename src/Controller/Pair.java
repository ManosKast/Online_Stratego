package Controller;

public class Pair <E> {
    private E first;
    private E second;

    public Pair(){
        this.first = null;
        this.second = null;
    }
    public Pair(E first, E second){
        this.first = first;
        this.second = second;
    }

    public E getFirst(){return first;}
    public E getSecond(){return second;}

    public void setFirst(E first){this.first = first;}
    public void setSecond(E second){this.second = second;}

    public String toString(){
        return "(" + first + ", " + second + ")";
    }
}
