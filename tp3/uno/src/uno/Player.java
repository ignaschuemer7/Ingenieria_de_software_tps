package uno;
import java.util.*;

public class Player {
    private String name;
    private List<Card> hand = new ArrayList<>();

    public Player(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    // Levantar carta del mazo del juego
    public void drawCard(Game game) {
        Card c = game.drawCard();
        hand.add(c);
    }
    // Agregar carta al jugador (por efecto o configuración)
    public void addCard(Card card) {
        hand.add(card);
    }
    // Quitar carta de la mano (por jugarla)
    public void removeCard(Card card) {
        if (!hand.remove(card)) {
            throw new IllegalArgumentException("La carta no está en la mano del jugador.");
        }
    }
    // Comprobar si la mano contiene la carta
    public boolean hasCard(Card card) {
        return hand.contains(card);
    }

    public int getHandSize() {
        return hand.size();
    }

    public List<Card> getHand() {
        return hand;
    }
}
