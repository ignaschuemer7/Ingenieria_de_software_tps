package org.udesa.uno.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.udesa.uno.model.*;

@SpringBootTest
public class UnoServiceTest {

    @Autowired
    private UnoService unoService;

    @MockitoBean
    private Dealer dealer;

    private List<String> validPlayers;
    private static final int HAND_SIZE = 7;

    @BeforeEach
    public void beforeEach() {
        validPlayers = Arrays.asList("Player1", "Player2", "Player3");
        when(dealer.fullDeck())
                .thenReturn(createMockDeck());
    }

    @Test
    public void test01CanCreateNewMatchWithValidPlayers() {
        UUID matchId = unoService.newMatch(validPlayers);

        assertNotNull(matchId);

        // Verify the match was created and the correct number of cards were dealt
        Card activeCard = unoService.activeCard(matchId);
        assertNotNull(activeCard);

        List<Card> playerHand = unoService.playerHand(matchId);
        assertNotNull(playerHand);
        assertEquals(HAND_SIZE, playerHand.size());
    }

    @Test
    public void test02CanCreateMultipleIndependentMatchesAndHands() {
        List<String> players1 = Arrays.asList("Alice", "Bob");
        List<String> players2 = Arrays.asList("Charlie", "David");

        UUID matchId1 = unoService.newMatch(players1);
        UUID matchId2 = unoService.newMatch(players2);

        assertNotNull(matchId1);
        assertNotNull(matchId2);
        assertFalse(matchId1.equals(matchId2));

        // Both matches should be accessible independently with correct hand sizes
        assertNotNull(unoService.activeCard(matchId1));
        assertNotNull(unoService.activeCard(matchId2));
        assertEquals(HAND_SIZE, unoService.playerHand(matchId1).size());
        assertEquals(HAND_SIZE, unoService.playerHand(matchId2).size());
    }

    @Test
    public void test03CanGetActiveCardFromValidMatch() {
        UUID matchId = unoService.newMatch(validPlayers);

        Card activeCard = unoService.activeCard(matchId);

        assertNotNull(activeCard);
    }

    @Test
    public void test04CanNotGetActiveCardFromInvalidMatch() {
        UUID invalidMatchId = UUID.randomUUID();

        assertThrowsLike(() -> unoService.activeCard(invalidMatchId),
                "Match not found");
    }

    @Test
    public void test05CanGetPlayerHandFromValidMatch() {
        UUID matchId = unoService.newMatch(validPlayers);

        List<Card> playerHand = unoService.playerHand(matchId);

        assertNotNull(playerHand);
        assertEquals(HAND_SIZE, playerHand.size());
    }

    @Test
    public void test06CanNotGetPlayerHandFromInvalidMatch() {
        UUID invalidMatchId = UUID.randomUUID();

        assertThrowsLike(() -> unoService.playerHand(invalidMatchId),
                "Match not found");
    }

    @Test
    public void test07CanPlayNumberCardWithValidMatchAndPlayer() {
        UUID matchId = unoService.newMatch(validPlayers);
        String playerName = "Player1";

        // Create a JsonCard for a number card
        JsonCard numberCard = new JsonCard("Red", 2, "NumberCard", false);

        unoService.play(matchId, playerName, numberCard);

        // Verify the play was processed (no exception thrown)
        assertNotNull(unoService.activeCard(matchId));
    }

    @Test
    public void test08CanPlaySkipCardWithValidMatchAndPlayer() {
        UUID matchId = unoService.newMatch(validPlayers);
        String playerName = "Player1";

        JsonCard skipCard = new JsonCard("Red", null, "SkipCard", false);

        unoService.play(matchId, playerName, skipCard);

        assertNotNull(unoService.activeCard(matchId));
    }

    @Test
    public void test09CanPlayWildCardWithValidMatchAndPlayer() {
        UUID matchId = unoService.newMatch(validPlayers);
        String playerName = "Player1";

        JsonCard wildCard = new JsonCard("Blue", null, "WildCard", false);

        unoService.play(matchId, playerName, wildCard);

        assertNotNull(unoService.activeCard(matchId));
    }

    @Test
    public void test10CanNotPlayCardWithInvalidMatch() {
        UUID invalidMatchId = UUID.randomUUID();
        String playerName = "Player1";
        JsonCard numberCard = new JsonCard("Red", 3, "NumberCard", false);

        assertThrowsLike(() -> unoService.play(invalidMatchId, playerName, numberCard),
                "Match not found");
    }

    @Test
    public void test11CanDrawCardWithValidMatchAndPlayer() {
        UUID matchId = unoService.newMatch(validPlayers);
        String playerName = "Player1";

        // Get initial hand size
        List<Card> initialHand = unoService.playerHand(matchId);
        int initialSize = initialHand.size();
        assertEquals(HAND_SIZE, initialSize);

        unoService.drawCard(matchId, playerName);

        // Verify hand size increased
        List<Card> newHand = unoService.playerHand(matchId);
        assertEquals(initialSize + 1, newHand.size());
    }

    @Test
    public void test12CanNotDrawCardWithInvalidMatch() {
        UUID invalidMatchId = UUID.randomUUID();
        String playerName = "Player1";

        assertThrowsLike(() -> unoService.drawCard(invalidMatchId, playerName),
                "Match not found");
    }

