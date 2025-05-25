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

    public Card removeCard(Card card) {
        Card found = hand.stream()
                .filter(c -> c.equals(card))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(Game.PlayerHasNotCard));

        hand.remove(found);
        return found;
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
