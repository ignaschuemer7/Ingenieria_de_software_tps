package uno;

public class NumberedCard extends Card {
    private int number;

    public NumberedCard(String color, int number) {
        super(color);
        this.number = number;
    }

    @Override
    public boolean canStackOn(Card deckCard) { return deckCard.matchesNumber(this) || deckCard.matchesColor(this); }

    @Override
    public void action(Game game) { game.nextTurn(); }

    @Override
    public boolean matchesNumber(Card card) { return card.getNumber() == this.number; }

    @Override
    public boolean equals(Card c) { return c.matchesNumber(this) && c.matchesColor(this); }

    @Override
    public int getNumber() { return number; }
}
