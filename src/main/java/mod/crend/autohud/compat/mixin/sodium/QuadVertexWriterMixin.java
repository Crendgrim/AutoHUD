package mod.crend.autohud.compat.mixin.sodium;

import me.jellysquid.mods.sodium.client.model.vertex.formats.quad.writer.QuadVertexBufferWriterNio;
import me.jellysquid.mods.sodium.client.model.vertex.formats.quad.writer.QuadVertexBufferWriterUnsafe;
import me.jellysquid.mods.sodium.client.model.vertex.formats.quad.writer.QuadVertexWriterFallback;
import mod.crend.autohud.component.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = {QuadVertexBufferWriterNio.class, QuadVertexBufferWriterUnsafe.class, QuadVertexWriterFallback.class}, remap = false)
public class QuadVertexWriterMixin {

	@ModifyVariable(method="writeQuad", at=@At("HEAD"), ordinal = 0, argsOnly = true)
	int autoHud$writeQuadTransparency(int color) {
		if (Hud.inRender) {
			return Hud.modifyArgb(color);
		}
		return color;
	}

}
