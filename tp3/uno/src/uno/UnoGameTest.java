package uno;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

public class UnoGameTest {
    private Game game;
    private Card[] deck;
    private String[] names;
    private int handSize;

    // Cartas de prueba
    private NumberedCard numRed5;
    private NumberedCard numRed6;
    private NumberedCard numBlue5;
    private NumberedCard numBlue6;
    private NumberedCard numGreen5;
    private NumberedCard numGreen6;
    private NumberedCard numGreen7;
    private SkipCard skipRed;
    private NumberedCard numRed7;
    private ReverseCard revGreen;
    private ReverseCard revRed;
    private Draw2Card draw2Red;
    private WildCard wild;

    @BeforeEach
    public void setUp() {
        // Configuración de mazo y jugadores
        numRed5    = new NumberedCard("Red", 5);
        numRed6    = new NumberedCard("Red", 6);
        numBlue5   = new NumberedCard("Blue", 5);
        numBlue6   = new NumberedCard("Blue", 6);
        numGreen5  = new NumberedCard("Green", 5);
        numGreen6  = new NumberedCard("Green", 6);
        skipRed    = new SkipCard("Red");
        numRed7    = new NumberedCard("Red", 7);
        revGreen   = new ReverseCard("Green");
        revRed    = new ReverseCard("Red");
        draw2Red  = new Draw2Card("Red");
        wild       = new WildCard();
        numGreen7  = new NumberedCard("Green", 7);

        deck = new Card[]{ //primer carta al pozo y el resto a los jugadores
                numRed5, numRed6, numBlue5, numBlue6, numGreen5, numGreen6, skipRed, numRed7, revGreen, revRed, numGreen7, draw2Red, wild, numGreen7
        };
        names = new String[]{"Ana", "Beto", "Cami"};
        handSize = 2;
        game = new Game(deck, names, handSize);
    }



    @Test public void testInitialDeal() {
        while (true){
            Player p = game.getCurrentPlayer();
            assertEquals(handSize, p.getHandSize(), "Cada jugador debe recibir 2 cartas");
            if (p.getName().equals("Cami")) break;
            game.nextTurn();
        }
        assertNotNull(game.getCurrentCard(), "Debe haber carta inicial en el pozo");
    }

    @Test public void testPlayNumberedAdvancesTurn() {
        Player ana = game.getCurrentPlayer();
        Card c = ana.getHand().stream()
                .filter(ca -> ca instanceof NumberedCard)
                .findFirst().get();
        game.playCard("Ana", c);
        assertEquals("Beto", game.getCurrentPlayer().getName());
    }

    @Test public void testSkipCardSkipsOnePlayer() {
        Player ana = game.getCurrentPlayer();
        ana.addCard(skipRed);
        game.playCard("Ana", skipRed);
        assertEquals("Cami", game.getCurrentPlayer().getName());
    }

    @Test public void testReverseCardInvertsOrder() {
        Player ana = game.getCurrentPlayer();
        ana.addCard(revRed);
        game.playCard("Ana", revRed);
        assertEquals("Cami", game.getCurrentPlayer().getName());
    }

    @Test public void testDraw2CardMakesNextDrawAndSkip() {
        Player ana = game.getCurrentPlayer();
        ana.addCard(draw2Red);
        game.playCard("Ana", draw2Red);
        assertEquals(handSize + 2, ana.getRightPlayer().getHandSize());
        assertEquals("Cami", game.getCurrentPlayer().getName());
    }

    @Test public void testWildCardAlwaysPlayable() {
        Player ana = game.getCurrentPlayer();
        ana.addCard(wild);
        assertDoesNotThrow(() -> game.playCard("Ana", wild.beRed()));
        assertEquals("Beto", game.getCurrentPlayer().getName());
    }

    @Test public void testPenaltyWhenNotCallingUno() {
//        Player ana = game.getCurrentPlayer();
        game.playCard("Ana", numRed6);
        assertEquals(3, game.getCurrentPlayer().getHandSize(), "Debe robar 2 cartas por no cantar UNO");
    }

    @Test public void testGameEndsWhenHandEmpty() {
        game.playCard("Ana", numBlue5.callOne());
        game.playCard("Beto", numBlue6.callOne());
        game.playCard("Cami", numGreen6.callOne());
        game.playCard("Ana", numRed6);
        assertTrue(game.isFinished(), "El juego debe terminar");
        assertEquals("Ana", game.getWinner().getName());
    }

    private void play(String player, Card card) {
        game.playCard(player, card);
    }

    private void assertNext(String expected) {
        assertEquals(expected, game.getCurrentPlayer().getName());
    }

    private static void assertThrowsLike(Executable executable, String message) {
        assertEquals(message, assertThrows(Exception.class, executable).getMessage());
    }
}
