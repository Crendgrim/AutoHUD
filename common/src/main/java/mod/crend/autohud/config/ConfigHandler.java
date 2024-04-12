package mod.crend.autohud.config;

import mod.crend.libbamboo.opt.ConfigStore;

public class ConfigHandler {

    public static final ConfigStore<Config> CONFIG_STORE = new ConfigStore<>(Config.class);

    public ConfigHandler() {
        init();
    }

    public boolean dynamicOnLoad() { return CONFIG_STORE.config().dynamicOnLoad; }
    public int timeRevealed() { return CONFIG_STORE.config().ticksRevealed; }
    public boolean animationMove() { return CONFIG_STORE.config().animationMove; }
    public boolean animationFade() { return CONFIG_STORE.config().animationFade; }
    public boolean animationNone() { return !CONFIG_STORE.config().animationMove && !CONFIG_STORE.config().animationFade; }
    public double animationSpeed() { return CONFIG_STORE.config().animationSpeed; }
    public double animationSpeedMoveIn() { return CONFIG_STORE.config().animationSpeeds.moveIn == 0 ? CONFIG_STORE.config().animationSpeed : CONFIG_STORE.config().animationSpeeds.moveIn; }
    public double animationSpeedMoveOut() { return CONFIG_STORE.config().animationSpeeds.moveOut == 0 ? CONFIG_STORE.config().animationSpeed : CONFIG_STORE.config().animationSpeeds.moveOut; }
    public double animationSpeedFadeIn() { return CONFIG_STORE.config().animationSpeeds.fadeIn == 0 ? CONFIG_STORE.config().animationSpeed : CONFIG_STORE.config().animationSpeeds.fadeIn; }
    public double animationSpeedFadeOut() { return CONFIG_STORE.config().animationSpeeds.fadeOut == 0 ? CONFIG_STORE.config().animationSpeed : CONFIG_STORE.config().animationSpeeds.fadeOut; }
    public RevealType revealType() { return CONFIG_STORE.config().revealType; }
    public EventPolicy onScreenOpen() { return CONFIG_STORE.config().events.onScreenOpen; }
    public EventPolicy onMoving() { return CONFIG_STORE.config().events.onMoving; }
    public EventPolicy onStandingStill() { return CONFIG_STORE.config().events.onStandingStill; }
    public EventPolicy onPauseScreen() { return CONFIG_STORE.config().events.onPauseScreen; }
    public EventPolicy onSneaking() { return CONFIG_STORE.config().events.onSneaking; }
    public EventPolicy onFlying() { return CONFIG_STORE.config().events.onFlying; }
    public boolean onUsingItem() { return CONFIG_STORE.config().events.onUsingItem; }
    public boolean onMining() { return CONFIG_STORE.config().events.onMining; }
    public boolean onSwinging() { return CONFIG_STORE.config().events.onSwinging; }
    public boolean onAttacking() { return CONFIG_STORE.config().events.onAttacking; }
    public boolean statusEffectTimer() { return CONFIG_STORE.config().statusEffectTimer; }

    public abstract static class IComponent {
        IComponent(Config.IComponent config, Config.AdvancedComponent values, Config.DefaultValues defaultValues) {
            this.config = config;
            this.values = values;
            this.defaultValues = defaultValues;
        }
        Config.IComponent config;
        Config.AdvancedComponent values;
        Config.DefaultValues defaultValues;

        public abstract boolean active();
        public boolean onChange() { return false; }

        public ScrollDirection direction() { return values.direction; }
        public double speedMultiplier() { return (values.speedMultiplier < 0 ? defaultValues.speedMultiplier : values.speedMultiplier); }
        public int distance() { return (values.distance < 0 ? defaultValues.distance : values.distance); }
        public double maximumFade() { return (values.maximumFade < 0 ? Math.min(defaultValues.maximumFade, 1.0d) : Math.min(values.maximumFade, 1.0d)); }
    }
    public abstract static class IFadeOnlyComponent extends IComponent {
        IFadeOnlyComponent(Config.IComponent config, Config.AdvancedFadeOnlyComponent values, Config.DefaultValues defaultValues) {
            super(config, new Config.AdvancedComponent(), defaultValues);
            this.fadeOnlyValues = values;
        }
        Config.AdvancedFadeOnlyComponent fadeOnlyValues;

