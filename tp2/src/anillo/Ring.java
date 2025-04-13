package anillo;
import java.util.Stack;

public class Ring {

    private Link current;
    private Stack<Link> stack;

    public Ring() {
        current = new Empty(null);
        stack = new Stack<>();
        stack.push(current);
    }

    public Ring next() {
        current = current.getNext();
        return this;
    }

    public Object current() {
        return current.getData();
    }

    public Ring add(Object cargo) {
        Link newLink = new NoEmpty(cargo);
        Link topLink = stack.peek();
        current = topLink.add(current, newLink);
        stack.push(current);
        return this;
    }

    public Ring remove() {
        stack.pop();
        Link topLink = stack.peek();
        current = topLink.remove( current );
        return this;
    }
    
}



