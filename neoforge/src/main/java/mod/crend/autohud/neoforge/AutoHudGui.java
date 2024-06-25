package mod.crend.autohud.neoforge;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.render.AutoHudRenderer;
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

	public void preRender(DrawContext context, Component component, RenderTickCounter tickDelta) {
		if (AutoHud.config.animationMove()) {
			context.getMatrices().translate(component.getOffsetX(tickDelta.getTickDelta(true)), component.getOffsetY(tickDelta.getTickDelta(true)), 0);
		}
		AutoHudRenderer.preInjectFade(component);
	}
	public void postRender(DrawContext context, Component component, RenderTickCounter tickDelta) {
		AutoHudRenderer.postInjectFade();
		if (AutoHud.config.animationMove()) {
			context.getMatrices().translate(-component.getOffsetX(tickDelta.getTickDelta(true)), -component.getOffsetY(tickDelta.getTickDelta(true)), 0);
		}
	}

	public Optional<Component> getComponent(Identifier id) {
		if (AutoHud.targetStatusBars && STATUS_BAR_COMPONENTS.containsKey(id)) {
			return Optional.of(STATUS_BAR_COMPONENTS.get(id));
		} else if (AutoHud.targetScoreboard && id.equals(SCOREBOARD_SIDEBAR)) {
			return Optional.of(Component.Scoreboard);
		} else if (AutoHud.targetHotbar && id.equals(HOTBAR)) {
			return Optional.of(Component.Hotbar);
		} else if (AutoHud.targetHotbar && id.equals(SELECTED_ITEM_NAME)) {
			return Optional.of(Component.Tooltip);
		} else if (Component.Chat.config.active() && id.equals(CHAT)) {
			return Optional.of(Component.Chat);
		} else if (Component.ActionBar.config.active() && id.equals(TITLE)) {
			return Optional.of(Component.ActionBar);
		} else if (Component.BossBar.config.active() && id.equals(BOSS_OVERLAY)) {
			return Optional.of(Component.BossBar);
		}
		return Optional.empty();
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
		getComponent(event.getName()).ifPresent(
				component -> {
					if (component.fullyHidden()
							&& component.config.maximumFade() == 0
							&& !(component.equals(Component.Hotbar) && AutoHud.config.getHotbarItemsMaximumFade() > 0.0f)
							&& !(component.equals(Component.ExperienceBar) && AutoHudRenderer.experienceLevelOverridesBar())
					) {
						event.setCanceled(true);
					} else {
						preRender(event.getGuiGraphics(), component, event.getPartialTick());
					}
				}
		);
	}
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void cancelHudComponent(RenderGuiLayerEvent.Pre event) {
		if (event.isCanceled()) {
			getComponent(event.getName()).ifPresent(
					component -> postRender(event.getGuiGraphics(), component, event.getPartialTick())
			);
		}
	}
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void postHudComponent(RenderGuiLayerEvent.Post event) {
		getComponent(event.getName()).ifPresent(
				component -> postRender(event.getGuiGraphics(), component, event.getPartialTick())
		);
	}

	@SubscribeEvent
	public void preChat(CustomizeGuiOverlayEvent.Chat event) {
		if (Component.Chat.config.active()) {
			AutoHudRenderer.preInjectFade(Component.Chat);
		}
	}
}
