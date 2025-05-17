package uno;

public class Draw2Card extends ColoredCard {
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
    public boolean matchesNumber(Card other) {
        return false;
    }
    @Override
    public boolean matchesType(Card other) {
        return (other instanceof Draw2Card);
    }
}
