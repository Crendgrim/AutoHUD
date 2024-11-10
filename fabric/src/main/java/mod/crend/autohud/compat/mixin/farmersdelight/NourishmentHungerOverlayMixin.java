package mod.crend.autohud.compat.mixin.farmersdelight;

//? if farmers_delight_refabricated && 1.20.1 {
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.HungerManager;
import org.spongepowered.asm.mixin.Mixin;
import vectorwing.farmersdelight.client.gui.NourishmentHungerOverlay;

@Mixin(value = NourishmentHungerOverlay.class, remap = false)
public class NourishmentHungerOverlayMixin {
	@WrapMethod(
			method = "drawNourishmentOverlay"
	)
	private static void autoHud$wrap(HungerManager stats, MinecraftClient mc, DrawContext context, int left, int top, boolean naturalHealing, Operation<Void> original) {
		ComponentRenderer.HUNGER.wrap(context, () -> original.call(stats, mc, context, left, top, naturalHealing));
	}
}
//?} else {
/*import mod.crend.libbamboo.VersionUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = VersionUtils.class, remap = false)
public class NourishmentHungerOverlayMixin {
}
*///?}
