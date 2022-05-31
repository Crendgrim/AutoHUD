package mod.crend.autohud.component;

import mod.crend.autohud.AutoHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class State {

    private Map<StatusEffect, StatusEffectInstance> previousStatusEffects;

    public State(ClientPlayerEntity player) {
        initStates(player);
        previousStatusEffects = new HashMap<>();
    }
   public void initStates(ClientPlayerEntity player) {
        Component.Hotbar.state = new ValueComponentState<>(Component.Hotbar, player::getMainHandStack, true);
        Component.Tooltip.state = new ValueComponentState<>(Component.Tooltip, player::getMainHandStack, true);
        Component.Health.state = new PolicyComponentState(Component.Health, () -> (int) player.getHealth(), () -> (int) player.getMaxHealth());
        Component.Hunger.state = new PolicyComponentState(Component.Hunger, () -> player.getHungerManager().getFoodLevel(), 20);
        Component.Armor.state = new PolicyComponentState(Component.Armor, player::getArmor, 20, true);
        Component.Air.state = new PolicyComponentState(Component.Air, player::getAir, player::getMaxAir);
        Component.ExperienceBar.state = new ValueComponentState<>(Component.ExperienceBar, () -> player.totalExperience, true);
        Component.Scoreboard.state = ScoreboardComponentState.createScoreboardComponent(Component.Scoreboard);

        AutoHud.apis.forEach(api -> api.initState(player));
    }

    private boolean revealHotbarOnDurability(ItemStack itemStack) {
        if (itemStack.isDamageable()
            && itemStack.getDamage() >= (100 - AutoHud.config.getHotbarDurabilityPercentage()) / 100.0 * itemStack.getMaxDamage()
            && (itemStack.getMaxDamage() - itemStack.getDamage()) < AutoHud.config.getHotbarDurabilityTotal()
        ) {
            Component.Hotbar.revealCombined();
            Component.Tooltip.revealCombined();
            return true;
        }
        return false;
    }

    public void tick(ClientPlayerEntity player) {
        if (player == null) return;

        Component.tickAll();

        if (AutoHud.config.isHotbarOnLowDurability()) {
            if (!revealHotbarOnDurability(player.getMainHandStack())) {
                revealHotbarOnDurability(player.getOffHandStack());
            }
        }

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

        AutoHud.apis.forEach(api -> api.tickState(player));
    }
    public void render(float tickDelta) {
        Component.getComponents().forEach(c -> c.render(tickDelta));
        Component.getStatusEffectComponents().forEach(c -> c.render(tickDelta));
    }
}
