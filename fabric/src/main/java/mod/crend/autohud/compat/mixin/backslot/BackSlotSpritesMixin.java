package mod.crend.autohud.compat.mixin.backslot;

import mod.crend.autohud.component.Component;
import mod.crend.autohud.render.AutoHudRenderer;
import net.backslot.client.sprite.BackSlotSprites;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackSlotSprites.class)
public class BackSlotSpritesMixin {
	@Inject(method = "lambda$init$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;getScaledWindowWidth()I", shift = At.Shift.AFTER))
	private static void autoHud$preBackslots(DrawContext drawContext, float tickDelta, CallbackInfo ci) {
		AutoHudRenderer.preInject(drawContext, Component.Hotbar);
	}
	@Inject(method = "lambda$init$0", at = @At(value = "INVOKE", target = "Lnet/backslot/client/sprite/BackSlotSprites;renderHotbarItem(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/MinecraftClient;IIFLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V", ordinal = 1, shift = At.Shift.AFTER))
	private static void autoHud$postBackslots(DrawContext drawContext, float tickDelta, CallbackInfo ci) {
		AutoHudRenderer.postInject(drawContext);
	}
}
