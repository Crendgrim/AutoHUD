package mod.crend.autohud.forge.mixin.gui;

//? if <1.21 {
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Components;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeGui.class)
public class ForgeGuiMixin {

	@Inject(method = "render", at = @At("HEAD"))
	private void autoHud$preRender(DrawContext context, float tickDelta, CallbackInfo ci) {
		AutoHudRenderer.inRender = true;
		AutoHudRenderer.tickDelta = tickDelta;
	}
	@Inject(method = "render", at = @At("RETURN"))
	private void autoHud$postRender(DrawContext context, float tickDelta, CallbackInfo ci) {
		AutoHudRenderer.inRender = false;
	}

	@Inject(method = "renderHealth", at = @At("HEAD"))
	private void autoHud$preHealth(int width, int height, DrawContext context, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			ComponentRenderer.HEALTH.beginFade(context);
		}
	}

	@Inject(method = "renderArmor", at = @At("HEAD"))
	private void autoHud$preArmor(DrawContext context, int width, int height, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			ComponentRenderer.ARMOR.beginFade(context);
		}
	}

	@Inject(method = "renderFood", at = @At("HEAD"))
	private void autoHud$preFood(int width, int height, DrawContext context, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			ComponentRenderer.HUNGER.beginFade(context);
		}
	}

	@Inject(method = "renderAir", at  = @At("HEAD"))
	private void autoHud$preAir(int width, int height, DrawContext context, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			ComponentRenderer.AIR.beginFade(context);
		}
	}

	@Inject(method = "renderHealthMount", at  = @At("HEAD"))
	private void autoHud$preRenderHealthMount(int width, int height, DrawContext context, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			ComponentRenderer.MOUNT_HEALTH.beginFade(context);
		}
	}
}
//?} else {

/*import mod.crend.libbamboo.VersionUtils;

@Mixin(VersionUtils.class)
public class ForgeGuiMixin {
}
*///?}