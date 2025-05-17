package uno;

public abstract class ColoredCard extends Card {
    private String color;
    public ColoredCard(String color) {
        this.color = color;
    }
    public String getColor() {
        return color;
    }
    @Override
    public boolean matchesColor(Card other) {
        if (other instanceof ColoredCard) {
            return ((ColoredCard) other).getColor() == this.color;
        }
        return false;
    }
}
