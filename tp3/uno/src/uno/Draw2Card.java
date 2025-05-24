package uno;

public class Draw2Card extends Card {
    public Draw2Card(String color) {
        super(color);
    }
    @Override
    public void action(Game game) {
        // El siguiente jugador roba 2 cartas y pierde el turno
        game.nextTurn();
        Player next = game.getCurrentPlayer();
        next.addCard(game.drawCard());
        next.addCard(game.drawCard());
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
