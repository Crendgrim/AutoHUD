package mod.crend.autohud.compat.mixin.detailab;

//? if detailab && <1.20.5 {
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.redlimerl.detailab.render.ArmorBarRenderer;
import mod.crend.autohud.render.RenderWrapper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ArmorBarRenderer.class, remap = false)
public class ArmorBarRendererMixin {
	@WrapMethod(method = "render")
	void autoHud$wrapRender(DrawContext context, PlayerEntity player, Operation<Void> original) {
		RenderWrapper.ARMOR.withCustomFramebuffer().wrap(context, () -> original.call(context, player));
	}
}
//?} else {
/*import mod.crend.libbamboo.VersionUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VersionUtils.class)
public class ArmorBarRendererMixin {
}
*///?}
