package mod.crend.autohud.config;

import mod.crend.autohud.AutoHud;
import mod.crend.libbamboo.auto.annotation.*;
import mod.crend.libbamboo.type.ItemOrTag;
import net.minecraft.item.Items;

import java.util.List;
//? if yacl {
import dev.isxander.yacl3.config.v2.api.SerialEntry;
//?} else {
/*import mod.crend.libbamboo.opt.yacl.SerialEntry;
*///?}

@AutoYaclConfig(modid = AutoHud.MOD_ID, filename = "autohud.json5")
public class Config implements Cloneable {

    /* MAIN OPTIONS */
    @SerialEntry public boolean dynamicOnLoad = true;
    @NumericRange(min = 10, max = 200, interval = 10)
    @SerialEntry public int ticksRevealed = 40;
    @SerialEntry public boolean animationMove = true;
    @Disable
    @SerialEntry public boolean animationFade = true;
    @FloatingPointRange(min = 1, max = 4.0, interval = 0.25)
    @SerialEntry public double animationSpeed = 2.0;

    public static class AnimationSpeeds {
        AnimationSpeeds() { }

        @FloatingPointRange(min = 1, max = 4.0, interval = 0.25)
        @EnableIf(field = "animationMove", value = EnableIf.BooleanPredicate.class)
        @SerialEntry public double moveIn = 0;
        @FloatingPointRange(min = 1, max = 4.0, interval = 0.25)
        @EnableIf(field = "animationMove", value = EnableIf.BooleanPredicate.class)
        @SerialEntry public double moveOut = 0;
        @FloatingPointRange(min = 1, max = 4.0, interval = 0.25)
        @EnableIf(field = "animationFade", value = EnableIf.BooleanPredicate.class)
        @SerialEntry public double fadeIn = 0;
        @FloatingPointRange(min = 1, max = 4.0, interval = 0.25)
        @EnableIf(field = "animationFade", value = EnableIf.BooleanPredicate.class)
        @SerialEntry public double fadeOut = 0;
    }
    @SerialEntry public AnimationSpeeds animationSpeeds = new AnimationSpeeds();
    @SerialEntry public RevealType revealType = RevealType.Stacked;

    public static class Events {
        Events() { }

        @SerialEntry public EventPolicy onScreenOpen = EventPolicy.Nothing;
        @SerialEntry public EventPolicy onMoving = EventPolicy.Nothing;
        @SerialEntry public EventPolicy onStandingStill = EventPolicy.Nothing;
        @SerialEntry public EventPolicy onPauseScreen = EventPolicy.Nothing;
        @SerialEntry public EventPolicy onSneaking = EventPolicy.Nothing;
        @SerialEntry public EventPolicy onFlying = EventPolicy.Nothing;
        @SerialEntry public boolean onUsingItem = false;
        @SerialEntry public boolean onMining = false;
        @SerialEntry public boolean onSwinging = false;
        @SerialEntry public boolean onAttacking = false;
    }
    @SerialEntry public Events events = new Events();

    @Category(name = "components", group = "statusEffects")
    @SerialEntry public boolean statusEffectTimer = true;

    /* COMPONENTS */
    public static class DefaultValues {
        DefaultValues() { }

        @FloatingPointRange(min = 0.1, max = 5.0, interval = 0.1)
        @SerialEntry public double speedMultiplier = 1.0;
        @NumericRange(min = 0, max = 200, interval = 5)
        @SerialEntry public int distance = 60;
        @FloatingPointRange(min = 0.0, max = 1.0, interval = 0.1)
        @SerialEntry public double maximumFade = 0.0d;
    }
    @Category(name = "advanced")
    @TransitiveObject
    @SerialEntry public DefaultValues defaultValues = new DefaultValues();

    public static class AdvancedComponent {
        AdvancedComponent() { }

        @Translation(key = "autohud.option.advanced.direction")
        @SerialEntry public ScrollDirection direction = ScrollDirection.Down;
        @Translation(key = "autohud.option.advanced.speedMultiplier")
        @FloatingPointRange(min = 0.1, max = 5.0, interval = 0.1)
        @SerialEntry public double speedMultiplier = -1;
        @Translation(key = "autohud.option.advanced.distance")
        @NumericRange(min = 0, max = 200, interval = 5)
        @SerialEntry public int distance = -1;
        @Translation(key = "autohud.option.advanced.maximumFade")
        @FloatingPointRange(min = 0.0, max = 1.0, interval = 0.1)
        @SerialEntry public double maximumFade = -1d;
        @Translation(key = "autohud.option.advanced.renderWhenHidden")
        @SerialEntry public boolean renderWhenHidden = false;
    }
    public static class AdvancedFadeOnlyComponent {
        AdvancedFadeOnlyComponent() { }

