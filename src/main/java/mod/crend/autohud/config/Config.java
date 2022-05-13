package mod.crend.autohud.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import mod.crend.autohud.component.Crosshair;
import mod.crend.autohud.component.CrosshairModifier;
import mod.crend.autohud.component.ScrollDirection;

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
    public static class AdvancedComponent {
        private AdvancedComponent() { }

        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        ScrollDirection direction = ScrollDirection.DOWN;
        double speedMultiplier = 1.0;
        int distance = 60;

        public ScrollDirection direction() { return direction; }
        public double speedMultiplier() { return speedMultiplier; }
        public int distance() { return distance; }
    }
    public static abstract class IComponent {
        @ConfigEntry.Gui.Excluded
        public AdvancedComponent values = null;
        public abstract boolean active();
        public boolean onChange() { return false; };
    }
    public static class SimpleComponent extends IComponent {
        private SimpleComponent() { }
        boolean active = true;

        @Override
        public boolean active() {
            return active;
        }
    }
    public static class PolicyComponent extends IComponent {
        private PolicyComponent() { }
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        RevealPolicy policy = RevealPolicy.Changing;

        @Override
        public boolean active() {
            return policy != RevealPolicy.Disabled;
        }

        @Override
        public boolean onChange() {
            return policy != RevealPolicy.Always;
        }

        public RevealPolicy policy() {
            return policy;
        }
    }
    public static class BooleanComponent extends IComponent {
        private BooleanComponent() { }

        boolean active = true;
        boolean onChange = true;

        @Override
        public boolean active() {
            return active;
        }

        @Override
        public boolean onChange() {
            return onChange;
        }
    }

    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("components")
    PolicyComponent health = new PolicyComponent();
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("components")
    PolicyComponent hunger = new PolicyComponent();
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("components")
    PolicyComponent air = new PolicyComponent();
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("components")
    PolicyComponent armor = new PolicyComponent();
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("components")
    BooleanComponent experience = new BooleanComponent();
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("components")
    BooleanComponent mountJumpBar = new BooleanComponent();
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("components")
    PolicyComponent mountHealth = new PolicyComponent();
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("components")
    BooleanComponent hotbar = new BooleanComponent();
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("components")
    BooleanComponent statusEffects = new BooleanComponent();
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("components")
    boolean hidePersistentStatusEffects = true;
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("components")
    SimpleComponent scoreboard = new SimpleComponent();

    public PolicyComponent health() { return health; }
    public PolicyComponent armor() { return armor; }
    public PolicyComponent hunger() { return hunger; }
    public PolicyComponent air() { return air; }
    public BooleanComponent experience() { return experience; }
    public BooleanComponent mountJumpBar() { return mountJumpBar; }
    public PolicyComponent mountHealth() { return mountHealth; }
    public BooleanComponent hotbar() { return hotbar; }
    public BooleanComponent statusEffects() { return statusEffects; }
    public boolean hidePersistentStatusEffects() { return hidePersistentStatusEffects; }
    public SimpleComponent scoreboard() { return scoreboard; }


    @ConfigEntry.Category("components")
    @ConfigEntry.Gui.CollapsibleObject()
    AdvancedComponents advanced = new AdvancedComponents();

    public static class AdvancedComponents {
        @ConfigEntry.Gui.TransitiveObject
        AdvancedComponent hotbar = new AdvancedComponent();
        @ConfigEntry.Gui.TransitiveObject
        AdvancedComponent health = new AdvancedComponent();
        @ConfigEntry.Gui.TransitiveObject
        AdvancedComponent armor = new AdvancedComponent();
        @ConfigEntry.Gui.TransitiveObject
        AdvancedComponent hunger = new AdvancedComponent();
        @ConfigEntry.Gui.TransitiveObject
        AdvancedComponent air = new AdvancedComponent();
        @ConfigEntry.Gui.TransitiveObject
        AdvancedComponent experience = new AdvancedComponent();
        @ConfigEntry.Gui.TransitiveObject
        AdvancedComponent mountJumpBar = new AdvancedComponent();
        @ConfigEntry.Gui.TransitiveObject
        AdvancedComponent mountHealth = new AdvancedComponent();
        @ConfigEntry.Gui.TransitiveObject
        AdvancedComponent statusEffects = new AdvancedComponent();
        @ConfigEntry.Gui.TransitiveObject
        AdvancedComponent scoreboard = new AdvancedComponent();
        private AdvancedComponents() {
            statusEffects.direction = ScrollDirection.UP;
            scoreboard.direction = ScrollDirection.RIGHT;
            scoreboard.distance = 100;
        }
    }

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

    /* DEFAULT OVERRIDES */
    private Config() {
        hunger.policy = RevealPolicy.Low;
        air.policy = RevealPolicy.NotFull;
        bindValues();
    }

    /* OPTIONS END */
    private void bindValues() {
        health.values = advanced.health;
        armor.values = advanced.armor;
        hunger.values = advanced.hunger;
        air.values = advanced.air;
        hotbar.values = advanced.hotbar;
        experience.values = advanced.experience;
        mountJumpBar.values = advanced.mountJumpBar;
        mountHealth.values = advanced.mountHealth;
        statusEffects.values = advanced.statusEffects;
        scoreboard.values = advanced.scoreboard;
    }

    @Override
    public void validatePostLoad() throws ValidationException {
        ConfigData.super.validatePostLoad();
        if (ticksRevealed < 20) ticksRevealed = 20;
        animationSpeed = Math.min(10.0, Math.max(0.1, animationSpeed));
        bindValues();
    }

}