        @Override
        public ScrollDirection direction() { return ScrollDirection.Up; }
        @Override
        public double speedMultiplier() { return (fadeOnlyValues.speedMultiplier < 0 ? defaultValues.speedMultiplier : fadeOnlyValues.speedMultiplier); }
        @Override
        public int distance() { return 0; }
        @Override
        public double maximumFade() { return (fadeOnlyValues.maximumFade < 0 ? Math.min(defaultValues.maximumFade, 1.0d) : Math.min(fadeOnlyValues.maximumFade, 1.0d)); }
    }
    public static class SimpleComponent extends IComponent {
        private SimpleComponent(Config.SimpleComponent config, Config.AdvancedComponent values, Config.DefaultValues defaultValues) {
            super(config, values, defaultValues);
        }

        @Override
        public boolean active() {
            return ((Config.SimpleComponent) config).active;
        }
    }

    public static class PolicyComponent extends IComponent {
        private PolicyComponent(Config.PolicyComponent config, Config.AdvancedComponent values, Config.DefaultValues defaultValues) {
            super(config, values, defaultValues);
        }

        @Override
        public boolean active() {
            return policy() != RevealPolicy.Always;
        }

        @Override
        public boolean onChange() {
            return policy() != RevealPolicy.Always;
        }

        public RevealPolicy policy() {
            return ((Config.PolicyComponent) config).policy;
        }
    }
    public static class BooleanComponent extends IComponent {
        private BooleanComponent(Config.BooleanComponent config, Config.AdvancedComponent values, Config.DefaultValues defaultValues) {
            super(config, values, defaultValues);
        }

        @Override
        public boolean active() {
            return ((Config.BooleanComponent) config).active;
        }

        @Override
        public boolean onChange() {
            return ((Config.BooleanComponent) config).onChange;
        }
    }
    public static class BooleanFadeOnlyComponent extends IFadeOnlyComponent {
        private BooleanFadeOnlyComponent(Config.BooleanComponent config, Config.AdvancedFadeOnlyComponent values, Config.DefaultValues defaultValues) {
            super(config, values, defaultValues);
        }

        @Override
        public boolean active() {
            return ((Config.BooleanComponent) config).active;
        }

        @Override
        public boolean onChange() {
            return ((Config.BooleanComponent) config).onChange;
        }
    }
    public static final IComponent None = new SimpleComponent(new Config.SimpleComponent(), new Config.AdvancedComponent(), new Config.DefaultValues());
    public static final IComponent DummyPolicyComponent = new PolicyComponent(new Config.PolicyComponent(), new Config.AdvancedComponent(), new Config.DefaultValues());
    public static final IComponent DummyBooleanComponent = new BooleanComponent(new Config.BooleanComponent(), new Config.AdvancedComponent(), new Config.DefaultValues());

    PolicyComponent health;
    PolicyComponent armor;
    PolicyComponent hunger;
    PolicyComponent air;
    BooleanComponent experience;
    BooleanComponent experienceBar;
    BooleanComponent mountJumpBar;
    PolicyComponent mountHealth;
    BooleanComponent hotbar;
    BooleanComponent statusEffects;
    BooleanComponent scoreboard;
    BooleanFadeOnlyComponent crosshair;
    SimpleComponent chat;
    IComponent chatIndicator;
    SimpleComponent actionBar;
    SimpleComponent bossBar;

