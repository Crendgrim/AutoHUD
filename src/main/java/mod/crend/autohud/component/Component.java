package mod.crend.autohud.component;

import mod.crend.autohud.AutoHud;

import java.util.Arrays;
import java.util.List;

public class Component {

    public static Component Hotbar = new Component(ScrollDirection.DOWN);
    public static Component Tooltip = new Component(ScrollDirection.DOWN);
    public static Component ExperienceBar = new Component(ScrollDirection.DOWN);
    public static Component Armor = new Component(ScrollDirection.DOWN);
    public static Component Health = new Component(ScrollDirection.DOWN);
    public static Component Hunger = new Component(ScrollDirection.DOWN);
    public static Component Air = new Component(ScrollDirection.DOWN);
    public static Component MountHealth = new Component(ScrollDirection.DOWN);
    public static Component MountJumpBar = new Component(ScrollDirection.DOWN);
    public static Component Scoreboard = new Component(ScrollDirection.RIGHT);
    public static Component StatusEffects = new Component(ScrollDirection.UP);

    private static final List<Component> components = Arrays.asList(
            Hotbar,
            Tooltip,
            ExperienceBar,
            Armor,
            Health,
            Hunger,
            Air,
            MountHealth,
            MountJumpBar,
            Scoreboard,
            StatusEffects
    );

    public static void revealAll() {
        components.forEach(Component::reveal);
    }
    public static void hideAll() {
        components.forEach(Component::hide);
    }

    Component(ScrollDirection direction) {
        this(direction, AutoHud.config.animationSpeed());
    }
    Component(ScrollDirection direction, double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
        this.direction = direction;
        this.bound = switch(direction) {
            case UP, DOWN -> 60;
            case RIGHT -> 100;
        };
    }

    private final ScrollDirection direction;

    private final int bound;
    private double delta = 0;
    private double speed = 0;
    private final double speedMultiplier;

    private float visibleTime = 1;

    public double getDeltaX() {
        return switch (direction) {
            case UP, DOWN -> 0;
            case RIGHT -> delta;
        };
    }
    public double getDeltaY() {
        return switch (direction) {
            case UP -> -delta;
            case DOWN -> delta;
            case RIGHT -> 0;
        };
    }

    public boolean isHidden() {
        return !fullyRevealed();
    }
    public void reveal() {
        reveal(AutoHud.config.timeRevealed());
    }
    public void reveal(int time) {
        visibleTime = time;
    }
    public void hide() {
        visibleTime = 0;
    }
    private boolean fullyRevealed() {
        return delta == 0;
    }
    private boolean fullyHidden() {
        return delta == bound;
    }

    private void speedDelta(float tickDelta) {
        if (delta > bound / 2.0) speed -= speedMultiplier * tickDelta;
        else speed += speedMultiplier * tickDelta;
        speed = Math.max(0.75, speed);
    }
    private void moveIn(float tickDelta) {
        // use negative multiplier in argument to reverse speed curve
        speedDelta(-tickDelta);
        delta = Math.max(0, delta - speed * tickDelta);
    }
    private void moveOut(float tickDelta) {
        speedDelta(tickDelta);
        delta = Math.min(bound, delta + speed * tickDelta);
    }
    public void render(float tickDelta) {
        if (visibleTime == 0) {
            if (fullyHidden()) {
                speed = 0;
            } else {
                moveOut(tickDelta);
            }
        } else if (fullyRevealed()) {
            speed = 0;
        } else {
            moveIn(tickDelta);
        }
        if (Hud.isDynamic()) {
            visibleTime = Math.max(0, visibleTime - tickDelta);
        }
    }

}
