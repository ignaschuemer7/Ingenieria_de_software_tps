package uno;

public abstract class Controller {
    public abstract Player next(Player player);
    public abstract Controller switchController();
}

class RightController extends Controller {
    @Override
    public Player next(Player player) {
        return player.getRightPlayer();
    }

    @Override
    public Controller switchController() {
        return new LeftController();
    }
}

class LeftController extends Controller {
    @Override
    public Player next(Player player) {
        return player.getLeftPlayer();
    }

    @Override
    public Controller switchController() {
        return new RightController();
    }
}
