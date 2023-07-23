package mod.crend.autohud.forge.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.render.AutoHudRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VanillaGuiOverlay.class)
@Debug(export = true)
public class VanillaGuiOverlayMixin {

	@WrapOperation(
			method = "lambda$static$5(Lnet/minecraftforge/client/gui/overlay/ForgeGui;Lnet/minecraft/client/gui/DrawContext;FII)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraftforge/client/gui/overlay/ForgeGui;renderHotbar(FLnet/minecraft/client/gui/DrawContext;)V"
			)
	)
	private static void autoHud$wrapHotbar(ForgeGui instance, float tickDelta, DrawContext context, Operation<Void> original) {
		if (AutoHud.targetHotbar) {
			AutoHudRenderer.preInject(context, Component.Hotbar);
		}
		original.call(instance, tickDelta, context);
		if (AutoHud.targetHotbar) {
			AutoHudRenderer.postInject(context);
		}
	}

	@WrapOperation(
			method = "lambda$static$8(Lnet/minecraftforge/client/gui/overlay/ForgeGui;Lnet/minecraft/client/gui/DrawContext;FII)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraftforge/client/gui/overlay/ForgeGui;renderHealth(IILnet/minecraft/client/gui/DrawContext;)V"
			)
	)
	private static void autoHud$wrapHealth(ForgeGui instance, int width, int height, DrawContext context, Operation<Void> original) {
		if (AutoHud.targetStatusBars) {
			AutoHudRenderer.preInject(context, Component.Health);
		}
		original.call(instance, width, height, context);
		if (AutoHud.targetStatusBars) {
			AutoHudRenderer.postInject(context);
		}
	}
	@WrapOperation(
			method = "lambda$static$9(Lnet/minecraftforge/client/gui/overlay/ForgeGui;Lnet/minecraft/client/gui/DrawContext;FII)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraftforge/client/gui/overlay/ForgeGui;renderArmor(Lnet/minecraft/client/gui/DrawContext;II)V"
			)
	)
	private static void autoHud$wrapArmor(ForgeGui instance, DrawContext context, int width, int height, Operation<Void> original) {
		if (AutoHud.targetStatusBars) {
			AutoHudRenderer.preInject(context, Component.Armor);
		}
		original.call(instance, context, width, height);
		if (AutoHud.targetStatusBars) {
			AutoHudRenderer.postInject(context);
		}
	}
	@WrapOperation(
			method = "lambda$static$10(Lnet/minecraftforge/client/gui/overlay/ForgeGui;Lnet/minecraft/client/gui/DrawContext;FII)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraftforge/client/gui/overlay/ForgeGui;renderFood(IILnet/minecraft/client/gui/DrawContext;)V"
			)
	)
	private static void autoHud$wrapHunger(ForgeGui instance, int width, int height, DrawContext context, Operation<Void> original) {
		if (AutoHud.targetStatusBars) {
			AutoHudRenderer.preInject(context, Component.Hunger);
		}
		original.call(instance, width, height, context);
		if (AutoHud.targetStatusBars) {
			AutoHudRenderer.postInject(context);
		}
	}

	@WrapOperation(
			method = "lambda$static$23(Lnet/minecraftforge/client/gui/overlay/ForgeGui;Lnet/minecraft/client/gui/DrawContext;FII)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraftforge/client/gui/overlay/ForgeGui;renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"
			)
	)
	private static void autoHud$wrapScoreboard(ForgeGui instance, DrawContext context, ScoreboardObjective objective, Operation<Void> original) {
		if (AutoHud.targetScoreboard) {
			AutoHudRenderer.preInject(context, Component.Scoreboard);
		}
		original.call(instance, context, objective);
		if (AutoHud.targetScoreboard) {
			AutoHudRenderer.postInject(context);
		}
	}
}
