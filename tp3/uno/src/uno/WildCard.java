package uno;

public class WildCard extends UncoloredCard {
    @Override
    public void action(Game game) {
        // Al jugar un comodín, se asigna color en Game y avanza turno
        game.nextTurn();
    }
    // Hereda teGusta... de UncoloredCard
}
