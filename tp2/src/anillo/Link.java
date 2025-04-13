package anillo;

public abstract class Link {
    protected Object data;
    protected Link next;
    protected Link previous;

    public static final String CanNotGetNext = "Can't get next, empty ring";
    public static final String CanNotGetData = "Can't get data, empty ring";

    public Link( Object o ){
        data = o;
        next = this;
        previous = this;
    }

    public abstract Object getData();

    public abstract Link getNext();

    public abstract Link remove( Link n );

    public abstract Link add( Link current, Link newLink );

}

class EmptyLink extends Link{
    
    public EmptyLink (Object o ){
        super (o);
    }

    public Link getNext(){
        throw new RuntimeException(CanNotGetNext);
    }

    public Object getData(){
        throw new RuntimeException(CanNotGetData);
    }

    public Link remove( Link n ){
        return null;
    }

    public Link add( Link current, Link newLink ){
        return newLink;
    }

}

class NonEmptyLink extends Link{
    public NonEmptyLink ( Object o ){
        super (o);
    }

    public Link getNext(){
        return next;
    }

    public Object getData(){
        return data;
    }

    public Link remove( Link n ){
        n.previous.next = n.next;
        n.next.previous = n.previous;
        return n.next;
    }

    public Link add( Link current, Link newLink ){
        newLink.next = current;
        newLink.previous = current.previous;
        current.previous.next = newLink;
        current.previous = newLink;
        return newLink;
    }

}