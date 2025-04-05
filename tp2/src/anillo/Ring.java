package anillo;

public class Ring {

    private Node current;
    private int ringLength;

    public Ring next() {
        current = current.getNext();
        return this;
    }

    public Object current() {
        return current.getData();
    }

    public Ring add( Object cargo ) {
        Node newNode = new Node( cargo );
        if (current != null){
            Node priorNode = current.getPrevious();

            newNode.setNext( this.current );
            newNode.setPrevious(priorNode);

            priorNode.setNext(newNode);
            current.setPrevious(newNode);
        }
        current = newNode;
        ringLength ++;
        return this;
    }

    public Ring remove() {
        if (ringLength == 1){
            current = null;
        } else {
            Node priorNode = current.getPrevious();
            Node nextNode = current.getNext();

            priorNode.setNext(nextNode);
            nextNode.setPrevious(priorNode);

            current = nextNode;
        }

        ringLength--;
        return this;
    }
}
