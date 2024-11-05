package mod.crend.autohud.component;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.state.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
//? if >=1.21.2
/*import net.minecraft.component.type.EquippableComponent;*/

//? if >=1.20.5 {
/*import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
*///?}
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
//? if <1.21.3
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
//? if >=1.21.2 {
/*import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.consume.ConsumeEffect;
*///?}
import net.minecraft.potion.Potion;
//? if <1.20.5
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.hit.HitResult;

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
        Component.Hotbar.state = new ItemStackComponentState(Component.Hotbar, player::getMainHandStack, true);
        Component.Tooltip.state = new ItemStackComponentState(Component.Tooltip, player::getMainHandStack, true);
        Component.Health.state = new EnhancedPolicyComponentState(Component.Health,
                () -> Math.round(player.getHealth()),
                () -> Math.round(player.getMaxHealth()),
                this::canHeal);
        Component.Hunger.state = new EnhancedPolicyComponentState(Component.Hunger,
                () -> player.getHungerManager().getFoodLevel(),
                20,
                () -> player.getHungerManager().getFoodLevel() < 20 && isFood(player.getMainHandStack()));
        Component.Armor.state = new EnhancedPolicyComponentState(Component.Armor,
                player::getArmor,
                20,
                () ->
                        //? if <1.21.2 {
                        player.getMainHandStack().getItem() instanceof Equipment equipment
                                && equipment.getSlotType().isArmorSlot()
                                && player.canEquip(player.getMainHandStack())
                        //?} else {
                        /*{
                            ItemStack mainHandStack = player.getMainHandStack();
                            EquippableComponent equipment = mainHandStack.get(DataComponentTypes.EQUIPPABLE);
                            return equipment != null
                                && equipment.slot().isArmorSlot()
                                && player.canEquip(mainHandStack, equipment.slot());
                        }
                        *///?}
                   , true);
        Component.Air.state = new PolicyComponentState(Component.Air, player::getAir, player::getMaxAir);
        Component.ExperienceLevel.state = new ValueComponentState<>(Component.ExperienceLevel, () -> player.experienceLevel, true);
        Component.ExperienceBar.state = new ValueComponentState<>(Component.ExperienceBar, () -> player.totalExperience, true);
        Component.Scoreboard.state = new ScoreboardComponentState(Component.Scoreboard);
        Component.MountJumpBar.state = new ComponentState(Component.MountJumpBar);
        Component.Crosshair.state = new BooleanComponentState(Component.Crosshair, State::shouldShowCrosshair);
        Component.Chat.state = new ComponentState(Component.Chat);
        Component.ChatIndicator.state = new ComponentState(Component.ChatIndicator);
        Component.ChatIndicator.hideNow();
        Component.ActionBar.state = new ComponentState(Component.ActionBar);
        Component.BossBar.state = new ComponentState(Component.BossBar);

        AutoHud.apis.forEach(api -> api.initState(player));
    }

    private static boolean shouldShowCrosshair() {
        HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;
        return (hitResult != null && hitResult.getType() != HitResult.Type.MISS);
    }

    private boolean isFood(ItemStack itemStack) {
        //? if <1.20.5 {
        return itemStack.isFood();
        //?} else if <1.21.2 {
        /*return itemStack.contains(DataComponentTypes.FOOD);
        *///?} else
        /*return itemStack.contains(DataComponentTypes.CONSUMABLE);*/
         
    }

    private boolean isHealEffect(StatusEffectInstance effect) {
        return (effect.getEffectType() == StatusEffects.REGENERATION
                || effect.getEffectType() == StatusEffects.INSTANT_HEALTH
                || effect.getEffectType() == StatusEffects.HEALTH_BOOST
                || effect.getEffectType() == StatusEffects.ABSORPTION);

    }
    private boolean canHeal() {
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

        if (AutoHud.config.revealExperienceTextWithHotbar()) {
            Component.ExperienceLevel.synchronizeFrom(Component.ExperienceBar, Component.Hotbar);
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
