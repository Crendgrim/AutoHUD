//? if armorchroma && armorchroma: <=1.2.6 {
package mod.crend.autohud.compat.mixin.armorchroma;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.gui.DrawContext;
import nukeduck.armorchroma.GuiArmor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = GuiArmor.class, remap = false)
public class GuiArmorMixin {

	@WrapMethod(method = "draw")
	void autoHud$wrapArmor(DrawContext context, int left, int top, Operation<Void> original) {
		ComponentRenderer.ARMOR_FADE.wrap(context, () -> original.call(context, left, top));
	}

}
//?}
