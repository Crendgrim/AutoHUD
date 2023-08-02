package mod.crend.autohud.forge;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.ModKeyBindings;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.screen.ConfigScreenFactory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

@Mod.EventBusSubscriber(modid = AutoHud.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AutoHudModEvents {
    public static final String REGISTER_API = "register_api";

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        MixinExtrasBootstrap.init();
        AutoHud.init();
		ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class,
				() -> new ConfigGuiHandler.ConfigGuiFactory(
					(minecraft,screen)->ConfigScreenFactory.makeScreen(screen)
				)
        );
    }

    @SubscribeEvent
    static void onInterModProcess(InterModProcessEvent event) {
        InterModComms.getMessages(AutoHud.MOD_ID, REGISTER_API::equals)
            .map(msg -> (AutoHudApi) msg.messageSupplier().get())
            .forEach(AutoHud::addApi);
    }

    @SubscribeEvent
    static void onKeyMappingsRegister(FMLClientSetupEvent event) {
        ModKeyBindings.ALL.forEach(ClientRegistry::registerKeyBinding);
    }
}