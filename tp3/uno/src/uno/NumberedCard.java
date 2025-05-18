package uno;

public class NumberedCard extends Card {
    private int number;
    public NumberedCard(String color, int number) {
        super(color);
        this.number = number;
    }
    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public void action(Game game) {
        game.nextTurn();
    }

    @Override
    public boolean matchesNumber(Card other) {
        return other.matchesTypeNumberedCard(this) &&
                other.getNumber() == this.number;
    }

    @Override
    public boolean matchesTypeNumberedCard(Card other) {
        return true;
    }

}
