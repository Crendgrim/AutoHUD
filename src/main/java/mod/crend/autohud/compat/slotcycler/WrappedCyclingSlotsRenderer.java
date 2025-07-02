//? if hotbarslotcycling {
/*package mod.crend.autohud.compat.slotcycler;

import fuzs.hotbarslotcycling.api.v1.client.CyclingSlotsRenderer;
import mod.crend.autohud.AutoHud;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class WrappedCyclingSlotsRenderer implements CyclingSlotsRenderer {

	CyclingSlotsRenderer parent;

	public WrappedCyclingSlotsRenderer(CyclingSlotsRenderer parent) {
		this.parent = parent;
	}

	public static void register() {
		CyclingSlotsRenderer.setSlotsRenderer(new WrappedCyclingSlotsRenderer(CyclingSlotsRenderer.getSlotsRenderer()));
	}

	@Override
	public void renderSlots(DrawContext context, int screenWidth, int screenHeight, float partialTick, TextRenderer font, PlayerEntity player, ItemStack backwardStack, ItemStack selectedStack, ItemStack forwardStack) {
		HotbarSlotCyclingComponents.COMPONENT_WRAPPER.wrap(context, () -> parent.renderSlots(context, screenWidth, screenHeight, partialTick, font, player, backwardStack, selectedStack, forwardStack));
	}

	@Override
	public boolean testValidStacks(ItemStack backwardStack, ItemStack selectedStack, ItemStack forwardStack) {
		boolean original = parent.testValidStacks(backwardStack, selectedStack, forwardStack);
		if (!AutoHud.targetHotbar) return original;
		if (!original) {
			HotbarSlotCyclingComponents.HOTBAR_SLOT_CYCLING_COMPONENT.forceHide();
		}
		return !HotbarSlotCyclingComponents.HOTBAR_SLOT_CYCLING_COMPONENT.fullyHidden() || (AutoHud.config.animationFade() && AutoHud.config.getHotbarItemsMaximumFade() > 0.0f);
	}

	@Override
	public void renderSlotBackgrounds(DrawContext context, int posX, int posY, boolean renderForwardStack, boolean renderBackwardStack, boolean renderToRight) {
		HotbarSlotCyclingComponents.BACKGROUND_WRAPPER.wrap(context, () -> parent.renderSlotBackgrounds(context, posX, posY, renderForwardStack, renderBackwardStack, renderToRight));
	}

	@Override
	public void renderSlotItems(DrawContext context, int posX, int posY, float partialTick, TextRenderer font, PlayerEntity player, ItemStack selectedStack, ItemStack forwardStack, ItemStack backwardStack, boolean renderToRight) {
		HotbarSlotCyclingComponents.ITEM_WRAPPER.wrap(context, () -> parent.renderSlotItems(context, posX, posY, partialTick, font, player, selectedStack, forwardStack, backwardStack, renderToRight));
	}

	@Override
	public void renderItemInSlot(DrawContext context, int posX, int posY, float partialTick, TextRenderer font, PlayerEntity player, ItemStack stack) {
		parent.renderItemInSlot(context, posX, posY, partialTick, font, player, stack);
	}
}
*///?}
