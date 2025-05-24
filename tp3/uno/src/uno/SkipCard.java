package uno;

public class SkipCard extends Card {
    public SkipCard(String color) {
        super(color);
    }
    @Override
    public void action(Game game) {
        // Saltar al siguiente jugador
        game.nextTurn();
        game.nextTurn();
    }

    @Override
    public boolean canStackOn(Card deckCard) {
        return deckCard.matchesSymbol(this) || deckCard.matchesColor(this);
    }

    public boolean equals(Card c) {
        return c.matchesSymbol(this) && c.matchesColor(this);
    }
}