    public PolicyComponent health() { return health; }
    public PolicyComponent armor() { return armor; }
    public PolicyComponent hunger() { return hunger; }
    public PolicyComponent air() { return air; }
    public BooleanComponent experience() { return experience; }
    public BooleanComponent experienceBar() { return experienceBar; }
    public BooleanComponent mountJumpBar() { return mountJumpBar; }
    public PolicyComponent mountHealth() { return mountHealth; }
    public BooleanComponent hotbar() { return hotbar; }
    public boolean isHotbarOnSlotChange() { return CONFIG_STORE.config().hotbar.onSlotChange; }
    public boolean isHotbarOnLowDurability() { return CONFIG_STORE.config().hotbar.onLowDurability; }
    public int getHotbarDurabilityPercentage() { return CONFIG_STORE.config().hotbar.durabilityPercentage; }
    public int getHotbarDurabilityTotal() { return CONFIG_STORE.config().hotbar.durabilityTotal; }
    public float getHotbarItemsMaximumFade() { return CONFIG_STORE.config().hotbar.maximumFadeHotbarItems; }
    public boolean revealExperienceTextWithHotbar() { return CONFIG_STORE.config().hotbar.revealExperienceTextWithHotbar; }
    public boolean revealExperienceTextOnTargetingEnchantingBlock() { return CONFIG_STORE.config().revealExperienceTextOnTargetingEnchantingBlock; }
    public BooleanComponent statusEffects() { return statusEffects; }
    public boolean hidePersistentStatusEffects() { return CONFIG_STORE.config().hidePersistentStatusEffects; }
    public BooleanComponent scoreboard() { return scoreboard; }
    public BooleanFadeOnlyComponent crosshair() { return crosshair; }
    public boolean shouldRevealScoreboardOnTitleChange() { return CONFIG_STORE.config().scoreboard.scoreboard.onChange; }
    public boolean shouldRevealScoreboardOnScoreChange() { return CONFIG_STORE.config().scoreboard.onScoreChange; }
    public boolean shouldRevealScoreboardOnTeamChange() { return CONFIG_STORE.config().scoreboard.onTeamChange; }
    public SimpleComponent chat() { return chat; }
    public IComponent chatIndicator() { return chatIndicator; }
    public SimpleComponent actionBar() { return actionBar; }
    public SimpleComponent bossBar() { return bossBar; }

    private void init() {
        health = new PolicyComponent(CONFIG_STORE.config().health, CONFIG_STORE.config().advanced.health, CONFIG_STORE.config().defaultValues);
        armor = new PolicyComponent(CONFIG_STORE.config().armor, CONFIG_STORE.config().advanced.armor, CONFIG_STORE.config().defaultValues);
        hunger = new PolicyComponent(CONFIG_STORE.config().hunger, CONFIG_STORE.config().advanced.hunger, CONFIG_STORE.config().defaultValues);
        air = new PolicyComponent(CONFIG_STORE.config().air, CONFIG_STORE.config().advanced.air, CONFIG_STORE.config().defaultValues);
        experience = new BooleanComponent(CONFIG_STORE.config().experience, CONFIG_STORE.config().advanced.experience, CONFIG_STORE.config().defaultValues);
        experienceBar = new BooleanComponent(CONFIG_STORE.config().experienceBar, CONFIG_STORE.config().advanced.experience, CONFIG_STORE.config().defaultValues);
        mountJumpBar = new BooleanComponent(CONFIG_STORE.config().mountJumpBar, CONFIG_STORE.config().advanced.mountJumpBar, CONFIG_STORE.config().defaultValues);
        mountHealth = new PolicyComponent(CONFIG_STORE.config().mountHealth, CONFIG_STORE.config().advanced.mountHealth, CONFIG_STORE.config().defaultValues);
        hotbar = new BooleanComponent(CONFIG_STORE.config().hotbar.hotbar, CONFIG_STORE.config().advanced.hotbar, CONFIG_STORE.config().defaultValues);
        statusEffects = new BooleanComponent(CONFIG_STORE.config().statusEffects, CONFIG_STORE.config().advanced.statusEffects, CONFIG_STORE.config().defaultValues);
        scoreboard = new BooleanComponent(CONFIG_STORE.config().scoreboard.scoreboard, CONFIG_STORE.config().advanced.scoreboard, CONFIG_STORE.config().defaultValues);
        crosshair = new BooleanFadeOnlyComponent(CONFIG_STORE.config().crosshair, CONFIG_STORE.config().advanced.crosshair, CONFIG_STORE.config().defaultValues);
        chat = new SimpleComponent(CONFIG_STORE.config().chat.chat, CONFIG_STORE.config().advanced.chat, CONFIG_STORE.config().defaultValues);
        chatIndicator = new IComponent(new Config.IComponent(), CONFIG_STORE.config().advanced.chat, CONFIG_STORE.config().defaultValues) {
            @Override
            public boolean active() {
                return CONFIG_STORE.config().chat.displayIndicator;
            }
        };
        actionBar = new SimpleComponent(CONFIG_STORE.config().actionBar, CONFIG_STORE.config().advanced.actionBar, CONFIG_STORE.config().defaultValues);
        bossBar = new SimpleComponent(CONFIG_STORE.config().bossBar, CONFIG_STORE.config().advanced.bossBar, CONFIG_STORE.config().defaultValues);
    }
}
