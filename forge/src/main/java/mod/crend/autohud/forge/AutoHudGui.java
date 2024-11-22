//? if <1.20.5 {
package mod.crend.autohud.forge;

import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static net.minecraftforge.client.gui.overlay.VanillaGuiOverlay.*;

public class AutoHudGui extends ForgeGui {

	public static final Map<Identifier, ComponentRenderer> COMPONENT_RENDERERS = new HashMap<>();
	static {
		COMPONENT_RENDERERS.put(PLAYER_HEALTH.id(), ComponentRenderer.HEALTH);
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
	}

	public AutoHudGui() {
		super(MinecraftClient.getInstance());
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void preHudComponent(RenderGuiOverlayEvent.Pre event) {
		Optional.ofNullable(COMPONENT_RENDERERS.get(event.getOverlay().id())).ifPresent(
				wrapper -> {
					if (wrapper.isActive() && !wrapper.doRender()) {
						event.setCanceled(true);
					}
					// Forge only: need to begin render for canceled events
					wrapper.beginRender(event.getGuiGraphics());
				}
		);
	}
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void cancelHudComponent(RenderGuiOverlayEvent.Pre event) {
		if (event.isCanceled()) {
			Optional.ofNullable(COMPONENT_RENDERERS.get(event.getOverlay().id())).ifPresent(
					wrapper -> wrapper.endRender(event.getGuiGraphics())
			);
		}
	}
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void postHudComponent(RenderGuiOverlayEvent.Post event) {
		Optional.ofNullable(COMPONENT_RENDERERS.get(event.getOverlay().id())).ifPresent(
				wrapper -> wrapper.endRender(event.getGuiGraphics())
		);
	}
}
//?}
