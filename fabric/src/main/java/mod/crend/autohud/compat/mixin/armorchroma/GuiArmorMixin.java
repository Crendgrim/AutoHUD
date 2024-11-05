package mod.crend.autohud.compat.mixin.armorchroma;

//? if armorchroma && armorchroma: <=1.2.6 {
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import mod.crend.autohud.render.RenderWrapper;
import net.minecraft.client.gui.DrawContext;
import nukeduck.armorchroma.GuiArmor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = GuiArmor.class, remap = false)
public class GuiArmorMixin {

	@WrapMethod(method = "draw")
	void autoHud$wrapArmor(DrawContext context, int left, int top, Operation<Void> original) {
		RenderWrapper.ARMOR.withCustomFramebuffer().wrap(context, () -> original.call(context, left, top));
	}

}
//?} else {
/*import mod.crend.libbamboo.VersionUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VersionUtils.class)
public class GuiArmorMixin {
}
*///?}
