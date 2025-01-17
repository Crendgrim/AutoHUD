package mod.crend.autohud.component;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.config.ConfigHandler;
import mod.crend.autohud.component.state.ComponentState;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.autohud.render.ComponentRenderer;
import mod.crend.libbamboo.VersionUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
//? if <1.21.2
import net.minecraft.item.Equipment;
//? if >=1.20.5
/*import net.minecraft.registry.entry.RegistryEntry;*/

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class Component {
    private static final List<Component> mainHudComponents = new ArrayList<>();

    //? if <1.20.5 {
    private static final Map<StatusEffect, Component> statusEffectComponents = new HashMap<>();
    //?} else {
    /*private static final Map<RegistryEntry<StatusEffect>, Component> statusEffectComponents = new HashMap<>();
     *///?}
    private static final List<Component> components = new ArrayList<>();

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
        components.forEach(Component::tick);
        statusEffectComponents.values().forEach(Component::tick);
        Components.ChatIndicator.tick();
    }

    private Component(
            Identifier identifier,
            Supplier<Boolean> isTargeted,
            ConfigHandler.IComponent config,
            final List<Component> stackComponents,
            boolean inMainHud,
            boolean register,
            Function<ClientPlayerEntity, ComponentState> stateProvider
    ) {
        this.identifier = identifier;
        this.isTargeted = isTargeted;
        this.config = config;
        this.stackComponents = new ArrayList<>(stackComponents);
        this.inMainHud = inMainHud;
        if (inMainHud) {
            mainHudComponents.add(this);
        }
        this.stateProvider = stateProvider;
        if (register) registerComponent(this);
    }

    public static Builder builder(String name) {
        return builder(VersionUtils.getIdentifier(AutoHud.MOD_ID, name));
    }
    public static Builder builder(String path, String name) {
        return builder(VersionUtils.getIdentifier(path, name));
    }
    public static Builder builder(Identifier identifier) {
        return new Builder(identifier);
    }

    public static class Builder {
        Identifier identifier;
        Supplier<Boolean> isTargeted = () -> true;
        ConfigHandler.IComponent config = ConfigHandler.None;
        List<Component> stackComponents = List.of();
        boolean inMainHud = false;
        boolean register = true;
        private Function<ClientPlayerEntity, ComponentState> stateProvider;

        private Builder(Identifier identifier) {
            this.identifier = identifier;
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

        public Builder state(Function<ClientPlayerEntity, ComponentState> stateProvider) {
            this.stateProvider = stateProvider;
            return this;
        }

        public Builder register(boolean register) {
            this.register = register;
            return this;
        }

        public Component build() {
            return new Component(identifier, isTargeted, config, stackComponents, inMainHud, register, stateProvider);
        }
    }

    public static void registerStatusEffect(/*? if <1.20.5 {*/StatusEffect/*?} else {*//*RegistryEntry<StatusEffect>*//*?}*/ effect) {
        if (!statusEffectComponents.containsKey(effect)) {
            Component c = builder(effect/*? if >=1.20.5 {*//*.value()*//*?}*/.getTranslationKey())
                    .config(AutoHud.config.statusEffects())
                    .isTargeted(() -> AutoHud.targetStatusEffects)
                    // Don't register as a main component
                    .register(false)
                    .build();
            c.offset = 1.0d;
            c.alpha = 0.0d;
            statusEffectComponents.put(effect, c);
            ComponentRenderer.registerStatusEffectComponent(c);
        }
    }
    public static Component get(/*? if <1.20.5 {*/StatusEffect/*?} else {*//*RegistryEntry<StatusEffect>*//*?}*/ effect) {
        registerStatusEffect(effect);
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
    private final Function<ClientPlayerEntity, ComponentState> stateProvider;
    private final List<Component> stackComponents;
    public double alpha = 1;
    public double alphaDelta = 0;
    public double offset = 0;
    public double offsetDelta = 0;
    public final Identifier identifier;
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
    public boolean shouldRender() {
        if (!config.active()) return true;
        if (AutoHud.config.animationFade()) {
            return !fullyHidden() || config.maximumFade() > 0;
        }
        return !fullyHidden();
    }

    // This method is used to ensure that linked components start their hide animation at the same time
    private void keepRevealed(float time) {
        if (isActive() && visibleTime > 0 && visibleTime < time) {
            visibleTime = time;
        }
    }

    private void synchronize(float newVisibleTime, double newOffset, double newAlpha, double newOffsetDelta, double newAlphaDelta, Component... components) {
        for (Component component : components) {
            newVisibleTime = Math.max(newVisibleTime, component.visibleTime);
            if (newOffset >= component.offset) {
                // we would be more visible next tick than the original
                if (offset + offsetDelta < component.offset + component.offsetDelta) {
                    newOffsetDelta = component.offset + component.offsetDelta - offset;
                } else {
                    newOffsetDelta = component.offsetDelta;
                }
                newOffset = component.offset;
            }
            if (newAlpha <= component.alpha) {
                // we would be more visible next tick than the original
                if (alpha + alphaDelta > component.alpha + component.alphaDelta) {
                    newAlphaDelta = alpha - (component.alpha + component.alphaDelta);
                } else {
                    newAlphaDelta = component.alphaDelta;
                }
                newAlpha = component.alpha;
            }
        }
        visibleTime = newVisibleTime;
        offset = newOffset;
        alpha = newAlpha;
        offsetDelta = newOffsetDelta;
        alphaDelta = newAlphaDelta;
    }
    public void synchronizeFromHidden(Component... components) {
        synchronize(0, 1.0d, 0.0d, 0d, 0d, components);
    }
    public void synchronizeFrom(Component... components) {
        synchronize(visibleTime, offset, alpha, offsetDelta, alphaDelta, components);
    }
    /*
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
    }*/
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

    public void initState(ClientPlayerEntity player) {
        if (stateProvider != null) this.state = stateProvider.apply(player);
    }
    public void updateState() {
        if (state != null) {
            state.update();
        }
    }
    public void updateStateNextTick() {
        if (state != null) {
            state.updateNextTick();
        }
    }

    public boolean isMoreVisibleThan(Component other) {
        return this.offset < other.offset;
    }

    public void tick() {
        if (state != null) {
            state.tick();
        }
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
