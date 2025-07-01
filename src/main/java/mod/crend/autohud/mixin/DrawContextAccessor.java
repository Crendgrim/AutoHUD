//? if >=1.21.6 {
/*package mod.crend.autohud.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.GuiRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DrawContext.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public interface DrawContextAccessor {
	@Accessor
	GuiRenderState getState();
}
*///?}
