package mod.crend.autohud.compat.mixin.environmentz;

//? if environmentz {
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
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
	@WrapMethod(method = "renderPlayerTemperatureIcon")
	private static void autoHud$wrapTemperatureIcon(DrawContext context, MinecraftClient client, PlayerEntity playerEntity, boolean heat, int xValue, int yValue, int extra, int intensity, int scaledWidth, int scaledHeight, Operation<Void> original) {
		EnvironmentZCompat.TEMPERATURE_WRAPPER.wrap(context, () ->
				original.call(context, client, playerEntity, heat, xValue, yValue, extra, intensity, scaledWidth, scaledHeight)
		);
	}

	@WrapMethod(method = "renderThermometerIcon")
	private static void autoHud$wrapThermometerIcon(DrawContext context, MinecraftClient client, PlayerEntity playerEntity, int xValue, int yValue, int scaledWidth, int scaledHeight, Operation<Void> original) {
		EnvironmentZCompat.THERMOMETER_WRAPPER.wrap(context, () ->
				original.call(context, client, playerEntity, xValue, yValue, scaledWidth, scaledHeight)
		);
	}

	@ModifyArg(
			method = {"renderPlayerTemperatureIcon", "renderThermometerIcon"},
			at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"),
			index = 3
	)
	private static float autoHud$transparentPlayerTemperatureIcon(float alpha) {
		return (AutoHudRenderer.inRender ? AutoHudRenderer.alpha * alpha : alpha);
	}

}
//?} else {
/*import mod.crend.libbamboo.VersionUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = VersionUtils.class, remap = false)
public class TemperatureHudRenderingMixin {
}
*///?}