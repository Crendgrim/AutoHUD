package mod.crend.autohud.component;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.config.Config;
import mod.crend.autohud.config.RevealType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.effect.StatusEffect;

import java.util.*;

public class Component {
    public static Component Armor = new Component("Armor", AutoHud.config.armor());
    public static Component Health = new Component("Health", AutoHud.config.health(), List.of(Armor));
    public static Component Air = new Component("Air", AutoHud.config.air());
    public static Component Hunger = new Component("Hunger", AutoHud.config.hunger(), List.of(Air));
    public static Component MountHealth = new Component("MountHealth", AutoHud.config.mountHealth(), List.of(Air));
    public static Component MountJumpBar = new Component("MountJumpBar", AutoHud.config.mountJumpBar());
    public static Component ExperienceBar = new Component("ExperienceBar", AutoHud.config.experience(), List.of(Health, Armor, Hunger, MountHealth, Air));
    public static Component Hotbar = new Component("Hotbar", AutoHud.config.hotbar(), List.of(Health, Armor, Hunger, MountHealth, Air, ExperienceBar));
    public static Component Tooltip = new Component("Tooltip", AutoHud.config.hotbar());
    public static Component Scoreboard = new Component("Scoreboard", AutoHud.config.scoreboard());

    private static final Map<StatusEffect, Component> statusEffectComponents = new HashMap<>();
    private static final List<Component> components = List.of(
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
        components.forEach(Component::revealCombined);
        statusEffectComponents.values().forEach(Component::revealCombined);
    }
    public static void hideAll() {
        components.forEach(Component::hide);
        statusEffectComponents.values().forEach(Component::hide);
    }

    Component(String name, Config.IComponent config) {
        this(name, config, new ArrayList<>());
    }
    Component(String name, Config.IComponent config, final List<Component> stackComponents) {
        this.name = name;
        this.config = config;
        this.stackComponents = stackComponents;
    }

    public static void register(StatusEffect effect) {
        if (!statusEffectComponents.containsKey(effect)) {
            Component c = new Component(effect.getTranslationKey(), AutoHud.config.statusEffects());
            c.delta = c.config.values.distance();
            statusEffectComponents.put(effect, c);
        }
    }
    public static Component get(StatusEffect effect) {
        register(effect);
        return statusEffectComponents.get(effect);
    }
    public static Collection<Component> getComponents() {
        return components;
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

    private final Config.IComponent config;
    private final List<Component> stackComponents;
    private double delta = 0;
    private double speed = 0;
    private final String name;
    private float visibleTime = 1;

    public double getDeltaX() {
        return switch (config.values.direction()) {
            case UP, DOWN -> 0;
            case LEFT -> -delta;
            case RIGHT -> delta;
        };
    }
    public double getDeltaY() {
        return switch (config.values.direction()) {
            case UP -> -delta;
            case DOWN -> delta;
            case LEFT, RIGHT -> 0;
        };
    }

    public boolean isHidden() {
        return !fullyRevealed();
    }
    public void revealFromHidden() {
        delta = config.values.distance();
        reveal();
    }
    public void reveal() {
        visibleTime = AutoHud.config.timeRevealed();
    }
    public void revealCombined() {
        visibleTime = AutoHud.config.timeRevealed();
        if (config.active() && AutoHud.config.revealType() == RevealType.COMBINED) {
            components.forEach(c -> c.visibleTime = Math.max(c.visibleTime, visibleTime));
        } else if (config.active() && AutoHud.config.revealType() == RevealType.HIDE_COMBINED) {
            components.forEach(c -> c.keepRevealed(visibleTime));
        } else if (config.active() && AutoHud.config.revealType() == RevealType.STACKED) {
            stackComponents.forEach(c -> c.keepRevealed(visibleTime));
        }
    }
    public void revealNow() {
        visibleTime = AutoHud.config.timeRevealed();
        delta = 0;
    }
    public void hide() {
        if (!config.active()) return;
        visibleTime = 0;
    }
    private boolean fullyRevealed() {
        return delta == 0;
    }
    public boolean fullyHidden() {
        return delta == config.values.distance();
    }
    private void keepRevealed(float time) {
        if (config.active() && visibleTime > 0 && visibleTime < time) {
            visibleTime = time;
        }
    }

    private void speedDelta(float tickDelta) {
        if (delta > config.values.distance() / 2.0) speed -= AutoHud.config.animationSpeed() * config.values.speedMultiplier() * tickDelta;
        else speed += AutoHud.config.animationSpeed() * config.values.speedMultiplier() * tickDelta;
        speed = Math.max(0.75, speed);
    }
    private void moveIn(float tickDelta) {
        // use negative multiplier in argument to reverse speed curve
        speedDelta(-tickDelta);
        delta = Math.max(0, delta - speed * tickDelta);
    }
    private void moveOut(float tickDelta) {
        speedDelta(tickDelta);
        delta = Math.min(config.values.distance(), delta + speed * tickDelta);
    }
    public void revealIf(boolean trigger) {
        if (!config.active() || config.onChange() && trigger) {
            revealCombined();
        }
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
        if (config.active() && Hud.isDynamic()) {
            visibleTime = Math.max(0, visibleTime - tickDelta);
        }
    }
}
