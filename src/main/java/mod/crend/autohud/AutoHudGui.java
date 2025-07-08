//? if (fabric && >=1.21.6) || (forge && <1.20.5) || neoforge {
/*package mod.crend.autohud;

import mod.crend.autohud.component.Components;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

//? if fabric {
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
//?} else if forge {
/^import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import static net.minecraftforge.client.gui.overlay.VanillaGuiOverlay.*;
^///?} else if neoforge {
/^import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import static net.neoforged.neoforge.client.gui.VanillaGuiLayers.*;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
^///?}

public class AutoHudGui /^? if forge {^//^extends ForgeGui^//^?}^/ {
	public static final Map<Identifier, ComponentRenderer> COMPONENT_RENDERERS = new HashMap<>();
	static {
		//? if fabric {
		COMPONENT_RENDERERS.put(VanillaHudElements.HEALTH_BAR, ComponentRenderer.HEALTH);
		COMPONENT_RENDERERS.put(VanillaHudElements.ARMOR_BAR, ComponentRenderer.ARMOR);
		COMPONENT_RENDERERS.put(VanillaHudElements.FOOD_BAR, ComponentRenderer.HUNGER);
		COMPONENT_RENDERERS.put(VanillaHudElements.AIR_BAR, ComponentRenderer.AIR);
		COMPONENT_RENDERERS.put(VanillaHudElements.MOUNT_HEALTH, ComponentRenderer.MOUNT_HEALTH);
		// TODO other bars
		COMPONENT_RENDERERS.put(VanillaHudElements.INFO_BAR, ComponentRenderer.INFO_BAR);
		COMPONENT_RENDERERS.put(VanillaHudElements.EXPERIENCE_LEVEL, ComponentRenderer.EXPERIENCE_LEVEL);

		COMPONENT_RENDERERS.put(VanillaHudElements.CROSSHAIR, ComponentRenderer.CROSSHAIR);
		COMPONENT_RENDERERS.put(VanillaHudElements.SCOREBOARD, ComponentRenderer.SCOREBOARD);
		COMPONENT_RENDERERS.put(VanillaHudElements.HOTBAR, ComponentRenderer.HOTBAR);
		COMPONENT_RENDERERS.put(VanillaHudElements.HELD_ITEM_TOOLTIP, ComponentRenderer.TOOLTIP);
		COMPONENT_RENDERERS.put(VanillaHudElements.CHAT, ComponentRenderer.CHAT);
		COMPONENT_RENDERERS.put(VanillaHudElements.OVERLAY_MESSAGE, ComponentRenderer.ACTION_BAR);
		COMPONENT_RENDERERS.put(VanillaHudElements.BOSS_BAR, ComponentRenderer.BOSS_BAR);
		//?} else if forge {
		/^COMPONENT_RENDERERS.put(PLAYER_HEALTH.id(), ComponentRenderer.HEALTH);
		COMPONENT_RENDERERS.put(ARMOR_LEVEL.id(), ComponentRenderer.ARMOR);
		COMPONENT_RENDERERS.put(FOOD_LEVEL.id(), ComponentRenderer.HUNGER);
		COMPONENT_RENDERERS.put(AIR_LEVEL.id(), ComponentRenderer.AIR);
		COMPONENT_RENDERERS.put(MOUNT_HEALTH.id(), ComponentRenderer.MOUNT_HEALTH);
		COMPONENT_RENDERERS.put(JUMP_BAR.id(), ComponentRenderer.MOUNT_JUMP_BAR);
		COMPONENT_RENDERERS.put(EXPERIENCE_BAR.id(), ComponentRenderer.EXPERIENCE_BAR_FORGE);
		//RENDER_WRAPPERS.put(EXPERIENCE_LEVEL.id(), ComponentRenderer.EXPERIENCE_LEVEL);

		COMPONENT_RENDERERS.put(SCOREBOARD.id(), ComponentRenderer.SCOREBOARD);
		COMPONENT_RENDERERS.put(HOTBAR.id(), ComponentRenderer.HOTBAR);
		COMPONENT_RENDERERS.put(ITEM_NAME.id(), ComponentRenderer.TOOLTIP);
		COMPONENT_RENDERERS.put(CHAT_PANEL.id(), ComponentRenderer.CHAT);
		COMPONENT_RENDERERS.put(TITLE_TEXT.id(), ComponentRenderer.ACTION_BAR);
		COMPONENT_RENDERERS.put(BOSS_EVENT_PROGRESS.id(), ComponentRenderer.BOSS_BAR);
		^///?} else {
		/^COMPONENT_RENDERERS.put(PLAYER_HEALTH, ComponentRenderer.HEALTH);
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
		^///?}
	}

	//? if fabric {

	public static void register() {
		COMPONENT_RENDERERS.forEach((identifier, renderer) -> HudElementRegistry.replaceElement(identifier, wrap(renderer)));
	}

	public static Function<HudElement, HudElement> wrap(ComponentRenderer renderer) {
		return original -> ((drawContext, renderTickCounter) -> renderer.wrap(drawContext, () -> original.render(drawContext, renderTickCounter)));
	}

	//?} else {

	/^public AutoHudGui() {
		//? if forge
		/^¹super(MinecraftClient.getInstance());¹^/
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void preHudComponent(/^¹? if forge {¹^//^¹RenderGuiOverlayEvent¹^//^¹?} else {¹^/RenderGuiLayerEvent/^¹?}¹^/.Pre event) {
		Optional.ofNullable(COMPONENT_RENDERERS.get(
				//? if forge {
				/^¹event.getOverlay().id()
				¹^///?} else
				event.getName()
		)).ifPresent(
				wrapper -> {
					if (wrapper.isActive() && !wrapper.doRender()) {
						event.setCanceled(true);
					}
					wrapper.beginRender(event.getGuiGraphics());
				}
		);
	}
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void cancelHudComponent(/^¹? if forge {¹^//^¹RenderGuiOverlayEvent¹^//^¹?} else {¹^/RenderGuiLayerEvent/^¹?}¹^/.Pre event) {
		if (event.isCanceled()) {
			Optional.ofNullable(COMPONENT_RENDERERS.get(
					//? if forge {
					/^¹event.getOverlay().id()
					¹^///?} else
					event.getName()
			)).ifPresent(
					wrapper -> wrapper.endRender(event.getGuiGraphics())
			);
		}
	}
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void postHudComponent(/^¹? if forge {¹^//^¹RenderGuiOverlayEvent¹^//^¹?} else {¹^/RenderGuiLayerEvent/^¹?}¹^/.Post event) {
		Optional.ofNullable(COMPONENT_RENDERERS.get(
				//? if forge {
				/^¹event.getOverlay().id()
				¹^///?} else
				event.getName()
		)).ifPresent(
				wrapper -> wrapper.endRender(event.getGuiGraphics())
		);
	}
	^///?}

	//? if neoforge {
	/^@SubscribeEvent
	public void preChat(CustomizeGuiOverlayEvent.Chat event) {
		if (Components.Chat.config.active()) {
			ComponentRenderer.CHAT.beginFade(event.getGuiGraphics());
		}
	}
	^///?}
}
*///?}
