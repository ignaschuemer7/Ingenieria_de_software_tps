package uno;

public class WildCard extends Card {
    public WildCard() {
        super(null);
    }

    private WildCard setColor(String color) {
        this.color = color;
        return this;
    }

    public WildCard beRed() {
        return setColor("Red");
    }
    public WildCard beGreen() {
        return setColor("Green");
    }
    public WildCard beBlue() {
        return setColor("Blue");
    }
    public WildCard beYellow() {
        return setColor("Yellow");
    }

    @Override
    public boolean canStackOn(Card deckCard){
        return true;
    }

    @Override
    public void action(Game game) { game.nextTurn(); }

    @Override
    public boolean equals(Card c) {
        return c.matchesColor(this);
    }
}
