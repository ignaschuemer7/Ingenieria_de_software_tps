package org.udesa.uno.service;

import org.springframework.stereotype.Component;
import org.udesa.uno.model.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

@Component
public class Dealer {

    public List<Card> fullDeck() {
        List<Card> deck = new ArrayList<>();
        String[] colors = { "Red", "Green", "Blue", "Yellow" };
        for (String color : colors) {
            deck.addAll(cardsOn(color));
        }
        // Add 4 WildCards
        for (int i = 0; i < 4; i++) {
            deck.add(new WildCard());
        }
        Collections.shuffle(deck);
        return deck;
    }

    private List<Card> cardsOn(String color) {
        List<Card> cards = new ArrayList<>();
        // One ZeroCard per color
        cards.add(new NumberCard(color, 0));
        // Two NumberCards from 1 to 9 per color
        for (int i = 1; i <= 9; i++) {
            cards.add(new NumberCard(color, i));
            cards.add(new NumberCard(color, i));
        }
        // Two Draw2, Reverse, and Skip cards per color
        for (int i = 0; i < 2; i++) {
            cards.add(new Draw2Card(color));
            cards.add(new ReverseCard(color));
            cards.add(new SkipCard(color));
        }
        return cards;
    }

}
