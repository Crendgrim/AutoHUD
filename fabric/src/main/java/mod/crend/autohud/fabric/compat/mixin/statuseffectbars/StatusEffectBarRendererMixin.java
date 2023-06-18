package mod.crend.autohud.fabric.compat.mixin.statuseffectbars;

import io.github.a5b84.statuseffectbars.StatusEffectBarRenderer;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Hud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = StatusEffectBarRenderer.class, remap = false)
public class StatusEffectBarRendererMixin {
	@ModifyVariable(
			method = "render",
			at = @At("HEAD"),
			ordinal = 1,
			argsOnly = true)
	private static int autoHud$renderWithOffset(int y, MatrixStack matrices, StatusEffectInstance effect) {
		if (Hud.inRender) {
			return y + (int) Component.get(effect.getEffectType()).getOffsetY(Hud.tickDelta);
		}
		return y;
	}
}
