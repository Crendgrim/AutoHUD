package mod.crend.autohud.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import mod.crend.autohud.component.Crosshair;
import mod.crend.autohud.component.CrosshairModifier;

@me.shedaniel.autoconfig.annotation.Config(name = "autohud")
public class Config implements ConfigData {
    /* MAIN OPTIONS */
    boolean dynamicOnLoad = true;
    int ticksRevealed = 100;
    double animationSpeed = 2.0;
    @ConfigEntry.Gui.Tooltip(count = 3)
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    RevealType revealType = RevealType.INDIVIDUAL;
    boolean statusEffectTimer = true;
    public boolean dynamicOnLoad() { return dynamicOnLoad; }
    public int timeRevealed() { return ticksRevealed; }
    public double animationSpeed() { return animationSpeed; }
    public RevealType revealType() { return revealType; }
    public boolean statusEffectTimer() { return statusEffectTimer; }

    /* COMPONENTS */
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
    boolean hideStatusEffects = true;
    @ConfigEntry.Category("components")
    boolean revealActiveStatusEffects = true;
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("components")
    boolean hidePersistentStatusEffects = true;
    public boolean hideStatusEffects() { return hideStatusEffects; }
    public boolean revealActiveStatusEffects() { return revealActiveStatusEffects; }
    public boolean hidePersistentStatusEffects() { return hidePersistentStatusEffects; }

    @ConfigEntry.Category("components")
    boolean hideScoreboard = true;
    public boolean hideScoreboard() { return hideScoreboard; }

    /* DYNAMIC CROSSHAIR */
    @ConfigEntry.Category("dynamicCrosshair")
    @ConfigEntry.Gui.Tooltip
    boolean dynamicCrosshair = true;
    @ConfigEntry.Category("dynamicCrosshair")
    @ConfigEntry.Gui.Tooltip
    boolean dynamicCrosshairEnableWithHud = true;
    @ConfigEntry.Category("dynamicCrosshair")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    CrosshairSettings crosshairSettings = new CrosshairSettings();

    static class CrosshairSettings {
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        InteractableCrosshairPolicy onBlock = InteractableCrosshairPolicy.IfTargeting;
        boolean onEntity = true;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairPolicy holdingTool = CrosshairPolicy.Always;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairPolicy holdingRangedWeapon = CrosshairPolicy.Always;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairPolicy holdingThrowable = CrosshairPolicy.Always;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        BlockCrosshairPolicy holdingBlock = BlockCrosshairPolicy.IfInteractable;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        BlockCrosshairPolicy holdingUsableItem = BlockCrosshairPolicy.IfInteractable;
    }
    public boolean dynamicCrosshair() { return dynamicCrosshair; }
    public boolean dynamicCrosshairEnableWithHud() { return dynamicCrosshairEnableWithHud; }
    public InteractableCrosshairPolicy dynamicCrosshairOnBlock() { return crosshairSettings.onBlock; }
    public boolean dynamicCrosshairOnEntity() { return crosshairSettings.onEntity; }
    public CrosshairPolicy dynamicCrosshairHoldingTool() { return crosshairSettings.holdingTool; }
    public CrosshairPolicy dynamicCrosshairHoldingRangedWeapon() { return crosshairSettings.holdingRangedWeapon; }
    public CrosshairPolicy dynamicCrosshairHoldingThrowable() { return crosshairSettings.holdingThrowable; }
    public BlockCrosshairPolicy dynamicCrosshairHoldingBlock() { return crosshairSettings.holdingBlock; }
    public BlockCrosshairPolicy dynamicCrosshairHoldingUsableItem() { return crosshairSettings.holdingUsableItem; }

    @ConfigEntry.Category("dynamicCrosshair")
    boolean dynamicCrosshairStyle = true;
    @ConfigEntry.Category("dynamicCrosshair")
    @ConfigEntry.Gui.CollapsibleObject
    CrosshairStyle crosshairStyle = new CrosshairStyle();
    @ConfigEntry.Category("dynamicCrosshair")
    @ConfigEntry.Gui.CollapsibleObject
    CrosshairModifiers crosshairModifiers = new CrosshairModifiers();
    public boolean dynamicCrosshairStyle() { return dynamicCrosshairStyle; }
    static class CrosshairStyle {
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        Crosshair regular = Crosshair.CROSS;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        Crosshair onBlock = Crosshair.CROSS;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        Crosshair onEntity = Crosshair.DIAGONAL_CROSS;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        Crosshair holdingTool = Crosshair.SQUARE;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        Crosshair holdingRangedWeapon = Crosshair.CIRCLE;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        Crosshair holdingThrowable = Crosshair.CIRCLE;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        Crosshair holdingBlock = Crosshair.DIAGONAL_SQUARE;
    }
    static class CrosshairModifiers {
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairModifier modInteractable = CrosshairModifier.BRACKETS;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairModifier modCorrectTool = CrosshairModifier.DOT;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairModifier modIncorrectTool = CrosshairModifier.DIAGONAL_CROSS;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairModifier modUsableItem = CrosshairModifier.ROUND_BRACKETS;
    }

    public Crosshair getCrosshairRegular() { return crosshairStyle.regular; }
    public Crosshair getCrosshairOnBlock() { return crosshairStyle.onBlock; }
    public Crosshair getCrosshairOnEntity() { return crosshairStyle.onEntity; }
    public Crosshair getCrosshairHoldingTool() { return crosshairStyle.holdingTool; }
    public Crosshair getCrosshairHoldingRangedWeapon() { return crosshairStyle.holdingRangedWeapon; }
    public Crosshair getCrosshairHoldingThrowable() { return crosshairStyle.holdingThrowable; }
    public Crosshair getCrosshairHoldingBlock() { return crosshairStyle.holdingBlock; }
    public CrosshairModifier crosshairModInteractable() { return crosshairModifiers.modInteractable; }
    public CrosshairModifier crosshairModCorrectTool() { return crosshairModifiers.modCorrectTool; }
    public CrosshairModifier crosshairModIncorrectTool() { return crosshairModifiers.modIncorrectTool; }
    public CrosshairModifier crosshairModUsableItem() { return crosshairModifiers.modUsableItem; }

    /* OPTIONS END */
    @Override
    public void validatePostLoad() throws ValidationException {
        ConfigData.super.validatePostLoad();
        if (ticksRevealed < 20) ticksRevealed = 20;
        animationSpeed = Math.min(10.0, Math.max(0.1, animationSpeed));
    }

}
