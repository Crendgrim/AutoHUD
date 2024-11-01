package mod.crend.autohud.neoforge.compat;

//? if <1.21 {
public class HotbarSlotCyclingCompat {
	public static void init() { }
}
//?} else {
/*import fuzs.hotbarslotcycling.api.v1.client.CyclingSlotsRenderer;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.state.ItemStackComponentState;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.libbamboo.render.CustomFramebufferRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class HotbarSlotCyclingCompat implements AutoHudApi {
	public static Component HotbarSlotCyclerComponent = Component.builder("hotbarslotcycling").config(AutoHud.config.hotbar()).build();
	public static ItemStack forwardStack = ItemStack.EMPTY;

	@Override
	public String modId() {
		return "hotbarslotcycling";
	}

	@Override
	public void initState(ClientPlayerEntity player) {
		Component.registerComponent(HotbarSlotCyclerComponent);
		HotbarSlotCyclerComponent.state = new ItemStackComponentState(HotbarSlotCyclerComponent, () -> forwardStack, true);
		HotbarSlotCyclerComponent.reveal();
	}

	@Override
	public void tickState(ClientPlayerEntity player) {
		// With bad timing, we can sometimes be one tick off from the hotbar.
		// That will make the slot cycler start moving just a bit earlier, which looks bad.
		if (HotbarSlotCyclerComponent.fullyRevealed()) {
			HotbarSlotCyclerComponent.synchronizeFrom(Component.Hotbar);
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
			if (AutoHud.targetHotbar) {
				AutoHudRenderer.preInject(context, HotbarSlotCyclingCompat.HotbarSlotCyclerComponent);
			}
			parent.renderSlots(context, screenWidth, screenHeight, partialTick, font, player, backwardStack, selectedStack, forwardStack);
			if (AutoHud.targetHotbar) {
				AutoHudRenderer.postInject(context);
			}
		}

		@Override
		public boolean testValidStacks(ItemStack backwardStack, ItemStack selectedStack, ItemStack forwardStack) {
			boolean original = parent.testValidStacks(backwardStack, selectedStack, forwardStack);
			if (!AutoHud.targetHotbar) return original;
			HotbarSlotCyclingCompat.forwardStack = forwardStack;
			if (!original) {
				HotbarSlotCyclingCompat.HotbarSlotCyclerComponent.forceHide();
			}
			return !HotbarSlotCyclingCompat.HotbarSlotCyclerComponent.fullyHidden() || (AutoHud.config.animationFade() && AutoHud.config.getHotbarItemsMaximumFade() > 0.0f);
		}

		@Override
		public void renderSlotBackgrounds(DrawContext context, int posX, int posY, boolean renderForwardStack, boolean renderBackwardStack, boolean renderToRight) {
			// For some reason, rendering the background texture as transparent does not work. Render them to a texture first instead.
			if (AutoHud.targetHotbar && AutoHud.config.animationFade()) {
				if (!HotbarSlotCyclingCompat.HotbarSlotCyclerComponent.fullyHidden()) {
					AutoHudRenderer.postInjectFade(context);
					CustomFramebufferRenderer.init();
					parent.renderSlotBackgrounds(context, posX, posY, renderForwardStack, renderBackwardStack, renderToRight);
					AutoHudRenderer.preInjectFadeWithReverseTranslation(context, HotbarSlotCyclingCompat.HotbarSlotCyclerComponent, 0);
					CustomFramebufferRenderer.draw(context);
					AutoHudRenderer.postInjectFadeWithReverseTranslation(context);
				}
			} else {
				parent.renderSlotBackgrounds(context, posX, posY, renderForwardStack, renderBackwardStack, renderToRight);
			}
		}

		@Override
		public void renderSlotItems(DrawContext context, int posX, int posY, float partialTick, TextRenderer font, PlayerEntity player, ItemStack selectedStack, ItemStack forwardStack, ItemStack backwardStack, boolean renderToRight) {
			if (AutoHud.targetHotbar && AutoHud.config.animationFade()) {
				// Don't render items if they're fully invisible anyway
				if (!HotbarSlotCyclingCompat.HotbarSlotCyclerComponent.fullyHidden() || AutoHud.config.getHotbarItemsMaximumFade() > 0.0f) {
					// We need to reset the renderer because otherwise the first item gets drawn with double alpha
					AutoHudRenderer.postInjectFade(context);
					// Setup custom framebuffer
					CustomFramebufferRenderer.init();
					// Have the original call draw onto the custom framebuffer
					parent.renderSlotItems(context, posX, posY, partialTick, font, player, selectedStack, forwardStack, backwardStack, renderToRight);
					// Render the contents of the custom framebuffer as a texture with transparency onto the main framebuffer
					AutoHudRenderer.preInjectFadeWithReverseTranslation(context, HotbarSlotCyclingCompat.HotbarSlotCyclerComponent, AutoHud.config.getHotbarItemsMaximumFade());
					CustomFramebufferRenderer.draw(context);
					AutoHudRenderer.postInjectFadeWithReverseTranslation(context);
				}
			} else {
				parent.renderSlotItems(context, posX, posY, partialTick, font, player, selectedStack, forwardStack, backwardStack, renderToRight);
			}
		}

		@Override
		public void renderItemInSlot(DrawContext context, int posX, int posY, float partialTick, TextRenderer font, PlayerEntity player, ItemStack stack) {
			parent.renderItemInSlot(context, posX, posY, partialTick, font, player, stack);
		}
	}
}
*///?}
