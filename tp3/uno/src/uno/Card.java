package uno;


public abstract class Card {
    private boolean oneCalled = false;
    // Efecto de la carta en el juego
    public abstract void action(Game game);
    // Métodos para doble dispatch de validación
    public abstract boolean matchesColor(Card other);
    public abstract boolean matchesNumber(Card other);
    public abstract boolean matchesType(Card other);
    // Validar si esta carta puede jugarse sobre lastCard
    public boolean isValid(Card lastCard) {
        return lastCard.matchesColor(this) ||
                lastCard.matchesNumber(this) ||
                lastCard.matchesType(this);
    }
    // Cantar UNO
    public void callOne() {
        oneCalled = true;
    }
    public boolean isOneCalled() {
        return oneCalled;
    }
}
