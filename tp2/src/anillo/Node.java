package anillo;

public abstract class Node {
    protected Object data;
    private Node next; //preguntar si es mejor hacer un método que sea setNext en lugar de que sea público
    private Node previous;

    public Node( Object o ){
        data = o;
        next = this;
        previous = this;
    }

    public void setNext( Node n){
        this.next = n.getNode(this);
    }

    public void setPrevious( Node p ){
        this.previous = p.getNode(this);
    }

    public Node getNext(){
        return next;
    }

    public Node getPrevious(){
        return previous;
    }

    public abstract Object getData();

    public abstract Node getNode( Node n);

    public abstract Node getNextWithError();

    public abstract Node remove( Node n );
}

class Empty extends Node{
    public Empty ( Object o ){
        super (o);

    }
    public Node getNode( Node n){
        return n;
    }
    public Node getNextWithError(){
        throw new RuntimeException("Can't get next");
    }
    public Object getData(){
        throw new RuntimeException("Can't get data");
    }
    public Node remove( Node n ){
        return null;
    }
}

class NoEmpty extends Node{
    public NoEmpty ( Object o ){
        super (o);
    }
    public Node getNode( Node n){
        return this;
    }
    public Node getNextWithError(){
        return getNext();
    }
    public Object getData(){
        return data;
    }
    public Node remove( Node n ){

        Node priorNode = n.getPrevious();
        Node nextNode = n.getNext();

        priorNode.setNext(nextNode);
        nextNode.setPrevious(priorNode);

        return nextNode;
    }

}