package mod.crend.autohud;

import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Components;
import mod.crend.autohud.config.ConfigHandler;
import mod.crend.autohud.render.AutoHudRenderer;
import net.minecraft.client.MinecraftClient;

//? if fabric {
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
//? if <=1.21.4 {
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
 //?} else if <=1.21.5 {
/*import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
*///?} else {
/*import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
*///?}
//?} else if forge {
/*import mod.crend.libbamboo.ConfigScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
//? if <1.21 {
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
//?} else {
/^import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
^///?}
*///?} else {
/*import mod.crend.libbamboo.ConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
*///?}

//? if forge || neoforge {
/*@Mod(AutoHud.MOD_ID)*/
//?} else
@Entrypoint
public class AutoHudMod /*? if fabric {*/implements ClientModInitializer/*?}*/ {

    //? if fabric {
    @Override
    public void onInitializeClient() {
        AutoHud.loadConfig();
        AutoHud.init();

        ModKeyBindings.ALL.forEach(KeyBindingHelper::registerKeyBinding);
        ClientTickEvents.END_CLIENT_TICK.register(ModKeyBindings::clientTick);

        //? if <=1.21.4 {
        HudRenderCallback.EVENT.register(AutoHudRenderer::renderChatMessageIndicator);
         //?} else if <=1.21.5 {
        /*HudLayerRegistrationCallback.EVENT.register(event ->
            event.addLayer(IdentifiedLayer.of(AutoHud.CHAT_INDICATOR, AutoHudRenderer::renderChatMessageIndicator))
        );
        *///?} else {
        /*HudElementRegistry.addLast(AutoHud.CHAT_INDICATOR, AutoHudRenderer::renderChatMessageIndicator);
        AutoHudGui.register();
        *///?}

        AutoHudCompat.registerCompatibilityProviders();
        FabricLoader.getInstance().getEntrypointContainers(AutoHud.MOD_ID, AutoHudApi.class).forEach(entrypoint -> {
            AutoHud.addApi(entrypoint.getEntrypoint());
        });
    }
    //?}

    //? if forge {
    /*public AutoHudMod(/^? if >=1.21.1 {^//^FMLJavaModLoadingContext context^//^?}^/) {
        AutoHud.loadConfig();
        ConfigScreen.register(
                //? if <1.21.1 {
                ModLoadingContext.get(),
                //?} else
                /^context,^/
                () -> ConfigHandler.CONFIG_STORE
        );
    }
    *///?} else if neoforge {
    /*public AutoHudMod() {
        AutoHud.loadConfig();
    }
    *///?}


    //? if neoforge {
    /*@EventBusSubscriber(modid = AutoHud.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class GameBus {
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
    *///?}

    //? if forge || neoforge {
    /*
    //? if forge {
    /^@Mod.EventBusSubscriber(modid = AutoHud.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    ^///?} else {
    @EventBusSubscriber(modid = AutoHud.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    //?}
    public static class ModBus {

        @SubscribeEvent
        static void onClientSetup(FMLClientSetupEvent event) {
            AutoHud.init();
            //? if forge && <1.20.5
            /^MinecraftForge.EVENT_BUS.register(new AutoHudGui());^/
            //? if neoforge
            /^NeoForge.EVENT_BUS.register(new AutoHudGui());^/
            //? if neoforge {
            /^ConfigScreen.register(() -> ConfigHandler.CONFIG_STORE);
            NeoForge.EVENT_BUS.addListener(ModBus::onClientTick);
            ^///?} else if <1.21.6 {
            MinecraftForge.EVENT_BUS.addListener(ModBus::onClientTick);
            //?} else
            /^TickEvent.ClientTickEvent.Post.BUS.addListener(ModBus::onClientTick);^/
            AutoHudCompat.registerCompatibilityProviders();
        }

        @SubscribeEvent
        static void onInterModProcess(InterModProcessEvent event) {
            InterModComms.getMessages(AutoHud.MOD_ID, AutoHud.REGISTER_API::equals)
                    .map(msg -> (AutoHudApi) msg.messageSupplier().get())
                    .forEach(AutoHud::addApi);
        }

        @SubscribeEvent
        static void onKeyMappingsRegister(RegisterKeyMappingsEvent event) {
            ModKeyBindings.ALL.forEach(event::register);
        }

        //? if neoforge {
        /^@SubscribeEvent
        static void onRegisterOverlaysEvent(RegisterGuiLayersEvent event) {
            event.registerAboveAll(AutoHud.CHAT_INDICATOR, AutoHudRenderer::renderChatMessageIndicator);
        }
        ^///?} else if <1.20.5 {
        @SubscribeEvent
        static void onRegisterOverlaysEvent(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll(AutoHud.CHAT_INDICATOR.getPath(), (forgeGui, context, f, i, j) -> AutoHudRenderer.renderChatMessageIndicator(context, f));
        }
        //?}

        static void onClientTick(
                //? if neoforge {
                /^ClientTickEvent.Post event
                ^///?} else if <1.21.6 {
                TickEvent.ClientTickEvent event
                //?} else
                /^TickEvent.ClientTickEvent.Post event^/
        ) {
            //? if forge && <1.21.6
            /^if (event.phase == TickEvent.Phase.START) return;^/
            ModKeyBindings.clientTick(MinecraftClient.getInstance());
        }

    }
    *///?}
}
