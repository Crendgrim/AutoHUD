package mod.crend.autohud.component;

import mod.crend.autohud.AutoHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

public class State {

    private ItemStack previousStack;

    private int previousExperience;
    private StatState health;
    private StatState food;
    private StatState armor;
    private StatState air;
    private int previousStatusEffects;

    public State(ClientPlayerEntity player) {
        previousStack = player.getMainHandStack();
        previousExperience = player.totalExperience;
        previousStatusEffects = player.getStatusEffects().size();
        health = new StatState(Component.Health, (int) player.getHealth(), (int) player.getMaxHealth());
        food = new StatState(Component.Hunger, player.getHungerManager().getFoodLevel(), 20);
        armor = new StatState(Component.Armor, player.getArmor(), 20);
        air = new StatState(Component.Air, player.getAir(), player.getMaxAir());
    }

    public void render(ClientPlayerEntity player, float tickDelta) {
        if (!AutoHud.config.hideHotbar() || (AutoHud.config.revealOnItemChange() && previousStack != player.getMainHandStack())) {
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
        if (!AutoHud.config.hideMount()) {
            Component.MountHealth.reveal();
            Component.MountJumpBar.reveal();
        }
        Component.MountHealth.render(tickDelta);
        Component.MountJumpBar.render(tickDelta);

        if (!AutoHud.config.hideExperience() || (AutoHud.config.revealOnExperienceChange() && previousExperience != player.totalExperience)) {
            Component.ExperienceBar.reveal();
            previousExperience = player.totalExperience;
        }
        Component.ExperienceBar.render(tickDelta);

        if (!AutoHud.config.hideStatusEffects() || (AutoHud.config.revealOnStatusEffectsChange() && previousStatusEffects != player.getStatusEffects().size())) {
            Component.StatusEffects.reveal();
            previousStatusEffects = player.getStatusEffects().size();
        }
        Component.StatusEffects.render(tickDelta);

        if (!AutoHud.config.hideScoreboard()) {
            Component.Scoreboard.reveal();
        }
        Component.Scoreboard.render(tickDelta);
    }
}
