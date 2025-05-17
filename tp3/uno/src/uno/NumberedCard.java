package uno;

public class NumberedCard extends ColoredCard {
    private int number;
    public NumberedCard(String color, int number) {
        super(color);
        this.number = number;
    }
    public int getNumber() {
        return number;
    }
    @Override
    public void action(Game game) {
        // Carta normal: simplemente avanza turno
        game.nextTurn();
    }
    @Override
    public boolean matchesNumber(Card other) {
        if (other instanceof NumberedCard) {
            return ((NumberedCard) other).getNumber() == this.number;
        }
        return false;
    }
    @Override
    public boolean matchesType(Card other) {
        // Los números sólo coinciden por color o número, no por tipo
        return false;
    }
}
