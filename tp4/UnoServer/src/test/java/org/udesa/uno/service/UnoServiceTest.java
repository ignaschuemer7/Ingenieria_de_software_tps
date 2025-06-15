package org.udesa.uno.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
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
    private static final int handSize = 7;

    @BeforeEach
    public void beforeEach() {
        validPlayers = Arrays.asList("Player1", "Player2", "Player3");
        when(dealer.fullDeck()).thenReturn(createMockDeck());
    }

    @Test
    public void test01CanCreateNewMatchWithValidPlayers() {
        UUID matchId = unoService.newMatch(validPlayers);
        assertNotNull(matchId);
        assertNotNull(unoService.activeCard(matchId));
        assertEquals(handSize, unoService.playerHand(matchId).size());
    }

    @Test
    public void test02CanCreateMultipleIndependentMatchesAndHands() {
        UUID matchId1 = unoService.newMatch(validPlayers);
        UUID matchId2 = unoService.newMatch(validPlayers);
        assertNotEquals(matchId1, matchId2);
        assertNotNull(unoService.activeCard(matchId1));
        assertNotNull(unoService.activeCard(matchId2));
        assertEquals(handSize, unoService.playerHand(matchId1).size());
        assertEquals(handSize, unoService.playerHand(matchId2).size());
    }

    @Test
    public void test03CanNotGetActiveCardFromInvalidMatch() {
        assertThrowsLike(() -> unoService.activeCard(UUID.randomUUID()), UnoService.matchNotFound);
    }

    @Test
    public void test04CanNotGetPlayerHandFromInvalidMatch() {
        assertThrowsLike(() -> unoService.playerHand(UUID.randomUUID()), UnoService.matchNotFound);
    }

    @Test
    public void test05CanPlayNumberCardWithValidMatchAndPlayer() {
        UUID matchId = unoService.newMatch(validPlayers);
        unoService.play(matchId, "Player1", new JsonCard("Red", 2, "NumberCard", false));
        assertNotNull(unoService.activeCard(matchId));
    }

    @Test
    public void test06CanNotPlayCardWithInvalidMatch() {
        assertThrowsLike(() -> unoService.play(UUID.randomUUID(), "Player1", new JsonCard("Red", null, "ReverseCard", false)),
                UnoService.matchNotFound);
    }

    @Test
    public void test07CanDrawCardWithValidMatchAndPlayer() {
        UUID matchId = unoService.newMatch(validPlayers);
        assertEquals(handSize, unoService.playerHand(matchId).size());
        unoService.drawCard(matchId, "Player1");
        assertEquals(handSize + 1, unoService.playerHand(matchId).size());
    }

    @Test
    public void test08CanNotDrawCardWithInvalidMatch() {
        assertThrowsLike(() -> unoService.drawCard(UUID.randomUUID(), "Player1"), UnoService.matchNotFound);
    }

    @Test
    public void test09CanPlayDraw2CardWithValidMatchAndPlayer() {
        UUID matchId = unoService.newMatch(validPlayers);
        unoService.play(matchId, "Player1", new JsonCard("Red", null, "Draw2Card", false));
        assertNotNull(unoService.activeCard(matchId));
        assertEquals(handSize + 2, unoService.playerHand(matchId).size());
    }

    @Test
    public void test10SessionsRemainIndependentAfterOperations() {
        UUID matchId1 = unoService.newMatch(Arrays.asList("Alice", "Bob"));
        UUID matchId2 = unoService.newMatch(Arrays.asList("Charlie", "David"));

        unoService.drawCard(matchId1, "Alice");
        unoService.drawCard(matchId1, "Alice");
        unoService.play(matchId2, "Charlie", new JsonCard("Red", null, "SkipCard", false));

        assertEquals(handSize + 2, unoService.playerHand(matchId1).size());
        assertEquals(handSize - 1, unoService.playerHand(matchId2).size());
    }

    @Test
    public void test11ConsecutiveOperationsOnSameMatchWork() {
        UUID matchId = unoService.newMatch(validPlayers);
        unoService.play(matchId, "Player1", new JsonCard("Blue", null, "WildCard", false));
        unoService.play(matchId, "Player2", new JsonCard("Blue", null, "SkipCard", false));
        unoService.drawCard(matchId, "Player1");
        assertNotNull(unoService.activeCard(matchId));
        assertNotNull(unoService.playerHand(matchId));
    }

    // Helper methods
    private void assertThrowsLike(Executable executable, String expectedMessage) {
        RuntimeException exception = assertThrows(RuntimeException.class, executable);
        assertEquals(expectedMessage, exception.getMessage());
    }

    public static List<Card> createMockDeck() {
        return Arrays.asList(
                // Top card on the discard pile
                new NumberCard("Red", 1),
                // Player 1 hand (7 cards)
                new NumberCard("Red", 2),
                new NumberCard("Yellow", 5),
                new WildCard(),
                new ReverseCard("Red"),
                new Draw2Card("Red"),
                new SkipCard("Red"),
                new NumberCard("Red", 6),
                // Player 2 hand (7 cards)
                new NumberCard("Blue", 7),
                new NumberCard("Green", 8),
                new NumberCard("Yellow", 9),
                new SkipCard("Red"),
                new SkipCard("Blue"),
                new ReverseCard("Green"),
                new ReverseCard("Yellow"),
                // Player 3 hand (7 cards)
                new Draw2Card("Red"),
                new Draw2Card("Blue"),
                new WildCard(),
                new NumberCard("Red", 1),
                new NumberCard("Blue", 2),
                new NumberCard("Green", 3),
                new NumberCard("Yellow", 4),
                // Additional cards in the deck
                new SkipCard("Green"),
                new SkipCard("Yellow"),
                new ReverseCard("Red"),
                new Draw2Card("Yellow")
        );
    }
}
