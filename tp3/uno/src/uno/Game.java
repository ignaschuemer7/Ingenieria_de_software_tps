package uno;

import java.util.*;

public class Game {
    private List<Player> players;
    private Card currentCard;
    private int currentPlayerIndex;
    private int direction = 1; // 1 para avance normal, -1 para reversa
    private final Deque<Card> deck;
    private boolean finished = false;
    private Player winner;

    public Game(Card[] deckArray, String[] names, int handSize) {
        // 1) mazo como deque: facilita draw y no usamos if
        this.deck = new ArrayDeque<>(Arrays.asList(deckArray));
        // 2) primer carta al pozo
        this.currentCard = deck.removeFirst();
        // 3) crear jugadores en orden y repartir
        this.players = new ArrayList<>();
        for (String name : names) {
            Player p = new Player(name);
            for (int i = 0; i < handSize; i++) {
                p.addCard(drawCard());
            }
            players.add(p);
        }
    }

    // Tomar una carta del mazo y eliminarla del mazo
    public Card drawCard() {
        return deck.removeFirst();
    }

    // Método principal para jugar una carta de un jugador
    // Si la carta que se le pasa a Jugar es una WildCard, ya debe tener asignado un color desde los test
    // Tenemos que tener un método en wildcard que reciba una que no tiene color y cree una con color.
    public void jugar(String playerName, Card card) {
        if (finished) {
            throw new IllegalStateException("El juego ya ha terminado.");
        }
        Player currentPlayer = players.get(currentPlayerIndex);
        if (!currentPlayer.getName().equals(playerName)) {
            throw new IllegalArgumentException("No es el turno del jugador " + playerName);
        }
        if (!currentPlayer.hasCard(card)) {
            throw new IllegalArgumentException("El jugador no tiene esa carta.");
        }
        // Verificar jugada válida (WildCard puede jugarse en cualquier momento)
        if (!card.isValid(currentCard) && !(card instanceof WildCard)) {
            throw new IllegalArgumentException("Jugada ilegal: la carta no coincide.");
        }

        // Remover carta de la mano y colocarla como carta actual
        currentPlayer.removeCard(card);
        currentCard = card;
        // Ejecutar acción de la carta
        card.action(this);
        // Penalización por no cantar UNO
        if (currentPlayer.getHandSize() == 0 && !card.isOneCalled()) {
            // Robar 2 cartas
            // print by terminal
            currentPlayer.addCard(drawCard());
            currentPlayer.addCard(drawCard());
        }
        // Verificar fin de juego (mano vacía)
        if (currentPlayer.getHandSize() == 0) {
            finished = true;
            winner = currentPlayer;
        }
    }

    // Mover al siguiente jugador según la dirección actual
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + direction + players.size()) % players.size();
    }
    public List<Player> getPlayers() {
        return players;
    }
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
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
    public int getDirection() {
        return direction;
    }
    public void reverseDirection() {
        direction *= -1;
    }
}
