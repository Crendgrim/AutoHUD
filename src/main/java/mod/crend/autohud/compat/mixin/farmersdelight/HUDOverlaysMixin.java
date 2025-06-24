//? if farmers_delight_refabricated && >=1.21 {
/*package mod.crend.autohud.compat.mixin.farmersdelight;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import vectorwing.farmersdelight.client.gui.HUDOverlays;

@Mixin(value = HUDOverlays.class, remap = false)
public class HUDOverlaysMixin {
	@WrapMethod(
			method = "drawComfortOverlay"
	)
	private static void autoHud$wrapComfortOverlay(PlayerEntity player, MinecraftClient minecraft, DrawContext context, int left, int top, Operation<Void> original) {
		ComponentRenderer.HEALTH.wrap(context, () -> original.call(player, minecraft, context, left, top));
	}

	@WrapMethod(
			method = "drawNourishmentOverlay"
	)
	private static void autoHud$wrapNourishmentOverlay(HungerManager foodData, MinecraftClient minecraft, DrawContext context, int right, int top, boolean naturalHealing, Operation<Void> original) {
		ComponentRenderer.HEALTH.wrap(context, () -> original.call(foodData, minecraft, context, right, top, naturalHealing));
	}
}
*///?}
