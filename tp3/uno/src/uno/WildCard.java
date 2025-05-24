package uno;

public class WildCard extends Card {
    public WildCard() {
        super(null);
    }
    @Override
    public void action(Game game) {
        game.nextTurn();
    }

    private WildCard setColor(String color) {
        this.color = color;
        return this;
    }

    public boolean canStackOn(Card card){
        return true;
    }

    public WildCard beRed() {
        return setColor("Red");
    }
    public WildCard beGreen() {
        return setColor("Green");
    }
    public WildCard beBlue() {
        return setColor("Blue");
    }
    public WildCard beYellow() {
        return setColor("Yellow");
    }
}
