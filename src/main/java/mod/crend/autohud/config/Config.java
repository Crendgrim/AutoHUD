package mod.crend.autohud.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@me.shedaniel.autoconfig.annotation.Config(name = "autohud")
public class Config implements ConfigData {
    boolean dynamicOnLoad = true;
    int ticksRevealed = 100;
    double animationSpeed = 2.0;
    public boolean dynamicOnLoad() { return dynamicOnLoad; }
    public int timeRevealed() { return ticksRevealed; }
    public double animationSpeed() { return animationSpeed; }

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Category("components")
    RevealPolicy onHealthChange = RevealPolicy.Changing;
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Category("components")
    RevealPolicy onHungerChange = RevealPolicy.Low;
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Category("components")
    RevealPolicy onAirChange = RevealPolicy.NotFull;
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Category("components")
    RevealPolicy onArmorChange = RevealPolicy.Changing;
    public RevealPolicy onHealthChange() { return onHealthChange; }
    public RevealPolicy onHungerChange() { return onHungerChange; }
    public RevealPolicy onAirChange() { return onAirChange; }
    public RevealPolicy onArmorChange() { return onArmorChange; }

    @ConfigEntry.Category("components")
    boolean hideExperience = true;
    @ConfigEntry.Category("components")
    boolean revealOnExperienceChange = true;
    public boolean hideExperience() { return hideExperience; }
    public boolean revealOnExperienceChange() { return revealOnExperienceChange; }

    @ConfigEntry.Category("components")
    boolean hideMount = true;
    @ConfigEntry.Category("components")
    boolean revealOnMountJump = true;
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Category("components")
    RevealPolicy onMountHealthChange = RevealPolicy.Changing;
    public boolean hideMount() { return hideMount; }
    public boolean revealOnMountJump() { return revealOnMountJump; }
    public RevealPolicy onMountHealthChange() { return onMountHealthChange; }

    @ConfigEntry.Category("components")
    boolean hideHotbar = true;
    @ConfigEntry.Category("components")
    boolean revealOnItemChange = true;
    public boolean hideHotbar() { return hideHotbar; }
    public boolean revealOnItemChange() { return revealOnItemChange; }

    @ConfigEntry.Category("components")
    boolean hideStatusEffects = false;
    @ConfigEntry.Category("components")
    boolean revealOnStatusEffectsChange = true;
    @ConfigEntry.Category("components")
    @ConfigEntry.Gui.Tooltip
    boolean hideTurtleHelmetWaterBreathing = true;
    public boolean hideStatusEffects() { return hideStatusEffects; }
    public boolean revealOnStatusEffectsChange() { return revealOnStatusEffectsChange; }
    public boolean hideTurtleHelmetWaterBreathing() { return hideTurtleHelmetWaterBreathing; }

    @ConfigEntry.Category("components")
    boolean hideScoreboard = true;
    public boolean hideScoreboard() { return hideScoreboard; }

    @ConfigEntry.Category("dynamicCrosshair")
    @ConfigEntry.Gui.Tooltip
    boolean dynamicCrosshair = true;
    @ConfigEntry.Category("dynamicCrosshair")
    @ConfigEntry.Gui.Tooltip
    boolean dynamicCrosshairEnableWithHud = true;
    @ConfigEntry.Category("dynamicCrosshair")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    InteractableCrosshairPolicy dynamicCrosshairOnBlock = InteractableCrosshairPolicy.IfInteractable;
    @ConfigEntry.Category("dynamicCrosshair")
    boolean dynamicCrosshairOnEntity = true;
    @ConfigEntry.Category("dynamicCrosshair")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    CrosshairPolicy dynamicCrosshairHoldingTool = CrosshairPolicy.Always;
    @ConfigEntry.Category("dynamicCrosshair")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    CrosshairPolicy dynamicCrosshairHoldingRangedWeapon = CrosshairPolicy.Always;
    @ConfigEntry.Category("dynamicCrosshair")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    CrosshairPolicy dynamicCrosshairHoldingThrowable = CrosshairPolicy.Always;
    @ConfigEntry.Category("dynamicCrosshair")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    BlockCrosshairPolicy dynamicCrosshairHoldingBlock = BlockCrosshairPolicy.IfInteractable;
    @ConfigEntry.Category("dynamicCrosshair")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    BlockCrosshairPolicy dynamicCrosshairHoldingUsableItem = BlockCrosshairPolicy.IfInteractable;

    public boolean dynamicCrosshair() { return dynamicCrosshair; }
    public boolean dynamicCrosshairEnableWithHud() { return dynamicCrosshairEnableWithHud; }
    public InteractableCrosshairPolicy dynamicCrosshairOnBlock() { return dynamicCrosshairOnBlock; }
    public boolean dynamicCrosshairOnEntity() { return dynamicCrosshairOnEntity; }
    public CrosshairPolicy dynamicCrosshairHoldingTool() { return dynamicCrosshairHoldingTool; }
    public CrosshairPolicy dynamicCrosshairHoldingRangedWeapon() { return dynamicCrosshairHoldingRangedWeapon; }
    public CrosshairPolicy dynamicCrosshairHoldingThrowable() { return dynamicCrosshairHoldingThrowable; }
    public BlockCrosshairPolicy dynamicCrosshairHoldingBlock() { return dynamicCrosshairHoldingBlock; }
    public BlockCrosshairPolicy dynamicCrosshairHoldingUsableItem() { return dynamicCrosshairHoldingUsableItem; }

    /* OPTIONS END */
    @Override
    public void validatePostLoad() throws ValidationException {
        ConfigData.super.validatePostLoad();
        if (ticksRevealed < 20) ticksRevealed = 20;
        animationSpeed = Math.min(10.0, Math.max(0.1, animationSpeed));
    }

}
