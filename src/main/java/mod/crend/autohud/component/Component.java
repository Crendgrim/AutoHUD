package mod.crend.autohud.component;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.config.RevealType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.effect.StatusEffect;

import java.util.*;

public class Component {
    public static Component Hotbar = new Component("Hotbar", ScrollDirection.DOWN);
    public static Component Tooltip = new Component("Tooltip", ScrollDirection.DOWN);
    public static Component ExperienceBar = new Component("ExperienceBar", ScrollDirection.DOWN);
    public static Component Armor = new Component("Armor", ScrollDirection.DOWN);
    public static Component Health = new Component("Health", ScrollDirection.DOWN);
    public static Component Hunger = new Component("Hunger", ScrollDirection.DOWN);
    public static Component Air = new Component("Air", ScrollDirection.DOWN);
    public static Component MountHealth = new Component("MountHealth", ScrollDirection.DOWN);
    public static Component MountJumpBar = new Component("MountJumpBar", ScrollDirection.DOWN);
    public static Component Scoreboard = new Component("Scoreboard", ScrollDirection.RIGHT);

    private static final Map<StatusEffect, Component> statusEffectComponents = new HashMap<>();
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
            Scoreboard
    );

    public static void revealAll() {
        components.forEach(Component::reveal);
        statusEffectComponents.values().forEach(Component::reveal);
    }
    public static void hideAll() {
        components.forEach(Component::hide);
        statusEffectComponents.values().forEach(Component::hide);
    }

    Component(String name, ScrollDirection direction) {
        this(name, direction, AutoHud.config.animationSpeed());
    }
    Component(String name, ScrollDirection direction, double speedMultiplier) {
        this.name = name;
        this.speedMultiplier = speedMultiplier;
        this.direction = direction;
        this.bound = switch(direction) {
            case UP, DOWN -> 60;
            case RIGHT -> 100;
        };
    }

    public static void register(StatusEffect effect) {
        if (!statusEffectComponents.containsKey(effect)) {
            Component c = new Component(effect.getTranslationKey(), ScrollDirection.UP);
            c.delta = c.bound;
            statusEffectComponents.put(effect, c);
        }
    }
    public static Component get(StatusEffect effect) {
        register(effect);
        return statusEffectComponents.get(effect);
    }
    public static Collection<Component> getStatusEffectComponents() {
        return statusEffectComponents.values();
    }
    public static Component findBySprite(Sprite sprite) {
        StatusEffectSpriteManager statusEffectSpriteManager = MinecraftClient.getInstance().getStatusEffectSpriteManager();
        for (StatusEffect effect : statusEffectComponents.keySet()) {
            if (statusEffectSpriteManager.getSprite(effect) == sprite) {
                return statusEffectComponents.get(effect);
            }
        }
        return null;
    }

    private final ScrollDirection direction;
    private final int bound;
    private double delta = 0;
    private double speed = 0;
    private final double speedMultiplier;
    private final String name;
    private boolean active = true;
    private float visibleTime = 1;


    public void enable() {
        active = true;
    }
    public void disable() {
        active = false;
    }

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
    public void revealFromHidden() {
        delta = bound;
        revealThisOnly();
    }
    public void revealThisOnly() {
        visibleTime = AutoHud.config.timeRevealed();
    }
    public void reveal() {
        reveal(AutoHud.config.timeRevealed());
    }
    public void reveal(int time) {
        if (active && AutoHud.config.revealType() == RevealType.COMBINED) {
            components.forEach(c -> c.visibleTime = Math.max(c.visibleTime, time));
        }
        else if (active && AutoHud.config.revealType() == RevealType.HIDE_COMBINED) {
            components.forEach(c -> c.keepRevealed(time));
        }
        visibleTime = time;
    }
    public void hide() {
        if (!active) return;
        visibleTime = 0;
    }
    private boolean fullyRevealed() {
        return delta == 0;
    }
    public boolean fullyHidden() {
        return delta == bound;
    }
    public void keepRevealed() {
        keepRevealed(AutoHud.config.timeRevealed());
    }
    public void keepRevealed(int time) {
        if (active && visibleTime > 0 && visibleTime < time) {
            visibleTime = time;
        }
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
        if (active && Hud.isDynamic()) {
            visibleTime = Math.max(0, visibleTime - tickDelta);
        }
    }
}
