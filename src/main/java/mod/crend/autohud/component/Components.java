package mod.crend.autohud.component;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.state.BooleanComponentState;
import mod.crend.autohud.component.state.ComponentState;
import mod.crend.autohud.component.state.EnhancedPolicyComponentState;
import mod.crend.autohud.component.state.ItemStackComponentState;
import mod.crend.autohud.component.state.PolicyComponentState;
import mod.crend.autohud.component.state.ScoreboardComponentState;
import mod.crend.autohud.component.state.ValueComponentState;
//? if <1.21.2 {
import net.minecraft.item.Equipment;
//?} else {
/*import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.item.ItemStack;
*///?}

import java.util.function.Supplier;

public class Components {
	public static final Supplier<Boolean> TARGET_CHAT = () -> AutoHud.targetChat;
	public static final Supplier<Boolean> TARGET_CROSSHAIR = () -> AutoHud.targetCrosshair;
	public static final Supplier<Boolean> TARGET_EXPERIENCE_BAR = () -> AutoHud.targetExperienceBar;
	public static final Supplier<Boolean> TARGET_HOTBAR = () -> AutoHud.targetHotbar;
	public static final Supplier<Boolean> TARGET_SCOREBOARD = () -> AutoHud.targetScoreboard;
	public static final Supplier<Boolean> TARGET_STATUS_BARS = () -> AutoHud.targetStatusBars;

	public static Component Armor = Component.builder("armor")
			.isTargeted(TARGET_STATUS_BARS)
			.config(AutoHud.config.armor())
			.inMainHud()
			.state(player -> new EnhancedPolicyComponentState(Components.Armor,
					player::getArmor,
					20,
					() ->
							//? if <1.21.2 {
							player.getMainHandStack().getItem() instanceof Equipment equipment
									&& equipment.getSlotType().isArmorSlot()
									&& player.canEquip(player.getMainHandStack())
							//?} else {
							/*{
								ItemStack mainHandStack = player.getMainHandStack();
								EquippableComponent equipment = mainHandStack.get(DataComponentTypes.EQUIPPABLE);
								return equipment != null
									&& equipment.slot().isArmorSlot()
									&& player.canEquip(mainHandStack, equipment.slot());
							}
							*///?}
					, true
			))
			.build();

	public static Component Health = Component.builder("health")
			.isTargeted(TARGET_STATUS_BARS)
			.config(AutoHud.config.health())
			.stackComponents(Armor)
			.inMainHud()
			.state(player -> new EnhancedPolicyComponentState(Components.Health,
					() -> Math.round(player.getHealth()),
					() -> Math.round(player.getMaxHealth()),
					() -> AutoHud.config.revealBarsWhenHoldingConsumableItem()
							&& State.canHeal()
			))
			.build();

	public static Component Air = Component.builder("air")
			.isTargeted(TARGET_STATUS_BARS)
			.config(AutoHud.config.air())
			.inMainHud()
			.state(player -> new PolicyComponentState(Components.Air, player::getAir, player::getMaxAir))
			.build();

	public static Component Hunger = Component.builder("hunger")
			.isTargeted(TARGET_STATUS_BARS)
			.config(AutoHud.config.hunger())
			.stackComponents(Air)
			.inMainHud()
			.state(player -> new EnhancedPolicyComponentState(Components.Hunger,
					() -> player.getHungerManager().getFoodLevel(),
					20,
					() -> AutoHud.config.revealBarsWhenHoldingConsumableItem()
							&& player.getHungerManager().getFoodLevel() < 20
							&& State.isFood(player.getMainHandStack())
			))
			.build();


	public static Component MountHealth = Component.builder("mount_health")
			.isTargeted(TARGET_STATUS_BARS)
			.config(AutoHud.config.mountHealth())
			.stackComponents(Air)
			.inMainHud()
			.build();

	public static Component MountJumpBar = Component.builder("mount_jump_bar")
			.isTargeted(TARGET_STATUS_BARS)
			.config(AutoHud.config.mountJumpBar())
			.inMainHud()
			.state(player -> new ComponentState(Components.MountJumpBar))
			.build();


	public static Component ExperienceLevel = Component.builder("experience_level")
			.isTargeted(TARGET_EXPERIENCE_BAR)
			.config(AutoHud.config.experience())
			.inMainHud()
			.state(player -> new ValueComponentState<>(Components.ExperienceLevel, () -> player.experienceLevel, true))
			.build();

	public static Component ExperienceBar = Component.builder("experience_bar")
			.isTargeted(TARGET_EXPERIENCE_BAR)
			.config(AutoHud.config.experienceBar())
			.stackComponents(Health, Hunger, MountHealth, ExperienceLevel)
			.inMainHud()
			.state(player -> new ValueComponentState<>(Components.ExperienceBar, () -> player.totalExperience, true))
			.build();


	public static Component Hotbar = Component.builder("hotbar")
			.isTargeted(TARGET_HOTBAR)
			.config(AutoHud.config.hotbar())
			.stackComponents(ExperienceBar)
			.inMainHud()
			.state(player -> new ComponentState(Components.Hotbar))
			.build();

	public static Component Tooltip = Component.builder("tooltip")
			.isTargeted(TARGET_HOTBAR)
			.config(AutoHud.config.tooltip())
			.inMainHud()
			.state(player -> new ItemStackComponentState(Components.Tooltip, player::getMainHandStack, true))
			.build();


	public static Component Scoreboard = Component.builder("scoreboard")
			.isTargeted(TARGET_SCOREBOARD)
			.config(AutoHud.config.scoreboard())
			.state(player -> new ScoreboardComponentState(Components.Scoreboard))
			.build();


	public static Component Crosshair = Component.builder("crosshair")
			.isTargeted(TARGET_CROSSHAIR)
			.config(AutoHud.config.crosshair())
			.state(player -> new BooleanComponentState(Components.Crosshair, State::shouldShowCrosshair))
			.build();


	public static Component Chat = Component.builder("chat")
			.isTargeted(TARGET_CHAT)
			.config(AutoHud.config.chat())
			.state(player -> new ComponentState(Components.Chat))
			.build();

	public static Component ChatIndicator = Component.builder("chat_indicator")
			.isTargeted(TARGET_CHAT)
			.config(AutoHud.config.chatIndicator())
			.state(player -> new ComponentState(Components.ChatIndicator))
			.register(false)
			.build();


	public static Component ActionBar = Component.builder("action_bar")
			.config(AutoHud.config.actionBar())
			.state(player -> new ComponentState(Components.ActionBar))
			.build();

	public static Component BossBar = Component.builder("boss_bar")
			.config(AutoHud.config.bossBar())
			.state(player -> new ComponentState(Components.BossBar))
			.build();
}
