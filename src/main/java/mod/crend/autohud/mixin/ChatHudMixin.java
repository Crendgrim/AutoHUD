package mod.crend.autohud.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import mod.crend.autohud.component.Components;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class ChatHudMixin {

	@Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("TAIL"))
	private void autoHud$showChatMessageIndicator(Text message, MessageSignatureData signature, MessageIndicator indicator, CallbackInfo ci) {
		if (Components.Chat.config.active() && Components.ChatIndicator.config.active() && Components.Chat.isHidden()) {
			Components.ChatIndicator.reveal();
		}
	}

}
