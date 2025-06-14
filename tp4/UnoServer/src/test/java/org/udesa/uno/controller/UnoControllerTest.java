package org.udesa.uno.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.udesa.uno.model.JsonCard;
import org.udesa.uno.model.Match;
import org.udesa.uno.model.Player;
import org.udesa.uno.service.Dealer;
import org.udesa.uno.service.UnoService;
import org.udesa.uno.service.UnoServiceTest;
import org.udesa.uno.model.ColoredCard;

@SpringBootTest
@AutoConfigureMockMvc
public class UnoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Dealer dealer;

    private ObjectMapper objectMapper = new ObjectMapper();

    // ========== NEW MATCH TESTS ==========

    @BeforeEach
    public void beforeEach() {
        when(dealer.fullDeck()).thenReturn(UnoServiceTest.createMockDeck());
    }

    @Test
    public void test01CanCreateNewMatchWithValidPlayers() throws Exception {
        String matchId = newGame();

        assertNotNull(UUID.fromString(matchId));
    }

    @Test
    public void test02CanCreateNewMatchWithMultiplePlayers() throws Exception {
        String matchId = newGameWithPlayers("Player1", "Player2", "Player3");

        assertNotNull(UUID.fromString(matchId));
    }

    @Test
    public void test03CannotCreateNewMatchWithOnePlayer() throws Exception {
        mockMvc.perform(post("/newmatch?players=OnlyPlayer"))
                .andDo(print())
                .andExpect(status().is(400));
    }

    @Test
    public void test04CannotCreateNewMatchWithEmptyPlayerName() throws Exception {
        mockMvc.perform(post("/newmatch?players=ValidPlayer&players="))
                .andDo(print())
                .andExpect(status().is(400));
    }

    // ========== PLAY CARD TESTS ==========

    @Test
    public void test05CanPlayValidCard() throws Exception {
        String matchId = newGame();
        JsonCard validCard = new JsonCard("Red", 2, "NumberCard", false);
        mockMvc.perform(post("/play/" + matchId + "/Emilio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCard.toString()))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void test06CannotPlayCardWhenNotPlayerTurn() throws Exception {
        String matchId = newGame();
        List<JsonCard> hand = getPlayerHand(matchId);

        // Try to play as Julio when it's Emilio's turn
        String response = mockMvc.perform(post("/play/" + matchId + "/Julio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(hand.get(0).toString()))
                .andDo(print())
                .andExpect(status().is(500))
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains(Player.NotPlayersTurn + "Julio"));
    }

    @Test
    public void test07CannotPlayInvalidCard() throws Exception {
        String matchId = newGame();
        JsonCard activeCard = getActiveCard(matchId);

        // Create an invalid card (different color and number from active card)
        JsonCard invalidCard = createInvalidCard(activeCard);

        String response = mockMvc.perform(post("/play/" + matchId + "/Emilio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidCard.toString()))
                .andDo(print())
                .andExpect(status().is(500))
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains(Match.NotACardInHand + "Emilio"));
    }

    @Test
    public void test08CannotPlayInvalidColorCard() throws Exception {
        String matchId = newGame();
        // Create a card that's not in player's hand
        JsonCard notInHand = new JsonCard("Gray", 99, "NumberCard", false);

        String response = mockMvc.perform(post("/play/" + matchId + "/Emilio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(notInHand.toString()))
                .andDo(print())
                .andExpect(status().is(400))
                .andReturn().getResponse().getContentAsString();

        assertEquals("Error: " + ColoredCard.invalidColor, response);
    }

    @Test
    public void test09CannotPlayInNonExistentMatch() throws Exception {
        UUID fakeMatchId = UUID.randomUUID();
        JsonCard anyCard = new JsonCard("Red", 5, "NumberCard", false);

        String response = mockMvc.perform(post("/play/" + fakeMatchId + "/Emilio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(anyCard.toString()))
                .andDo(print())
                .andExpect(status().is(500))
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains(UnoService.matchNotFound));
    }

    // ========== DRAW CARD TESTS ==========

    @Test
    public void test10CanDrawCard() throws Exception {
        String matchId = newGame();
        List<JsonCard> handBefore = getPlayerHand(matchId);

        mockMvc.perform(post("/draw/" + matchId + "/Emilio"))
                .andDo(print())
                .andExpect(status().isOk());

        List<JsonCard> handAfter = getPlayerHand(matchId);
        assertEquals(handBefore.size() + 1, handAfter.size());
    }

    @Test
    public void test11CannotDrawCardWhenNotPlayerTurn() throws Exception {
        String matchId = newGame();

        String response = mockMvc.perform(post("/draw/" + matchId + "/Julio"))
                .andDo(print())
                .andExpect(status().is(500))
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains(Player.NotPlayersTurn + "Julio"));
    }

    @Test
    public void test12CannotDrawCardInNonExistentMatch() throws Exception {
        UUID fakeMatchId = UUID.randomUUID();

        String response = mockMvc.perform(post("/draw/" + fakeMatchId + "/Emilio"))
                .andDo(print())
                .andExpect(status().is(500))
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains(UnoService.matchNotFound));
    }

    // ========== ACTIVE CARD TESTS ==========

    @Test
    public void test13CanGetActiveCard() throws Exception {
        String matchId = newGame();

        JsonCard activeCard = getActiveCard(matchId);

        assertNotNull(activeCard);
        assertNotNull(activeCard.getColor());
        assertNotNull(activeCard.getType());
    }

    @Test
    public void test14CannotGetActiveCardFromNonExistentMatch() throws Exception {
        UUID fakeMatchId = UUID.randomUUID();

        String response = mockMvc.perform(get("/activecard/" + fakeMatchId))
                .andDo(print())
                .andExpect(status().is(500))
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains(UnoService.matchNotFound));
    }

    // ========== PLAYER HAND TESTS ==========

    @Test
    public void test15CanGetPlayerHand() throws Exception {
        String matchId = newGame();

        List<JsonCard> hand = getPlayerHand(matchId);

        assertNotNull(hand);
        assertTrue(hand.size() > 0);

        // Verify all cards have valid properties
        for (JsonCard card : hand) {
            assertNotNull(card.getColor());
            assertNotNull(card.getType());
        }
    }

    @Test
    public void test16CannotGetPlayerHandFromNonExistentMatch() throws Exception {
        UUID fakeMatchId = UUID.randomUUID();

        String response = mockMvc.perform(get("/playerhand/" + fakeMatchId))
                .andDo(print())
                .andExpect(status().is(500))
                .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains(UnoService.matchNotFound));
    }

    // ========== GAME FLOW TESTS ==========

    @Test
    public void test17PlayCardChangesActiveCard() throws Exception {
        String matchId = newGame();
        JsonCard originalActiveCard = getActiveCard(matchId);
        List<JsonCard> hand = getPlayerHand(matchId);

        JsonCard validCard = findValidCard(hand, originalActiveCard);
        if (validCard != null) {
            mockMvc.perform(post("/play/" + matchId + "/Emilio")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validCard.toString()))
                    .andExpect(status().isOk());

            JsonCard newActiveCard = getActiveCard(matchId);

            // The active card should have changed
            assertTrue(!originalActiveCard.toString().equals(newActiveCard.toString()));
        }
    }

    @Test
    public void test18PlayCardReducesHandSize() throws Exception {
        String matchId = newGame();
        List<JsonCard> originalHand = getPlayerHand(matchId);
        JsonCard activeCard = getActiveCard(matchId);

        JsonCard validCard = findValidCard(originalHand, activeCard);
        if (validCard != null) {
            mockMvc.perform(post("/play/" + matchId + "/Emilio")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validCard.toString()))
                    .andExpect(status().isOk());
        }
    }

    // ========== HELPER METHODS ==========

    private String newGame() throws Exception {
        return newGameWithPlayers("Emilio", "Julio");
    }

    private String newGameWithPlayers(String... players) throws Exception {
        StringBuilder urlBuilder = new StringBuilder("/newmatch");
        for (int i = 0; i < players.length; i++) {
            urlBuilder.append(i == 0 ? "?" : "&");
            urlBuilder.append("players=").append(players[i]);
        }

        String response = mockMvc.perform(post(urlBuilder.toString()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).asText();
    }

    private List<JsonCard> getPlayerHand(String matchId) throws Exception {
        String response = mockMvc.perform(get("/playerhand/" + matchId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(response, new TypeReference<List<JsonCard>>() {});
    }

    private JsonCard getActiveCard(String matchId) throws Exception {
        String response = mockMvc.perform(get("/activecard/" + matchId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(response, JsonCard.class);
    }

    private JsonCard findValidCard(List<JsonCard> hand, JsonCard activeCard) {
        for (JsonCard card : hand) {
            if (isValidPlay(card, activeCard)) {
                return card;
            }
        }
        return null; // No valid card found
    }

    private boolean isValidPlay(JsonCard card, JsonCard activeCard) {
        // A card is valid if it matches color, number, or is a wild card
        return card.getColor().equals(activeCard.getColor()) ||
                (card.getNumber() != null && card.getNumber().equals(activeCard.getNumber())) ||
                card.getType().contains("Wild");
    }

    private JsonCard createInvalidCard(JsonCard activeCard) {
        // Create a card that doesn't match color, number, and isn't wild
        String differentColor = activeCard.getColor().equals("Red") ? "Blue" : "Red";
        Integer differentNumber = activeCard.getNumber() != null ?
                (activeCard.getNumber() == 1 ? 9 : 1) : 5;

        return new JsonCard(differentColor, differentNumber, "NumberCard", false);
    }
}