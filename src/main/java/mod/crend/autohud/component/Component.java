package mod.crend.autohud.component;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.config.ConfigHandler;
import mod.crend.autohud.component.state.ComponentState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.*;
import java.util.function.Supplier;

public class Component {
    private static final List<Component> mainHudComponents = new ArrayList<>();
    public static final Supplier<Boolean> TARGET_STATUS_BARS = () -> AutoHud.targetStatusBars;
    public static final Supplier<Boolean> TARGET_EXPERIENCE_BAR = () -> AutoHud.targetExperienceBar;
    public static final Supplier<Boolean> TARGET_SCOREBOARD = () -> AutoHud.targetScoreboard;
    public static final Supplier<Boolean> TARGET_HOTBAR = () -> AutoHud.targetHotbar;
    public static final Supplier<Boolean> TARGET_CROSSHAIR = () -> AutoHud.targetCrosshair;
    public static final Supplier<Boolean> TARGET_CHAT = () -> AutoHud.targetChat;

    public static Component Armor = builder("Armor").isTargeted(TARGET_STATUS_BARS).config(AutoHud.config.armor()).inMainHud().build();
    public static Component Health = builder("Health").isTargeted(TARGET_STATUS_BARS).config(AutoHud.config.health()).stackComponents(Armor).inMainHud().build();
    public static Component Air = builder("Air").isTargeted(TARGET_STATUS_BARS).config(AutoHud.config.air()).inMainHud().build();
    public static Component Hunger = builder("Hunger").isTargeted(TARGET_STATUS_BARS).config(AutoHud.config.hunger()).stackComponents(Air).inMainHud().build();
    public static Component MountHealth = builder("MountHealth").isTargeted(TARGET_STATUS_BARS).config(AutoHud.config.mountHealth()).stackComponents(Air).inMainHud().build();
    public static Component MountJumpBar = builder("MountJumpBar").isTargeted(TARGET_STATUS_BARS).config(AutoHud.config.mountJumpBar()).inMainHud().build();
    public static Component ExperienceLevel = builder("Experience").isTargeted(TARGET_EXPERIENCE_BAR).config(AutoHud.config.experience()).inMainHud().build();
    public static Component ExperienceBar = builder("ExperienceBar").isTargeted(TARGET_EXPERIENCE_BAR).config(AutoHud.config.experienceBar()).stackComponents(Health, Hunger, MountHealth, ExperienceLevel).inMainHud().build();
    public static Component Hotbar = builder("Hotbar").isTargeted(TARGET_HOTBAR).config(AutoHud.config.hotbar()).stackComponents(ExperienceBar).inMainHud().build();
    public static Component Tooltip = builder("Tooltip").isTargeted(TARGET_HOTBAR).config(AutoHud.config.hotbar()).inMainHud().build();
    public static Component Scoreboard = builder("Scoreboard").isTargeted(TARGET_SCOREBOARD).config(AutoHud.config.scoreboard()).build();
    public static Component Crosshair = builder("Crosshair").isTargeted(TARGET_CROSSHAIR).config(AutoHud.config.crosshair()).build();
    public static Component Chat = builder("Chat").isTargeted(TARGET_CHAT).config(AutoHud.config.chat()).build();
    public static Component ChatIndicator = builder("ChatIndicator").isTargeted(TARGET_CHAT).config(AutoHud.config.chatIndicator()).build();
    public static Component ActionBar = builder("ActionBar").config(AutoHud.config.actionBar()).build();
    public static Component BossBar = builder("BossBar").config(AutoHud.config.bossBar()).build();

