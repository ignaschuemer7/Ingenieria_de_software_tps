package uno;


public abstract class Card {
    private boolean oneCalled = false;
    protected String color;

    // Constructor
    public Card(String color) {
        this.color = color;
    }

    // Efecto de la carta en el juego
    public abstract void action(Game game);


    //throw error if getNumber() is called
    public int getNumber() { throw new UnsupportedOperationException("This card does not have a number."); }

    public String getColor() {
        return color;
    }


    public boolean matchesSymbol(Card other) { return this.getClass().getSimpleName().equals(other.getClass().getSimpleName()); }
    public boolean matchesNumber(Card other){ return false; }
    public boolean matchesColor(Card other) {
        return this.color.equals(other.color);
    }


//    public boolean matchesTypeDraw2Card(Card other) { return false; }
//    public boolean matchesTypeNumberedCard(Card other) { return false; }
//    public boolean matchesTypeSkipCard(Card other) { return false; }
//    public boolean matchesTypeReverseCard(Card other) { return false; }


    // Validar si esta carta puede jugarse sobre lastCard
    public abstract boolean canStackOn(Card card);

    // Cantar UNO
    public Card callOne() {
        oneCalled = true;
        return this;
    }

    public boolean isOneCalled() {
        return oneCalled;
    }

    public abstract boolean equals(Card c);

}