package anillo;
import java.util.Stack;

public class Ring {

    private Node current;
    private int ringLength;
    private Stack<Node> stack;

    public Ring() {
        current = new Empty(null);
        stack = new Stack<>();
        stack.push(current);
    }

    public Ring next() {
        current = current.getNextWithError();
        return this;
    }

    public Object current() {
        return current.getData();
    }

    public Ring add(Object cargo) {
        Node newNode = new NoEmpty(cargo);

        newNode.setNext(this.current);

        Node priorNode = current.getPrevious();
        newNode.setPrevious(priorNode);
        priorNode.setNext(newNode);
        current.setPrevious(newNode);
        current = newNode;
        stack.push(current);
        return this;
    }


    public Ring remove() {
        this.stack.pop();
        Node topNode = this.stack.peek();
        current = topNode.remove( current );
        return this;
    }
    
}



