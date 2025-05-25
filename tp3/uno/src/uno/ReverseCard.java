package uno;

public class ReverseCard extends Card {
    public ReverseCard(String color) {
        super(color);
    }

    @Override
    public boolean canStackOn(Card deckCard) { return deckCard.matchesSymbol(this) || deckCard.matchesColor(this); }

    @Override
    public void action(Game game) {
        game.reverseDirection();
        game.nextTurn();
    }

    @Override
    public boolean equals(Card c) {
        return c.matchesSymbol(this) && c.matchesColor(this);
    }
}
