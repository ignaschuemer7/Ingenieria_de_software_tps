package org.udesa.uno.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.udesa.uno.model.Card;
import org.udesa.uno.model.JsonCard;
import org.udesa.uno.model.NumberCard;
import org.udesa.uno.service.UnoService;

@SpringBootTest
@AutoConfigureMockMvc
public class UnoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UnoService unoService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private UUID testMatchId;
    private String testPlayer;
    private JsonCard testJsonCard;
    private Card testCard;

    @BeforeEach
    public void beforeEach() {
        testMatchId = UUID.randomUUID();
        testPlayer = "TestPlayer";
        testCard = new NumberCard("Red", 5);
        testJsonCard = testCard.asJson();
    }

    // ========== NEW MATCH TESTS ==========

    @Test
    public void test01CanCreateNewMatchWithValidPlayers() throws Exception {
        List<String> players = Arrays.asList("Player1", "Player2", "Player3");
        when(unoService.newMatch(players)).thenReturn(testMatchId);

        UUID returnedMatchId = createNewMatch(players);

        assertNotNull(returnedMatchId);
        assertEquals(testMatchId, returnedMatchId);
        verify(unoService).newMatch(players);
    }

    @Test // THIS IS A DUPLICATE OF test01, BUT WITH TWO PLAYERS
    public void test02CanCreateNewMatchWithTwoPlayers() throws Exception {
        List<String> players = Arrays.asList("Player1", "Player2");
        when(unoService.newMatch(players)).thenReturn(testMatchId);

        UUID returnedMatchId = createNewMatch(players);

        assertNotNull(returnedMatchId);
        assertEquals(testMatchId, returnedMatchId);
    }

    @Test
    public void test03NewMatchReturnsUniqueIds() throws Exception {
        List<String> players = Arrays.asList("Player1", "Player2");
        UUID firstMatchId = UUID.randomUUID();
        UUID secondMatchId = UUID.randomUUID();

        when(unoService.newMatch(players))
                .thenReturn(firstMatchId)
                .thenReturn(secondMatchId);

        UUID firstResult = createNewMatch(players);
        UUID secondResult = createNewMatch(players);

        assertNotNull(firstResult);
        assertNotNull(secondResult);
        assertTrue(!firstResult.equals(secondResult));
    }

    @Test
    public void test04CannotCreateNewMatchWithEmptyPlayerList() throws Exception {
        List<String> emptyPlayers = Arrays.asList();
        when(unoService.newMatch(emptyPlayers))
                .thenThrow(new IllegalArgumentException("Players list cannot be empty"));

        createNewMatchFailing(emptyPlayers);
    }

    // ========== PLAY CARD TESTS ==========

