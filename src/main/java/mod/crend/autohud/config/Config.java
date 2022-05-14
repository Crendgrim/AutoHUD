package mod.crend.autohud.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@me.shedaniel.autoconfig.annotation.Config(name = "autohud")
public class Config implements ConfigData {
    int ticksRevealed = 100;
    public int timeRevealed() {
        return ticksRevealed;
    }
    double animationSpeed = 2.0;
    public double animationSpeed() { return animationSpeed; }

    RevealPolicy onHealthChange = RevealPolicy.Changing;
    RevealPolicy onHungerChange = RevealPolicy.Low;
    RevealPolicy onAirChange = RevealPolicy.NotFull;
    RevealPolicy onArmorChange = RevealPolicy.Changing;
    public RevealPolicy onHealthChange() { return onHealthChange; }
    public RevealPolicy onHungerChange() { return onHungerChange; }
    public RevealPolicy onAirChange() { return onAirChange; }
    public RevealPolicy onArmorChange() { return onArmorChange; }

    boolean hideExperience = true;
    boolean revealOnExperienceChange = true;
    public boolean hideExperience() { return hideExperience; }
    public boolean revealOnExperienceChange() { return revealOnExperienceChange; }

    boolean hideMount = true;
    boolean revealOnMountJump = true;
    RevealPolicy onMountHealthChange = RevealPolicy.Changing;
    public boolean hideMount() { return hideMount; }
    public boolean revealOnMountJump() { return revealOnMountJump; }
    public RevealPolicy onMountHealthChange() { return onMountHealthChange; }

    boolean hideHotbar = true;
    boolean revealOnItemChange = true;
    public boolean hideHotbar() { return hideHotbar; }
    public boolean revealOnItemChange() { return revealOnItemChange; }

    boolean hideStatusEffects = false;
    boolean revealOnStatusEffectsChange = true;
    @ConfigEntry.Gui.Tooltip
    boolean hideTurtleHelmetWaterBreathing = true;
    public boolean hideStatusEffects() { return hideStatusEffects; }
    public boolean revealOnStatusEffectsChange() { return revealOnStatusEffectsChange; }
    public boolean hideTurtleHelmetWaterBreathing() { return hideTurtleHelmetWaterBreathing; }

    boolean hideScoreboard = true;
    public boolean hideScoreboard() { return hideScoreboard; }

    @ConfigEntry.Gui.Tooltip
    boolean dynamicCrosshair = true;
    public boolean dynamicCrosshair() { return dynamicCrosshair; }

    /* OPTIONS END */
    @Override
    public void validatePostLoad() throws ValidationException {
        ConfigData.super.validatePostLoad();
        if (ticksRevealed < 20) ticksRevealed = 20;
        animationSpeed = Math.min(10.0, Math.max(0.1, animationSpeed));
    }
}
