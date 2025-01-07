package mod.crend.autohud.component;

import mod.crend.autohud.AutoHud;
import mod.crend.libbamboo.type.ItemOrTag;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.util.hit.HitResult;

//? if <1.20.5 {
import net.minecraft.potion.PotionUtil;
//?} else {
/*import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
*///?}
//? if >=1.21.2 {
/*import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.consume.ConsumeEffect;
*///?}

import java.util.HashMap;
import java.util.Map;

public class State {

    //? if <1.20.5 {
    private Map<StatusEffect, StatusEffectInstance> previousStatusEffects;
    //?} else {
    /*Map<RegistryEntry<StatusEffect>, StatusEffectInstance> previousStatusEffects;
     *///?}

    public State(ClientPlayerEntity player) {
        initStates(player);
        previousStatusEffects = new HashMap<>();
    }
   public void initStates(ClientPlayerEntity player) {
        Component.getComponents().forEach(component -> component.initState(player));
        Components.ChatIndicator.hideNow();

        AutoHud.apis.forEach(api -> api.initState(player));
    }

    static boolean shouldShowCrosshair() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.crosshairTarget != null && client.crosshairTarget.getType() != HitResult.Type.MISS) {
            return true;
        }
        if (client.player != null) {
            for (ItemOrTag itemOrTag : AutoHud.config.crosshairAlwaysVisible()) {
                if (itemOrTag.matches(client.player.getMainHandStack().getItem()) || itemOrTag.matches(client.player.getOffHandStack().getItem())) {
                    return true;
                }
            }
        }
        return false;
    }

    static boolean isFood(ItemStack itemStack) {
        //? if <1.20.5 {
        return itemStack.isFood();
        //?} else if <1.21.2 {
        /*return itemStack.contains(DataComponentTypes.FOOD);
        *///?} else
        /*return itemStack.contains(DataComponentTypes.CONSUMABLE);*/
         
    }

    private static boolean isHealEffect(StatusEffectInstance effect) {
        return (effect.getEffectType() == StatusEffects.REGENERATION
                || effect.getEffectType() == StatusEffects.INSTANT_HEALTH
                || effect.getEffectType() == StatusEffects.HEALTH_BOOST
                || effect.getEffectType() == StatusEffects.ABSORPTION);

    }
    static boolean canHeal() {
        ItemStack itemStack = MinecraftClient.getInstance().player.getMainHandStack();
        if (isFood(itemStack)) {

            //? if <1.20.5 {
            var statusEffects = itemStack.getItem().getFoodComponent().getStatusEffects();
            for (var effect : statusEffects) {
                if (isHealEffect(effect.getFirst())) {
                    return true;
                }
            }
            //?} else if <1.21.2 {
            /*var statusEffects = itemStack.get(DataComponentTypes.FOOD).effects();
            for (var effect : statusEffects) {
                if (isHealEffect(effect.effect())) {
                    return true;
                }
            }
            *///?} else {
            /*var consumeEffects = itemStack.get(DataComponentTypes.CONSUMABLE).onConsumeEffects();
            for (var consumeEffect : consumeEffects) {
                if (consumeEffect instanceof ApplyEffectsConsumeEffect effect) {
                    for (var statusEffect : effect.effects()) {
                        if (isHealEffect(statusEffect)) {
                            return true;
                        }
                    }
                }
            }
            *///?}
        } else if (itemStack.getItem() instanceof PotionItem) {
            //? if <1.20.5 {
            Potion potion = PotionUtil.getPotion(itemStack);
            //?} else {
            /*Potion potion = itemStack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT)
                    .potion()
                    .orElse(Potions.WATER)
                    .value();
            *///?}
            for (StatusEffectInstance effect : potion.getEffects()) {
                if (isHealEffect(effect)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean revealHotbarOnDurability(ItemStack itemStack) {
        if (itemStack.isDamageable()
            && itemStack.getDamage() >= (100 - AutoHud.config.getHotbarDurabilityPercentage()) / 100.0 * itemStack.getMaxDamage()
            && (itemStack.getMaxDamage() - itemStack.getDamage()) < AutoHud.config.getHotbarDurabilityTotal()
        ) {
            Components.Hotbar.revealCombined();
            Components.Tooltip.revealCombined();
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

        if (AutoHud.config.revealExperienceTextWithHotbar()) {
            Components.ExperienceLevel.synchronizeFrom(Components.ExperienceBar, Components.Hotbar);
        }

        if (AutoHud.config.statusEffects().active()) {
            if (AutoHud.config.statusEffects().onChange()) {
                //? if <1.20.5 {
                Map<StatusEffect, StatusEffectInstance> newStatusEffects = new HashMap<>();
                //?} else {
                /*Map<RegistryEntry<StatusEffect>, StatusEffectInstance> newStatusEffects = new HashMap<>();
                *///?}
                var effects = player.getActiveStatusEffects();
                for (var effect : effects.keySet()) {
                    StatusEffectInstance effectInstance = effects.get(effect);
                    if (effectInstance.shouldShowIcon() || AutoHud.config.showHiddenStatusEffects()) {
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
            if (AutoHud.config.hidePersistentStatusEffects()) {
                //? if <1.20.5 {
                Map<StatusEffect, StatusEffectInstance> newStatusEffects = new HashMap<>();
                //?} else {
                /*Map<RegistryEntry<StatusEffect>, StatusEffectInstance> newStatusEffects = new HashMap<>();
                 *///?}
                var effects = player.getActiveStatusEffects();
                for (var effect : effects.keySet()) {
                    StatusEffectInstance effectInstance = effects.get(effect);
                    if (effectInstance.shouldShowIcon()) {
                        if (previousStatusEffects.containsKey(effect) && previousStatusEffects.get(effect).equals(effectInstance)) {
                            Component.get(effect).hideNow();
                        }
                        newStatusEffects.put(effect, new StatusEffectInstance(effectInstance));
                    }
                }
                previousStatusEffects = newStatusEffects;
            }
        }

        AutoHud.apis.forEach(api -> api.tickState(player));
    }
}
