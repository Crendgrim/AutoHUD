package mod.crend.autohud.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@me.shedaniel.autoconfig.annotation.Config(name = "autohud")
public class Config implements ConfigData {
    /* MAIN OPTIONS */
    boolean dynamicOnLoad = true;
    int ticksRevealed = 150;
    boolean animationMove = true;
    boolean animationFade = true;
    double animationSpeed = 1.0;
    public static class AnimationSpeeds {
        AnimationSpeeds() { }

        double moveIn = 0;
        double moveOut = 0;
        double fadeIn = 0;
        double fadeOut = 0;
    }
    @ConfigEntry.Gui.CollapsibleObject
    AnimationSpeeds animationSpeeds = new AnimationSpeeds();

    @ConfigEntry.Gui.Tooltip(count = 5)
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    RevealType revealType = RevealType.Stacked;
    boolean statusEffectTimer = true;

    /* COMPONENTS */
    public static class DefaultValues {
        DefaultValues() { }

        double speedMultiplier = 1.0;
        int distance = 60;
        double maximumFade = 0.0d;
    }
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("components")
    DefaultValues defaultValues = new DefaultValues();

    public static class AdvancedComponent {
        AdvancedComponent() { }

        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        ScrollDirection direction = ScrollDirection.Down;
        double speedMultiplier = -1;
        int distance = -1;
        double maximumFade = -1d;
    }
    public static class IComponent {
    }
    public static class SimpleComponent extends IComponent {
        private SimpleComponent() { }
        boolean active = true;
    }
    public static class PolicyComponent extends IComponent {
        private PolicyComponent() { }
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        RevealPolicy policy = RevealPolicy.Changing;
    }
    public static class BooleanComponent extends IComponent {
        private BooleanComponent() { }
        boolean active = true;
        boolean onChange = true;
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
    BooleanComponent statusEffects = new BooleanComponent();
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Category("components")
    boolean hidePersistentStatusEffects = true;

    @ConfigEntry.Category("components")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded=true)
    HotbarComponents hotbar = new HotbarComponents();
    @ConfigEntry.Category("components")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded=true)
    ScoreboardComponents scoreboard = new ScoreboardComponents();
    @ConfigEntry.Category("components")
    @ConfigEntry.Gui.CollapsibleObject()
    AdvancedComponents advanced = new AdvancedComponents();

    
    public static class HotbarComponents {
        @ConfigEntry.Gui.TransitiveObject
        BooleanComponent hotbar = new BooleanComponent();
        boolean onSlotChange = true;
        boolean onLowDurability = true;
        @ConfigEntry.BoundedDiscrete(max=100)
        int durabilityPercentage = 10;
        int durabilityTotal = 20;
        float maximumFadeHotbarItems = 0.0f;
    }
    
    public static class ScoreboardComponents {
        @ConfigEntry.Gui.TransitiveObject
        BooleanComponent scoreboard = new BooleanComponent();
        boolean onScoreChange = true;
        @ConfigEntry.Gui.Tooltip
        boolean onTeamChange = true;
    }
    
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
            statusEffects.direction = ScrollDirection.Up;
            scoreboard.direction = ScrollDirection.Right;
            scoreboard.distance = 100;
        }
    }

    /* DEFAULT OVERRIDES */
    private Config() {
        hunger.policy = RevealPolicy.Low;
        air.policy = RevealPolicy.NotFull;
    }

    /* OPTIONS END */

    @Override
    public void validatePostLoad() throws ValidationException {
        ConfigData.super.validatePostLoad();
        if (ticksRevealed < 20) ticksRevealed = 20;
        animationSpeed = Math.min(10.0, Math.max(0.1, animationSpeed));
    }

}
