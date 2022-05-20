package mod.crend.autohud.component;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.config.ConfigHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.effect.StatusEffect;

import java.util.*;

public class Component {
    private static final List<Component> mainHudComponents = new ArrayList<>();

    public static Component Armor = new Component("Armor", AutoHud.config.armor(), true);
    public static Component Health = new Component("Health", AutoHud.config.health(), List.of(Armor), true);
    public static Component Air = new Component("Air", AutoHud.config.air(), true);
    public static Component Hunger = new Component("Hunger", AutoHud.config.hunger(), List.of(Air), true);
    public static Component MountHealth = new Component("MountHealth", AutoHud.config.mountHealth(), List.of(Air), true);
    public static Component MountJumpBar = new Component("MountJumpBar", AutoHud.config.mountJumpBar(), true);
    public static Component ExperienceBar = new Component("ExperienceBar", AutoHud.config.experience(), List.of(Health, Hunger, MountHealth), true);
    public static Component Hotbar = new Component("Hotbar", AutoHud.config.hotbar(), List.of(ExperienceBar), true);
    public static Component Tooltip = new Component("Tooltip", AutoHud.config.hotbar(), true);
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

    public Component(String name, ConfigHandler.IComponent config, final List<Component> stackComponents, boolean inMainHud) {
        this.name = name;
        this.config = config;
        this.stackComponents = new ArrayList<>(stackComponents);
        this.inMainHud = inMainHud;
        if (inMainHud) {
            mainHudComponents.add(this);
        }
    }
    public Component(String name) {
        this(name, List.of(), false);
    }
    public Component(String name, boolean inMainHud) {
        this(name, List.of(), inMainHud);
    }
    public Component(String name, ConfigHandler.IComponent config) {
        this(name, config, List.of(), false);
    }
    public Component(String name, ConfigHandler.IComponent config, boolean inMainHud) {
        this(name, config, List.of(), inMainHud);
    }
    public Component(String name, final List<Component> stackComponents) {
        this(name, ConfigHandler.None, stackComponents, false);
    }
    public Component(String name, final List<Component> stackComponents, boolean inMainHud) {
        this(name, ConfigHandler.None, stackComponents, inMainHud);
    }
    public Component(String name, ConfigHandler.IComponent config, final List<Component> stackComponents) {
        this(name, config, stackComponents, false);
    }

    public static void register(StatusEffect effect) {
        if (!statusEffectComponents.containsKey(effect)) {
            Component c = new Component(effect.getTranslationKey(), AutoHud.config.statusEffects());
            c.delta = c.config.distance();
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

    protected final ConfigHandler.IComponent config;
    public ComponentState state = null;
    private final List<Component> stackComponents;
    private double delta = 0;
    private double speed = 0;
    private final String name;
    private final boolean inMainHud;
    private float visibleTime = 1;

    public double getDeltaX() {
        return switch (config.direction()) {
            case Up, Down -> 0;
            case Left -> -delta * config.distance();
            case Right -> delta * config.distance();
        };
    }
    public double getDeltaY() {
        return switch (config.direction()) {
            case Up -> -delta * config.distance();
            case Down -> delta * config.distance();
            case Left, Right -> 0;
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
        delta = 1.0;
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
     * - Combined: force all components to be revealed
     * - HideCombined: force all already revealed components to stay revealed
     * - Stacked: force all currently revealed components whose animation path
     *            intersects this component to stay revealed. This ensures that
     *            e.g. the health bar does not move through the item hotbar.
     * - Grouped: force all components to be revealed that are logically grouped.
     */
    public void revealCombined() {
        visibleTime = AutoHud.config.timeRevealed();
        if (config.active()) {
            switch (AutoHud.config.revealType()) {
                case Combined -> components.forEach(c -> c.visibleTime = Math.max(c.visibleTime, visibleTime));
                case HideCombined -> components.forEach(c -> c.keepRevealed(visibleTime));
                case Stacked -> keepRevealedStacked(visibleTime);
                case Grouped -> revealGrouped(visibleTime);
                case Individual -> {}
            }
        }
    }
    private void revealGrouped(float visibleTime) {
        if (inMainHud) {
            mainHudComponents.forEach(c -> c.visibleTime = Math.max(c.visibleTime, visibleTime));
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
        // Check next tick whether we actually want to hide.
        // This ensures that components that are set to always-show do not get hidden indefinitely.
        if (state != null) {
            state.updateNextTick();
        }
    }
    private boolean fullyRevealed() {
        return delta == 0;
    }
    public boolean fullyHidden() {
        return delta == 1;
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
        if (delta > 0.5) speed -= AutoHud.config.animationSpeed() * config.speedMultiplier() * tickDelta * 0.005;
        else speed += AutoHud.config.animationSpeed() * config.speedMultiplier() * tickDelta * 0.005;
        speed = Math.min(0.5, Math.max(0.0125, speed));
    }
    private void moveIn(float tickDelta) {
        // use negative multiplier in argument to reverse speed curve
        speedDelta(-tickDelta);
        delta = Math.max(0, delta - speed * tickDelta);
    }
    private void moveOut(float tickDelta) {
        speedDelta(tickDelta);
        delta = Math.min(1, delta + speed * tickDelta);
    }
    public void revealIf(boolean trigger) {
        if (!config.active() || config.onChange() && trigger) {
            revealCombined();
        }
    }

    public void updateState() {
        if (state != null) {
            state.update();
        }
    }

    public void render(float tickDelta) {
        if (visibleTime == 0) { // hide component
            if (fullyHidden()) { // component is fully hidden, keep it in place
                speed = 0;
            } else if (state == null || !state.scheduledUpdate()) { // component is not yet fully hidden, animate unless update scheduled
                moveOut(tickDelta);
            }
        } else if (fullyRevealed()) { // show component, fully revealed, keep it in place
            speed = 0;
        } else { // show component, not yet fully revealed, animate
            moveIn(tickDelta);
        }
        if (config.active() && Hud.actDynamic() && visibleTime > 0) {
            // this is where the component gets "ticked" to ensure smooth start to hide animation
            visibleTime = Math.max(0, visibleTime - tickDelta);
            // if we would unhide next tick, update state again to make sure
            if (visibleTime == 0 && state != null) {
                state.updateNextTick();
            }
        }
    }
}
