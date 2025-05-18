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
    public boolean matchesType(Card other) {
        return other.matchesTypeSkipCard(this);
    }

    @Override
    public boolean matchesTypeSkipCard(Card other) {
        return true;
    }
}

