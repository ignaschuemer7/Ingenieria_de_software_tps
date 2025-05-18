package uno;

public class ReverseCard extends Card {
    public ReverseCard(String color) {
        super(color);
    }
    @Override
    public void action(Game game) {
        game.reverseDirection();
        game.nextTurn();
    }

    @Override
    public boolean matchesType(Card other) {
        return other.matchesTypeReverseCard(this);
    }

    @Override
    public boolean matchesTypeReverseCard(Card other) {
        return true;
    }
}
