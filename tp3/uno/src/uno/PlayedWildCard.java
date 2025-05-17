package uno;

public class PlayedWildCard extends ColoredCard {
    public PlayedWildCard(String color) {
        super(color);
    }
    @Override
    public void action(Game game) {
        game.nextTurn();
    }
    @Override
    public boolean matchesNumber(Card other) {
        return false;
    }
    @Override
    public boolean matchesType(Card other) {
        // Comodín coincide solo con otro comodín
        return (other instanceof WildCard);
    }
}
