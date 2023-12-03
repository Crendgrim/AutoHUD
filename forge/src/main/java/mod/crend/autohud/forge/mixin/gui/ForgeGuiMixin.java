package mod.crend.autohud.forge.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.render.AutoHudRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeGui.class)
public class ForgeGuiMixin {

	@Inject(method = "renderHealth", at = @At("HEAD"))
	private void autoHud$preHealth(int width, int height, DrawContext context, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			AutoHudRenderer.preInjectFade(Component.Health);
		}
	}

	@Inject(method = "renderArmor", at = @At("HEAD"))
	private void autoHud$preArmor(DrawContext context, int width, int height, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			AutoHudRenderer.preInjectFade(Component.Armor);
		}
	}

	@Inject(method = "renderFood", at = @At("HEAD"))
	private void autoHud$preFood(int width, int height, DrawContext context, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			AutoHudRenderer.preInjectFade(Component.Hunger);
		}
	}

	@Inject(method = "renderAir", at  = @At("HEAD"))
	private void autoHud$preAir(int width, int height, DrawContext context, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			AutoHudRenderer.preInjectFade(Component.Air);
		}
	}

	@Inject(method = "renderHealthMount", at  = @At("HEAD"))
	private void autoHud$preRenderHealthMount(int width, int height, DrawContext context, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			AutoHudRenderer.preInjectFade(Component.MountHealth);
		}
	}

	@WrapOperation(method = "renderBossHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/BossBarHud;render(Lnet/minecraft/client/gui/DrawContext;)V"))
	private void autoHud$wrapBossBar(BossBarHud instance, DrawContext context, Operation<Void> original) {
		if (Component.BossBar.config.active()) {
			AutoHudRenderer.preInject(context, Component.BossBar);
		}
		original.call(instance, context);
		if (Component.BossBar.config.active()) {
			AutoHudRenderer.postInject(context);
		}
	}

	@Inject(method = "renderRecordOverlay", at = @At(value = "HEAD"))
	private void autoHud$preActionBar(int width, int height, float partialTick, DrawContext context, CallbackInfo ci) {
		if (Component.ActionBar.config.active()) {
			AutoHudRenderer.preInject(context, Component.ActionBar);
		}
	}

	@Inject(method = "renderRecordOverlay", at = @At(value = "TAIL"))
	private void autoHud$postActionBar(int width, int height, float partialTick, DrawContext context, CallbackInfo ci) {
		if (Component.ActionBar.config.active()) {
			AutoHudRenderer.postInject(context);
		}
	}

}
