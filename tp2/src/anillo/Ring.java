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

    public Ring add( Object cargo ) {
        Link newLink = new NonEmpty(cargo);
        Link topLink = stack.peek(); // Allow to indentify how to add the new link
        current = topLink.add(current, newLink);
        stack.push(current);
        return this;
    }

    public Ring remove() {
        stack.pop();
        Link topLink = stack.peek(); // Allow to indentify how to remove the current link
        current = topLink.remove( current );
        return this;
    }
    
}