package mod.crend.autohud.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import mod.crend.autohud.component.ScrollDirection;

@me.shedaniel.autoconfig.annotation.Config(name = "autohud")
public class Config implements ConfigData {
    /* MAIN OPTIONS */
    boolean dynamicOnLoad = true;
    int ticksRevealed = 150;
    double animationSpeed = 1.0;
    @ConfigEntry.Gui.Tooltip(count = 3)
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    RevealType revealType = RevealType.STACKED;
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
        public AdvancedComponent values = new AdvancedComponent();
        public abstract boolean active();
        public boolean onChange() { return false; }
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
            return policy != RevealPolicy.Always;
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
    @ConfigEntry.Gui.Excluded
    public static final IComponent None = new IComponent() {
        @Override
        public boolean active() {
            return true;
        }
    };

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
    @ConfigEntry.Category("components")
    boolean hotbarOnSlotChange = true;
    @ConfigEntry.Category("components")
    boolean hotbarOnLowDurability = true;
    @ConfigEntry.Category("components")
    @ConfigEntry.BoundedDiscrete(max=100)
    int hotbarDurabilityPercentage = 10;
    @ConfigEntry.Category("components")
    int hotbarDurabilityTotal = 20;
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
    public boolean isHotbarOnSlotChange() { return hotbarOnSlotChange; }
    public boolean isHotbarOnLowDurability() { return hotbarOnLowDurability; }
    public int getHotbarDurabilityPercentage() { return hotbarDurabilityPercentage; }
    public int getHotbarDurabilityTotal() { return hotbarDurabilityTotal; }
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
