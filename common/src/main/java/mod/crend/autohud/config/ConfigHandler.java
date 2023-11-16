package mod.crend.autohud.config;

import mod.crend.yaclx.opt.ConfigStore;

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
    public static class SimpleComponent extends IComponent {
        private SimpleComponent(Config.SimpleComponent config, Config.AdvancedComponent values, Config.DefaultValues defaultValues) {
            super(config, values, defaultValues);
        }
        boolean active = true;

        @Override
        public boolean active() {
            return active;
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
    public static final IComponent None = new SimpleComponent(new Config.SimpleComponent(), new Config.AdvancedComponent(), new Config.DefaultValues());
    public static final IComponent DummyPolicyComponent = new PolicyComponent(new Config.PolicyComponent(), new Config.AdvancedComponent(), new Config.DefaultValues());
    public static final IComponent DummyBooleanComponent = new BooleanComponent(new Config.BooleanComponent(), new Config.AdvancedComponent(), new Config.DefaultValues());

    PolicyComponent health;
    PolicyComponent armor;
    PolicyComponent hunger;
    PolicyComponent air;
    BooleanComponent experience;
    BooleanComponent mountJumpBar;
    PolicyComponent mountHealth;
    BooleanComponent hotbar;
    BooleanComponent statusEffects;
    BooleanComponent scoreboard;

    public PolicyComponent health() { return health; }
    public PolicyComponent armor() { return armor; }
    public PolicyComponent hunger() { return hunger; }
    public PolicyComponent air() { return air; }
    public BooleanComponent experience() { return experience; }
    public BooleanComponent mountJumpBar() { return mountJumpBar; }
    public PolicyComponent mountHealth() { return mountHealth; }
    public BooleanComponent hotbar() { return hotbar; }
    public boolean isHotbarOnSlotChange() { return CONFIG_STORE.config().hotbar.onSlotChange; }
    public boolean isHotbarOnLowDurability() { return CONFIG_STORE.config().hotbar.onLowDurability; }
    public int getHotbarDurabilityPercentage() { return CONFIG_STORE.config().hotbar.durabilityPercentage; }
    public int getHotbarDurabilityTotal() { return CONFIG_STORE.config().hotbar.durabilityTotal; }
    public float getHotbarItemsMaximumFade() { return CONFIG_STORE.config().hotbar.maximumFadeHotbarItems; }
    public BooleanComponent statusEffects() { return statusEffects; }
    public boolean hidePersistentStatusEffects() { return CONFIG_STORE.config().hidePersistentStatusEffects; }
    public BooleanComponent scoreboard() { return scoreboard; }
    public boolean shouldRevealScoreboardOnTitleChange() { return CONFIG_STORE.config().scoreboard.scoreboard.onChange; }
    public boolean shouldRevealScoreboardOnScoreChange() { return CONFIG_STORE.config().scoreboard.onScoreChange; }
    public boolean shouldRevealScoreboardOnTeamChange() { return CONFIG_STORE.config().scoreboard.onTeamChange; }

    private void init() {
        health = new PolicyComponent(CONFIG_STORE.config().health, CONFIG_STORE.config().advanced.health, CONFIG_STORE.config().defaultValues);
        armor = new PolicyComponent(CONFIG_STORE.config().armor, CONFIG_STORE.config().advanced.armor, CONFIG_STORE.config().defaultValues);
        hunger = new PolicyComponent(CONFIG_STORE.config().hunger, CONFIG_STORE.config().advanced.hunger, CONFIG_STORE.config().defaultValues);
        air = new PolicyComponent(CONFIG_STORE.config().air, CONFIG_STORE.config().advanced.air, CONFIG_STORE.config().defaultValues);
        experience = new BooleanComponent(CONFIG_STORE.config().experience, CONFIG_STORE.config().advanced.experience, CONFIG_STORE.config().defaultValues);
        mountJumpBar = new BooleanComponent(CONFIG_STORE.config().mountJumpBar, CONFIG_STORE.config().advanced.mountJumpBar, CONFIG_STORE.config().defaultValues);
        mountHealth = new PolicyComponent(CONFIG_STORE.config().mountHealth, CONFIG_STORE.config().advanced.mountHealth, CONFIG_STORE.config().defaultValues);
        hotbar = new BooleanComponent(CONFIG_STORE.config().hotbar.hotbar, CONFIG_STORE.config().advanced.hotbar, CONFIG_STORE.config().defaultValues);
        statusEffects = new BooleanComponent(CONFIG_STORE.config().statusEffects, CONFIG_STORE.config().advanced.statusEffects, CONFIG_STORE.config().defaultValues);
        scoreboard = new BooleanComponent(CONFIG_STORE.config().scoreboard.scoreboard, CONFIG_STORE.config().advanced.scoreboard, CONFIG_STORE.config().defaultValues);
    }
}
