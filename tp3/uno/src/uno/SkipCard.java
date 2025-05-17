package uno;

public class SkipCard extends ColoredCard {
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
    public boolean matchesNumber(Card other) {
        return false;
    }
    @Override
    public boolean matchesType(Card other) {
        return (other instanceof SkipCard);
    }
}

