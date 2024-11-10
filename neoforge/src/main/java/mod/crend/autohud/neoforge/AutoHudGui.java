package mod.crend.autohud.neoforge;

import mod.crend.autohud.component.Components;
import mod.crend.autohud.render.ComponentRenderer;
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

	public static Map<Identifier, ComponentRenderer> COMPONENT_RENDERERS = new HashMap<>();
	static {
		COMPONENT_RENDERERS.put(PLAYER_HEALTH, ComponentRenderer.HEALTH);
		COMPONENT_RENDERERS.put(ARMOR_LEVEL, ComponentRenderer.ARMOR);
		COMPONENT_RENDERERS.put(FOOD_LEVEL, ComponentRenderer.HUNGER);
		COMPONENT_RENDERERS.put(AIR_LEVEL, ComponentRenderer.AIR);
		COMPONENT_RENDERERS.put(VEHICLE_HEALTH, ComponentRenderer.MOUNT_HEALTH);
		COMPONENT_RENDERERS.put(JUMP_METER, ComponentRenderer.MOUNT_JUMP_BAR);
		COMPONENT_RENDERERS.put(EXPERIENCE_BAR, ComponentRenderer.EXPERIENCE_BAR);
		COMPONENT_RENDERERS.put(EXPERIENCE_LEVEL, ComponentRenderer.EXPERIENCE_LEVEL);

		COMPONENT_RENDERERS.put(SCOREBOARD_SIDEBAR, ComponentRenderer.SCOREBOARD);
		COMPONENT_RENDERERS.put(HOTBAR, ComponentRenderer.HOTBAR);
		COMPONENT_RENDERERS.put(SELECTED_ITEM_NAME, ComponentRenderer.TOOLTIP);
		COMPONENT_RENDERERS.put(CHAT, ComponentRenderer.CHAT);
		COMPONENT_RENDERERS.put(TITLE, ComponentRenderer.ACTION_BAR);
		COMPONENT_RENDERERS.put(BOSS_OVERLAY, ComponentRenderer.BOSS_BAR);
	}

	/*
	 * NOTE: NeoForge before 1.21 handles events differently to Forge.
	 * In Forge and modern NeoForge, canceling the event here would still call the handler below, so we would have to
	 * still do the preRender step (and then immediately undo it in the other event).
	 * In older NeoForge, this does not happen, and the second handler does not get invoked. So we can skip the matrix
	 * translation right away.
	 * We still keep the second event subscriber to hopefully catch any situation where another mod cancels one
	 * of the handled overlay events.
	 */
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void preHudComponent(RenderGuiLayerEvent.Pre event) {
		Optional.ofNullable(COMPONENT_RENDERERS.get(event.getName())).ifPresent(
				wrapper -> {
					if (wrapper.isActive() && !wrapper.doRender()) {
						event.setCanceled(true);
					}
					//? if <1.21
					else
					{
						wrapper.beginRender(event.getGuiGraphics());
					}
				}
		);
	}
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void cancelHudComponent(RenderGuiLayerEvent.Pre event) {
		if (event.isCanceled()) {
			Optional.ofNullable(COMPONENT_RENDERERS.get(event.getName())).ifPresent(
					wrapper -> wrapper.endRender(event.getGuiGraphics())
			);
		}
	}
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void postHudComponent(RenderGuiLayerEvent.Post event) {
		Optional.ofNullable(COMPONENT_RENDERERS.get(event.getName())).ifPresent(
				wrapper -> wrapper.endRender(event.getGuiGraphics())
		);
	}

	@SubscribeEvent
	public void preChat(CustomizeGuiOverlayEvent.Chat event) {
		if (Components.Chat.config.active()) {
			ComponentRenderer.CHAT.beginFade(event.getGuiGraphics());
		}
	}
}