        @Translation(key = "autohud.option.advanced.speedMultiplier")
        @FloatingPointRange(min = 0.1, max = 5.0, interval = 0.1)
        @SerialEntry public double speedMultiplier = -1;
        @Translation(key = "autohud.option.advanced.maximumFade")
        @FloatingPointRange(min = 0.0, max = 1.0, interval = 0.1)
        @SerialEntry public double maximumFade = -1d;
    }
    public static class IComponent {
    }
    public static class SimpleComponent extends IComponent {
        SimpleComponent() { }
        @SerialEntry public boolean active = true;
    }
    public static class PolicyComponent extends IComponent {
        PolicyComponent() { }
        @SerialEntry public RevealPolicy policy = RevealPolicy.Changing;
    }
    public static class BooleanComponent extends IComponent {
        BooleanComponent() { }
        @SerialEntry public boolean active = true;
        @EnableIf(field = "active", value = EnableIf.BooleanPredicate.class)
        @SerialEntry public boolean onChange = true;
    }

    @Category(name = "components", group = "statusBars")
    @SerialEntry public PolicyComponent health = new PolicyComponent();
    @Category(name = "components", group = "statusBars")
    @SerialEntry public PolicyComponent hunger = new PolicyComponent();
    @Category(name = "components", group = "statusBars")
    @SerialEntry public boolean revealBarsWhenHoldingConsumableItem = true;
    @Category(name = "components", group = "statusBars")
    @SerialEntry public PolicyComponent air = new PolicyComponent();
    @Category(name = "components", group = "statusBars")
    @SerialEntry public PolicyComponent armor = new PolicyComponent();
    @Category(name = "components", group = "statusBars")
    @SerialEntry public BooleanComponent experience = new BooleanComponent();
    @Category(name = "components", group = "statusBars")
    @EnableIf(field = "experience.active", value = EnableIf.BooleanPredicate.class)
    @SerialEntry public BooleanComponent experienceBar = new BooleanComponent();
    @Category(name = "components", group = "statusBars")
    @SerialEntry public boolean revealExperienceTextOnTargetingEnchantingBlock = true;
    @Category(name = "components", group = "statusBars")
    @SerialEntry public BooleanComponent mountJumpBar = new BooleanComponent();
    @Category(name = "components", group = "statusBars")
    @SerialEntry public PolicyComponent mountHealth = new PolicyComponent();
    @Category(name = "components", group = "statusEffects")
    @SerialEntry public BooleanComponent statusEffects = new BooleanComponent();
    @Category(name = "components", group = "statusEffects")
    @SerialEntry public boolean hidePersistentStatusEffects = true;
    @Category(name = "components", group = "statusEffects")
    @SerialEntry public boolean showHiddenStatusEffects = false;

    @Category(name = "components")
    @SerialEntry public HotbarComponents hotbar = new HotbarComponents();
    @Category(name = "components")
    @SerialEntry public ScoreboardComponents scoreboard = new ScoreboardComponents();

    @Category(name = "components")
    @SerialEntry public ChatComponent chat = new ChatComponent();
    @Category(name = "components", group = "various")
    @SerialEntry public SimpleComponent actionBar = new SimpleComponent();
    @Category(name = "components", group = "various")
    @SerialEntry public SimpleComponent bossBar = new SimpleComponent();

    @Category(name = "components")
    @SerialEntry public BooleanComponent crosshair = new BooleanComponent();
    @Category(name = "components")
    @SerialEntry public List<ItemOrTag> crosshairAlwaysVisible = List.of(new ItemOrTag(Items.BOW), new ItemOrTag(Items.CROSSBOW), new ItemOrTag(Items.TRIDENT));

