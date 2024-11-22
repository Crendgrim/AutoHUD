package mod.crend.autohud.neoforge;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.ModKeyBindings;
import mod.crend.autohud.compat.RaisedCompat;
import mod.crend.autohud.component.Components;
import mod.crend.autohud.render.AutoHudRenderer;
import net.minecraft.client.MinecraftClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
//? if <1.20.5 {
import net.neoforged.neoforge.event.TickEvent;
//?} else {
/*import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
*///?}

@Mod(AutoHud.MOD_ID)
//? if <1.20.5 {
@Mod.EventBusSubscriber(value = Dist.CLIENT)
//?} else
/*@EventBusSubscriber(value = Dist.CLIENT)*/
public class AutoHudNeoForge {
    static boolean raisedCompat = false;

    public AutoHudNeoForge() {
        AutoHud.loadConfig();
    }

    // Do not use @SubscribeEvent here because the mod "placebo" forces the game bus to run early for some reason.
    // This causes the key bindings to get ticked before the config is loaded...
    static void onClientTick(
            //? if <1.20.5 {
            TickEvent.ClientTickEvent event
            //?} else
            /*ClientTickEvent.Post event*/
    ) {
        //? if <1.20.5
        if (event.phase == TickEvent.Phase.START) return;

        ModKeyBindings.clientTick(MinecraftClient.getInstance());

        if (raisedCompat) {
            RaisedCompat.tick();
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
        if (Components.Chat.config.active() && Components.ChatIndicator.config.active() && Components.Chat.isHidden()) {
            Components.ChatIndicator.reveal();
        }
    }

}
