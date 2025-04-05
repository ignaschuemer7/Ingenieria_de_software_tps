package anillo;

public class Ring {

    private Node current;
    private int ringLength;

    public Ring(){
        current = new Empty(null);
        ringLength = 0;
    }

    public Ring next() {
        current = current.getNextWithError();
        return this;
    }

    public Object current() {
        return current.getData();
    }

    public Ring add( Object cargo ) {
        Node newNode = new NoEmpty( cargo );

        newNode.setNext( this.current );

        Node priorNode = current.getPrevious();
        newNode.setPrevious(priorNode);

        // esto no nos cambia en el caso de que el anillo este vacío
        // ya que le estamos seteando el siguiente al nodo empty que ya había
        priorNode.setNext(newNode);
        // esto no nos cambia en el caso de que el anillo este vacío
        // ya que le estamos seteando el previo al nodo empty que ya había
        current.setPrevious(newNode);

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
