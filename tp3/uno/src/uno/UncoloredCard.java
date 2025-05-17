package uno;

public abstract class UncoloredCard extends Card {
    @Override
    public boolean matchesColor(Card other) {
        // Comodín sobre la mesa permite cualquier color
        return true;
    }
    @Override
    public boolean matchesNumber(Card other) {
        return false;
    }
    @Override
    public boolean matchesType(Card other) {
        // No coincide por tipo con ninguna otra (se maneja en subclase Wild)
        return false;
    }
}

