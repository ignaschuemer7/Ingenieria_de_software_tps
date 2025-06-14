package org.udesa.uno.model;

import java.util.Objects;
import java.util.Set;

public abstract class ColoredCard extends Card {
    public static final String invalidColor = "Invalid color";
    protected String color = "";

    private static final Set<String> VALID_COLORS = Set.of("Red", "Yellow", "Green", "Blue");

    public ColoredCard(String aColor) {
        if (!VALID_COLORS.contains(aColor)) {
            throw new IllegalArgumentException(invalidColor);
        }
        this.color = aColor;
    }
    public boolean acceptsOnTop( Card aCard ) { return  aCard.yourColorIs( color() );   }
    public boolean yourColorIs( String aColor ) { return color.equals( aColor );  }
    public String color() { return color;  }

    public boolean equals( Object o ) { return super.equals( o ) && color.equals( ColoredCard.class.cast( o ).color );  }
    public int hashCode() {             return Objects.hash( color );}

    public JsonCard asJson() { return new JsonCard( color, null, getClass().getSimpleName(), unoShouted() ); }
}
