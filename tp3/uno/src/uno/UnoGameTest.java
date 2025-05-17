package uno;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import java.util.*;

public class UnoGameTest {
    private Game game;
    private Card[] deck;
    private String[] names;
    private int handSize;

    // Cartas de prueba
    private NumberedCard numRed5;
    private NumberedCard numGreen7;
    private NumberedCard lastValid;
    private SkipCard skipRed;
    private ReverseCard revGreen;
    private Draw2Card draw2Blue;
    private WildCard wild;

    @BeforeEach
    public void setUp() {
        // Configuración de mazo y jugadores
        numRed5    = new NumberedCard("Rojo", 5);
        skipRed    = new SkipCard("Rojo");
        revGreen   = new ReverseCard("Verde");
        draw2Blue  = new Draw2Card("Azul");
        wild       = new WildCard();
        numGreen7  = new NumberedCard("Verde", 7);
        lastValid  = new NumberedCard("Rojo", 9);

        deck = new Card[]{
                numRed5, skipRed, revGreen, draw2Blue, wild, numGreen7
        };
        names = new String[]{"Ana", "Beto", "Cami"};
        handSize = 2;
        game = new Game(deck, names, handSize);

        // Dar la carta final a Ana desde el principio
        game.getPlayers().getFirst().addCard(lastValid);
    }



    @Test public void testInitialDeal() {
        for (Player p : game.getPlayers()) {
            assertEquals(handSize, p.getHandSize(), "Cada jugador debe recibir 2 cartas");
        }
        assertNotNull(game.getCurrentCard(), "Debe haber carta inicial en el pozo");
    }

    @Test public void testPlayNumberedAdvancesTurn() {
        Player ana = game.getPlayers().getFirst();
        Card c = ana.getHand().stream()
                .filter(ca -> ca instanceof NumberedCard)
                .findFirst().get();
        game.jugar("Ana", c);
        assertEquals("Beto", game.getCurrentPlayer().getName());
    }

    @Test public void testSkipCardSkipsOnePlayer() {
        Player ana = game.getPlayers().getFirst();
        ana.addCard(skipRed);
        game.jugar("Ana", skipRed);
        assertEquals("Cami", game.getCurrentPlayer().getName());
    }

    @Test public void testReverseCardInvertsOrder() {
        Player ana = game.getPlayers().getFirst();
        ana.addCard(revGreen);
        game.jugar("Ana", revGreen);
        assertEquals("Cami", game.getCurrentPlayer().getName());
    }

    @Test public void testDraw2CardMakesNextDrawAndSkip() {
        Player ana = game.getPlayers().getFirst();
        ana.addCard(draw2Blue);
        game.jugar("Ana", draw2Blue);
        assertEquals(handSize + 2, game.getPlayers().get(1).getHandSize());
        assertEquals("Cami", game.getCurrentPlayer().getName());
    }

    @Test public void testWildCardAlwaysPlayable() {
        Player ana = game.getPlayers().getFirst();
        ana.addCard(wild);
        assertDoesNotThrow(() -> game.jugar("Ana", wild));
        assertEquals("Beto", game.getCurrentPlayer().getName());
    }

    @Test public void testPenaltyWhenNotCallingUno() {
        Player ana = game.getPlayers().getFirst();
        ana.getHand().clear();
        ana.addCard(lastValid);
        game.jugar("Ana", lastValid);
        assertEquals(2, ana.getHandSize(), "Debe robar 2 cartas por no cantar UNO");
    }

    @Test public void testGameEndsWhenHandEmpty() {
        Player ana = game.getPlayers().getFirst();
        ana.getHand().clear();
        ana.addCard(lastValid);
        game.jugar("Ana", lastValid);
        assertTrue(game.isFinished(), "El juego debe terminar");
        assertEquals("Ana", game.getWinner().getName());
    }

    private void play(String player, Card card) {
        game.jugar(player, card);
    }

    private void assertNext(String expected) {
        assertEquals(expected, game.getCurrentPlayer().getName());
    }

    private static void assertThrowsLike(Executable executable, String message) {
        assertEquals(message, assertThrows(Exception.class, executable).getMessage());
    }
}
