//? if dehydration {
package mod.crend.autohud.compat.mixin.dehydration;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import mod.crend.autohud.compat.DehydrationCompat;
import net.dehydration.thirst.ThirstHudRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ThirstHudRender.class, remap = false)
@MixinEnvironment(value = "compat", type = MixinEnvironment.Env.CLIENT)
public class DehydrationMixin {
    @WrapMethod(
            method = "renderThirstHud"
    )
    private static void autoHud$test(
            DrawContext context, MinecraftClient client, PlayerEntity playerEntity, int scaledWidth, int scaledHeight, int ticks,
            //? if dehydration: <=1.3.6
            int vehicleHeartCount, float flashAlpha, float otherFlashAlpha,
            Operation<Void> original
    ) {

        DehydrationCompat.THIRST_WRAPPER.wrap(context, () ->
                original.call(
                        context, client, playerEntity, scaledWidth, scaledHeight, ticks
                        //? if dehydration: <=1.3.6
                        , vehicleHeartCount, flashAlpha, otherFlashAlpha
                )
        );

    }
}
//?}
