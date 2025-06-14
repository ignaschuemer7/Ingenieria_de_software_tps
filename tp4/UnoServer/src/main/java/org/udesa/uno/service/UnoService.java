package org.udesa.uno.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.udesa.uno.model.Card;
import org.udesa.uno.model.GameStatus;
import org.udesa.uno.model.JsonCard;
import org.udesa.uno.model.Match;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UnoService {
    public static String matchNotFound = "Match not found";
    @Autowired
    private Dealer dealer;
    private Map<UUID, Match> sessions = new HashMap<UUID, Match>();

    public UUID newMatch(List<String> players) {
        UUID newKey = UUID.randomUUID();
        sessions.put(newKey, Match.fullMatch(dealer.fullDeck(), players));
        return newKey;
    }

    private Match getMatch(UUID matchId) {
        Match match = sessions.get(matchId);
        if (match == null) {
            throw new RuntimeException(matchNotFound);
        }
        return match;
    }

    public Match play(UUID matchId, String player, JsonCard card) {
        Match match = getMatch(matchId);
        match.play(player, card.asCard());
        return match;
    }

    public List<Card> playerHand(UUID matchId) {
        return getMatch(matchId).playerHand();
    }

    public Card activeCard(UUID matchId) {
        return getMatch(matchId).activeCard();
    }

    public void drawCard(UUID matchId, String player) {
        getMatch(matchId).drawCard(player);
    }
}