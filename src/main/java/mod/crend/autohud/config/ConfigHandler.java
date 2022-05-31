package mod.crend.autohud.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.client.gui.screen.Screen;

public class ConfigHandler {
    public static Screen getScreen(Screen parent) {
        return AutoConfig.getConfigScreen(Config.class, parent).get();
    }

    Config config;

    public ConfigHandler() {
        AutoConfig.register(Config.class, JanksonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(Config.class).getConfig();
        init();
    }

    public boolean dynamicOnLoad() { return config.dynamicOnLoad; }
    public int timeRevealed() { return config.ticksRevealed; }
    public double animationSpeed() { return config.animationSpeed; }
    public RevealType revealType() { return config.revealType; }
    public boolean statusEffectTimer() { return config.statusEffectTimer; }

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
    public static final IComponent None = new IComponent(new Config.IComponent(), new Config.AdvancedComponent(), new Config.DefaultValues()) {
        @Override
        public boolean active() {
            return true;
        }
    };

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
    public boolean isHotbarOnSlotChange() { return config.hotbar.onSlotChange; }
    public boolean isHotbarOnLowDurability() { return config.hotbar.onLowDurability; }
    public int getHotbarDurabilityPercentage() { return config.hotbar.durabilityPercentage; }
    public int getHotbarDurabilityTotal() { return config.hotbar.durabilityTotal; }
    public BooleanComponent statusEffects() { return statusEffects; }
    public boolean hidePersistentStatusEffects() { return config.hidePersistentStatusEffects; }
    public BooleanComponent scoreboard() { return scoreboard; }
    public boolean shouldRevealScoreboardOnTitleChange() { return config.scoreboard.scoreboard.onChange; }
    public boolean shouldRevealScoreboardOnScoreChange() { return config.scoreboard.onScoreChange; }
    public boolean shouldRevealScoreboardOnTeamChange() { return config.scoreboard.onTeamChange; }

    private void init() {
        health = new PolicyComponent(config.health, config.advanced.health, config.defaultValues);
        armor = new PolicyComponent(config.armor, config.advanced.armor, config.defaultValues);
        hunger = new PolicyComponent(config.hunger, config.advanced.hunger, config.defaultValues);
        air = new PolicyComponent(config.air, config.advanced.air, config.defaultValues);
        experience = new BooleanComponent(config.experience, config.advanced.experience, config.defaultValues);
        mountJumpBar = new BooleanComponent(config.mountJumpBar, config.advanced.mountJumpBar, config.defaultValues);
        mountHealth = new PolicyComponent(config.mountHealth, config.advanced.mountHealth, config.defaultValues);
        hotbar = new BooleanComponent(config.hotbar.hotbar, config.advanced.hotbar, config.defaultValues);
        statusEffects = new BooleanComponent(config.statusEffects, config.advanced.statusEffects, config.defaultValues);
        scoreboard = new BooleanComponent(config.scoreboard.scoreboard, config.advanced.scoreboard, config.defaultValues);
    }
}
