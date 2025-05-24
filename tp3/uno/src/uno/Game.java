package uno;

import java.util.*;

public class Game {
    private Player currentPlayer;
    private Card currentCard;
    private final Deque<Card> deck;
    private boolean finished = false;
    private Player winner;
    private Controller controller = new RightController(); // Ronda inicial para la derecha

    public static final String GameIsOver = "The game is already over.";
    public static final String DoesNotHaveCard = "The player does not have that card.";
    public static final String NoTurn = "It is not the player's turn.";

    public Game(Card[] deckArray, String[] names, int handSize) {
        this.deck = new ArrayDeque<>(Arrays.asList(deckArray));

        this.currentCard = deck.removeFirst();

        ArrayList <Player> players = new ArrayList<>();
        for (String name : names) {
            Player p = new Player(name);
            for (int i = 0; i < handSize; i++) {
                p.addCard(drawCard());
            }
            players.add(p);
        }

        // Conectar jugadores en círculo
        int numPlayers = players.size();
        for (int i = 0; i < numPlayers; i++) {
            Player current = players.get(i);
            Player left = players.get((i - 1 + numPlayers) % numPlayers);
            Player right = players.get((i + 1) % numPlayers);
            current.setLeftPlayer(left);
            current.setRightPlayer(right);
        }

        // Establecer jugador inicial
        this.currentPlayer = players.getFirst();
    }

    // Tomar una carta del mazo y eliminarla del mazo
    public Card drawCard() {
        return deck.removeFirst();
    }

    // pickCard() en el cual como el jugador no tiene opción para jugar, toma una carta, si la carta es jugable la juega y cambia el turno
    // (esto de jugar la carta lo hacemos en los Tests nosotros ya que sino no podemos jugar y cantar uno a la vez)
    // Si la carta no es jugable vuelve a tomar, y así hasta que encuentre una carta jugable.
    public void pickCard(String playerName) { // tiene el nombre del jugador pero no se si es necesario
        Card card = drawCard();
        currentPlayer.addCard(card);
        if (card.canStackOn(currentCard)){
            return;
        }
        pickCard(playerName);
    }

    // Metodo principal para jugar una carta de un jugador
    // Si la carta que se le pasa a Jugar es una WildCard, ya debe tener asignado un color desde los test
    // Tenemos que tener un metodo en wildcard que reciba una que no tiene color y cree una con color.
    public void playCard(String playerName, Card card) {
        if (finished) {
            throw new IllegalStateException(GameIsOver);
        }
        if (!currentPlayer.getName().equals(playerName)) {
            throw new IllegalArgumentException(NoTurn);
        }
        if (!currentPlayer.hasCard(card)) {
            throw new IllegalArgumentException(DoesNotHaveCard);
        }

        if (!card.canStackOn(currentCard)){ // Si la carta no es jugable, el estado de la partida no cambia
            return;
//            throw new IllegalArgumentException(IllegalPlay);
        }

        currentPlayer.removeCard(card);
        currentCard = card;

        // Penalización por no cantar UNO o Cantar UNO sin tener una sola carta
        if (currentPlayer.getHandSize() == 1 && !card.isOneCalled() ||
            (card.isOneCalled() && currentPlayer.getHandSize() != 1)) {
            // Robar 2 cartas
            currentPlayer.addCard(drawCard());
            currentPlayer.addCard(drawCard());
        }

        // Verificar fin de juego (mano vacía)
        if (currentPlayer.getHandSize() == 0) {
            finished = true;
            winner = currentPlayer;
        }

        card.action(this);

    }

    // Mover al siguiente jugador según la dirección actual
    public Game nextTurn() {
        currentPlayer = controller.next(currentPlayer);
        return this;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public boolean isFinished() {
        return finished;
    }

    public Player getWinner() {
        return winner;
    }

    public void reverseDirection() {
        controller = controller.switchController();
    }
}
