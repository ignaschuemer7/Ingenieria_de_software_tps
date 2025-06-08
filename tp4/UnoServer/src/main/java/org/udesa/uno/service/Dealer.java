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
        ArrayList<Card> deck = new ArrayList<>();
        //Agegar cartas de color Red
        deck.addAll( cardsOn("Red") );
        //Agregar cartas de color Green
        deck.addAll( cardsOn("Green") );
        //Agregar cartas de color Blue
        deck.addAll( cardsOn("Blue") );
        //Agregar cartas de color Yellow
        deck.addAll( cardsOn("Yellow") );

        //Mezclar
        Collections.shuffle(deck);
        return deck;
    }

    private List<Card> cardsOn(String color) {
        return List.of( new WildCard(), new SkipCard(color),
                        new Draw2Card(color), new ReverseCard(color),
                        new NumberCard(color, 1),
                        new NumberCard(color, 2), new NumberCard(color, 3),
                        new NumberCard(color, 4), new NumberCard(color, 5),
                        new NumberCard(color, 6), new NumberCard(color, 7),
                        new NumberCard(color, 8), new NumberCard(color, 9) );
    }
}
