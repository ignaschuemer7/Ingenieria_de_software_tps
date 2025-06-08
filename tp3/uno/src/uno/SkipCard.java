package uno;

public class SkipCard extends Card {
    public SkipCard(String color) {
        super(color);
    }

    @Override
    public boolean canStackOn(Card deckCard) {
        return deckCard.matchesSymbol(this) || deckCard.matchesColor(this);
    }

    @Override
    public void action(Game game) {
        game.nextTurn();
        game.nextTurn();
    }

    @Override
    public boolean equals(Card c) {
        return c.matchesSymbol(this) && c.matchesColor(this);
    }
}

