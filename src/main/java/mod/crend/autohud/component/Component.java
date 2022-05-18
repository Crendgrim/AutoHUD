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
    public static Component ExperienceBar = new Component("ExperienceBar", AutoHud.config.experience(), List.of(Health, Hunger, MountHealth));
    public static Component Hotbar = new Component("Hotbar", AutoHud.config.hotbar(), List.of(ExperienceBar));
    public static Component Tooltip = new Component("Tooltip", AutoHud.config.hotbar());
    public static Component Scoreboard = new Component("Scoreboard", AutoHud.config.scoreboard());

    private static final Map<StatusEffect, Component> statusEffectComponents = new HashMap<>();
    private static final List<Component> components = new ArrayList<>(List.of(
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
    ));
    public static void registerComponent(Component component) {
        components.add(component);
    }

    public static void revealAll() {
        components.forEach(Component::revealCombined);
        statusEffectComponents.values().forEach(Component::revealCombined);
    }
    public static void hideAll() {
        components.forEach(Component::hide);
        statusEffectComponents.values().forEach(Component::hide);
    }
    public static void tickAll() {
        components.forEach(c -> {
            if (c.state != null) c.state.tick();
        });
    }

    public Component(String name, Config.IComponent config) {
        this(name, config, List.of());
    }
    public Component(String name, Config.IComponent config, final List<Component> stackComponents) {
        this.name = name;
        this.config = config;
        this.stackComponents = new ArrayList<>(stackComponents);
    }
    public Component(String name) {
        this(name, List.of());
    }
    public Component(String name, final List<Component> stackComponents) {
        this(name, Config.None, stackComponents);
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

    protected final Config.IComponent config;
    public ComponentState state = null;
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

    /**
     * Set the current state to hidden and begin reveal process.
     *
     * This differs from regular reveal() in that it forces the
     * reveal animation to play out.
     */
    public void revealFromHidden() {
        delta = config.values.distance();
        reveal();
    }

    /**
     * Reveal the component. If not revealed yet, starts an animation.
     */
    public void reveal() {
        visibleTime = AutoHud.config.timeRevealed();
    }

    /**
     * Reveal the component, and, depending on config, also all
     * others that should be revealed:
     * - COMBINED: force all components to be revealed
     * - HIDE_COMBINED: force all already revealed components to stay revealed
     * - STACKED: force all currently revealed components whose animation path
     *            intersects this component to stay revealed. This ensures that
     *            e.g. the health bar does not move through the item hotbar.
     */
    public void revealCombined() {
        visibleTime = AutoHud.config.timeRevealed();
        if (config.active() && AutoHud.config.revealType() == RevealType.COMBINED) {
            components.forEach(c -> c.visibleTime = Math.max(c.visibleTime, visibleTime));
        } else if (config.active() && AutoHud.config.revealType() == RevealType.HIDE_COMBINED) {
            components.forEach(c -> c.keepRevealed(visibleTime));
        } else if (config.active() && AutoHud.config.revealType() == RevealType.STACKED) {
            keepRevealedStacked(visibleTime);
        }
    }
    private void keepRevealedStacked(float visibleTime) {
        keepRevealed(visibleTime);
        stackComponents.forEach(c -> {
            c.keepRevealedStacked(visibleTime);
        });
    }
    public void addStackComponent(Component component) {
        stackComponents.add(component);
    }

    /**
     * Force the component to be revealed now, without an animation.
     */
    public void revealNow() {
        visibleTime = AutoHud.config.timeRevealed();
        delta = 0;
    }

    /**
     * Hides the component. If not hidden yet, starts an animation.
     */
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

    // This method is used to ensure that linked components start their hide animation at the same time
    private void keepRevealed(float time) {
        if (config.active() && visibleTime > 0 && visibleTime < time) {
            visibleTime = time;
        }
    }

    /* Maths to ensure a more visually interesting speed curve for the animation.
     * Speeds up until half way, then slows down.
     * This causes the animation to feel smoother.
     */
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
        if (visibleTime == 0) { // hide component
            if (fullyHidden()) { // component is fully hidden, keep it in place
                speed = 0;
            } else { // component is not yet fully hidden, animate
                moveOut(tickDelta);
            }
        } else if (fullyRevealed()) { // show component, fully revealed, keep it in place
            speed = 0;
        } else { // show component, not yet fully revealed, animate
            moveIn(tickDelta);
        }
        if (config.active() && Hud.actDynamic()) {
            // this is where the component gets "ticked" to ensure smooth start to hide animation
            visibleTime = Math.max(0, visibleTime - tickDelta);
        }
    }
}
