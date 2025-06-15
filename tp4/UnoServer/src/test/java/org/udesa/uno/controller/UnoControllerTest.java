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
import org.udesa.uno.model.*;
import org.udesa.uno.service.Dealer;
import org.udesa.uno.service.UnoService;
import org.udesa.uno.service.UnoServiceTest;

@SpringBootTest
    @AutoConfigureMockMvc
    public class UnoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private Dealer dealer;

        private ObjectMapper objectMapper = new ObjectMapper();

        @BeforeEach
        public void beforeEach() {
            when(dealer.fullDeck()).thenReturn(UnoServiceTest.createMockDeck());
        }

        // ========== NEW MATCH TESTS ==========

        @Test
        public void test01CanCreateNewMatchWithValidPlayers() throws Exception {
            String matchId = newMatch("Player1", "Player2");
            assertNotNull(UUID.fromString(matchId));
        }

        @Test
        public void test02CanCreateNewMatchWithMultiplePlayers() throws Exception {
            String matchId = newMatch("Player1", "Player2", "Player3");
            assertNotNull(UUID.fromString(matchId));
        }

        @Test
        public void test03CannotCreateNewMatchWithOnePlayer() throws Exception {
            newMatchFailing("OnlyPlayer");
        }

        @Test
        public void test04CannotCreateNewMatchWithEmptyPlayerName() throws Exception {
            newMatchFailing("ValidPlayer", "");
        }

        @Test
        public void test05NoPlayersParams() throws Exception {
            newMatchFailing();
        }

        // ========== PLAY CARD TESTS ==========

        @Test
        public void test06CanPlayValidCard() throws Exception {
            String matchId = newMatch("Player1", "Player2");
            // From our deterministic deck, Player1 has Red 2 which can be played on Red 1
            JsonCard validCard = new JsonCard("Red", 2, "NumberCard", false);
            playCard(matchId, "Player1", validCard);
        }

        @Test
        public void test07CannotPlayCardWhenNotPlayerTurn() throws Exception {
            String matchId = newMatch("Player1", "Player2");
            JsonCard anyCard = new JsonCard("Red", 2, "NumberCard", false);

            String response = playCardFailingInternal(matchId, "Player2", anyCard);
            assertTrue(response.contains(Player.NotPlayersTurn + "Player2"));
        }

        @Test
        public void test08CannotPlayInvalidCard() throws Exception {
            String matchId = newMatch("Player1", "Player2");
            // Create a card that Player1 doesn't have in hand
            JsonCard invalidCard = new JsonCard("Green", 9, "NumberCard", false);

            String response = playCardFailingInternal(matchId, "Player1", invalidCard);
            assertTrue(response.contains(Match.NotACardInHand + "Player1"));
        }

        @Test
        public void test09CannotPlayInvalidColorCard() throws Exception {
            String matchId = newMatch("Player1", "Player2");
            JsonCard invalidColorCard = new JsonCard("Gray", 99, "NumberCard", false);

            String response = playCardFailingBadRequest(matchId, "Player1", invalidColorCard);
            assertTrue(response.contains(ColoredCard.invalidColor));
        }

        @Test
        public void test10CannotPlayInNonExistentMatch() throws Exception {
            UUID fakeMatchId = UUID.randomUUID();
            JsonCard anyCard = new JsonCard("Red", 5, "NumberCard", false);

            String response = playCardFailingInternal(fakeMatchId.toString(), "Player1", anyCard);
            assertTrue(response.contains(UnoService.matchNotFound));
        }

        @Test
        public void test11CannotPlayInvalidPlayer() throws Exception {
            String matchId = newMatch("Player1", "Player2");
            // From our deterministic deck, Player1 has Red 2 which can be played on Red 1
            JsonCard validCard = new JsonCard("Red", 2, "NumberCard", false);
            String response = playCardFailingInternal(matchId, "InvalidPlayer", validCard);
            assertTrue(response.contains(Player.NotPlayersTurn + "InvalidPlayer"));
        }

        @Test
        public void test11CannotPlayWithMalformedJson() throws Exception {
            String matchId = newMatch("Player1", "Player2");
            String malformedJson = "{\"color\":\"Red\",\"number\":2"; // missing closing brace and fields
            String response = mockMvc.perform(post("/play/" + matchId + "/Player1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(malformedJson))
                    .andDo(print())
                    .andExpect(status().is(500))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            assertTrue(response.contains("Internal Server Error") || response.contains("JSON parsing error"));
        }

        @Test
        public void test12CannotPlayWithMissingFields() throws Exception {
            String matchId = newMatch("Player1", "Player2");
            // Missing 'type' and 'shout' fields
            String incompleteJson = "{\"color\":\"Red\",\"number\":2}";
            String response = mockMvc.perform(post("/play/" + matchId + "/Player1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(incompleteJson))
                    .andDo(print())
                    .andExpect(status().is(404))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            assertTrue(response.contains("JSON Parse Error"));
        }

        // ========== DRAW CARD TESTS ==========

        @Test
        public void test10CanDrawCard() throws Exception {
            String matchId = newMatch("Player1", "Player2");
            List<JsonCard> handBefore = getPlayerHand(matchId);

            drawCard(matchId, "Player1");

            List<JsonCard> handAfter = getPlayerHand(matchId);
            assertEquals(handBefore.size() + 1, handAfter.size());
        }

        @Test
        public void test11CannotDrawCardWhenNotPlayerTurn() throws Exception {
            String matchId = newMatch("Player1", "Player2");

            String response = drawCardFailing(matchId, "Player2");
            assertTrue(response.contains(Player.NotPlayersTurn + "Player2"));
        }

        @Test
        public void test12CannotDrawCardInNonExistentMatch() throws Exception {
            UUID fakeMatchId = UUID.randomUUID();

            String response = drawCardFailing(fakeMatchId.toString(), "Player1");
            assertTrue(response.contains(UnoService.matchNotFound));
        }

        // ========== ACTIVE CARD TESTS ==========

        @Test
        public void test13CanGetActiveCard() throws Exception {
            String matchId = newMatch("Player1", "Player2");

            JsonCard activeCard = getActiveCard(matchId);

            assertNotNull(activeCard);
            assertNotNull(activeCard.getColor());
            assertNotNull(activeCard.getType());
        }

        @Test
        public void test14CannotGetActiveCardFromNonExistentMatch() throws Exception {
            UUID fakeMatchId = UUID.randomUUID();

            getActiveCardFailing(fakeMatchId.toString());
        }

        // ========== PLAYER HAND TESTS ==========

        @Test
        public void test15CanGetPlayerHand() throws Exception {
            String matchId = newMatch("Player1", "Player2");

            List<JsonCard> hand = getPlayerHand(matchId);

            assertNotNull(hand);
            assertEquals(7, hand.size()); // Standard UNO hand size

            // Verify all cards have valid properties
            for (JsonCard card : hand) {
                assertNotNull(card.getColor());
                assertNotNull(card.getType());
            }
        }

        @Test
        public void test16CannotGetPlayerHandFromNonExistentMatch() throws Exception {
            UUID fakeMatchId = UUID.randomUUID();

            getPlayerHandFailing(fakeMatchId.toString());
        }


        // ========== HELPER METHODS ==========

        private String newMatch(String... players) throws Exception {
            StringBuilder urlBuilder = getNewMatchUrlBuilder(players);

            return mockMvc.perform(post(urlBuilder.toString()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString()
                    .replaceAll("\"", ""); // Remove quotes from UUID string
        }

        private void newMatchFailing(String... players) throws Exception {
            StringBuilder urlBuilder = getNewMatchUrlBuilder(players);

            mockMvc.perform(post(urlBuilder.toString()))
                        .andDo(print())
                        .andExpect(status().is(400));
            }

        private static StringBuilder getNewMatchUrlBuilder(String[] players) {
            StringBuilder urlBuilder = new StringBuilder("/newmatch");
            for (int i = 0; i < players.length; i++) {
                urlBuilder.append(i == 0 ? "?" : "&");
                urlBuilder.append("players=").append(players[i]);
            }
            return urlBuilder;
        }

        private void playCard(String matchId, String player, JsonCard card) throws Exception {
            mockMvc.perform(post("/play/" + matchId + "/" + player)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(card)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        private String playCardFailing(String matchId, String player, JsonCard card, int status) throws Exception {
            return mockMvc.perform(post("/play/" + matchId + "/" + player)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(card)))
                    .andDo(print())
                    .andExpect(status().is(status))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
        }

        private String playCardFailingInternal(String matchId, String player, JsonCard card) throws Exception {
            return playCardFailing(matchId, player, card, 500);
        }

        private String playCardFailingBadRequest(String matchId, String player, JsonCard card) throws Exception {
            return playCardFailing(matchId, player, card, 400);
        }

        private void drawCard(String matchId, String player) throws Exception {
            mockMvc.perform(post("/draw/" + matchId + "/" + player))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        private String drawCardFailing(String matchId, String player) throws Exception {
            return mockMvc.perform(post("/draw/" + matchId + "/" + player))
                    .andDo(print())
                    .andExpect(status().is(500))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
        }

        private JsonCard getActiveCard(String matchId) throws Exception {
            String response = mockMvc.perform(get("/activecard/" + matchId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            return objectMapper.readValue(response, JsonCard.class);
        }

        private void getActiveCardFailing(String matchId) throws Exception {
            String response = mockMvc.perform(get("/activecard/" + matchId))
                    .andDo(print())
                    .andExpect(status().is(500))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertTrue(response.contains(UnoService.matchNotFound));
        }

        private List<JsonCard> getPlayerHand(String matchId) throws Exception {
            String response = mockMvc.perform(get("/playerhand/" + matchId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            return objectMapper.readValue(response, new TypeReference<List<JsonCard>>() {});
        }

        private void getPlayerHandFailing(String matchId) throws Exception {
            String response = mockMvc.perform(get("/playerhand/" + matchId))
                    .andDo(print())
                    .andExpect(status().is(500))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertTrue(response.contains(UnoService.matchNotFound));
        }
}