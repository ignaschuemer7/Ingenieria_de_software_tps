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
            newNode.setNext( this.current );
            Node priorNode = current;
            for (int i = 0; i < ringLength - 1; i++){
                priorNode = priorNode.getNext();
            }
            priorNode.setNext( newNode );
        }
        current = newNode;
        ringLength ++;
        return this;
    }

    public Ring remove() {
        if (ringLength == 1){
            current = null;
        } else {

            Node priorNode = current;
            for (int i=0; i<ringLength - 1; i++){
                priorNode = priorNode.getNext();
            }

            priorNode.setNext(current.getNext());
            current = current.getNext();
        }

        ringLength--;
        return this;
    }
}
