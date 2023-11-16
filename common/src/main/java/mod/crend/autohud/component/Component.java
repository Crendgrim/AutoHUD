package mod.crend.autohud.component;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.config.ConfigHandler;
import mod.crend.autohud.component.state.ComponentState;
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
    public static Component ActionBar = new Component("ActionBar", AutoHud.config.actionBar());
    public static Component BossBar = new Component("BossBar", AutoHud.config.bossBar());

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
            Scoreboard,
            ActionBar,
            BossBar
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
            if (c.state != null) {
                c.state.tick();
                c.tick();
            }
        });
        statusEffectComponents.values().forEach(Component::tick);
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
            c.offset = 1.0d;
            c.alpha = 0.0d;
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

    public final ConfigHandler.IComponent config;
    public ComponentState state = null;
    private final List<Component> stackComponents;
    private double alpha = 1;
    private double alphaDelta = 0;
    private double offset = 0;
    private double offsetDelta = 0;
    private final String name;
    private final boolean inMainHud;
    private float visibleTime = 1;

    public double getOffsetX(float tickDelta) {
        if (!AutoHud.config.animationMove() && AutoHud.config.animationFade()) return 0;
        return switch (config.direction()) {
            case Up, Down -> 0;
            case Left -> -(offset + tickDelta * offsetDelta) * config.distance();
            case Right -> (offset + tickDelta * offsetDelta) * config.distance();
        };
    }
    public double getOffsetY(float tickDelta) {
        if (!AutoHud.config.animationMove() && AutoHud.config.animationFade()) return 0;
        return switch (config.direction()) {
            case Up -> -(offset + tickDelta * offsetDelta) * config.distance();
            case Down -> (offset + tickDelta * offsetDelta) * config.distance();
            case Left, Right -> 0;
        };
    }
    public float getAlpha(float tickDelta) {
        if (!AutoHud.config.animationFade()) return 1.0f;
        return (float) ((1.0d - config.maximumFade()) * (alpha + tickDelta * alphaDelta) + config.maximumFade());
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
        offset = 1.0d;
        alpha = 0.0d;
        offsetDelta = 0;
        alphaDelta = 0;
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
        offset = 0.0d;
        offsetDelta = 0;
        alpha = 1.0d;
        alphaDelta = 0;
    }

    /**
     * Force the component to be hidden now, without an animation.
     */
    public void hideNow() {
        visibleTime = 0;
        offset = 1.0d;
        alpha = 0.0d;
        offsetDelta = 0;
        alphaDelta = 0;
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
        return offset == 0 && alpha == 1;
    }
    public boolean fullyHidden() {
        return offset == 1 && alpha == 0;
    }

    // This method is used to ensure that linked components start their hide animation at the same time
    private void keepRevealed(float time) {
        if (config.active() && visibleTime > 0 && visibleTime < time) {
            visibleTime = time;
        }
    }

    public void synchronizeFrom(Component other) {
        offset = Math.min(offset, other.offset);
        alpha = Math.max(alpha, other.alpha);
        if (other.offsetDelta < 0) {
            offsetDelta = Math.min(offsetDelta, other.offsetDelta);
            alphaDelta = Math.max(alphaDelta, other.alphaDelta);
        } else {
            offsetDelta = Math.max(offsetDelta, other.offsetDelta);
            alphaDelta = Math.min(alphaDelta, other.alphaDelta);
        }
        visibleTime = Math.max(visibleTime, other.visibleTime);
    }
    private void moveIn() {
        offset = Math.max(0, offset + offsetDelta);
        alpha = Math.min(1, alpha + alphaDelta);
        double offsetSpeed = Math.sqrt(0.01 + offset) * 0.1 * AutoHud.config.animationSpeedMoveIn();
        double alphaSpeed = 0.05 * AutoHud.config.animationSpeedFadeIn();
        if (offset - offsetSpeed <= 0) {
            offsetDelta = -offset;
        } else {
            offsetDelta = -offsetSpeed;
        }
        if (alpha + alphaSpeed >= 1) {
            alphaDelta = 1 - alpha;
        } else {
            alphaDelta = alphaSpeed;
        }
    }
    private void moveOut() {
        offset = Math.min(1, offset + offsetDelta);
        alpha = Math.max(0, alpha + alphaDelta);
        double offsetSpeed = Math.sqrt(0.01 + offset) * 0.1 * AutoHud.config.animationSpeedMoveOut();
        double alphaSpeed = 0.05 * AutoHud.config.animationSpeedFadeOut();
        if (offset + offsetSpeed >= 1) {
            offsetDelta = 1 - offset;
        } else {
            offsetDelta = offsetSpeed;
        }
        if (alpha - alphaSpeed <= 0) {
            alphaDelta = -alpha;
        } else {
            alphaDelta = -alphaSpeed;
        }
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

    public void tick() {
        if (visibleTime == 0) { // hide component
            if (!fullyHidden()) {
                if (state == null || !state.scheduledUpdate()) { // component is not yet fully hidden, animate unless update scheduled
                    if (AutoHud.config.animationNone()) {
                        hideNow();
                    } else {
                        moveOut();
                    }
                }
            }
            if (offset == 1) {
                offsetDelta = 0;
            }
            if (alpha == 0) {
                alphaDelta = 0;
            }
        } else if (!fullyRevealed()) { // show component, not yet fully revealed, animate
            if (AutoHud.config.animationNone()) {
                revealNow();
            } else {
                moveIn();
            }
        } else {
            if (offset == 0) {
                offsetDelta = 0;
            }
            if (alpha == 1) {
                alphaDelta = 0;
            }
        }
        if (config.active() && Hud.actDynamic() && visibleTime > 0) {
            // this is where the component gets "ticked" to ensure smooth start to hide animation
            visibleTime = Math.max(0, visibleTime - 1);
            // if we would unhide next tick, update state again to make sure
            if (visibleTime == 0 && state != null) {
                state.updateNextTick();
            }
        }
    }
}