    //? if <1.20.5 {
    private static final Map<StatusEffect, Component> statusEffectComponents = new HashMap<>();
    //?} else {
    /*private static final Map<RegistryEntry<StatusEffect>, Component> statusEffectComponents = new HashMap<>();
     *///?}
    private static final List<Component> components = new ArrayList<>(List.of(
            Hotbar,
            Tooltip,
            ExperienceBar,
            ExperienceLevel,
            Armor,
            Health,
            Hunger,
            Air,
            MountHealth,
            MountJumpBar,
            Scoreboard,
            Crosshair,
            Chat,
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
    public static void revealAll(float visibleTime) {
        components.forEach(component -> component.revealCombined(visibleTime));
        statusEffectComponents.values().forEach(component -> component.revealCombined(visibleTime));
    }
    public static void forceHideAll() {
        components.forEach(Component::forceHide);
    }
    public static void updateAll() {
        components.forEach(component -> {
            if (component.state != null) component.state.updateNextTick();
        });
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
        if (ChatIndicator.state != null) {
            ChatIndicator.state.tick();
            ChatIndicator.tick();
        }
    }

    private Component(String name, Supplier<Boolean> isTargeted, ConfigHandler.IComponent config, final List<Component> stackComponents, boolean inMainHud) {
        this.name = name;
        this.isTargeted = isTargeted;
        this.config = config;
        this.stackComponents = new ArrayList<>(stackComponents);
        this.inMainHud = inMainHud;
        if (inMainHud) {
            mainHudComponents.add(this);
        }
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder {
        String name;
        Supplier<Boolean> isTargeted = () -> true;
        ConfigHandler.IComponent config = ConfigHandler.None;
        List<Component> stackComponents = List.of();
        boolean inMainHud = false;

        private Builder(String name) {
            this.name = name;
        }

        public Builder isTargeted(Supplier<Boolean> isTargeted) {
            this.isTargeted = isTargeted;
            return this;
        }

        public Builder config(ConfigHandler.IComponent config) {
            this.config = config;
            return this;
        }

        public Builder stackComponents(Component... stackComponents) {
            return stackComponents(Arrays.stream(stackComponents).toList());
        }

        public Builder stackComponents(List<Component> stackComponents) {
            this.stackComponents = stackComponents;
            return this;
        }

        public Builder inMainHud() {
            return inMainHud(true);
        }

        public Builder inMainHud(boolean inMainHud) {
            this.inMainHud = inMainHud;
            return this;
        }

        public Component build() {
            return new Component(name, isTargeted, config, stackComponents, inMainHud);
        }
    }

    public static void register(/*? if <1.20.5 {*/StatusEffect/*?} else {*//*RegistryEntry<StatusEffect>*//*?}*/ effect) {
        if (!statusEffectComponents.containsKey(effect)) {
            Component c = builder(effect/*? if >=1.20.5 {*//*.value()*//*?}*/.getTranslationKey()).config(AutoHud.config.statusEffects()).build();
            c.offset = 1.0d;
            c.alpha = 0.0d;
            statusEffectComponents.put(effect, c);
        }
    }
    public static Component get(/*? if <1.20.5 {*/StatusEffect/*?} else {*//*RegistryEntry<StatusEffect>*//*?}*/ effect) {
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
        for (var effect : statusEffectComponents.keySet()) {
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
    public final String name;
    private final Supplier<Boolean> isTargeted;
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

    public boolean isActive() {
        return config.active() && isTargeted.get();
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
        revealCombined(AutoHud.config.timeRevealed());
    }
    public void revealCombined(float visibleTime) {
        this.visibleTime = visibleTime;
        if (isActive()) {
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
        if (!isActive()) return;
        visibleTime = 0;
        // Check next tick whether we actually want to hide.
        // This ensures that components that are set to always-show do not get hidden indefinitely.
        if (state != null) {
            state.updateNextTick();
        }
    }
    public void forceHide() {
        if (!isActive()) return;
        visibleTime = 0;
    }
    public boolean fullyRevealed() {
        return offset == 0 && alpha == 1;
    }
    public boolean fullyHidden() {
        return offset == 1 && alpha == 0;
    }

    // This method is used to ensure that linked components start their hide animation at the same time
    private void keepRevealed(float time) {
        if (isActive() && visibleTime > 0 && visibleTime < time) {
            visibleTime = time;
        }
    }

    public void synchronizeFrom(Component a, Component b) {
        offset = Math.min(offset, Math.min(a.offset, b.offset));
        alpha = Math.max(alpha, Math.max(a.alpha, b.alpha));
        visibleTime = Math.max(visibleTime, Math.max(a.visibleTime, b.visibleTime));
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
        double offsetSpeed = Math.sqrt(0.01 + offset) * 0.1 * AutoHud.config.animationSpeedMoveIn() * config.speedMultiplier();
        double alphaSpeed = 0.05 * AutoHud.config.animationSpeedFadeIn() * config.speedMultiplier();
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
        double offsetSpeed = Math.sqrt(0.01 + offset) * 0.1 * AutoHud.config.animationSpeedMoveOut() * config.speedMultiplier();
        double alphaSpeed = 0.05 * AutoHud.config.animationSpeedFadeOut() * config.speedMultiplier();
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
        if (!isActive() || config.onChange() && trigger) {
            revealCombined();
        }
    }

    public void updateState() {
        if (state != null) {
            state.update();
        }
    }

    public boolean isMoreVisibleThan(Component other) {
        return this.offset < other.offset;
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
        if (isActive() && Hud.actDynamic() && visibleTime > 0) {
            // this is where the component gets "ticked" to ensure smooth start to hide animation
            visibleTime = Math.max(0, visibleTime - 1);
            // if we would unhide next tick, update state again to make sure
            if (visibleTime == 0 && state != null) {
                state.updateNextTick();
            }
        }
    }
}
