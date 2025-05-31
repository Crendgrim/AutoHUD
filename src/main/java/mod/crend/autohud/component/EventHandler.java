package mod.crend.autohud.component;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.state.PolicyComponentState;
import mod.crend.autohud.config.EventPolicy;
import mod.crend.libbamboo.event.*;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.hit.HitResult;

public class EventHandler {
	public static void registerEvents() {
		ScreenEvent.CHAT.register(() -> {
			Components.Chat.revealNow();
			Components.ChatIndicator.hide();
		});
		ScreenEvent.OPEN.register(screen -> {
			if (AutoHud.config.onScreenOpen() == EventPolicy.Hide) Component.forceHideAll();
		});
		ScreenEvent.TICK.register(screen -> {
			if (AutoHud.config.onScreenOpen() == EventPolicy.Reveal) Component.revealAll();
		});
		ScreenEvent.CLOSE.register(() -> {
			switch (AutoHud.config.onScreenOpen()) {
				case Hide -> Component.updateAll();
				case Reveal -> Component.hideAll();
			}
		});
		ScreenEvent.PAUSE_TICK.register(() -> {
			switch (AutoHud.config.onPauseScreen()) {
				case Reveal -> Component.revealAll();
				case Hide -> Component.forceHideAll();
			}
		});
		ScreenEvent.UNPAUSE.register(() -> {
			switch (AutoHud.config.onPauseScreen()) {
				case Hide -> Component.updateAll();
				case Reveal -> Component.hideAll();
			}
		});

		MoveEvent.MOVING.register((player, position) -> {
			switch (AutoHud.config.onMoving()) {
				case Reveal -> Component.revealAll();
				case Hide -> Component.forceHideAll();
				default -> {
				}
			}
		});
		MoveEvent.STANDING_STILL.register((player, position) -> {
			switch (AutoHud.config.onStandingStill()) {
				case Reveal -> Component.revealAll();
				case Hide -> Component.forceHideAll();
				default -> {
					if (AutoHud.config.onMoving() == EventPolicy.Hide) Component.updateAll();
				}
			}
		});
		SneakEvent.TICK.register(player -> {
			switch (AutoHud.config.onSneaking()) {
				case Reveal -> Component.revealAll();
				case Hide -> Component.forceHideAll();
			}
		});
		SneakEvent.STOP.register(player -> {
			switch (AutoHud.config.onSneaking()) {
				case Reveal -> Component.hideAll();
				case Hide -> Component.updateAll();
			}
		});
		FlyEvent.TICK.register(player -> {
			switch (AutoHud.config.onFlying()) {
				case Reveal -> Component.revealAll();
				case Hide -> Component.forceHideAll();
			}
		});
		FlyEvent.STOP.register(player -> {
			switch (AutoHud.config.onFlying()) {
				case Reveal -> Component.hideAll();
				case Hide -> Component.updateAll();
			}
		});

		StatusEvent.HEALTH.register((value, old, max) -> Components.Health.updateState());
		StatusEvent.FOOD.register((value, old, max) -> Components.Hunger.updateState());
		StatusEvent.AIR.register((value, old, max) -> Components.Air.updateState());
		StatusEvent.EXPERIENCE.register((progress, total, level) -> {
			Components.ExperienceLevel.updateState();
			Components.ExperienceBar.updateState();
		});
		HotbarEvent.MAIN_HAND_CHANGE.register((stack, type) -> {
			switch (type) {
				case ITEM -> {
					if (Components.Hotbar.config.onChange()) {
						Components.Hotbar.revealCombined();
					}
				}
				case STACK_COUNT -> {
					if (AutoHud.config.isHotbarOnStackSizeChange()) {
						Components.Hotbar.revealCombined();
					}
				}
				case DAMAGE -> {
					if (AutoHud.config.isHotbarOnDamageChange()) {
						Components.Hotbar.revealCombined();
					}
				}
			}
			Components.Health.updateStateNextTick();
			Components.Hunger.updateStateNextTick();
			Components.Armor.updateStateNextTick();
		});
		HotbarEvent.OFF_HAND_CHANGE.register((stack, type) -> {
			switch (type) {
				case ITEM -> {
					if (Components.Hotbar.config.onChange()) {
						Components.Hotbar.revealCombined();
					}
				}
				case STACK_COUNT -> {
					if (AutoHud.config.isHotbarOnStackSizeChange()) {
						Components.Hotbar.revealCombined();
					}
				}
				case DAMAGE -> {
					if (AutoHud.config.isHotbarOnDamageChange()) {
						Components.Hotbar.revealCombined();
					}
				}
			}
			Components.Health.updateStateNextTick();
			Components.Hunger.updateStateNextTick();
			Components.Armor.updateStateNextTick();
		});
		HotbarEvent.SELECTED_SLOT_CHANGE.register(() -> {
			if (AutoHud.config.isHotbarOnSlotChange()) {
				Components.Hotbar.revealCombined();

				if (AutoHud.config.revealExperienceTextWithHotbar()) {
					Components.ExperienceLevel.synchronizeFrom(Components.Hotbar);
				}
			}
		});

		MountEvent.START.register((player, vehicle) -> {
			Components.MountHealth.state = new PolicyComponentState(Components.MountHealth, () -> (int) vehicle.getHealth(), () -> (int) vehicle.getMaxHealth());
			Components.MountHealth.revealCombined();
		});
		MountEvent.MOUNT_HEALTH_CHANGE.register((value, old, max) -> Components.MountHealth.updateState());
		MountEvent.MOUNT_JUMP.register(vehicle -> {
			if (AutoHud.config.mountJumpBar().onChange()) Components.MountJumpBar.revealNow();
		});
		MountEvent.STOP.register(player -> Components.MountHealth.state = null);

		InteractionEvent.USING_ITEM_TICK.register(player -> {
			if (AutoHud.config.onUsingItem()) {
				Component.revealAll();
			}
		});
		InteractionEvent.MINING_TICK.register(player -> {
			if (AutoHud.config.onMining()) {
				Component.revealAll();
			}
		});
		InteractionEvent.ATTACK.register((player, hitResult) -> {
			if (AutoHud.config.onSwinging()
					|| (AutoHud.config.onAttacking() && hitResult.getType() == HitResult.Type.ENTITY)
			) {
				Component.revealAll();
			}
		});

		TargetEvent.TARGETED_BLOCK_TICK.register(((blockPos, blockState) -> {
			if (AutoHud.config.revealExperienceTextOnTargetingEnchantingBlock()) {
				if (blockState.getBlock() == Blocks.ENCHANTING_TABLE || blockState.isIn(BlockTags.ANVIL)) {
					Components.ExperienceLevel.reveal();
				}
			}
		}));

	}
}
