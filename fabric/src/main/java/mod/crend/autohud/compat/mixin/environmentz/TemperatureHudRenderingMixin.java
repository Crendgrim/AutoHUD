package mod.crend.autohud.compat.mixin.environmentz;

//? if environmentz {
import mod.crend.autohud.compat.EnvironmentZCompat;
import mod.crend.autohud.render.AutoHudRenderer;
import net.environmentz.temperature.TemperatureHudRendering;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TemperatureHudRendering.class, remap = false)
public class TemperatureHudRenderingMixin {
	@Inject(method = "renderPlayerTemperatureIcon", at=@At("HEAD"))
	private static void autoHud$prePlayerTemperatureIcon(DrawContext context, MinecraftClient client, PlayerEntity playerEntity, boolean heat, int xValue, int yValue, int extra, int intensity, int scaledWidth, int scaledHeight, CallbackInfo ci) {
		AutoHudRenderer.preInject(context, EnvironmentZCompat.Temperature);
	}

	@ModifyArg(method = "renderPlayerTemperatureIcon", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"), index = 3)
	private static float autoHud$transparentPlayerTemperatureIcon(float alpha) {
		return (AutoHudRenderer.inRender ? AutoHudRenderer.alpha * alpha : alpha);
	}

	@Inject(method = "renderPlayerTemperatureIcon", at=@At("TAIL"))
	private static void autoHud$postPlayerTemperatureIcon(DrawContext context, MinecraftClient client, PlayerEntity playerEntity, boolean heat, int xValue, int yValue, int extra, int intensity, int scaledWidth, int scaledHeight, CallbackInfo ci) {
		AutoHudRenderer.postInject(context);
	}

	@Inject(method = "renderThermometerIcon", at=@At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V", shift = At.Shift.AFTER))
	private static void autoHud$preThermometer(DrawContext context, MinecraftClient client, PlayerEntity playerEntity, int xValue, int yValue, int scaledWidth, int scaledHeight, CallbackInfo ci) {
		AutoHudRenderer.preInject(context, EnvironmentZCompat.Thermometer);
	}
	@Inject(method = "renderThermometerIcon", at=@At("TAIL"))
	private static void autoHud$postThermometer(DrawContext context, MinecraftClient client, PlayerEntity playerEntity, int xValue, int yValue, int scaledWidth, int scaledHeight, CallbackInfo ci) {
		AutoHudRenderer.postInject(context);
	}

}
//?} else {
/*import mod.crend.libbamboo.VersionUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = VersionUtils.class, remap = false)
public class TemperatureHudRenderingMixin {
}
*///?}