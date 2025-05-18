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

    public boolean matchesNumber(Card other){ return false; }

    //throw error if getNumber() is called
    public int getNumber() { throw new UnsupportedOperationException("This card does not have a number."); }


    public boolean matchesType(Card other) {  return false; }

    public boolean matchesTypeDraw2Card(Card other) { return false; }
    public boolean matchesTypeNumberedCard(Card other) { return false; }
    public boolean matchesTypeSkipCard(Card other) { return false; }
    public boolean matchesTypeReverseCard(Card other) { return false; }

    public boolean matchesColor(Card other) {
        return this.color.equals(other.color);
    }

    // Validar si esta carta puede jugarse sobre lastCard
    public boolean canStackOn(Card card) {
        return card.matchesColor(this) ||
                card.matchesNumber(this) ||
                card.matchesType(this);
    }

    // Cantar UNO
    public Card callOne() {
        oneCalled = true;
        return this;
    }

    public boolean isOneCalled() {
        return oneCalled;
    }

}