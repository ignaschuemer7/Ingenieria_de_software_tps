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

    public void addCard(Card card) {
        hand.add(card);
    }

    public void removeCard(Card card) {
        hand.stream()
                .filter(c -> c.equals(card))
                .findFirst()
                .map(c -> { hand.remove(c); return c; })
                .orElseThrow(() -> new IllegalArgumentException(Game.PlayerHasNotCard));
    }

    public void setRightPlayer(Player rightPlayer) { this.rightPlayer = rightPlayer; }

    public void setLeftPlayer(Player leftPlayer) { this.leftPlayer = leftPlayer; }

    public Player getLeftPlayer() { return leftPlayer; }

    public Player getRightPlayer() { return rightPlayer; }

    public boolean hasCard(Card card) { return hand.stream().anyMatch(c -> c.equals(card)); }

    public String getName() { return name; }

    public int getHandSize() { return hand.size(); }

    public List<Card> getHand() { return hand; }

}
