package mod.crend.autohud.config;

import dev.isxander.yacl3.config.ConfigEntry;
import mod.crend.autohud.AutoHud;
import mod.crend.yaclx.auto.annotation.*;

@AutoYaclConfig(modid = AutoHud.MOD_ID, filename = "autohud.json5")
public class Config implements Cloneable {

    /* MAIN OPTIONS */
    @ConfigEntry public boolean dynamicOnLoad = true;
    @NumericRange(min = 10, max = 200, interval = 10)
    @ConfigEntry public int ticksRevealed = 40;
    @ConfigEntry public boolean animationMove = true;
    @ConfigEntry public boolean animationFade = true;
    @FloatingPointRange(min = 1, max = 4.0, interval = 0.25)
    @ConfigEntry public double animationSpeed = 2.0;

    public static class AnimationSpeeds {
        AnimationSpeeds() { }

        @FloatingPointRange(min = 1, max = 4.0, interval = 0.25)
        @EnableIf(field = "animationMove", value = EnableIf.BooleanPredicate.class)
        @ConfigEntry public double moveIn = 0;
        @FloatingPointRange(min = 1, max = 4.0, interval = 0.25)
        @EnableIf(field = "animationMove", value = EnableIf.BooleanPredicate.class)
        @ConfigEntry public double moveOut = 0;
        @FloatingPointRange(min = 1, max = 4.0, interval = 0.25)
        @EnableIf(field = "animationFade", value = EnableIf.BooleanPredicate.class)
        @ConfigEntry public double fadeIn = 0;
        @FloatingPointRange(min = 1, max = 4.0, interval = 0.25)
        @EnableIf(field = "animationFade", value = EnableIf.BooleanPredicate.class)
        @ConfigEntry public double fadeOut = 0;
    }
    @ConfigEntry public AnimationSpeeds animationSpeeds = new AnimationSpeeds();

    @ConfigEntry public RevealType revealType = RevealType.Stacked;
    @Category(name = "components", group = "statusEffects")
    @ConfigEntry public boolean statusEffectTimer = true;

    /* COMPONENTS */
    public static class DefaultValues {
        DefaultValues() { }

        @FloatingPointRange(min = 0.1, max = 3.0, interval = 0.1)
        @ConfigEntry public double speedMultiplier = 1.0;
        @NumericRange(min = 0, max = 200, interval = 5)
        @ConfigEntry public int distance = 60;
        @FloatingPointRange(min = 0.0, max = 1.0, interval = 0.1)
        @ConfigEntry public double maximumFade = 0.0d;
    }
    @Category(name = "advanced")
    @TransitiveObject
    @ConfigEntry public DefaultValues defaultValues = new DefaultValues();

    public static class AdvancedComponent {
        AdvancedComponent() { }

        @Translation(key = "autohud.option.advanced.direction")
        @ConfigEntry public ScrollDirection direction = ScrollDirection.Down;
        @Translation(key = "autohud.option.advanced.speedMultiplier")
        @FloatingPointRange(min = 0.1, max = 3.0, interval = 0.1)
        @ConfigEntry public double speedMultiplier = -1;
        @Translation(key = "autohud.option.advanced.distance")
        @NumericRange(min = 0, max = 200, interval = 5)
        @ConfigEntry public int distance = -1;
        @Translation(key = "autohud.option.advanced.maximumFade")
        @FloatingPointRange(min = 0.0, max = 1.0, interval = 0.1)
        @ConfigEntry public double maximumFade = -1d;
    }
    public static class IComponent {
    }
    public static class SimpleComponent extends IComponent {
        private SimpleComponent() { }
        @ConfigEntry public boolean active = true;
    }
    public static class PolicyComponent extends IComponent {
        private PolicyComponent() { }
        @ConfigEntry public RevealPolicy policy = RevealPolicy.Changing;
    }
    public static class BooleanComponent extends IComponent {
        private BooleanComponent() { }
        @ConfigEntry public boolean active = true;
        @EnableIf(field = "active", value = EnableIf.BooleanPredicate.class)
        @ConfigEntry public boolean onChange = true;
    }

