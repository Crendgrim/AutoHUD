package mod.crend.autohud.neoforge;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.render.AutoHudRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay.*;

public class AutoHudGui extends ExtendedGui {

	static Map<Identifier, Component> STATUS_BAR_COMPONENTS = new HashMap<>();
	static {
		STATUS_BAR_COMPONENTS.put(PLAYER_HEALTH.id(), Component.Health);
		STATUS_BAR_COMPONENTS.put(ARMOR_LEVEL.id(), Component.Armor);
		STATUS_BAR_COMPONENTS.put(FOOD_LEVEL.id(), Component.Hunger);
		STATUS_BAR_COMPONENTS.put(AIR_LEVEL.id(), Component.Air);
		STATUS_BAR_COMPONENTS.put(MOUNT_HEALTH.id(), Component.MountHealth);
		STATUS_BAR_COMPONENTS.put(JUMP_BAR.id(), Component.MountJumpBar);
		STATUS_BAR_COMPONENTS.put(EXPERIENCE_BAR.id(), Component.ExperienceBar);
	}

	public AutoHudGui() {
		super(MinecraftClient.getInstance());
	}

	public void preRender(DrawContext context, Component component, float tickDelta) {
		if (AutoHud.config.animationMove()) {
			context.getMatrices().translate(component.getOffsetX(tickDelta), component.getOffsetY(tickDelta), 0);
		}
		AutoHudRenderer.preInjectFade(component);
	}
	public void postRender(DrawContext context, Component component, float tickDelta) {
		AutoHudRenderer.postInjectFade();
		if (AutoHud.config.animationMove()) {
			context.getMatrices().translate(-component.getOffsetX(tickDelta), -component.getOffsetY(tickDelta), 0);
		}
	}

	public Optional<Component> getComponent(Identifier id) {
		if (AutoHud.targetStatusBars && STATUS_BAR_COMPONENTS.containsKey(id)) {
			return Optional.of(STATUS_BAR_COMPONENTS.get(id));
		} else if (AutoHud.targetScoreboard && id.equals(SCOREBOARD.id())) {
			return Optional.of(Component.Scoreboard);
		} else if (AutoHud.targetHotbar && id.equals(HOTBAR.id())) {
			return Optional.of(Component.Hotbar);
		} else if (AutoHud.targetHotbar && id.equals(ITEM_NAME.id())) {
			return Optional.of(Component.Tooltip);
		} else if (Component.Chat.config.active() && id.equals(CHAT_PANEL.id())) {
			return Optional.of(Component.Chat);
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
	public void preHudComponent(RenderGuiOverlayEvent.Pre event) {
		getComponent(event.getOverlay().id()).ifPresent(
				component -> {
					if (component.fullyHidden() && !(component.equals(Component.ExperienceBar) && AutoHudRenderer.experienceLevelOverridesBar())) {
						event.setCanceled(true);
					} else {
						preRender(event.getGuiGraphics(), component, event.getPartialTick());
					}
				}
		);
	}
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void cancelHudComponent(RenderGuiOverlayEvent.Pre event) {
		if (event.isCanceled()) {
			getComponent(event.getOverlay().id()).ifPresent(
					component -> postRender(event.getGuiGraphics(), component, event.getPartialTick())
			);
		}
	}
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void postHudComponent(RenderGuiOverlayEvent.Post event) {
		getComponent(event.getOverlay().id()).ifPresent(
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
