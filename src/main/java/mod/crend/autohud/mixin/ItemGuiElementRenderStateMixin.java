//? if >=1.21.6 {
/*package mod.crend.autohud.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import mod.crend.autohud.compat.ItemGuiElementRenderStateAccessor;
import net.minecraft.client.gui.render.state.ItemGuiElementRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemGuiElementRenderState.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class ItemGuiElementRenderStateMixin implements ItemGuiElementRenderStateAccessor {

    @Unique
    private final boolean autohud$isHudItem = ItemGuiElementRenderStateAccessor.STATE_HOLDER.get();

    @Override
    public boolean autohud$isHudItem() {
        return autohud$isHudItem;
    }


}
*///?}