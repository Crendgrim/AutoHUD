package mod.crend.autohud.component;

import com.mojang.datafixers.util.Pair;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.state.*;
import mod.crend.autohud.config.EventPolicy;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class State {

    private Map<StatusEffect, StatusEffectInstance> previousStatusEffects;
    private ItemStack previousItemStack;
    Vec3d previousPosition;
    static final int TICKS_UNTIL_STANDING_STILL = 5;
    int ticksUntilStandingStill = 0;
    boolean wasInPauseScreen = false;
    boolean screenWasOpen = false;
    boolean wasSneaking = false;
    boolean wasFlying = false;

    public State(ClientPlayerEntity player) {
        initStates(player);
        previousStatusEffects = new HashMap<>();
        previousItemStack = player.getMainHandStack().copy();
        previousPosition = player.getPos();
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
                () -> player.getHungerManager().getFoodLevel() < 20 && player.getMainHandStack().isFood());
        Component.Armor.state = new EnhancedPolicyComponentState(Component.Armor,
                player::getArmor,
                20,
                () -> player.getMainHandStack().getItem() instanceof Equipment equipment && equipment.getSlotType().isArmorSlot() && player.canEquip(player.getMainHandStack()), true);
        Component.Air.state = new PolicyComponentState(Component.Air, player::getAir, player::getMaxAir);
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

    private boolean isHealEffect(StatusEffectInstance effect) {
        return (effect.getEffectType() == StatusEffects.REGENERATION
                || effect.getEffectType() == StatusEffects.INSTANT_HEALTH
                || effect.getEffectType() == StatusEffects.HEALTH_BOOST
                || effect.getEffectType() == StatusEffects.ABSORPTION);

    }
    private boolean canHeal() {
        ItemStack itemStack = MinecraftClient.getInstance().player.getMainHandStack();
        if (itemStack.isFood()) {
            List<Pair<StatusEffectInstance, Float>> statusEffects = itemStack.getItem().getFoodComponent().getStatusEffects();
            for (Pair<StatusEffectInstance, Float> effect : statusEffects) {
                if (isHealEffect(effect.getFirst())) {
                    return true;
                }
            }
        } else if (itemStack.getItem() instanceof PotionItem) {
            Potion potion = PotionUtil.getPotion(itemStack);
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

        ItemStack mainHandStack = player.getMainHandStack();
        if (!ItemStack.areEqual(mainHandStack, previousItemStack)) {
            previousItemStack = mainHandStack.copy();
            Component.Health.state.updateNextTick();
            Component.Hunger.state.updateNextTick();
            Component.Armor.state.updateNextTick();
        }

        Component.tickAll();

        if (AutoHud.config.isHotbarOnLowDurability()) {
            if (!revealHotbarOnDurability(player.getMainHandStack())) {
                revealHotbarOnDurability(player.getOffHandStack());
            }
        }

        Screen currentScreen = MinecraftClient.getInstance().currentScreen;
        if (currentScreen instanceof ChatScreen) {
            Component.Chat.revealNow();
            Component.ChatIndicator.hide();
        }

        if (player.isSneaking()) {
            switch (AutoHud.config.onSneaking()) {
                case Reveal -> Component.revealAll();
                case Hide -> Component.forceHideAll();
            }
            wasSneaking = true;
        } else if (wasSneaking) {
            switch (AutoHud.config.onSneaking()) {
                case Reveal -> Component.hideAll();
                case Hide -> Component.updateAll();
            }
            wasSneaking = false;
        }

        if (player.isFallFlying()) {
            switch (AutoHud.config.onFlying()) {
                case Reveal -> Component.revealAll();
                case Hide -> Component.forceHideAll();
            }
            wasFlying = true;
        } else if (wasFlying) {
            switch (AutoHud.config.onFlying()) {
                case Reveal -> Component.hideAll();
                case Hide -> Component.updateAll();
            }
        }

        if (AutoHud.config.onUsingItem() && player.isUsingItem()) {
            Component.revealAll(1);
        }

        if (AutoHud.config.onMining() && MinecraftClient.getInstance().interactionManager.getBlockBreakingProgress() >= 0) {
            Component.revealAll(1);
        }

        Vec3d position = player.getPos();
        if (position != previousPosition) {
            switch (AutoHud.config.onMoving()) {
                case Reveal -> Component.revealAll();
                case Hide -> Component.forceHideAll();
                default -> {
                    if (ticksUntilStandingStill < TICKS_UNTIL_STANDING_STILL) {
                        switch (AutoHud.config.onStandingStill()) {
                            case Hide -> Component.updateAll();
                            case Reveal -> Component.hideAll();
                        }
                    }
                }
            }
            previousPosition = position;
            ticksUntilStandingStill = TICKS_UNTIL_STANDING_STILL;
        } else {
            if (ticksUntilStandingStill == 0) {
                switch (AutoHud.config.onStandingStill()) {
                    case Reveal -> Component.revealAll();
                    case Hide -> Component.forceHideAll();
                    default -> {
                        if (AutoHud.config.onMoving() == EventPolicy.Hide) Component.updateAll();
                    }
                }
            }
            else {
                if (ticksUntilStandingStill == TICKS_UNTIL_STANDING_STILL) {
                    switch (AutoHud.config.onMoving()) {
                        case Hide -> Component.updateAll();
                        case Reveal -> Component.hideAll();
                    }
                }
                --ticksUntilStandingStill;
            }
        }

        if (currentScreen instanceof HandledScreen<?>) {
            if (AutoHud.config.onScreenOpen() == EventPolicy.Reveal) Component.revealAll();
            if (!screenWasOpen) {
                if (AutoHud.config.onScreenOpen() == EventPolicy.Hide) Component.forceHideAll();
                screenWasOpen = true;
            }
        } else if (screenWasOpen) {
            switch (AutoHud.config.onScreenOpen()) {
                case Hide -> Component.updateAll();
                case Reveal -> Component.hideAll();
            }
            screenWasOpen = false;
        }

        if (AutoHud.config.onPauseScreen() != EventPolicy.Nothing) {
            if (MinecraftClient.getInstance().currentScreen instanceof GameMenuScreen) {
                switch (AutoHud.config.onPauseScreen()) {
                    case Reveal -> Component.revealAll();
                    case Hide -> Component.forceHideAll();
                }
                wasInPauseScreen = true;
            }
            else if (wasInPauseScreen) {
                switch (AutoHud.config.onPauseScreen()) {
                    case Hide -> Component.updateAll();
                    case Reveal -> Component.hideAll();
                }
                wasInPauseScreen = false;
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
            if (AutoHud.config.hidePersistentStatusEffects()) {
                Map<StatusEffect, StatusEffectInstance> newStatusEffects = new HashMap<>();
                Map<StatusEffect, StatusEffectInstance> effects = player.getActiveStatusEffects();
                for (StatusEffect effect : effects.keySet()) {
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
