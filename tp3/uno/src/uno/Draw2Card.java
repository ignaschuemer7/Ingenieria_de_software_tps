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
    public boolean matchesType(Card other) {
        return other.matchesTypeDraw2Card(this);
    }
    @Override
    public boolean matchesTypeDraw2Card(Card other) { return true; }

}
