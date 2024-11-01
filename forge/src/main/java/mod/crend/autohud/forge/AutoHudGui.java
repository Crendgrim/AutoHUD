package mod.crend.autohud.forge;

import mod.crend.autohud.render.RenderWrapper;
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

	static Map<Identifier, RenderWrapper> RENDER_WRAPPERS = new HashMap<>();
	static {
		RENDER_WRAPPERS.put(PLAYER_HEALTH.id(), RenderWrapper.HEALTH);
		RENDER_WRAPPERS.put(ARMOR_LEVEL.id(), RenderWrapper.ARMOR);
		RENDER_WRAPPERS.put(FOOD_LEVEL.id(), RenderWrapper.HUNGER);
		RENDER_WRAPPERS.put(AIR_LEVEL.id(), RenderWrapper.AIR);
		RENDER_WRAPPERS.put(MOUNT_HEALTH.id(), RenderWrapper.MOUNT_HEALTH);
		RENDER_WRAPPERS.put(JUMP_BAR.id(), RenderWrapper.MOUNT_JUMP_BAR);
		RENDER_WRAPPERS.put(EXPERIENCE_BAR.id(), RenderWrapper.EXPERIENCE_BAR);
		//RENDER_WRAPPERS.put(EXPERIENCE_LEVEL.id(), RenderWrapper.EXPERIENCE_LEVEL);

		RENDER_WRAPPERS.put(SCOREBOARD.id(), RenderWrapper.SCOREBOARD);
		RENDER_WRAPPERS.put(HOTBAR.id(), RenderWrapper.HOTBAR);
		RENDER_WRAPPERS.put(ITEM_NAME.id(), RenderWrapper.TOOLTIP);
		RENDER_WRAPPERS.put(CHAT_PANEL.id(), RenderWrapper.CHAT);
		RENDER_WRAPPERS.put(TITLE_TEXT.id(), RenderWrapper.ACTION_BAR);
		RENDER_WRAPPERS.put(BOSS_EVENT_PROGRESS.id(), RenderWrapper.BOSS_BAR);
	}

	public AutoHudGui() {
		super(MinecraftClient.getInstance());
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void preHudComponent(RenderGuiOverlayEvent.Pre event) {
		Optional.ofNullable(RENDER_WRAPPERS.get(event.getOverlay().id())).ifPresent(
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
			Optional.ofNullable(RENDER_WRAPPERS.get(event.getOverlay().id())).ifPresent(
					wrapper -> wrapper.endRender(event.getGuiGraphics())
			);
		}
	}
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void postHudComponent(RenderGuiOverlayEvent.Post event) {
		Optional.ofNullable(RENDER_WRAPPERS.get(event.getOverlay().id())).ifPresent(
				wrapper -> wrapper.endRender(event.getGuiGraphics())
		);
	}
}
