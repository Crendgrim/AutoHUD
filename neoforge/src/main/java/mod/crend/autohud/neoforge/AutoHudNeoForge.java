package mod.crend.autohud.neoforge;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.ModKeyBindings;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Hud;
import mod.crend.autohud.render.AutoHudRenderer;
import net.minecraft.client.MinecraftClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;

@Mod(AutoHud.MOD_ID)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class AutoHudNeoForge {
    @SubscribeEvent
    static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ModKeyBindings.clientTick(MinecraftClient.getInstance());
        }
    }

    @SubscribeEvent
    static void onPreRenderGuiEvent(RenderGuiEvent.Pre event) {
        AutoHudRenderer.startRender(event.getGuiGraphics(), event.getPartialTick());
    }
    @SubscribeEvent
    static void onPostRenderGuiEvent(RenderGuiEvent.Post event) {
        AutoHudRenderer.endRender();
    }

    @SubscribeEvent
    static void onChatMessageReceived(ClientChatReceivedEvent event) {
        if (Component.Chat.config.active() && Component.ChatIndicator.config.active() && Component.Chat.isHidden()) {
            Component.ChatIndicator.reveal();
        }
    }

}
