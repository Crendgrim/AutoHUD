package mod.crend.autohud.neoforge;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.autohud.render.RenderWrapper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static net.neoforged.neoforge.client.gui.VanillaGuiLayers.*;

public class AutoHudGui {

	static Map<Identifier, RenderWrapper> RENDER_WRAPPERS = new HashMap<>();
	static {
		RENDER_WRAPPERS.put(PLAYER_HEALTH, RenderWrapper.HEALTH);
		RENDER_WRAPPERS.put(ARMOR_LEVEL, RenderWrapper.ARMOR);
		RENDER_WRAPPERS.put(FOOD_LEVEL, RenderWrapper.HUNGER);
		RENDER_WRAPPERS.put(AIR_LEVEL, RenderWrapper.AIR);
		RENDER_WRAPPERS.put(VEHICLE_HEALTH, RenderWrapper.MOUNT_HEALTH);
		RENDER_WRAPPERS.put(JUMP_METER, RenderWrapper.MOUNT_JUMP_BAR);
		RENDER_WRAPPERS.put(EXPERIENCE_BAR, RenderWrapper.EXPERIENCE_BAR);
		RENDER_WRAPPERS.put(EXPERIENCE_LEVEL, RenderWrapper.EXPERIENCE_LEVEL);

		RENDER_WRAPPERS.put(SCOREBOARD_SIDEBAR, RenderWrapper.SCOREBOARD);
		RENDER_WRAPPERS.put(HOTBAR, RenderWrapper.HOTBAR);
		RENDER_WRAPPERS.put(SELECTED_ITEM_NAME, RenderWrapper.TOOLTIP);
		RENDER_WRAPPERS.put(CHAT, RenderWrapper.CHAT);
		RENDER_WRAPPERS.put(TITLE, RenderWrapper.ACTION_BAR);
		RENDER_WRAPPERS.put(BOSS_OVERLAY, RenderWrapper.BOSS_BAR);
	}

	static Map<Identifier, Component> STATUS_BAR_COMPONENTS = new HashMap<>();
	static {
		STATUS_BAR_COMPONENTS.put(PLAYER_HEALTH, Component.Health);
		STATUS_BAR_COMPONENTS.put(ARMOR_LEVEL, Component.Armor);
		STATUS_BAR_COMPONENTS.put(FOOD_LEVEL, Component.Hunger);
		STATUS_BAR_COMPONENTS.put(AIR_LEVEL, Component.Air);
		STATUS_BAR_COMPONENTS.put(VEHICLE_HEALTH, Component.MountHealth);
		STATUS_BAR_COMPONENTS.put(JUMP_METER, Component.MountJumpBar);
		STATUS_BAR_COMPONENTS.put(EXPERIENCE_BAR, Component.ExperienceBar);
		STATUS_BAR_COMPONENTS.put(EXPERIENCE_LEVEL, Component.ExperienceLevel);
	}

	/*
	 * NOTE: NeoForge handles events differently to Forge.
	 * In Forge, canceling the event here would still call the handler below, so we would have to still do the
	 * preRender step (and then immediately undo it in the other event).
	 * In NeoForge, this does not happen, and the second handler does not get invoked. So we can skip the matrix
	 * translation right away.
	 * We still keep the second event subscriber to hopefully catch any situation where another mod cancels one
	 * of the handled overlay events.
	 */
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void preHudComponent(RenderGuiLayerEvent.Pre event) {
		Optional.ofNullable(RENDER_WRAPPERS.get(event.getName())).ifPresent(
				wrapper -> {
					if (wrapper.isActive() && !wrapper.doRender()) {
						event.setCanceled(true);
					} else {
						wrapper.beginRender(event.getGuiGraphics());
					}
				}
		);
	}
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void cancelHudComponent(RenderGuiLayerEvent.Pre event) {
		if (event.isCanceled()) {
			Optional.ofNullable(RENDER_WRAPPERS.get(event.getName())).ifPresent(
					wrapper -> wrapper.endRender(event.getGuiGraphics())
			);
		}
	}
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void postHudComponent(RenderGuiLayerEvent.Post event) {
		Optional.ofNullable(RENDER_WRAPPERS.get(event.getName())).ifPresent(
				wrapper -> wrapper.endRender(event.getGuiGraphics())
		);
	}

	@SubscribeEvent
	public void preChat(CustomizeGuiOverlayEvent.Chat event) {
		if (Component.Chat.config.active()) {
			AutoHudRenderer.preInjectFade(Component.Chat);
		}
	}
}