@Test
public void test05CanPlayValidCardAndVerifyResponse() throws Exception {
    // Arrange
    JsonCard redFiveCard = new JsonCard("Red", 5, "NumberCard", false);

    // Act & Assert
    mockMvc.perform(post("/play/" + testMatchId + "/" + testPlayer)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(redFiveCard)))
            .andDo(print())
            .andExpect(status().isOk());

    // Verify service interaction
    verify(unoService).play(eq(testMatchId),
            eq(testPlayer),
            refEq(redFiveCard));
}

    @Test
    public void test06CannotPlayCardInInvalidMatch() throws Exception {
        doThrow(new RuntimeException("Match not found"))
                .when(unoService).play(eq(testMatchId), eq(testPlayer), any(JsonCard.class));

        playCardFailing(testMatchId, testPlayer, testJsonCard);
    }

    @Test
    public void test07CannotPlayInvalidCard() throws Exception {
        doThrow(new IllegalArgumentException("Invalid card play"))
                .when(unoService).play(eq(testMatchId), eq(testPlayer), any(JsonCard.class));

        playCardFailing(testMatchId, testPlayer, testJsonCard);
    }

    @Test
    public void test08CannotPlayCardWithInvalidPlayer() throws Exception {
        doThrow(new IllegalArgumentException("Invalid player"))
                .when(unoService).play(eq(testMatchId), eq("InvalidPlayer"), any(JsonCard.class));

        playCardFailing(testMatchId, "InvalidPlayer", testJsonCard);
    }

    // ========== DRAW CARD TESTS ==========

    @Test
    public void test09CanDrawCard() throws Exception {
        drawCard(testMatchId, testPlayer);

        verify(unoService).drawCard(testMatchId, testPlayer);
    }

    @Test
    public void test10CannotDrawCardInInvalidMatch() throws Exception {
        doThrow(new RuntimeException("Match not found"))
                .when(unoService).drawCard(testMatchId, testPlayer);

        drawCardFailing(testMatchId, testPlayer);
    }

    @Test
    public void test11CannotDrawCardWithInvalidPlayer() throws Exception {
        doThrow(new IllegalArgumentException("Invalid player"))
                .when(unoService).drawCard(testMatchId, "InvalidPlayer");

        drawCardFailing(testMatchId, "InvalidPlayer");
    }

    // ========== ACTIVE CARD TESTS ==========

    @Test
    public void test12CanGetActiveCard() throws Exception {
        when(unoService.activeCard(testMatchId)).thenReturn(testCard);

        JsonCard activeCard = getActiveCard(testMatchId);

        assertNotNull(activeCard);
        assertEquals(testJsonCard.getColor(), activeCard.getColor());
        assertEquals(testJsonCard.getNumber(), activeCard.getNumber());
        verify(unoService).activeCard(testMatchId);
    }

    @Test
    public void test13CannotGetActiveCardFromInvalidMatch() throws Exception {
        when(unoService.activeCard(testMatchId))
                .thenThrow(new RuntimeException("Match not found"));

        getActiveCardFailing(testMatchId);
    }

    // ========== PLAYER HAND TESTS ==========

    @Test
    public void test14CanGetPlayerHand() throws Exception {
        List<Card> playerCards = Arrays.asList(
                new NumberCard("Red", 1),
                new NumberCard("Blue", 3),
                new NumberCard("Green", 7)
        );
        when(unoService.playerHand(testMatchId)).thenReturn(playerCards);

        List<JsonCard> hand = getPlayerHand(testMatchId);

        assertNotNull(hand);
        assertEquals(3, hand.size());
        assertEquals("Red", hand.get(0).getColor());
        assertEquals(1, hand.get(0).getNumber());
        assertEquals("Blue", hand.get(1).getColor());
        assertEquals(3, hand.get(1).getNumber());
        assertEquals("Green", hand.get(2).getColor());
        assertEquals(7, hand.get(2).getNumber());
        verify(unoService).playerHand(testMatchId);
    }

    @Test
    public void test15CannotGetPlayerHandFromInvalidMatch() throws Exception {
        when(unoService.playerHand(testMatchId))
                .thenThrow(new RuntimeException("Match not found"));

        getPlayerHandFailing(testMatchId);
    }

    // ========== HELPER METHODS ==========

    private UUID createNewMatch(List<String> players) throws Exception {
        String response = mockMvc.perform(post("/newmatch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("players", String.join(",", players)))
                .andDo(print())
                .andExpect(status().is(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Remove quotes from UUID string response
        return UUID.fromString(response.replaceAll("\"", ""));
    }

    private void createNewMatchFailing(List<String> players) throws Exception {
        mockMvc.perform(post("/newmatch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("players", String.join(",", players)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    private void playCardFailing(UUID matchId, String player, JsonCard card) throws Exception {
        mockMvc.perform(post("/play/" + matchId + "/" + player)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(card)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    private void drawCard(UUID matchId, String player) throws Exception {
        mockMvc.perform(post("/draw/" + matchId + "/" + player)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(200));
    }

    private void drawCardFailing(UUID matchId, String player) throws Exception {
        mockMvc.perform(post("/draw/" + matchId + "/" + player)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    private JsonCard getActiveCard(UUID matchId) throws Exception {
        String response = mockMvc.perform(get("/activecard/" + matchId))
                .andDo(print())
                .andExpect(status().is(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, JsonCard.class);
    }

    private void getActiveCardFailing(UUID matchId) throws Exception {
        mockMvc.perform(get("/activecard/" + matchId))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    private List<JsonCard> getPlayerHand(UUID matchId) throws Exception {
        String response = mockMvc.perform(get("/playerhand/" + matchId))
                .andDo(print())
                .andExpect(status().is(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, new TypeReference<List<JsonCard>>() {});
    }

    private void getPlayerHandFailing(UUID matchId) throws Exception {
        mockMvc.perform(get("/playerhand/" + matchId))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}