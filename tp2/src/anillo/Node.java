package anillo;

public class Node {
    private Object data;
    public Node next; //preguntar si es mejor hacer un método que sea setNext en lugar de que sea público

    public Node( Object o ){
        data = o;
        next = this;
    }

    public Node getNext(){
        return next;
    }

    public Object getData() {
        return data;
    }
}
