package mod.crend.autohud.compat.mixin.farmersdelight;

//? if farmers_delight_refabricated && 1.20.1 {
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import vectorwing.farmersdelight.client.gui.ComfortHealthOverlay;

@Mixin(value = ComfortHealthOverlay.class, remap = false)
public class ComfortHealthOverlayMixin {
	@WrapMethod(
			method = "drawComfortOverlay"
	)
	private static void autoHud$wrap(PlayerEntity player, MinecraftClient minecraft, DrawContext context, int left, int top, Operation<Void> original) {
		ComponentRenderer.HEALTH.wrap(context, () -> original.call(player, minecraft, context, left, top));
	}
}
//?} else {
/*import mod.crend.libbamboo.VersionUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = VersionUtils.class, remap = false)
public class ComfortHealthOverlayMixin {
}
*///?}
