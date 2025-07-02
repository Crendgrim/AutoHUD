//? if hotbarslotcycling {
/*package mod.crend.autohud.compat.slotcycler;

import fuzs.hotbarslotcycling.api.v1.client.HotbarCyclingProvider;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.state.ItemStackComponentState;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.autohud.render.ComponentRenderer;

public class HotbarSlotCyclingComponents {

	public static Component HOTBAR_SLOT_CYCLING_COMPONENT = Component.builder("hotbarslotcycling")
			.isTargeted(() -> AutoHud.targetHotbar)
			.config(AutoHud.config.hotbar())
			.inMainHud()
			.state(player -> new ItemStackComponentState(
					HotbarSlotCyclingComponents.HOTBAR_SLOT_CYCLING_COMPONENT,
					() -> HotbarCyclingProvider.GLOBAL_PROVIDER[0].apply(player).getForwardStack(),
					true
			))
			.build();
	public static ComponentRenderer COMPONENT_WRAPPER = ComponentRenderer.of(HOTBAR_SLOT_CYCLING_COMPONENT);
	public static ComponentRenderer BACKGROUND_WRAPPER = ComponentRenderer.builder(HOTBAR_SLOT_CYCLING_COMPONENT)
			.fade()
			.isActive(() -> HOTBAR_SLOT_CYCLING_COMPONENT.isActive() && AutoHud.config.animationFade())
			.doRender(AutoHudRenderer::shouldRenderHotbarItems)
			.withCustomFramebuffer(true)
			.beginRender(COMPONENT_WRAPPER::endFade)
			.build();
	public static ComponentRenderer ITEM_WRAPPER = ComponentRenderer.builder(HOTBAR_SLOT_CYCLING_COMPONENT)
			.fade()
			.isActive(() -> HOTBAR_SLOT_CYCLING_COMPONENT.isActive() && AutoHud.config.animationFade())
			.doRender(() -> (
					// Render items when they're not fully hidden (in other words, visible in some way)
					!HOTBAR_SLOT_CYCLING_COMPONENT.fullyHidden()
							// If we are in fade mode, only render items if they're not fully transparent.
							|| (AutoHud.config.animationFade() && AutoHud.config.getHotbarItemsMaximumFade() > 0.0f)
							// If we are neither in fade nor move mode, skip rendering if it's hidden.
							// If we are in move mode, the items may still be visible in the "hidden" state!
							|| (!AutoHud.config.animationFade() && AutoHud.config.animationMove())
			))
			.withCustomFramebuffer(true)
			.beginRender(COMPONENT_WRAPPER::endFade)
			.build();
}
*///?}