    @Category(name = "components", group = "statusBars")
    @ConfigEntry public PolicyComponent health = new PolicyComponent();
    @Category(name = "components", group = "statusBars")
    @ConfigEntry public PolicyComponent hunger = new PolicyComponent();
    @Category(name = "components", group = "statusBars")
    @ConfigEntry public PolicyComponent air = new PolicyComponent();
    @Category(name = "components", group = "statusBars")
    @ConfigEntry public PolicyComponent armor = new PolicyComponent();
    @Category(name = "components", group = "statusBars")
    @ConfigEntry public BooleanComponent experience = new BooleanComponent();
    @Category(name = "components", group = "statusBars")
    @ConfigEntry public BooleanComponent mountJumpBar = new BooleanComponent();
    @Category(name = "components", group = "statusBars")
    @ConfigEntry public PolicyComponent mountHealth = new PolicyComponent();
    @Category(name = "components", group = "statusEffects")
    @ConfigEntry public BooleanComponent statusEffects = new BooleanComponent();
    @Category(name = "components", group = "statusEffects")
    @ConfigEntry public boolean hidePersistentStatusEffects = true;

    @Category(name = "components")
    @ConfigEntry public HotbarComponents hotbar = new HotbarComponents();
    @Category(name = "components")
    @ConfigEntry public ScoreboardComponents scoreboard = new ScoreboardComponents();
    @Category(name = "advanced")
    @Label(key = "autohud.option.advanced.label")
    @TransitiveObject
    @ConfigEntry public AdvancedComponents advanced = new AdvancedComponents();

    
    public static class HotbarComponents {
        @ConfigEntry public BooleanComponent hotbar = new BooleanComponent();
        @EnableIf(field = "hotbar.active", value = EnableIf.BooleanPredicate.class)
        @ConfigEntry public boolean onSlotChange = true;
        @EnableIf(field = "hotbar.active", value = EnableIf.BooleanPredicate.class)
        @ConfigEntry public boolean onLowDurability = true;
        @NumericRange(min = 0, max = 100, interval = 1)
        @EnableIf(field = "hotbar.active", value = EnableIf.BooleanPredicate.class)
        @EnableIf(field = "onLowDurability", value = EnableIf.BooleanPredicate.class)
        @ConfigEntry public int durabilityPercentage = 10;
        @EnableIf(field = "hotbar.active", value = EnableIf.BooleanPredicate.class)
        @EnableIf(field = "onLowDurability", value = EnableIf.BooleanPredicate.class)
        @ConfigEntry public int durabilityTotal = 20;
        @EnableIf(field = "hotbar.active", value = EnableIf.BooleanPredicate.class)
        @FloatingPointRange(min = 0.0f, max = 1.0f, interval = 0.1f)
        @ConfigEntry public float maximumFadeHotbarItems = 0.0f;
    }
    
    public static class ScoreboardComponents {
        @ConfigEntry public BooleanComponent scoreboard = new BooleanComponent();
        @EnableIf(field = "scoreboard.active", value = EnableIf.BooleanPredicate.class)
        @ConfigEntry public boolean onScoreChange = true;
        @EnableIf(field = "scoreboard.active", value = EnableIf.BooleanPredicate.class)
        @ConfigEntry public boolean onTeamChange = true;
    }
    
    public static class AdvancedComponents {
        @Translation(key = "autohud.group.hotbar")
        @ConfigEntry public AdvancedComponent hotbar = new AdvancedComponent();
        @Translation(key = "autohud.group.health")
        @ConfigEntry public AdvancedComponent health = new AdvancedComponent();
        @Translation(key = "autohud.group.armor")
        @ConfigEntry public AdvancedComponent armor = new AdvancedComponent();
        @Translation(key = "autohud.group.hunger")
        @ConfigEntry public AdvancedComponent hunger = new AdvancedComponent();
        @Translation(key = "autohud.group.air")
        @ConfigEntry public AdvancedComponent air = new AdvancedComponent();
        @Translation(key = "autohud.group.experience")
        @ConfigEntry public AdvancedComponent experience = new AdvancedComponent();
        @Translation(key = "autohud.group.mountJumpBar")
        @ConfigEntry public AdvancedComponent mountJumpBar = new AdvancedComponent();
        @Translation(key = "autohud.group.mountHealth")
        @ConfigEntry public AdvancedComponent mountHealth = new AdvancedComponent();
        @Translation(key = "autohud.group.statusEffects")
        @ConfigEntry public AdvancedComponent statusEffects = new AdvancedComponent();
        @Translation(key = "autohud.group.scoreboard")
        @ConfigEntry public AdvancedComponent scoreboard = new AdvancedComponent();
        private AdvancedComponents() {
            statusEffects.direction = ScrollDirection.Up;
            scoreboard.direction = ScrollDirection.Right;
            scoreboard.distance = 100;
        }
    }

    /* DEFAULT OVERRIDES */
    public Config() {
        hunger.policy = RevealPolicy.Low;
        air.policy = RevealPolicy.NotFull;
    }

    /* OPTIONS END */

    public Object clone() throws CloneNotSupportedException { return super.clone(); }
}
