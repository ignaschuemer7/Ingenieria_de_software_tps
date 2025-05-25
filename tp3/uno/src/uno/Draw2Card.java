package uno;

public class Draw2Card extends Card {
    public Draw2Card(String color) {
        super(color);
    }

    @Override
    public boolean canStackOn(Card deckCard) {
        return deckCard.matchesSymbol(this) || deckCard.matchesColor(this);
    }

    @Override
    public void action(Game game) { game.nextPlayerDraw2(); }

    @Override
    public boolean equals(Card c) {
        return c.matchesSymbol(this) && c.matchesColor(this);
    }
}
