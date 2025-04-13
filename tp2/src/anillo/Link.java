package anillo;

public abstract class Link {
    protected Object data;
    protected Link next;
    protected Link previous;

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

class Empty extends Link{
    public Empty ( Object o ){
        super (o);
    }

    public Link getNext(){
        throw new RuntimeException("Can't get next");
    }

    public Object getData(){
        throw new RuntimeException("Can't get data");
    }

    public Link remove( Link n ){
        return null;
    }

    public Link add( Link current, Link newLink ){
        return newLink;
    }

}

class NonEmpty extends Link{
    public NonEmpty ( Object o ){
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