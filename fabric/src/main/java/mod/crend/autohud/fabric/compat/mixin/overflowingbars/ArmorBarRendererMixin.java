package mod.crend.autohud.fabric.compat.mixin.overflowingbars;

import fuzs.overflowingbars.client.handler.ArmorBarRenderer;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Hud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ArmorBarRenderer.class, remap = false)
public class ArmorBarRendererMixin {
	@Inject(method = "renderToughnessBar", at=@At("HEAD"))
	private static void autoHud$preRenderArmorBar(MatrixStack matrixStack, int posX, int posY, PlayerEntity player, Profiler profiler, boolean left, boolean unmodified, CallbackInfo ci) {
		Hud.postInject(matrixStack);
		Hud.preInject(matrixStack, Component.Armor);
	}
}
