package uno;

import java.util.*;

public class Game {
    private Player currentPlayer;
    private Card currentCard;
    private final Deque<Card> deck;
    private boolean finished = false;
    private Player winner;
    private Controller controller = new RightController();

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

    // Metodo principal para jugar una carta de un jugador
    // Si la carta que se le pasa a Jugar es una WildCard, ya debe tener asignado un color desde los test
    // Tenemos que tener un metodo en wildcard que reciba una que no tiene color y cree una con color.
    public void playCard(String playerName, Card card) {
        if (finished) {
            throw new IllegalStateException("El juego ya ha terminado.");
        }
        if (!currentPlayer.getName().equals(playerName)) {
            throw new IllegalArgumentException("No es el turno del jugador " + playerName);
        }
        if (!currentPlayer.hasCard(card)) {
            throw new IllegalArgumentException("El jugador no tiene esa carta.");
        }
        // Verificar jugada válida (WildCard puede jugarse en cualquier momento)
        if (!card.canStackOn(currentCard)){
            throw new IllegalArgumentException("Jugada ilegal: la carta no es apilable.");
        }

        currentPlayer.removeCard(card);
        currentCard = card;

        // Penalización por no cantar UNO
        if (currentPlayer.getHandSize() == 1 && !card.isOneCalled()) {
            // Robar 2 cartas
            currentPlayer.addCard(drawCard());
            currentPlayer.addCard(drawCard());
        }

        // Cantar UNO sin tener una sola carta
        if (card.isOneCalled() && currentPlayer.getHandSize() != 1) {
                throw new IllegalArgumentException("El jugador no puede cantar UNO.");
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
