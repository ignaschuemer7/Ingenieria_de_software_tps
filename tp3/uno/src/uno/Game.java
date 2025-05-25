package uno;

import java.util.*;

public class Game {
    private Player currentPlayer;
    private Card currentCard;
    private final Deque<Card> deck;
    private boolean finished = false;
    private Player winner;
    private Controller controller = new RightController(); // Sentido de la ronda hacia la derecha al inicio

    public static final String GameIsOver = "The game is already over.";
    public static final String DoesNotHaveCard = "The player does not have that card.";
    public static final String NoTurn = "It is not the player's turn.";
    public static final String PlayerHasNotCard = "The card is not in the player's hand.";
    public static final String NoNumberedCard = "This card does not have a number.";

    public Game(Card[] deckArray, String[] names, int handSize) {
        this.deck = new ArrayDeque<>(Arrays.asList(deckArray));
        this.currentCard = deck.removeFirst();

        // Creamos la ronda de jugadores
        Player firstPlayer = new Player(names[0]);
        for (int i = 0; i < handSize; i++) {
            firstPlayer.addCard(drawCard());
        }
        Player prevPlayer = firstPlayer;
        Player currentPlayer;

        for (int i = 1; i < names.length; i++) {
            currentPlayer = new Player(names[i]);
            for (int j = 0; j < handSize; j++) {
                currentPlayer.addCard(drawCard());
            }
            prevPlayer.setRightPlayer(currentPlayer);
            currentPlayer.setLeftPlayer(prevPlayer);
            prevPlayer = currentPlayer;
        }
        prevPlayer.setRightPlayer(firstPlayer);
        firstPlayer.setLeftPlayer(prevPlayer);
        this.currentPlayer = firstPlayer;
    }

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
        if (!card.canStackOn(currentCard)){
            // Si la carta no es jugable, el estado de la partida no cambia
            return;
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
        // Efecto de la carta sobre el juego
        card.action(this);
    }

    // El jugador toma cartas del maso hasta que salga una jugable
    public void pickCard() {
        Card card = drawCard();
        currentPlayer.addCard(card);
        if (card.canStackOn(currentCard)){ return; }
        pickCard();
    }

    public Card drawCard() {
        return deck.removeFirst();
    }


    public Game nextTurn() {
        currentPlayer = controller.next(currentPlayer);
        return this;
    }

    public Game reverseDirection() {
        controller = controller.switchController();
        return this;
    }

    public Game nextPlayerDraw2() {
        nextTurn();
        Player next = getCurrentPlayer();
        next.addCard(drawCard());
        next.addCard(drawCard());
        nextTurn();
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

}
