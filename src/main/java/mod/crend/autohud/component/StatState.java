package mod.crend.autohud.component;

import mod.crend.autohud.config.RevealPolicy;

public class StatState {
    private int current;
    private final int max;
    private final Component component;

    public StatState(Component component, int initial, int max) {
        this.component = component;
        this.current = initial;
        this.max = max;
    }

    public void changeConditional(int newValue, float tickDelta, RevealPolicy revealPolicy) {
        boolean doReveal = switch (revealPolicy) {
            case NotFull -> (newValue < max);
            case Low -> (newValue <= max / 3);
            case Increasing -> (newValue > current);
            case Decreasing -> (newValue < current);
            case Changing -> (newValue != current);
            case Disabled -> !Hud.isDynamic();
            case Always -> true;
        };
        if (doReveal) {
            component.reveal();
        }
        component.render(tickDelta);
        current = newValue;
    }
}
