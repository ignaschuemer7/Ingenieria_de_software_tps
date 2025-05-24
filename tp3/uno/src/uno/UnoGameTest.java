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
    private NumberedCard numYellow1;
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
        numYellow1 = new NumberedCard("Yellow", 1);
        skipRed    = new SkipCard("Red");
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



    @Test public void test01InitialDeal() {
        while (true){
            Player p = game.getCurrentPlayer();
            assertEquals(handSize, p.getHandSize(), "Cada jugador debe recibir 2 cartas");
            if (p.getName().equals("Cami")) break;
            game.nextTurn();
        }
        assertNotNull(game.getCurrentCard(), "Debe haber carta inicial en el pozo");
    }

    @Test public void test02PlayNumberedAdvancesTurn() {
        Player ana = game.getCurrentPlayer();
        Card c = ana.getHand().stream()
                .filter(ca -> ca instanceof NumberedCard)
                .findFirst().get();
        game.playCard("Ana", c);
        assertEquals("Beto", game.getCurrentPlayer().getName());
    }

    @Test public void test03SkipCardSkipsOnePlayer() {
        Player ana = game.getCurrentPlayer();
        ana.addCard(skipRed);
        game.playCard("Ana", skipRed);
        assertEquals("Cami", game.getCurrentPlayer().getName());
    }

    @Test public void test04ReverseCardInvertsOrder() {
        Player ana = game.getCurrentPlayer();
        ana.addCard(revRed);
        game.playCard("Ana", revRed);
        assertEquals("Cami", game.getCurrentPlayer().getName());
    }

    @Test public void test05Draw2CardMakesNextDrawAndSkip() {
        Player ana = game.getCurrentPlayer();
        ana.addCard(draw2Red);
        game.playCard("Ana", draw2Red);
        assertEquals(handSize + 2, ana.getRightPlayer().getHandSize());
        assertEquals("Cami", game.getCurrentPlayer().getName());
    }

    @Test public void test06WildCardAlwaysPlayable() {
        Player ana = game.getCurrentPlayer();
        ana.addCard(wild);
        assertDoesNotThrow(() -> game.playCard("Ana", wild.beRed()));
        assertEquals("Beto", game.getCurrentPlayer().getName());
    }

    @Test public void test07PenaltyWhenNotCallingUno() {
//        Player ana = game.getCurrentPlayer();
        game.playCard("Ana", numRed6);
        game.playCard("Beto", numBlue6.callOne());
        game.playCard("Cami", numGreen6.callOne());
        assertEquals(3, game.getCurrentPlayer().getHandSize(), "Debe robar 2 cartas por no cantar UNO");
    }

    @Test public void test08GameEndsWhenHandEmpty() {
        game.playCard("Ana", numBlue5.callOne());
        game.playCard("Beto", numBlue6.callOne());
        game.playCard("Cami", numGreen6.callOne());
        game.playCard("Ana", numRed6);
        assertTrue(game.isFinished(), "El juego debe terminar");
        assertEquals("Ana", game.getWinner().getName());
    }

    @Test public void test09UnoWildCard() {
        Player ana = game.getCurrentPlayer();
        ana.addCard(wild);
        game.playCard("Ana", numBlue5);
        game.playCard("Beto", numBlue6.callOne());
        game.playCard("Cami", numGreen6.callOne());
        game.playCard("Ana", wild.beYellow().callOne());
        game.pickCard("Beto");
        assertDoesNotThrow(() -> game.playCard("Beto", numYellow1));
    }

    @Test public void test10CannotPlayInvalidCard() {
        Player ana = game.getCurrentPlayer();
        ana.addCard(numBlue6);
        game.playCard("Ana", numBlue6);
//        assertThrowsLike(() -> game.playCard("Ana", numBlue6), game.IllegalPlay);
        assertEquals("Ana", game.getCurrentPlayer().getName());
    }

    @Test public void test11PlayDraw2ThenWild() {
        Player ana = game.getCurrentPlayer();
        ana.addCard(draw2Red);
        game.playCard("Ana", draw2Red);
        Player cami = game.getCurrentPlayer();
        cami.addCard(wild);
        game.playCard("Cami", wild.beGreen());
        assertEquals("Ana", game.getCurrentPlayer().getName());
    }

    @Test public void test12PlayWildThenDraw2() {
        Player ana = game.getCurrentPlayer();
        ana.addCard(wild);
        game.playCard("Ana", wild.beRed());
        Player beto = game.getCurrentPlayer();
        beto.addCard(draw2Red);
        game.playCard("Beto", draw2Red);
        assertEquals("Ana", game.getCurrentPlayer().getName());
    }

    @Test public void test13TwoReverseEndsInSamePlayer() {
        Player ana = game.getCurrentPlayer();
        ana.addCard(revRed);
        game.playCard("Ana", revRed);
        assertEquals("Cami", game.getCurrentPlayer().getName());
        Player cami = game.getCurrentPlayer();
        cami.addCard(revRed);
        game.playCard("Cami", revRed);
        assertEquals("Ana", game.getCurrentPlayer().getName());
    }

    @Test public void test14WildCardChangesColor() {
        Player ana = game.getCurrentPlayer();
        ana.addCard(wild);
        game.playCard("Ana", wild.beGreen());
        assertEquals("Green", game.getCurrentCard().getColor());
    }

    @Test public void test15Draw2StackingNotAllowed() {
        Player ana = game.getCurrentPlayer();
        ana.addCard(draw2Red);
        game.playCard("Ana", draw2Red);
        Player beto = game.getCurrentPlayer();
        beto.addCard(draw2Red);
        assertThrowsLike(() -> game.playCard("Beto", draw2Red), game.NoTurn);
    }

    @Test public void test16SkipWithOnlyTwoPlayersSkipsBack() {
        game = new Game(deck, new String[]{"Ana", "Beto"}, handSize);
        Player ana = game.getCurrentPlayer();
        ana.addCard(skipRed);
        game.playCard("Ana", skipRed);
        assertEquals("Ana", game.getCurrentPlayer().getName());
    }

    @Test public void test17WildCardDoesNotAdvanceTurnWithoutPlay() {
        Player ana = game.getCurrentPlayer();
        ana.addCard(wild);
        game.playCard("Ana", wild.beBlue());
        assertEquals("Beto", game.getCurrentPlayer().getName());
    }

    @Test public void test18Draw2IncreasesCorrectHand() {
        Player ana = game.getCurrentPlayer();
        ana.addCard(draw2Red);
        int previous = ana.getRightPlayer().getHandSize();
        game.playCard("Ana", draw2Red);
        assertEquals(previous + 2, ana.getRightPlayer().getHandSize());
    }

    @Test public void test19WildOnWild(){
        Player ana = game.getCurrentPlayer();
        ana.addCard(wild);
        game.playCard("Ana", wild.beYellow());
        Player beto = game.getCurrentPlayer();
        beto.addCard(wild);
        game.playCard("Beto", wild.beGreen());
        assertDoesNotThrow(() -> game.playCard("Cami", numGreen6.callOne()));
    }

    @Test public void test20CallUnoNotLastCardThrows() {
        Player ana = game.getCurrentPlayer();
        ana.addCard(numBlue5);
        game.playCard("Ana", numBlue5.callOne()); // pick up 2 cards
        game.playCard("Beto", numBlue6.callOne());
        game.playCard("Cami", numGreen6.callOne());
        assertEquals(4, game.getCurrentPlayer().getHandSize());
    }

    @Test public void test21CanOnlyPlayIfCardMatchesColorOrNumber() {
        assertThrowsLike(() -> game.playCard("Ana", revGreen), game.DoesNotHaveCard);
    }

    @Test public void test22SkipThreeTimesEndsInSamePlayer() {
        Player ana = game.getCurrentPlayer();
        ana.addCard(skipRed);
        game.playCard("Ana", skipRed);
        Player cami = game.getCurrentPlayer();
        cami.addCard(skipRed);
        game.playCard("Cami", skipRed);
        Player beto = game.getCurrentPlayer();
        beto.addCard(skipRed);
        game.playCard("Beto", skipRed);
        assertEquals("Ana", game.getCurrentPlayer().getName());
    }

    @Test public void test23WinningDoesNotPlayMore() {
        game.playCard("Ana", numBlue5.callOne());
        game.playCard("Beto", numBlue6.callOne());
        game.playCard("Cami", numGreen6.callOne());
        game.playCard("Ana", numRed6);
        assertTrue(game.isFinished());
        assertThrowsLike(() -> game.playCard("Ana", numRed7), game.GameIsOver);
    }

    @Test public void test24WildCallUnoEndsGame() {
        Player ana = game.getCurrentPlayer();
        ana.addCard(wild);
        game.playCard("Ana", numBlue5);
        game.playCard("Beto", numBlue6.callOne());
        game.playCard("Cami", numGreen6.callOne());
        game.playCard("Ana", wild.beGreen().callOne());
        game.playCard("Beto", numGreen5);
        assertTrue(game.isFinished());
        assertEquals("Beto", game.getWinner().getName());
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
