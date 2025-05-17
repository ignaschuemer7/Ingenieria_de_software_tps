package uno;

public class ReverseCard extends ColoredCard {
    public ReverseCard(String color) {
        super(color);
    }
    @Override
    public void action(Game game) {
        game.reverseDirection();
        game.nextTurn();
    }
    @Override
    public boolean matchesNumber(Card other) {
        return false;
    }
    @Override
    public boolean matchesType(Card other) {
        return (other instanceof ReverseCard);
    }
}
