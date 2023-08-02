package mod.crend.autohud.forge.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Hud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraftforge.client.gui.ForgeIngameGui;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ForgeIngameGui.class)
@Debug(export = true)
public class VanillaGuiOverlayMixin {

    @WrapOperation(
        method = "lambda$static$5(Lnet/minecraftforge/client/gui/ForgeIngameGui;Lnet/minecraft/client/util/math/MatrixStack;FII)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/client/gui/ForgeIngameGui;renderHotbar(FLnet/minecraft/client/util/math/MatrixStack;)V"
        )
    )
    private static void autoHud$wrapHotbar(ForgeIngameGui instance, float tickDelta, MatrixStack matrixStack, Operation<Void> original) {
        if (AutoHud.targetHotbar) {
            Hud.preInject(matrixStack, Component.Hotbar);
        }
        original.call(instance, tickDelta, matrixStack);
        if (AutoHud.targetHotbar) {
            Hud.postInject(matrixStack);
        }
    }

    @WrapOperation(
        method = "lambda$static$8(Lnet/minecraftforge/client/gui/ForgeIngameGui;Lnet/minecraft/client/util/math/MatrixStack;FII)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/client/gui/ForgeIngameGui;renderHealth(IILnet/minecraft/client/util/math/MatrixStack;)V"
        )
    )
    private static void autoHud$wrapHealth(ForgeIngameGui instance, int width, int height, MatrixStack matrixStack, Operation<Void> original) {
        if (AutoHud.targetStatusBars) {
            Hud.preInject(matrixStack, Component.Health);
        }
        original.call(instance, width, height, matrixStack);
        if (AutoHud.targetStatusBars) {
            Hud.postInject(matrixStack);
        }
    }

    @WrapOperation(
        method = "lambda$static$9(Lnet/minecraftforge/client/gui/ForgeIngameGui;Lnet/minecraft/client/util/math/MatrixStack;FII)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/client/gui/ForgeIngameGui;renderArmor(Lnet/minecraft/client/util/math/MatrixStack;II)V"
        )
    )
    private static void autoHud$wrapArmor(ForgeIngameGui instance, MatrixStack matrixStack, int width, int height, Operation<Void> original) {
        if (AutoHud.targetStatusBars) {
            Hud.preInject(matrixStack, Component.Armor);
        }
        original.call(instance, matrixStack, width, height);
        if (AutoHud.targetStatusBars) {
            Hud.postInject(matrixStack);
        }
    }

    @WrapOperation(
        method = "lambda$static$10(Lnet/minecraftforge/client/gui/ForgeIngameGui;Lnet/minecraft/client/util/math/MatrixStack;FII)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/client/gui/ForgeIngameGui;renderFood(IILnet/minecraft/client/util/math/MatrixStack;)V"
        )
    )
    private static void autoHud$wrapHunger(ForgeIngameGui instance, int width, int height, MatrixStack matrixStack, Operation<Void> original) {
        if (AutoHud.targetStatusBars) {
            Hud.preInject(matrixStack, Component.Hunger);
        }
        original.call(instance, width, height, matrixStack);
        if (AutoHud.targetStatusBars) {
            Hud.postInject(matrixStack);
        }
    }

    @WrapOperation(
        method = "lambda$static$15(Lnet/minecraftforge/client/gui/ForgeIngameGui;Lnet/minecraft/client/util/math/MatrixStack;FII)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/client/gui/ForgeIngameGui;renderHeldItemTooltip(Lnet/minecraft/client/util/math/MatrixStack;)V"
        )
    )
    private static void autoHud$wrapTooltip(ForgeIngameGui instance, MatrixStack matrixStack, Operation<Void> original) {
        if (AutoHud.targetHotbar) {
            Hud.preInject(matrixStack, Component.Tooltip);
        }
        original.call(instance, matrixStack);
        if (AutoHud.targetStatusBars) {
            Hud.postInject(matrixStack);
        }
    }

    @WrapOperation(
        method = "lambda$static$23(Lnet/minecraftforge/client/gui/ForgeIngameGui;Lnet/minecraft/client/util/math/MatrixStack;FII)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/client/gui/ForgeIngameGui;renderScoreboardSidebar(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"
        )
    )
    private static void autoHud$wrapScoreboard(ForgeIngameGui instance, MatrixStack matrixStack, ScoreboardObjective objective, Operation<Void> original) {
        if (AutoHud.targetScoreboard) {
            Hud.preInject(matrixStack, Component.Scoreboard);
        }
        original.call(instance, matrixStack, objective);
        if (AutoHud.targetScoreboard) {
            Hud.postInject(matrixStack);
        }
    }
}