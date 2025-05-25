package uno;


public abstract class Card {
    private boolean oneCalled = false;
    protected String color;

    public Card(String color) { this.color = color; }

    public abstract boolean canStackOn(Card card);
    public abstract void action(Game game);

    public boolean matchesSymbol(Card other) {  return this.getClass().getSimpleName().equals(other.getClass().getSimpleName()); }
    public boolean matchesNumber(Card other) {  return false; }
    public boolean matchesColor(Card other) {   return this.color.equals(other.color); }

    public Card callOne() {
        oneCalled = true;
        return this;
    }

    public abstract boolean equals(Card c);

    public boolean isOneCalled() { return oneCalled; }
    public String getColor() {     return color; }
    public int getNumber() {       throw new UnsupportedOperationException(Game.NoNumberedCard); }
}