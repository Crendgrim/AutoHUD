package mod.crend.autohud.compat.mixin.statuseffectbars;

//? if statuseffectbars {
import io.github.a5b84.statuseffectbars.StatusEffectBarRenderer;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.render.AutoHudRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
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
	private static int autoHud$renderWithOffset(
			int y,
			DrawContext context,
			//? if >=1.21 {
			/*RenderTickCounter renderTickCounter,
			*///?} else if >=1.20.5
			/*float tickDelta,*/
			StatusEffectInstance effect
	) {
		if (AutoHudRenderer.inRender) {
			return y + (int) Component.get(effect.getEffectType()).getOffsetY(AutoHudRenderer.tickDelta);
		}
		return y;
	}
}
//?} else {
/*import mod.crend.libbamboo.VersionUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = VersionUtils.class, remap = false)
public class StatusEffectBarRendererMixin {
}
*///?}
