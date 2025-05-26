package uno;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;

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
    private NumberedCard numYellow1;
    private SkipCard skipRed;
    private SkipCard skipBlue;
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
        numYellow1 = new NumberedCard("Yellow", 1);
        skipRed    = new SkipCard("Red");
        skipBlue   = new SkipCard("Blue");
        numRed7    = new NumberedCard("Red", 7);
        revGreen   = new ReverseCard("Green");
        revRed    = new ReverseCard("Red");
        draw2Red  = new Draw2Card("Red");
        wild       = new WildCard();
        numGreen7  = new NumberedCard("Green", 7);

        deck = new Card[]{ //primer carta al pozo y el resto a los jugadores
                numRed5, numRed6, numBlue5, numBlue6, numGreen5, numGreen6, skipRed, numRed7, revGreen, revRed, numYellow1, numGreen7, draw2Red, wild, numGreen7
        };
        names = new String[]{"Ana", "Beto", "Cami"};
        handSize = 2;
        game = new Game(deck, names, handSize);
    }

    @Test
    public void test01InitialDeal() {
        // Verificar que cada jugador recibe la cantidad correcta de cartas usando Streams
        Arrays.stream(names).forEach(n -> {
            Player p = game.getCurrentPlayer();
            assertEquals(handSize, p.getHandSize(),
                    String.format("Cada jugador debe tener %d cartas", handSize));
            game.nextTurn();
        });
        assertNotNull(game.getCardOnTop(), "Debe haber una carta inicial en el pozo");
    }

    @Test public void test02playedCardisInTopOfDeck() {
        game.playCard("Ana", numBlue5.callOne());
        assertEquals(numBlue5, game.getCardOnTop());
    }

    @Test public void test03PlayNumberedAdvancesTurn() {
        game.playCard("Ana", numBlue5.callOne());
        assertEquals("Beto", game.getCurrentPlayer().getName());
    }

    @Test public void test04EqualsOfCardOnHand() {
        game.playCard("Ana", new NumberedCard("Red", 6).callOne());
        assertEquals(numRed6, game.getCardOnTop());
    }

    @Test public void test05SkipCardSkipsOnePlayer() {
        addCardCurrentPlayer(game, skipRed).playCard("Ana", skipRed);
        assertEquals("Cami", game.getCurrentPlayer().getName());
    }

    @Test public void test06ReverseCardInvertsOrder() {
        addCardCurrentPlayer(game, revRed).playCard("Ana", revRed);
        assertEquals("Cami", game.getCurrentPlayer().getName());
    }

    @Test public void test07Draw2CardMakesNextDrawAndSkip() {
        addCardCurrentPlayer(game, draw2Red).playCard("Ana", draw2Red);
        assertEquals("Cami", game.getCurrentPlayer().getName());
    }

    @Test public void test08NoTurnException(){
        assertThrowsLike(() -> game.playCard("Beto", numBlue6), Game.NoTurn);
    }

    @Test public void test09PlayWildCard() {
        assertDoesNotThrow(() -> addCardCurrentPlayer(game, wild).playCard("Ana", wild.beRed()));
        assertEquals("Beto", game.getCurrentPlayer().getName());
    }

    @Test public void test10WildCardChangesColor() {
        addCardCurrentPlayer(game, wild).playCard("Ana", wild.beGreen());
        assertEquals("Green", game.getCardOnTop().getColor());
    }

    @Test public void test11PenaltyWhenNotCallingUno() {
        game.playCard("Ana", numRed6); // Ana no canta UNO
        game.playCard("Beto", numBlue6.callOne());
        game.playCard("Cami", numGreen6.callOne());
        assertEquals(3, game.getCurrentPlayer().getHandSize());
    }

    @Test public void test12GameEndsWhenHandEmpty() {
        game.playCard("Ana", numBlue5.callOne());
        game.playCard("Beto", numBlue6.callOne());
        game.playCard("Cami", numGreen6.callOne());
        game.playCard("Ana", numRed6);
        assertTrue(game.isFinished());
        assertEquals("Ana", game.getWinner().getName());
    }

    @Test public void test13UnoWildCard() {
        addCardCurrentPlayer(game, wild);
        game.playCard("Ana", numBlue5);
        game.playCard("Beto", numBlue6.callOne());
        game.playCard("Cami", numGreen6.callOne());
        game.playCard("Ana", wild.beYellow().callOne());
        game.pickCard(); // Corresponde a "Beto". Agarra cartas hasta que salga una jugable
        assertDoesNotThrow(() -> game.playCard("Beto", numYellow1));
    }

    @Test public void test14CannotPlayInvalidCard() {
        addCardCurrentPlayer(game, numBlue6).playCard("Ana", numBlue6);
        // El turno de Ana no avanza porque la carta que quiere jugar no es jugable
        assertEquals("Ana", game.getCurrentPlayer().getName());
    }

    @Test public void test15PlayDraw2ThenWild() {
        addCardCurrentPlayer(game, draw2Red).playCard("Ana", draw2Red);
        addCardCurrentPlayer(game, wild).playCard("Cami", wild.beGreen());
        assertEquals("Ana", game.getCurrentPlayer().getName());
    }

    @Test public void test16TwoReverseEndsInSamePlayer() {
        addCardCurrentPlayer(game, revRed).playCard("Ana", revRed);
        addCardCurrentPlayer(game, revRed).playCard("Cami", revRed);
        assertEquals("Ana", game.getCurrentPlayer().getName());
    }

    @Test public void test17WildOnWild(){
        addCardCurrentPlayer(game, wild).playCard("Ana", wild.beYellow());
        addCardCurrentPlayer(game, wild).playCard("Beto", wild.beGreen());
        assertDoesNotThrow(() -> game.playCard("Cami", numGreen6.callOne()));
    }

    @Test public void test18CallUnoNotLastCardPenalty() {
        addCardCurrentPlayer(game, numBlue5).playCard("Ana", numBlue5.callOne()); // pick up 2 cards (penalty)
        game.playCard("Beto", numBlue6.callOne());
        game.playCard("Cami", numGreen6.callOne());
        assertEquals(4, game.getCurrentPlayer().getHandSize());
    }

    @Test public void test19PlayerDoesNotHaveCard() {
        assertThrowsLike(() -> game.playCard("Ana", revGreen), game.DoesNotHaveCard);
    }

    @Test public void test20SkipThreeTimesEndsInSamePlayer() {
        addCardCurrentPlayer(game, skipRed).playCard("Ana", skipRed);
        addCardCurrentPlayer(game, skipRed).playCard("Cami", skipRed);
        addCardCurrentPlayer(game, skipBlue).playCard("Beto", skipBlue);
        assertEquals("Ana", game.getCurrentPlayer().getName());
    }

    @Test public void test21WinningDoesNotPlayMore() {
        game.playCard("Ana", numBlue5.callOne());
        game.playCard("Beto", numBlue6.callOne());
        game.playCard("Cami", numGreen6.callOne());
        game.playCard("Ana", numRed6);
        assertTrue(game.isFinished());
        assertThrowsLike(() -> game.playCard("Ana", numRed7), game.GameIsOver);
    }

    @Test public void test22WildCallUnoEndsGame() {
        addCardCurrentPlayer(game, wild);
        game.playCard("Ana", numBlue5);
        game.playCard("Beto", numBlue6.callOne());
        addCardCurrentPlayer(game, numBlue5).playCard("Cami", numBlue5);
        game.playCard("Ana", wild.beGreen().callOne());
        game.playCard("Beto", numGreen5);
        assertTrue(game.isFinished());
        assertEquals("Beto", game.getWinner().getName());
    }

    private Game addCardCurrentPlayer(Game game, Card card) {
        Player current = game.getCurrentPlayer();
        current.addCard(card);
        return game;
    }

    private static void assertThrowsLike(Executable executable, String message) {
        assertEquals(message, assertThrows(Exception.class, executable).getMessage());
    }
}
