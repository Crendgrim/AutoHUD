//? if >=1.21.6 {
/*package mod.crend.autohud.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.crend.autohud.render.AutoHudRenderer;
import net.minecraft.client.font.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TextRenderer.Drawer.class)
public class TextRendererMixin {
	@ModifyReturnValue(method = "getRenderColor", at=@At("RETURN"))
	private int autoHud$tweakTransparency(int argb) {
		return AutoHudRenderer.modifyArgb(argb);
	}
}
*///?}
