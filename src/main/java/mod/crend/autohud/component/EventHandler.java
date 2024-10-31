package mod.crend.autohud.component;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.state.PolicyComponentState;
import mod.crend.autohud.config.EventPolicy;
import mod.crend.libbamboo.event.*;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class EventHandler {
	public static void registerEvents() {
		ScreenEvent.CHAT.register(() -> {
			Component.Chat.revealNow();
			Component.ChatIndicator.hide();
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

		StatusEvent.HEALTH.register((value, old, max) -> Component.Health.updateState());
		StatusEvent.FOOD.register((value, old, max) -> Component.Hunger.updateState());
		StatusEvent.AIR.register((value, old, max) -> Component.Air.updateState());
		StatusEvent.EXPERIENCE.register((progress, total, level) -> {
			Component.ExperienceLevel.updateState();
			Component.ExperienceBar.updateState();
		});
		HotbarEvent.MAIN_HAND_CHANGE.register(stack -> {
			Component.Health.state.updateNextTick();
			Component.Hunger.state.updateNextTick();
			Component.Armor.state.updateNextTick();
		});
		HotbarEvent.SELECTED_SLOT_CHANGE.register(() -> {
			if (AutoHud.config.isHotbarOnSlotChange()) {
				Component.Hotbar.revealCombined();
			}
		});

		MountEvent.START.register((player, vehicle) -> {
			Component.MountHealth.state = new PolicyComponentState(Component.MountHealth, () -> (int) vehicle.getHealth(), () -> (int) vehicle.getMaxHealth());
			Component.MountHealth.revealCombined();
		});
		MountEvent.MOUNT_HEALTH_CHANGE.register((value, old, max) -> Component.MountHealth.updateState());
		MountEvent.MOUNT_JUMP.register(vehicle -> {
			if (AutoHud.config.mountJumpBar().onChange()) Component.MountJumpBar.revealNow();
		});
		MountEvent.STOP.register(player -> Component.MountHealth.state = null);

		InteractionEvent.USING_ITEM_TICK.register(player -> {
			if (AutoHud.config.onUsingItem()) {
				Component.revealAll(1);
			}
		});
		InteractionEvent.MINING_TICK.register(player -> {
			if (AutoHud.config.onMining()) {
				Component.revealAll(1);
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
					Component.ExperienceLevel.reveal();
				}
			}
		}));

	}
}
