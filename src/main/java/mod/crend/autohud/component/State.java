package mod.crend.autohud.component;

import mod.crend.autohud.AutoHud;
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

    public void tick(ClientPlayerEntity player) {
        if (player == null) return;

        Component.Hotbar.revealIf(previousStack != player.getMainHandStack());
        Component.Tooltip.revealIf(previousStack != player.getMainHandStack());
        previousStack = player.getMainHandStack();

        health.changeConditional((int) player.getHealth(), AutoHud.config.health().policy());
        food.changeConditional(player.getHungerManager().getFoodLevel(), AutoHud.config.hunger().policy());
        armor.changeConditional(player.getArmor(), AutoHud.config.armor().policy());
        air.changeConditional(player.getAir(), AutoHud.config.air().policy());

        Component.ExperienceBar.revealIf(previousExperience != player.totalExperience);
        previousExperience = player.totalExperience;

        Component.Scoreboard.revealIf(false);

        if (AutoHud.config.statusEffects().active()) {
            if (AutoHud.config.statusEffects().onChange()) {
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
                            Component.get(effect).reveal();
                        }
                        newStatusEffects.put(effect, new StatusEffectInstance(effectInstance));
                    }
                }
                previousStatusEffects = newStatusEffects;
            }
        } else {
            Component.getStatusEffectComponents().forEach(Component::reveal);
        }
    }
    public void render(float tickDelta) {
        Component.getComponents().forEach(c -> c.render(tickDelta));
        Component.getStatusEffectComponents().forEach(c -> c.render(tickDelta));
    }
}