    @Category(name = "advanced")
    @Label(key = "autohud.option.advanced.label")
    @TransitiveObject
    @SerialEntry public AdvancedComponents advanced = new AdvancedComponents();

    
    public static class HotbarComponents {
        @SerialEntry public BooleanComponent hotbar = new BooleanComponent();
        @EnableIf(field = "hotbar.active", value = EnableIf.BooleanPredicate.class)
        @SerialEntry public boolean onDamageChange = false;
        @EnableIf(field = "hotbar.active", value = EnableIf.BooleanPredicate.class)
        @SerialEntry public boolean onStackSizeChange = true;
        @EnableIf(field = "hotbar.active", value = EnableIf.BooleanPredicate.class)
        @SerialEntry public boolean onSlotChange = true;
        @EnableIf(field = "hotbar.active", value = EnableIf.BooleanPredicate.class)
        @SerialEntry public boolean onLowDurability = true;
        @NumericRange(min = 0, max = 100, interval = 1)
        @EnableIf(field = "hotbar.active", value = EnableIf.BooleanPredicate.class)
        @EnableIf(field = "onLowDurability", value = EnableIf.BooleanPredicate.class)
        @SerialEntry public int durabilityPercentage = 10;
        @EnableIf(field = "hotbar.active", value = EnableIf.BooleanPredicate.class)
        @EnableIf(field = "onLowDurability", value = EnableIf.BooleanPredicate.class)
        @SerialEntry public int durabilityTotal = 20;
        @EnableIf(field = "hotbar.active", value = EnableIf.BooleanPredicate.class)
        @FloatingPointRange(min = 0.0f, max = 1.0f, interval = 0.1f)
        @SerialEntry public float maximumFadeHotbarItems = 0.0f;
        @EnableIf(field = "hotbar.active", value = EnableIf.BooleanPredicate.class)
        @EnableIf(field = "experience.active", value = EnableIf.BooleanPredicate.class)
        @SerialEntry public boolean revealExperienceTextWithHotbar = true;
        @SerialEntry public boolean hideTooltip = false;
    }
    
    public static class ScoreboardComponents {
        @SerialEntry public BooleanComponent scoreboard = new BooleanComponent();
        @EnableIf(field = "scoreboard.active", value = EnableIf.BooleanPredicate.class)
        @SerialEntry public boolean onScoreChange = true;
        @EnableIf(field = "scoreboard.active", value = EnableIf.BooleanPredicate.class)
        @SerialEntry public boolean onTeamChange = true;
    }

    public static class ChatComponent {
        @SerialEntry public SimpleComponent chat = new SimpleComponent();
        @EnableIf(field = "chat.active", value = EnableIf.BooleanPredicate.class)
        @SerialEntry public boolean displayIndicator = true;
    }
    
    public static class AdvancedComponents {
        @Translation(key = "autohud.group.hotbar")
        @SerialEntry public AdvancedComponent hotbar = new AdvancedComponent();
        @Translation(key = "autohud.group.health")
        @SerialEntry public AdvancedComponent health = new AdvancedComponent();
        @Translation(key = "autohud.group.armor")
        @SerialEntry public AdvancedComponent armor = new AdvancedComponent();
        @Translation(key = "autohud.group.hunger")
        @SerialEntry public AdvancedComponent hunger = new AdvancedComponent();
        @Translation(key = "autohud.group.air")
        @SerialEntry public AdvancedComponent air = new AdvancedComponent();
        @Translation(key = "autohud.group.experience")
        @SerialEntry public AdvancedComponent experience = new AdvancedComponent();
        @Translation(key = "autohud.group.mountJumpBar")
        @SerialEntry public AdvancedComponent mountJumpBar = new AdvancedComponent();
        @Translation(key = "autohud.group.mountHealth")
        @SerialEntry public AdvancedComponent mountHealth = new AdvancedComponent();
        @Translation(key = "autohud.group.statusEffects")
        @SerialEntry public AdvancedComponent statusEffects = new AdvancedComponent();
        @Translation(key = "autohud.group.scoreboard")
        @SerialEntry public AdvancedComponent scoreboard = new AdvancedComponent();
        @Translation(key = "autohud.group.crosshair")
        @SerialEntry public AdvancedFadeOnlyComponent crosshair = new AdvancedFadeOnlyComponent();
        @Translation(key = "autohud.group.chat")
        @SerialEntry public AdvancedComponent chat = new AdvancedComponent();
        @Translation(key = "autohud.group.actionBar")
        @SerialEntry public AdvancedComponent actionBar = new AdvancedComponent();
        @Translation(key = "autohud.group.bossBar")
        @SerialEntry public AdvancedComponent bossBar = new AdvancedComponent();
        private AdvancedComponents() {
            statusEffects.direction = ScrollDirection.Up;
            bossBar.direction = ScrollDirection.Up;
            chat.direction = ScrollDirection.Left;
            chat.distance = 100;
            scoreboard.direction = ScrollDirection.Right;
            scoreboard.distance = 100;
            crosshair.maximumFade = 0.1;
            crosshair.speedMultiplier = 2.5;
        }
    }

    /* DEFAULT OVERRIDES */
    public Config() {
        hunger.policy = RevealPolicy.Low;
        air.policy = RevealPolicy.NotFull;
        chat.chat.active = false;
        actionBar.active = false;
        bossBar.active = false;
    }

    /* OPTIONS END */

    public Object clone() throws CloneNotSupportedException { return super.clone(); }
}
