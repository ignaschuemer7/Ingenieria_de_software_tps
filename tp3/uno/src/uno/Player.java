package uno;
import java.util.*;

public class Player {
    private String name;
    private List<Card> hand = new ArrayList<>();
    private Player leftPlayer;
    private Player rightPlayer;

    public Player(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    // Agregar carta al jugador (por efecto o configuración)
    public void addCard(Card card) {
        hand.add(card);
    }

    // Quitar carta de la mano (por jugarla)
    public void removeCard(Card card) {
        hand.stream()
                .filter(c -> c.equals(card))
                .findFirst()
                .map(c -> { hand.remove(c); return c; })
                .orElseThrow(() -> new IllegalArgumentException(Game.PlayerHasNotCard));
    }

    // Comprobar si la mano contiene la carta
//    public boolean hasCard(Card card) {
//        return hand.contains(card);
//    }
    public boolean hasCard(Card card) {
        for (Card c : hand) {
            if (c.equals(card)) {
                return true;
            }
        }
        return false;
    }

    public int getHandSize() {
        return hand.size();
    }

    public List<Card> getHand() {
        return hand;
    }

    public Player getLeftPlayer() {
        return leftPlayer;
    }

    public void setLeftPlayer(Player leftPlayer) {
        this.leftPlayer = leftPlayer;
    }

    public Player getRightPlayer() {
        return rightPlayer;
    }

    public void setRightPlayer(Player rightPlayer) {
        this.rightPlayer = rightPlayer;
    }
}