    @Test
    public void test13MultipleDrawsIncreaseHandSizeCorrectly() {
        UUID matchId = unoService.newMatch(validPlayers);
        String playerName = "Player1";

        List<Card> initialHand = unoService.playerHand(matchId);
        int initialSize = initialHand.size();
        assertEquals(HAND_SIZE, initialSize);

        unoService.drawCard(matchId, playerName);
        unoService.drawCard(matchId, playerName);
        unoService.drawCard(matchId, playerName);

        List<Card> finalHand = unoService.playerHand(matchId);
        assertEquals(initialSize + 3, finalHand.size());
    }

    @Test
    public void test14CanCreateMatchWithTwoPlayers() {
        List<String> twoPlayers = Arrays.asList("Alice", "Bob");

        UUID matchId = unoService.newMatch(twoPlayers);

        assertNotNull(matchId);
        assertNotNull(unoService.activeCard(matchId));
        List<Card> hand = unoService.playerHand(matchId);
        assertNotNull(hand);
        assertEquals(HAND_SIZE, hand.size());
    }


    @Test
    public void test16SessionsRemainIndependentAfterOperations() {
        List<String> players1 = Arrays.asList("Alice", "Bob");
        List<String> players2 = Arrays.asList("Charlie", "David");

        UUID matchId1 = unoService.newMatch(players1);
        UUID matchId2 = unoService.newMatch(players2);

        // Perform operations on first match
        unoService.drawCard(matchId1, "Alice");
        unoService.drawCard(matchId1, "Alice");

        // Play a card in second match
        JsonCard skipCard = new JsonCard("Red", null, "SkipCard", false);
        unoService.play(matchId2, "Charlie", skipCard);

        // Both matches should still be accessible and independent
        List<Card> hand1 = unoService.playerHand(matchId1);
        List<Card> hand2 = unoService.playerHand(matchId2);

        assertNotNull(hand1);
        assertNotNull(hand2);
        assertNotNull(unoService.activeCard(matchId1));
        assertNotNull(unoService.activeCard(matchId2));
        assertEquals(HAND_SIZE + 2, hand1.size());
        assertEquals(HAND_SIZE - 1, hand2.size());
    }

    @Test
    public void test17CanPlayDraw2CardWithValidMatchAndPlayer() {
        UUID matchId = unoService.newMatch(validPlayers);

        JsonCard draw2Card = new JsonCard("Red", null, "Draw2Card", false);

        unoService.play(matchId, "Player1", draw2Card);

        assertNotNull(unoService.activeCard(matchId));
    }

    @Test
    public void test18CanPlayReverseCardWithValidMatchAndPlayer() {
        UUID matchId = unoService.newMatch(validPlayers);
        String playerName = "Player1";

        JsonCard reverseCard = new JsonCard("Red", null, "ReverseCard", false);

        unoService.play(matchId, playerName, reverseCard);

        assertNotNull(unoService.activeCard(matchId));
    }


    @Test
    public void test19ConsecutiveOperationsOnSameMatchWork() {
        UUID matchId = unoService.newMatch(validPlayers);
        String player1 = "Player1";
        String player2 = "Player2";

        // Draw cards for player1
        JsonCard wildCard = new JsonCard("Blue", null, "WildCard", false);
        unoService.play(matchId, player1, wildCard);

        // Play a card with player2
        JsonCard numberCard = new JsonCard("Blue", null, "SkipCard", false);
        unoService.play(matchId, player2, numberCard);

        // Draw more cards for player1
        unoService.drawCard(matchId, player1);

        // All operations should work without interfering with each other
        assertNotNull(unoService.activeCard(matchId));
        assertNotNull(unoService.playerHand(matchId));
    }

    // Helper methods
    private void assertThrowsLike(Executable executable, String expectedMessage) {
        RuntimeException exception = assertThrows(RuntimeException.class, executable);
        assertEquals(expectedMessage, exception.getMessage());
    }

    private List<Card> createMockDeck() {
        // Create a mock deck similar to what Dealer would create
        return Arrays.asList(
                new NumberCard("Red", 1),
                new NumberCard("Red", 2),
                new NumberCard("Yellow", 5),
                new WildCard(),
                new ReverseCard("Red"),
                new Draw2Card("Red"),
                new SkipCard("Red"),
                new NumberCard("Red", 6),
                new NumberCard("Blue", 7),
                new NumberCard("Green", 8),
                new NumberCard("Yellow", 9),
                new SkipCard("Red"),
                new SkipCard("Blue"),
                new ReverseCard("Green"),
                new ReverseCard("Yellow"),
                new Draw2Card("Red"),
                new Draw2Card("Blue"),
                new WildCard(),
                // Add more cards to ensure sufficient deck size
                new NumberCard("Red", 1),
                new NumberCard("Blue", 2),
                new NumberCard("Green", 3),
                new NumberCard("Yellow", 4),
                new SkipCard("Green"),
                new SkipCard("Yellow"),
                new ReverseCard("Red"),
                new Draw2Card("Yellow")
        );
    }
}