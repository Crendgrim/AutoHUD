package mod.crend.autohud.compat;

//? if hotbarslotcycling {
/*import fuzs.hotbarslotcycling.api.v1.client.CyclingSlotsRenderer;
import fuzs.hotbarslotcycling.api.v1.client.HotbarCyclingProvider;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Components;
import mod.crend.autohud.component.state.ItemStackComponentState;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.autohud.render.RenderWrapper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class HotbarSlotCyclingCompat implements AutoHudApi {
	public static Component HOTBAR_SLOT_CYCLING_COMPONENT = Component.builder("hotbarslotcycling")
			.isTargeted(() -> AutoHud.targetHotbar)
			.config(AutoHud.config.hotbar())
			.inMainHud()
			.state(player -> new ItemStackComponentState(
					HotbarSlotCyclingCompat.HOTBAR_SLOT_CYCLING_COMPONENT,
					() -> HotbarCyclingProvider.GLOBAL_PROVIDER[0].apply(player).getForwardStack(),
					true
			))
			.build();
	public static RenderWrapper COMPONENT_WRAPPER = RenderWrapper.of(HOTBAR_SLOT_CYCLING_COMPONENT);
	public static RenderWrapper BACKGROUND_WRAPPER = RenderWrapper.builder(HOTBAR_SLOT_CYCLING_COMPONENT)
			.fade()
			.isActive(() -> HOTBAR_SLOT_CYCLING_COMPONENT.isActive() && AutoHud.config.animationFade())
			.doRender(AutoHudRenderer::shouldRenderHotbarItems)
			.withCustomFramebuffer(true)
			.beginRender(AutoHudRenderer::postInjectFade)
			.build();
	public static RenderWrapper ITEM_WRAPPER = RenderWrapper.builder(HOTBAR_SLOT_CYCLING_COMPONENT)
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
			.beginRender(AutoHudRenderer::postInjectFade)
			.build();

	@Override
	public String modId() {
		return "hotbarslotcycling";
	}

	@Override
	public void initState(ClientPlayerEntity player) {
		HOTBAR_SLOT_CYCLING_COMPONENT.reveal();
	}

	@Override
	public void tickState(ClientPlayerEntity player) {
		// With bad timing, we can sometimes be one tick off from the hotbar.
		// That will make the slot cycler start moving just a bit earlier, which looks bad.
		if (HOTBAR_SLOT_CYCLING_COMPONENT.fullyRevealed()) {
			HOTBAR_SLOT_CYCLING_COMPONENT.synchronizeFrom(Components.Hotbar);
		}
	}

	public static void init() {
		// Fake this API being inserted via entry point
		AutoHud.addApi(new HotbarSlotCyclingCompat());
		CyclingSlotsRenderer.setSlotsRenderer(new WrappedCyclingSlotsRenderer(CyclingSlotsRenderer.getSlotsRenderer()));
	}

	static class WrappedCyclingSlotsRenderer implements CyclingSlotsRenderer {

		CyclingSlotsRenderer parent;

		public WrappedCyclingSlotsRenderer(CyclingSlotsRenderer parent) {
			this.parent = parent;
		}

		@Override
		public void renderSlots(DrawContext context, int screenWidth, int screenHeight, float partialTick, TextRenderer font, PlayerEntity player, ItemStack backwardStack, ItemStack selectedStack, ItemStack forwardStack) {
			COMPONENT_WRAPPER.wrap(context, () -> parent.renderSlots(context, screenWidth, screenHeight, partialTick, font, player, backwardStack, selectedStack, forwardStack));
		}

		@Override
		public boolean testValidStacks(ItemStack backwardStack, ItemStack selectedStack, ItemStack forwardStack) {
			boolean original = parent.testValidStacks(backwardStack, selectedStack, forwardStack);
			if (!AutoHud.targetHotbar) return original;
			if (!original) {
				HOTBAR_SLOT_CYCLING_COMPONENT.forceHide();
			}
			return !HOTBAR_SLOT_CYCLING_COMPONENT.fullyHidden() || (AutoHud.config.animationFade() && AutoHud.config.getHotbarItemsMaximumFade() > 0.0f);
		}

		@Override
		public void renderSlotBackgrounds(DrawContext context, int posX, int posY, boolean renderForwardStack, boolean renderBackwardStack, boolean renderToRight) {
			BACKGROUND_WRAPPER.wrap(context, () -> parent.renderSlotBackgrounds(context, posX, posY, renderForwardStack, renderBackwardStack, renderToRight));
		}

		@Override
		public void renderSlotItems(DrawContext context, int posX, int posY, float partialTick, TextRenderer font, PlayerEntity player, ItemStack selectedStack, ItemStack forwardStack, ItemStack backwardStack, boolean renderToRight) {
			ITEM_WRAPPER.wrap(context, () -> parent.renderSlotItems(context, posX, posY, partialTick, font, player, selectedStack, forwardStack, backwardStack, renderToRight));
		}

		@Override
		public void renderItemInSlot(DrawContext context, int posX, int posY, float partialTick, TextRenderer font, PlayerEntity player, ItemStack stack) {
			parent.renderItemInSlot(context, posX, posY, partialTick, font, player, stack);
		}
	}
}
*///?} else {
public class HotbarSlotCyclingCompat {
	public static void init() { }
}
//?}
