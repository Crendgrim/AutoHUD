package mod.crend.autohud.mixin.gui;

import mod.crend.autohud.render.CustomFramebufferRenderer;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Inject(method = "onResolutionChanged", at=@At("TAIL"))
	private void autoHud$onResolutionChanged(CallbackInfo ci) {
		CustomFramebufferRenderer.resizeFramebuffer();
	}

}
