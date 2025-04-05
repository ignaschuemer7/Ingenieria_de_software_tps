package anillo;

public class Node {
    private Object data;
    private Node next; //preguntar si es mejor hacer un método que sea setNext en lugar de que sea público
    private Node previous;

    public Node( Object o ){
        data = o;
        next = this;
        previous = this;
    }

    public void setNext( Node n){
        this.next = n;
    }

    public void setPrevious( Node p ){
        this.previous = p;
    }

    public Node getNext(){
        return next;
    }

    public Node getPrevious(){
        return previous;
    }

    public Object getData() {
        return data;
    }
}
