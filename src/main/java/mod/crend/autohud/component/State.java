package mod.crend.autohud.component;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.config.RevealPolicy;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class State {

    private ItemStack previousStack;

    private int previousExperience;
    private StatState health;
    private StatState food;
    private StatState armor;
    private StatState air;
    private Map<StatusEffect, StatusEffectInstance> previousStatusEffects;

    public State(ClientPlayerEntity player) {
        previousStack = player.getMainHandStack();
        previousExperience = player.totalExperience;
        previousStatusEffects = new HashMap<>();
        health = new StatState(Component.Health, (int) player.getHealth(), (int) player.getMaxHealth());
        food = new StatState(Component.Hunger, player.getHungerManager().getFoodLevel(), 20);
        armor = new StatState(Component.Armor, player.getArmor(), 20);
        air = new StatState(Component.Air, player.getAir(), player.getMaxAir());
    }

    private void disableIf(boolean condition, Component component) {
        if (condition) component.enable();
        else {
            component.reveal();
            component.disable();
        }
    }
    public void tick() {
        disableIf(AutoHud.config.hideHotbar(), Component.Hotbar);
        disableIf(AutoHud.config.hideHotbar(), Component.Tooltip);
        disableIf(AutoHud.config.onHealthChange() != RevealPolicy.Always, Component.Health);
        disableIf(AutoHud.config.onHungerChange() != RevealPolicy.Always, Component.Hunger);
        disableIf(AutoHud.config.onArmorChange() != RevealPolicy.Always, Component.Armor);
        disableIf(AutoHud.config.onAirChange() != RevealPolicy.Always, Component.Air);
        disableIf(AutoHud.config.hideMount() && AutoHud.config.onMountHealthChange() != RevealPolicy.Always, Component.MountHealth);
        disableIf(AutoHud.config.hideMount() && AutoHud.config.revealOnMountJump(), Component.MountJumpBar);
        disableIf(AutoHud.config.hideExperience(), Component.ExperienceBar);
        disableIf(AutoHud.config.hideScoreboard(), Component.Scoreboard);
        if (AutoHud.config.hideStatusEffects()) {
            Component.getStatusEffectComponents().forEach(Component::enable);
        } else {
            Component.getStatusEffectComponents().forEach(Component::revealThisOnly);
            Component.getStatusEffectComponents().forEach(Component::disable);
        }
    }
    public void render(ClientPlayerEntity player, float tickDelta) {
        if (AutoHud.config.hideHotbar() && (AutoHud.config.revealOnItemChange() && previousStack != player.getMainHandStack())) {
            Component.Hotbar.reveal();
            Component.Tooltip.reveal();
            previousStack = player.getMainHandStack();
        }
        Component.Hotbar.render(tickDelta);
        Component.Tooltip.render(tickDelta);

        health.changeConditional((int) player.getHealth(), tickDelta, AutoHud.config.onHealthChange());
        food.changeConditional(player.getHungerManager().getFoodLevel(), tickDelta, AutoHud.config.onHungerChange());
        armor.changeConditional(player.getArmor(), tickDelta, AutoHud.config.onArmorChange());
        air.changeConditional(player.getAir(), tickDelta, AutoHud.config.onAirChange());

        // These get updated in ClientPlayerEntityMixin
        Component.MountHealth.render(tickDelta);
        Component.MountJumpBar.render(tickDelta);

        if (AutoHud.config.hideExperience() && (AutoHud.config.revealOnExperienceChange() && previousExperience != player.totalExperience)) {
            Component.ExperienceBar.reveal();
            previousExperience = player.totalExperience;
        }
        Component.ExperienceBar.render(tickDelta);

        if (AutoHud.config.hideStatusEffects() && AutoHud.config.revealActiveStatusEffects()) {
            Map<StatusEffect, StatusEffectInstance> newStatusEffects = new HashMap<>();
            Map<StatusEffect, StatusEffectInstance> effects = player.getActiveStatusEffects();
            for (StatusEffect effect : effects.keySet()) {
                StatusEffectInstance effectInstance = effects.get(effect);
                if (effectInstance.shouldShowIcon()) {
                    if (effectInstance.getDuration() < 5) {
                        Component.get(effect).hide();
                    } else if (!previousStatusEffects.containsKey(effect)) {
                        Component.get(effect).revealFromHidden();
                    } else if (!AutoHud.config.hidePersistentStatusEffects() || !previousStatusEffects.get(effect).equals(effectInstance)) {
                        Component.get(effect).revealThisOnly();
                    }
                    newStatusEffects.put(effect, new StatusEffectInstance(effectInstance));
                }
            }
            previousStatusEffects = newStatusEffects;
        }
        Component.getStatusEffectComponents().forEach(c -> c.render(tickDelta));

        Component.Scoreboard.render(tickDelta);
    }
}
